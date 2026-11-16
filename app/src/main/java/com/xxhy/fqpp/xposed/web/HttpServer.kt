package com.xxhy.fqpp.xposed.web

import android.graphics.Bitmap
import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.utils.JsonUtils
import com.xxhy.fqpp.xposed.utils.LogUtils
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * 基于NanoHTTPD的HTTP服务器实现类
 * 用于处理客户端的HTTP请求，通过路由映射将不同URI请求分发到对应的处理器，并返回处理结果
 */
class HttpServer private constructor(port: Int) : NanoHTTPD(port) {

    companion object {
        // 单例实例，使用volatile保证多线程可见性
        @Volatile
        private var INSTANCE: HttpServer? = null
        // Gson实例，用于JSON序列化/反序列化（线程安全）
        private val GSON = Gson()
        // 路由映射表：URI路径 -> 对应的处理器
        private val ROUTE_MAP = createRouteMap()

        /**
         * 获取单例实例（线程安全）
         * 从SP中读取端口配置，默认使用SPConstants中定义的默认端口
         */
        @JvmStatic
        fun getInstance(): HttpServer {
            if (INSTANCE == null) {
                synchronized(HttpServer::class.java) {
                    if (INSTANCE == null) {
                        // 从SP获取端口配置，若未配置则使用默认值
                        val sp = ConfigManager.prefs()
                        val portStr = sp.getString(SPConstants.PORT, SPConstants.DEFAULT_PORT)
                            ?: SPConstants.DEFAULT_PORT
                        val port = portStr.toInt() // 解析端口号（默认值确保格式正确）
                        INSTANCE = HttpServer(port)
                    }
                }
            }
            return INSTANCE!!
        }

        /**
         * 初始化路由映射表
         * 将URI路径与DragonController中的处理方法关联
         */
        private fun createRouteMap(): Map<String, RouteHandler> {
            val map = HashMap<String, RouteHandler>(16) // 初始容量16，减少扩容开销
            map["/reading/bookapi/search/tab"] = RouteHandler { p -> DragonController.search(p) }          // 搜索接口
            map["/reading/bookapi/detail"] = RouteHandler { p -> DragonController.detail(p) }          // 详情接口
            map["/reading/bookapi/multi-detail"] = RouteHandler { p -> DragonController.multiDetail(p) }
            map["/reading/bookapi/directory/all_items"] = RouteHandler { p -> DragonController.catalog(p) }        // 目录接口
            map["/reading/reader/full"] = RouteHandler { p -> DragonController.content(p) }        // 内容接口
            map["/reading/reader/batch_full"] = RouteHandler { p -> DragonController.batchContent(p) }        // 内容接口
            map["/reading/bookapi/audio/playurl"] = RouteHandler { p -> DragonController.audioPlayURL(p) }     // 音频播放地址接口
            map["/reading/bookapi/audio/decrypt"] = RouteHandler { p -> DragonController.audioDecrypt(p) }     // 音频解密接口
            map["/reading/bookapi/bookshelf/info"] = RouteHandler { p -> DragonController.bookshelfInfo(p) }    // 书架接口
            map["/reading/bookapi/bookshelf/add"] = RouteHandler { p -> DragonController.addBookToShelf(p) }    // 书架接口
            map["/reading/bookapi/bookmall/cell/change"] = RouteHandler { p -> DragonController.bookMall(p) } // 书城接口
            map["/reading/bookapi/new_category/landing"] = RouteHandler { p -> DragonController.newCategory(p) } // 新分类接口
            map["/novel/commentapi/comment/list"] = RouteHandler { p -> DragonController.commentList(p) } // 评论列表接口（段评/章评/书评）
            map["/novel/commentapi/idea/list"] = RouteHandler { p -> DragonController.commentBubble(p) } // 评论气泡接口
            map["/novel/commentapi/reply/list"] = RouteHandler { p -> DragonController.replyList(p) } // 子评论（回复）列表接口
            map["/booksource"] = RouteHandler { p -> DragonController.bookSource(p) } // 导入书源接口
            return map
        }
    }

    /**
     * 处理HTTP请求的核心方法
     * 解析请求信息，分发到对应路由处理器，构建并返回响应
     * @param session HTTP会话对象，包含请求信息
     * @return 处理后的HTTP响应
     */
    override fun serve(session: IHTTPSession): Response {
        return try {
            val uri = session.uri       // 请求URI
            val method = session.method // 请求方法（GET/POST等）
            val headers = session.headers // 请求头

            LogUtils.log("HttpServer", "" + method.name + "," + uri)

            // 处理GET和POST方法
            if (Method.GET == method || Method.POST == method) {
                // 确保Content-Type包含UTF-8编码，避免中文乱码
                // SDK 21 兼容处理
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // API 24+ 使用 computeIfPresent
                    val contentType = headers["content-type"]
                    if (contentType != null && !contentType.contains("charset")) {
                        headers["content-type"] = contentType + "; charset=UTF-8"
                    }
                } else {
                    // API 21-23 兼容处理
                    val value = headers["content-type"]
                    if (value != null && !value.contains("charset")) {
                        headers["content-type"] = value + "; charset=UTF-8"
                    }
                }

                // 测试接口：返回ok
                if (uri.endsWith("/status")) {
                    return createCorsResponse("text/plain", "ok!", headers)
                }

                // 获取请求参数（GET的query参数或POST的表单参数）
                val params = session.parameters
                // 路由处理并获取返回数据
                val returnData = handleRoute(uri, params)
                // 构建响应并返回
                return buildResponse(returnData, headers)
            }

            // 不支持的请求方法，返回404
            create404Response()
        } catch (e: Throwable) {
            // 捕获所有异常，避免服务器崩溃，记录错误日志
            LogUtils.logError("HttpServer", "[HttpServer.serve] 处理HTTP请求失败:", e)
            createErrorResponse(e)
        }
    }

    /**
     * 根据请求URI匹配对应的路由处理器
     * 匹配规则：URI以路由表中的路径结尾即视为匹配
     * @param uri 请求URI
     * @param params 请求参数
     * @return 处理器返回的结果数据（可能为null）
     */
    private fun handleRoute(uri: String, params: Map<String, List<String>>): Any? {
        for (entry in ROUTE_MAP) {
            if (uri.endsWith(entry.key)) {
                // 匹配到路由，调用对应处理器
                return entry.value.handle(params)
            }
        }
        // 无匹配路由，返回null（后续会处理为404）
        return null
    }

    /**
     * 根据返回数据类型构建HTTP响应
     * 支持Bitmap（图片）和普通数据（文本/JSON）
     * @param returnData 处理器返回的数据
     * @param headers 请求头（用于处理CORS）
     * @return 构建完成的HTTP响应
     * @throws IOException 处理Bitmap时可能抛出IO异常
     */
    @Throws(IOException::class)
    private fun buildResponse(returnData: Any?, headers: Map<String, String>): Response {
        if (returnData == null) {
            return create404Response() // 无数据返回404
        }

        val response: Response
        if (returnData is Bitmap) {
            // 处理图片类型响应
            response = createBitmapResponse(returnData)
        } else if (returnData is ByteArray) {
            // 处理二进制数据响应（如解密后的音频数据）
            response = createBinaryResponse(returnData)
        } else {
            // 处理文本类型响应
            val dataStr = returnData.toString()
            // 根据内容判断MIME类型（JSON或普通文本）
            val mimeType = if (isJsonValid(dataStr))
                "application/json; charset=UTF-8"
            else "text/plain; charset=UTF-8"
            response = newFixedLengthResponse(Response.Status.OK, mimeType, dataStr)
        }

        // 添加CORS头信息
        return addCorsHeaders(response, headers)
    }

    /**
     * 构建Bitmap类型的响应（返回图片）
     * 将Bitmap压缩为PNG格式，转换为输入流返回
     * @param bitmap 图片对象
     * @return 包含图片数据的HTTP响应
     * @throws IOException 压缩或流处理异常
     */
    @Throws(IOException::class)
    private fun createBitmapResponse(bitmap: Bitmap): Response {
        // 自动关闭流资源
        ByteArrayOutputStream().use { outputStream ->
            // 压缩Bitmap为PNG（质量100%）
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray = outputStream.toByteArray()

            ByteArrayInputStream(byteArray).use { inputStream ->
                // 返回图片流，指定长度避免Chunked编码问题
                return newFixedLengthResponse(
                    Response.Status.OK,
                    "image/png",
                    inputStream,
                    byteArray.size.toLong()
                )
            }
        }
    }

    /**
     * 构建二进制数据类型的响应（返回音频等二进制数据）
     * @param data 二进制数据
     * @return 包含二进制数据的HTTP响应
     */
    private fun createBinaryResponse(data: ByteArray): Response {
        val inputStream = ByteArrayInputStream(data)
        val response = newFixedLengthResponse(
            Response.Status.OK,
            "audio/mpeg",
            inputStream,
            data.size.toLong()
        )
        response.addHeader("Accept-Ranges", "bytes")
        return response
    }

    /**
     * 构建404响应（资源未找到）
     * 返回HTML格式的404页面
     * @return 404响应
     */
    private fun create404Response(): Response {
        val html404 = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <title>404 Not Found</title>
              <style>
                body{font-family:sans-serif;text-align:center;margin-top:15%}
                h1{font-size:48px;color:#555}
                p{font-size:20px;color:#777}
              </style>
            </head>
            <body>
              <h1>404</h1>
              <p>资源未找到</p>
            </body>
            </html>
        """.trimIndent()

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", html404)
    }

    /**
     * 构建服务器内部错误响应（500）
     * 将异常信息序列化为JSON返回
     * @param t 触发错误的异常
     * @return 500响应
     */
    private fun createErrorResponse(t: Throwable): Response {
        return newFixedLengthResponse(
            Response.Status.INTERNAL_ERROR,
            "application/json",
            JsonUtils.toJson(t)
        )
    }

    /**
     * 为响应添加CORS（跨域资源共享）头信息
     * 允许跨域请求，支持GET和POST方法
     * @param response 原始响应
     * @param headers 请求头（用于获取Origin）
     * @return 添加CORS头后的响应
     */
    private fun addCorsHeaders(response: Response, headers: Map<String, String>): Response {
        response.addHeader("Access-Control-Allow-Methods", "GET,POST")
        // 允许请求的Origin，若不存在则允许所有（*）
        val origin = headers["origin"]
        response.addHeader("Access-Control-Allow-Origin", origin ?: "*")
        return response
    }

    /**
     * 创建带CORS头的文本响应
     * @param mimeType 内容类型
     * @param content 响应内容
     * @param headers 请求头
     * @return 带CORS头的响应
     */
    private fun createCorsResponse(mimeType: String, content: String, headers: Map<String, String>): Response {
        val response = newFixedLengthResponse(Response.Status.OK, mimeType, content)
        return addCorsHeaders(response, headers)
    }

    /**
     * 验证字符串是否为有效的JSON格式
     * @param json 待验证的字符串
     * @return 若为有效JSON返回true，否则false
     */
    private fun isJsonValid(json: String): Boolean {
        return try {
            GSON.fromJson(json, Any::class.java) // 尝试解析为JSON对象
            true
        } catch (e: JsonSyntaxException) {
            // 解析失败，不是有效JSON
            false
        }
    }

    /**
     * 路由处理器函数式接口
     * 定义路由处理方法的签名：接收请求参数，返回处理结果
     */
    private fun interface RouteHandler {
        fun handle(params: Map<String, List<String>>): Any?
    }

    /**
     * 重启服务器（先停止再启动）
     * @throws IOException 服务器启动/停止异常
     */
    @Throws(IOException::class)
    fun restart() {
        if (isAlive) {
            stop() // 若服务器已启动，先停止
        }
        start() // 启动服务器
    }
}
