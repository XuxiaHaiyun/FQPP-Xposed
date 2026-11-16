package com.xxhy.fqpp.xposed.web

import com.xxhy.fqpp.xposed.HookManager
import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.locate.DragonTargets
import com.xxhy.fqpp.xposed.utils.DragonUtils
import com.xxhy.fqpp.xposed.utils.FieldNameUtils
import com.xxhy.fqpp.xposed.utils.JsonUtils
import com.xxhy.fqpp.xposed.utils.ListUtils
import com.xxhy.fqpp.xposed.utils.LogUtils
import org.joor.Reflect

/**
 * Dragon服务类：
 * 提供与应用的RPC交互功能，封装了搜索书籍、获取书籍详情、音频播放地址、目录、内容等核心操作 内部通过反射调用目标应用的RPC接口，处理请求对象构建、参数设置及响应处理
 */
object DragonService {
  // ============================= 常量定义 =============================
  /**
   * 【定位特征】主RPC服务类（书城/书架/搜索/详情）（原ro4.b）
   * 类型：public class（非接口）
   * 关键特征：
   *   - 包含内部接口（通常名为a/b/c），该接口有@RpcOperation注解
   *   - 包含方法i0：参数GetSearchPageRequest，返回Observable<GetSearchTabDataResponse>
   *   - 包含方法K：参数GetDirectoryForItemIdRequest，返回Observable<GetDirectoryForItemIdResponse>
   *   - 包含方法z：参数GetBookShelfInfoRequest，返回Observable<GetBookShelfInfoResponse>
   *   - 包含方法U：参数GetNewCategoryLandingPageRequest，返回Observable<GetNewCategoryLandingPageResponse>
   *   - 包含方法h：参数BookDetailRequest，返回Observable<BookDetailResponse>
   *   - 包含方法f：参数AudioPlayURLRequest，返回Observable<AudioPlayURLResponse>
   *   - 包含方法j：参数GetBookMallCellChangeRequest，返回Observable<GetBookMallCellChangeResponse>
   * 查找方式：搜索包含上述方法签名的类（通过字节码工具如jadx查看）
   * 版本变化迹象：搜索、书架、详情等功能失效
   */
  private const val CLASS_R63_A = "ro4.b"
  /**
   * 【定位特征】阅读内容RPC(单章节内容获取)类（原ro4.f）
   * 类型：public class
   * 关键特征：
   *   - 包含方法k：参数FullRequest，返回Observable<FullResponse>
   *   - RPC路径特征：接口注解包含"/reading/reader/full/v:version/"
   *   - 关联类：FullRequest（含itemId/bookId/keyRegisterTs字段）、FullResponse
   * 查找方式：搜索返回FullResponse且参数为FullRequest的静态方法
   * 版本变化迹象：getContent返回空或异常，keyRegisterTs检查失败
   */
  private const val CLASS_R63_D = "ro4.f"
  /**
   * 【定位特征】SAAS层批处理类（原u56.a）
   * 类型：public class
   * 关键特征：
   *   - 包含方法h：参数BatchFullRequest，返回Observable<BatchFullResponse>
   *   - RPC路径特征："/reading/reader/batch_full/v:version/"
   *   - 区别于主RPC：处理的是SAAS层模型（readersaas.com.dragon.read.saas.rpc.model包）
   *   - BatchFullRequest含List<String> itemIds字段
   * 查找方式：搜索处理BatchFullRequest的类
   * 版本变化迹象：getBatchContent返回空，或提示类找不到
   */
  private const val CLASS_U56_A = "u56.a"

  /**
   * 【定位特征】密钥管理器（原w85.i）
   * 类型：public final class（Kotlin单例或Java工具类）
   * 关键特征：
   *   - 包含静态/单例方法o：参数(String userId, int keyVersion)，返回Single<DecryptKey>
   *   - 包含方法F：参数String userId，返回Single<DecryptKey>（重新注册密钥）
   *   - 包含方法C：参数String userId，返回int（获取keyRegisterTs时间戳）
   *   - 包含方法l：参数(String content, DecryptKey key)，返回byte[]
   *   - 包含方法k：参数(String encryptContent, String key)，返回byte[]（V1 AES解密）
   *   - 字符串常量："ac25c67ddd8f38c1b37a2348828e222e"（密钥派生盐值，Base64相关）
   *   - 字段特征：ConcurrentHashMap<String, String>类型的静态字段（内存缓存）
   * 查找方式：搜索包含上述盐值字符串的类，或搜索返回DecryptKey的静态方法
   * 版本变化迹象：解密失败，提示"key is empty"或"deviceId or userId is empty"
   */
  private const val CLASS_W85_I = "w85.i"
  /**
   * 【定位特征】解密工具类（原com.dragon.read.reader.utils.o）
   * 类型：public final class（Kotlin）
   * 关键特征：
   *   - 静态方法a：参数(ChapterInfo, DecryptKey)，返回String（自动处理压缩）
   *   - 静态方法b：参数(String content, DecryptKey, boolean compress, String bookId, String chapterId)，返回String
   *   - 内部调用w85.i.l进行实际解密
   *   - 处理compressStatus逻辑（调用com.dragon.read.util.r1.a解压）
   * 查找方式：搜索调用w85.i.l且处理compressStatus的类
   * 版本变化迹象：解密成功但内容乱码（压缩处理缺失），或找不到方法m（旧版工具类）
   * 历史变迁：旧版为com.dragon.read.reader.utils.m（方法b），新版迁移至o
   */
  private const val CLASS_DECRYPT_UTILS = "com.dragon.read.reader.utils.o"
  /**
   * 【定位特征】章节服务接口（原zm4.e）
   * 类型：public interface
   * 关键特征：
   *   - 方法r：参数(String content, DecryptKey key, boolean z, String bookId, String chapterId)，返回String
   *   - 方法b：参数(String userId, int keyVersion)，返回Single<DecryptKey>
   *   - 方法d：参数String userId，返回int（获取keyRegisterTs）
   *   - 获取方式：通过NsReaderServiceApi.IMPL.readerChapterService()
   * 查找方式：搜索包含方法r签名的接口，且该接口被NsReaderServiceApi引用
   * 版本变化迹象：通过readerChapterService获取的解密方法失效
   */
  private const val CLASS_CHAPTER_SERVICE = "zm4.e" // ReaderChapterService接口
  /**
   * 【定位特征】密钥数据类（原com.dragon.read.reader.DecryptKey）
   * 类型：public final class（Kotlin数据类）
   * 关键特征：
   *   - 字段e：String类型（V1密钥）
   *   - 字段f：String类型（V2 KMS密钥）
   *   - 字段c：int类型（keyVersion）
   *   - 字段b：int类型（metaVersion）
   *   - 方法c()：返回boolean（notNeedDecrypt标记）
   *   - 伴生对象方法b()：返回DecryptKey（构造明文密钥）
   *   - 枚举Source：包含SERVER/CACHE/NONE
   * 查找方式：搜索包含"notNeedDecrypt"或同时包含v1/v2密钥字段的类
   * 版本变化迹象：解密时NullPointerException，或无法构造DecryptKey实例
   */
  private const val CLASS_DECRYPT_KEY = "com.dragon.read.reader.DecryptKey"
  private const val CLASS_READER_SERVICE_API = "com.dragon.read.component.biz.api.NsReaderServiceApi"

  // ============================= 快速验证清单 =============================

  /**
   * 【版本升级检查清单】
   * 当应用更新后，按以下顺序验证：
   * 1. 搜索功能是否正常 -> 检查CLASS_MAIN_RPC（方法i0）
   * 2. 目录获取是否正常 -> 检查CLASS_MAIN_RPC（方法K）
   * 3. 内容获取是否解密成功 -> 检查CLASS_CONTENT_RPC（方法k）和CLASS_KEY_MANAGER（方法o）
   * 4. 批量获取是否正常 -> 检查CLASS_BATCH_RPC（方法h）
   * 5. 解密是否乱码 -> 检查CLASS_DECRYPT_UTILS（方法b）是否正确处理compressStatus
   *
   * 【常见变化模式】
   * - 包名变化：ro4 -> rp4 -> rq4（通常按字母表顺序递增）
   * - 方法名变化：单字母方法通常在a-z之间轮换（如i0->j0，K->L等）
   */

  // 类名常量RPC请求模型类
  private const val GET_SEARCH_PAGE_REQUEST_CLASS = "com.dragon.read.rpc.model.GetSearchPageRequest"
  private const val SEARCH_SOURCE_CLASS = "com.dragon.read.rpc.model.SearchSource"
  private const val SEARCH_TAB_TYPE_CLASS = "com.dragon.read.rpc.model.SearchTabType"
  private const val BOOK_DETAIL_REQUEST_CLASS = "com.dragon.read.rpc.model.BookDetailRequest"
  private const val BOOK_MULTI_DETAIL_REQUEST_CLASS = "com.dragon.read.rpc.model.MBookDetailRequest"
  private const val AUDIO_PLAY_URL_REQUEST_CLASS = "com.dragon.read.rpc.model.AudioPlayURLRequest"
  private const val AUDIO_PLAY_URL_REQ_TYPE_CLASS = "com.dragon.read.rpc.model.AudioPlayUrlReqType"
  private const val TONE_QUALITY_CLASS = "com.dragon.read.rpc.model.ToneQuality"
  private const val GET_DIRECTORY_REQUEST_CLASS = "com.dragon.read.rpc.model.GetDirectoryForItemIdRequest"
  private const val FULL_REQUEST_CLASS = "com.dragon.read.rpc.model.FullRequest"
  private const val SAAS_ITEM_CONTENT_CLASS = "readersaas.com.dragon.read.saas.rpc.model.ItemContent"
  private const val GET_BOOK_SHELF_INFO_REQUEST_CLASS = "com.dragon.read.rpc.model.GetBookShelfInfoRequest"
  private const val ADD_BOOK_SHELF_INFO_REQUEST_CLASS = "com.dragon.read.rpc.model.AddBookShelfInfoRequest"
  private const val GET_BOOK_MALL_REQUEST_CLASS = "com.dragon.read.rpc.model.GetBookMallCellChangeRequest"
  private const val GET_NEW_CATEGORY_REQUEST_CLASS = "com.dragon.read.rpc.model.GetNewCategoryLandingPageRequest"
  private const val BATCH_FULL_REQUEST_CLASS = "readersaas.com.dragon.read.saas.rpc.model.BatchFullRequest"

  // ============================= 核心业务方法 =============================

  /**
   * 获取评论列表（支持段评/章评/书评）
   * commentType: 0=章评, 1=段评, 2=书评
   * commentSource: 1=书评NovelBookComment, 2=段评NovelParaComment, 4=章评NovelItemComment
   *
   * 7.0.9.32 验证（jadx）：
   * - 章评：commentType=NewItem(4)，groupType=Book(1)，serverChannel=NovelItemList(18)
   * - 段评：commentType=Paragraph(1)，groupType=Item(15)
   * - 书评：commentType=Book(2)，groupType=Book(1)，serverChannel=NovelBookInnerList(1)
   *
   * @param itemId 章节ID（段评/章评）或书籍ID（书评）
   * @param bookId 书籍ID
   * @param commentType 评论类型：0=章评, 1=段评, 2=书评
   * @param paraIndex 段落索引（段评必填）
   * @param itemVersion 章节版本号（段评必填，传null则自动获取）
   * @param count 请求数量（默认20）
   * @param cursor 分页游标
   * @param sort 排序方式：1=SmartHot, 2=TimeAsc, 3=TimeDesc（默认1）
   * @param foldType 折叠类型：0=Normal, 1=Unfold, 2=Fold（默认0）
   * @param tag 话题标签（书评可选）
   * @param needCount 是否需要计数（默认true）
   * @return 评论列表结果或异常对象
   */
  @JvmStatic
  fun getCommentList(
    itemId: String, bookId: String?, commentType: Int,
    paraIndex: Int, itemVersion: String?, count: Int,
    cursor: String?, sort: Int, foldType: Int,
    tag: String?, needCount: Boolean,
  ): Any? {
    try {
      val request = createRequestObject(commentListRequestClass())

      // 设置基础参数
      Reflect.on(request).set("appID", 1967)

      // 根据commentType设置不同的枚举值
      val cl = DragonGlobals.getDragonClassLoader()

      // commentSource: 1=书评, 2=段评, 4=章评
      val commentSource = Reflect.onClass(commentSourceEnumClass(), cl)
        .call("findByValue", if (commentType == 2) 1 else if (commentType == 1) 2 else 4).get<Any?>()
      Reflect.on(request).set("commentSource", commentSource)

      // groupType: 书评=Book(1), 段评=Item(15), 章评=Book(1)
      val groupType = Reflect.onClass(commentRelativeTypeClass(), cl)
        .call("findByValue", if (commentType == 1) 15 else 1).get<Any?>()
      Reflect.on(request).set("groupType", groupType)

      // commentType枚举: 章评=NewItem(4), 段评=Paragraph(1), 书评=Book(2)
      val commentTypeEnum = Reflect.onClass(commentGroupTypeClass(), cl)
        .call("findByValue", if (commentType == 0) 4 else commentType).get<Any?>()
      Reflect.on(request).set("commentType", commentTypeEnum)

      // serverChannel
      val serverChannel: Any? = when {
        commentType == 2 -> {
          // 书评
          Reflect.onClass(commentChannelEnumClass(), cl)
            .call("findByValue", 1).get() // NovelBookInnerList
        }
        commentType == 0 -> {
          // 章评
          Reflect.onClass(commentChannelEnumClass(), cl)
            .call("findByValue", 18).get() // NovelItemList
        }
        else -> {
          // 段评
          Reflect.onClass(commentChannelEnumClass(), cl)
            .call("findByValue", 39).get() // NovelShortStoryParaList
        }
      }
      Reflect.on(request).set("serverChannel", serverChannel)

      // sort: SmartHot(1)/TimeAsc(2)/TimeDesc(3)
      val sortEnum = Reflect.onClass(commentSortEnumClass(), cl)
        .call("findByValue", if (sort > 0) sort else 1).get<Any?>()
      Reflect.on(request).set("sort", sortEnum)

      // groupID
      Reflect.on(request).set("groupID", itemId)

      // count和cursor
      Reflect.on(request).set("count", if (count > 0) count else 20)
      if (cursor != null && cursor.isNotEmpty()) {
        Reflect.on(request).set("cursor", cursor)
      }

      // businessParam
      val businessParam = createRequestObject(commentBusinessParamClass())
      Reflect.on(businessParam).set("needCount", needCount)

      // foldType: Normal(0)/Unfold(1)/Fold(2)
      if (foldType > 0) {
        val foldTypeEnum = Reflect.onClass(commentFoldTypeClass(), cl)
          .call("findByValue", foldType).get<Any?>()
        if (foldTypeEnum != null) {
          Reflect.on(businessParam).set("foldType", foldTypeEnum)
        }
      }

      // tag 话题标签（书评可选）
      if (tag != null && tag.isNotEmpty()) {
        Reflect.on(businessParam).set("tag", tag)
      }

      if (commentType == 2) {
        // 书评：groupID=bookId, businessParam只需bookID和needCount
        Reflect.on(businessParam).set("bookID", itemId) // 书评时itemId就是bookId
      } else if (commentType == 1) {
        // 段评：需要bookID、paraIndex、itemVersion
        Reflect.on(businessParam).set("bookID", bookId)
        Reflect.on(businessParam).set("paraIndex", paraIndex)
        var version = itemVersion
        if ((version == null || version.isEmpty()) && bookId != null && bookId.isNotEmpty()) {
          version = getItemVersion(bookId, itemId)
        }
        Reflect.on(businessParam).set("itemVersion", version ?: "")
      } else {
        // 章评：需要bookID，不需要itemVersion和paraIndex
        Reflect.on(businessParam).set("bookID", bookId)
      }

      Reflect.on(request).set("businessParam", businessParam)

      // 调用RPC接口（返回Observable，需blockingFirst获取实际响应）
      val observable = Reflect.onClass(commentApiServiceClass(), cl)
        .call("getCommentListRxJava", request).get<Any?>()
      return Reflect.on(observable).call("blockingFirst").get<Any?>()
    } catch (throwable: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getCommentList] 获取评论列表失败", throwable)
      return throwable
    }
  }

  /**
   * 获取子评论（回复）列表。
   * 7.0.9.32 验证（jadx，对应 /novel/commentapi/reply/list/{commentId}/v1）：
   * - 章评回复：commentSource=NovelItemReply(504)，commentType=NewItem(4)，groupType=Book(1)，serverChannel=NovelItemList(18)
   * - 段评回复：commentSource=NovelParaReply(502)，commentType=Paragraph(1)，groupType=Item(15)
   * - 书评回复：commentSource=NovelBookReply(501)，commentType=Book(2)，groupType=Book(1)
   * - businessParam=ReplyBusinessParam{bookID, needCount=false, refReplyID, insertReplyIDs}
   *
   * @param commentId 父评论ID（必填）
   * @param groupId 分组ID：章节ID（段评/章评）或书籍ID（书评）
   * @param bookId 书籍ID
   * @param commentType 评论类型：0=章评, 1=段评, 2=书评
   * @param count 请求数量（默认20）
   * @param cursor 分页游标
   * @return 子评论列表结果或异常对象
   */
  @JvmStatic
  fun getReplyList(
    commentId: String, groupId: String?, bookId: String?, commentType: Int,
    count: Int, cursor: String?,
  ): Any? {
    try {
      val cl = DragonGlobals.getDragonClassLoader()

      val request = createRequestObject(replyListRequestClass())

      // 设置基础参数
      Reflect.on(request).set("appID", 1967)
      Reflect.on(request).set("commentID", commentId)
      Reflect.on(request).set("groupID", groupId)

      // 子评论来源: 章评=504(NovelItemReply), 段评=502(NovelParaReply), 书评=501(NovelBookReply)
      val commentSource = Reflect.onClass(commentSourceEnumClass(), cl)
        .call("findByValue", if (commentType == 0) 504 else if (commentType == 1) 502 else 501).get<Any?>()
      Reflect.on(request).set("commentSource", commentSource)

      // commentType: 章评=NewItem(4), 段评=Paragraph(1), 书评=Book(2)
      val commentTypeEnum = Reflect.onClass(commentGroupTypeClass(), cl)
        .call("findByValue", if (commentType == 0) 4 else commentType).get<Any?>()
      Reflect.on(request).set("commentType", commentTypeEnum)

      // groupType: 段评=Item(15), 章评/书评=Book(1)
      val groupType = Reflect.onClass(commentRelativeTypeClass(), cl)
        .call("findByValue", if (commentType == 1) 15 else 1).get<Any?>()
      Reflect.on(request).set("groupType", groupType)

      // serverChannel: 与对应评论列表一致
      val serverChannel: Any? = when (commentType) {
        2 -> Reflect.onClass(commentChannelEnumClass(), cl).call("findByValue", 1).get<Any?>() // NovelBookInnerList
        0 -> Reflect.onClass(commentChannelEnumClass(), cl).call("findByValue", 18).get<Any?>() // NovelItemList
        else -> Reflect.onClass(commentChannelEnumClass(), cl).call("findByValue", 39).get<Any?>() // NovelShortStoryParaList
      }
      Reflect.on(request).set("serverChannel", serverChannel)

      Reflect.on(request).set("count", if (count > 0) count else 20)
      if (cursor != null && cursor.isNotEmpty()) {
        Reflect.on(request).set("cursor", cursor)
      }

      // businessParam=ReplyBusinessParam{bookID, needCount=false}
      val businessParam = createRequestObject(replyBusinessParamClass())
      Reflect.on(businessParam).set("bookID", bookId)
      Reflect.on(businessParam).set("needCount", false)
      Reflect.on(request).set("businessParam", businessParam)

      // 调用RPC接口（返回Observable，需blockingFirst获取实际响应）
      val observable = Reflect.onClass(commentApiServiceClass(), cl)
        .call("getReplyListRxJava", request).get<Any?>()
      return Reflect.on(observable).call("blockingFirst").get<Any?>()
    } catch (throwable: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getReplyList] 获取子评论列表失败，commentId：" + commentId, throwable)
      return throwable
    }
  }

  /**
   * 获取评论气泡（段评气泡数据）
   * 接口: /novel/commentapi/idea/list/:item_id/v1
   * commentSource: 3=NovelParaCommentExposed
   *
   * @param itemId 章节ID
   * @param bookId 书籍ID（用于自动获取itemVersion）
   * @param itemVersion 章节版本号（传null则自动获取）
   * @return 评论气泡数据或异常对象
   */
  @JvmStatic
  fun getCommentBubble(itemId: String, bookId: String?, itemVersion: String?): Any? {
    try {
      val cl = DragonGlobals.getDragonClassLoader()

      // 获取itemVersion
      var version = itemVersion
      if ((version == null || version.isEmpty()) && bookId != null && bookId.isNotEmpty()) {
        version = getItemVersion(bookId, itemId)
      }

      // 创建请求对象
      val request = createRequestObject("com.dragon.read.saas.ugc.model.GetIdeaListRequest")

      // 设置基础参数
      Reflect.on(request).set("appID", 1967)
      Reflect.on(request).set("complianceStatus", 0)

      // commentSource: 3 = NovelParaCommentExposed
      val commentSource = Reflect.onClass(commentSourceEnumClass(), cl)
        .call("findByValue", 3).get<Any?>()
      Reflect.on(request).set("commentSource", commentSource)
      Reflect.on(request).set("itemID", itemId)
      Reflect.on(request).set("itemVersion", version ?: "")

      // 调用RPC接口（返回Observable，需blockingFirst获取实际响应）
      val observable = Reflect.onClass(commentApiServiceClass(), cl)
        .call("getIdeaListRxJava", request).get<Any?>()
      return Reflect.on(observable).call("blockingFirst").get<Any?>()
    } catch (throwable: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getCommentBubble] 获取评论气泡失败", throwable)
      return throwable
    }
  }

  /**
   * 自动获取章节版本号(itemVersion)
   * 通过目录接口获取所有章节信息，在itemDataList中查找匹配itemId的章节，提取version
   *
   * @param bookId 书籍ID
   * @param itemId 章节ID
   * @return 章节版本号，获取失败返回null
   */
  @JvmStatic
  fun getItemVersion(bookId: String, itemId: String): String? {
    try {
      val cl = DragonGlobals.getDragonClassLoader()

      // 通过目录请求获取章节信息
      val directoryRequestClass = Class.forName(GET_DIRECTORY_REQUEST_CLASS, true, cl)
      val directoryRequest = Reflect.onClass(directoryRequestClass).create().get<Any?>()
      Reflect.on(directoryRequest).set("bookId", bookId.toLong())
      Reflect.on(directoryRequest).set("needVersion", true)

      // 调用目录接口
      val response = callFunction(rpcFacadeBClass(), directoryRequest, "K")
      if (response == null) return null

      // 从响应中提取version: Response -> data -> itemDataList -> 找到itemId匹配的项 -> version
      val data = Reflect.on(response).field("data").get<Any?>()
      if (data == null) return null

      val itemDataList = Reflect.on(data).field("itemDataList").get<List<*>?>()
      if (itemDataList == null) return null

      for (item in itemDataList) {
        val id = Reflect.on(item).field("itemId").get<String?>()
        if (itemId == id) {
          val version = Reflect.on(item).field("version").get<String?>()
          if (version != null && version.isNotEmpty()) {
            return version
          }
        }
      }
      return null
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getItemVersion] 获取章节版本号失败，bookId：" + bookId + "，itemId：" + itemId, t)
      return null
    }
  }

  /**
   * 搜索书籍
   *
   * @param keyword 搜索关键词
   * @param page 页码（从1开始）
   * @return 搜索结果（Observable的阻塞结果）或异常对象
   */
  @JvmStatic
  fun search(keyword: String, page: Int, count: Int, tabType: Int): Any? {
    try {
      // 创建搜索请求对象
      val searchRequest = createRequestObject(GET_SEARCH_PAGE_REQUEST_CLASS)

      // 设置搜索请求参数
      Reflect.on(searchRequest)
        .set("bookshelfSearchPlan", 4) // 书架搜索计划
        .set("bookstoreTab", 2) // 书店标签
        .set("clickedContent", "page_search_button") // 点击来源
        .set("query", keyword) // 搜索关键词

      // 设置搜索源（枚举类型）
      val searchSourceClass = Class.forName(SEARCH_SOURCE_CLASS, true, DragonGlobals.getDragonClassLoader())
      val searchSource = Reflect.onClass(searchSourceClass).call("findByValue", 1).get<Any?>()
      Reflect.on(searchRequest).set("searchSource", searchSource)

      // 获取用户是否登录
      val isLogin = DragonUtils.isLogin()
      var loginStatus: Short = 0
      if (isLogin) {
        loginStatus = 1
      }

      val pageSize = if (count > 0) count else 10 // 每页数据量
      // 设置其他搜索参数
      Reflect.on(searchRequest)
        .set("searchSourceId", "clks###")
        .set("tabName", "store")
        .set("userIsLogin", loginStatus) // 是否登录
        .set("offset", ((page - 1) * pageSize).toLong()) // 偏移量（分页）
        .set("count", pageSize.toLong()) // 每页数量
        .set("passback", ((page - 1) * pageSize).toString()) // 新版中int转为String

      // tabType: 搜索标签类型（可选，默认3=Book）
      val searchTabTypeClass = Class.forName(SEARCH_TAB_TYPE_CLASS, true, DragonGlobals.getDragonClassLoader())
      val tabTypeEnum = Reflect.onClass(searchTabTypeClass)
        .call("findByValue", if (tabType > 0) tabType else 3).get<Any?>()
      if (tabTypeEnum != null) {
        Reflect.on(searchRequest).set("tabType", tabTypeEnum)
      } else {
        Reflect.on(searchRequest).set("tabType", getSearchTabType())
      }

      // 调用搜索接口
      return callFunction(rpcFacadeBClass(), searchRequest, "i0")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.search] 搜索书籍失败，关键词：" + keyword + "，页码：" + page, t)
      return t
    }
  }

  /**
   * 搜索书籍
   *
   * @param keyword 搜索关键词
   * @param page 页码（从1开始）
   * @return 搜索结果（Observable的阻塞结果）或异常对象
   */
  @JvmStatic
  fun search(keyword: String, page: Int): Any? {
    return search(keyword, page, 10, 3)
  }

  /**
   * 获取书籍详情
   *
   * @param bookId 书籍ID（字符串形式的数字）
   * @return 书籍详情结果或异常对象
   */
  @JvmStatic
  fun getDetail(bookId: String, source: Int, withoutVideo: Boolean): Any? {
    // 参数验证
    if (!isValidNumericId(bookId)) {
      val e = IllegalArgumentException("无效的bookId：" + bookId)
      LogUtils.logError("DragonService", "[DragonService.getDetail] 书籍ID无效：" + bookId, e)
      return e
    }

    try {
      // 创建书籍详情请求对象并设置书籍ID
      val detailRequestClass = Class.forName(BOOK_DETAIL_REQUEST_CLASS, true, DragonGlobals.getDragonClassLoader())
      val detailRequest = Reflect.onClass(detailRequestClass).create().get<Any?>()
      Reflect.on(detailRequest).set("bookId", bookId.toLong())

      // source: DetailSource枚举（0=Default, 1=Cover, 2=NewVideoTab等）
      if (source > 0) {
        val detailSourceClass = Class.forName("com.dragon.read.rpc.model.DetailSource", true, DragonGlobals.getDragonClassLoader())
        val sourceEnum = Reflect.onClass(detailSourceClass).call("findByValue", source).get<Any?>()
        if (sourceEnum != null) {
          Reflect.on(detailRequest).set("source", sourceEnum)
        }
      }

      // withoutVideo: 是否不包含视频信息
      if (withoutVideo) {
        Reflect.on(detailRequest).set("withoutVideo", true)
      }

      // 调用详情接口
      return callFunction(rpcFacadeBClass(), detailRequest, "h")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getDetail] 获取书籍详情失败，bookId：" + bookId, t)
      return t
    }
  }

  /**
   * 获取书籍详情
   *
   * @param bookId 书籍ID（字符串形式的数字）
   * @return 书籍详情结果或异常对象
   */
  @JvmStatic
  fun getDetail(bookId: String): Any? {
    return getDetail(bookId, 0, false)
  }

  /**
   * 获取多个书籍详情
   *
   * @param bookId 书籍ID（字符串形式的数字）
   * @return 书籍详情结果或异常对象
   */
  @JvmStatic
  fun getMultiDetail(bookId: String): Any? {
    // 参数验证
    if (!isValidNumericId(bookId)) {
      // 支持逗号分隔的多个bookId，逐个验证
      if (bookId.contains(",")) {
        val bookIds = bookId.split(",")
        for (singleBookId in bookIds) {
          val trimmedId = singleBookId.trim()
          if (!isValidNumericId(trimmedId)) {
            val e = IllegalArgumentException("无效的bookId：" + trimmedId)
            LogUtils.logError("DragonService", "[DragonService.getDetail] 书籍ID无效：" + trimmedId, e)
            return e
          }
        }
      } else {
        val e = IllegalArgumentException("无效的bookId：" + bookId)
        LogUtils.logError("DragonService", "[DragonService.getDetail] 书籍ID无效：" + bookId, e)
        return e
      }
    }

    try {
      // 创建书籍详情请求对象并设置书籍ID
      val detailRequestClass = Class.forName(BOOK_MULTI_DETAIL_REQUEST_CLASS, true, DragonGlobals.getDragonClassLoader())
      val detailRequest = Reflect.onClass(detailRequestClass).create().get<Any?>()
      Reflect.on(detailRequest).set("bookId", bookId)

      // 调用详情接口
      return callFunction(rpcFacadeBClass(), detailRequest, "u0")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getMultiDetail] 获取书籍详情失败，bookId：" + bookId, t)
      return t
    }
  }

  /**
   * 获取音频播放地址
   *
   * @param bookId 书籍ID（字符串形式的数字）
   * @param itemId 章节/条目ID（字符串形式的数字）
   * @return 音频播放地址结果或异常对象
   */
  @JvmStatic
  fun getAudioPlayURL(bookId: String, itemId: String, toneQuality: Int, toneId: Long): Any? {
    // 参数验证
    if (!isValidNumericId(bookId) || !isValidNumericId(itemId)) {
      val e = IllegalArgumentException("无效的bookId或itemId")
      LogUtils.logError("DragonService", 
        "[DragonService.getAudioPlayURL] 书籍ID或条目ID无效：bookId=" + bookId + ", itemId=" + itemId, e)
      return e
    }

    try {
      // 创建音频播放请求对象
      val audioRequest = createRequestObject(AUDIO_PLAY_URL_REQUEST_CLASS)

      // 设置音频请求参数
      Reflect.on(audioRequest)
        .set("bookId", bookId.toLong())
        .set("itemId", itemId.toLong())
        .set("toneId", if (toneId > 0) toneId else 80L) // 默认80=多角色对话升级版
        .set("useServerHistory", false)
        .set("isToneInherit", true)
        .set("isLocalBook", false)

      // 设置请求类型（枚举）
      val playReqType = Reflect.onClass(AUDIO_PLAY_URL_REQ_TYPE_CLASS, DragonGlobals.getDragonClassLoader())
        .field("PLAY")
        .get<Any?>()
      Reflect.on(audioRequest).set("reqType", playReqType)

      // 设置音质（枚举）：32=LowQuality, 64=MediumQuality, 128=HighQuality
      val qualityEnum = Reflect.onClass(TONE_QUALITY_CLASS, DragonGlobals.getDragonClassLoader())
        .call("findByValue", if (toneQuality > 0) toneQuality else 128).get<Any?>()
      if (qualityEnum != null) {
        Reflect.on(audioRequest).set("toneQuality", qualityEnum)
      } else {
        val highQuality = Reflect.onClass(TONE_QUALITY_CLASS, DragonGlobals.getDragonClassLoader())
          .field("HighQuality")
          .get<Any?>()
        Reflect.on(audioRequest).set("toneQuality", highQuality)
      }

      // 调用音频地址接口
      return callFunction(rpcFacadeBClass(), audioRequest, "f")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", 
        "[DragonService.getAudioPlayURL] 获取音频播放地址失败，bookId=" + bookId + ", itemId=" + itemId, t)
      return t
    }
  }

  /**
   * 获取音频播放地址
   *
   * @param bookId 书籍ID（字符串形式的数字）
   * @param itemId 章节/条目ID（字符串形式的数字）
   * @return 音频播放地址结果或异常对象
   */
  @JvmStatic
  fun getAudioPlayURL(bookId: String, itemId: String): Any? {
    return getAudioPlayURL(bookId, itemId, 128, 0)
  }

  /**
   * 获取解密后的音频数据
   * 流程：获取音频URL -> 下载加密音频 -> AES/CBC/PKCS5Padding解密 -> 返回解密音频字节数据
   *
   * 解密算法（对应 w85.i.k）：
   *   1. Base64解码加密内容
   *   2. 取前16字节作为IV
   *   3. 取剩余字节作为密文
   *   4. AES/CBC/PKCS5Padding解密，密钥为encryptionKey（hex字符串转字节数组，填充/截断至16字节）
   *
   * @param bookId 书籍ID
   * @param itemId 章节/条目ID
   * @param toneQuality 音质（32/64/128）
   * @return 解密后的音频字节数据，或异常对象
   */
  @JvmStatic
  fun getDecryptedAudio(bookId: String, itemId: String, toneQuality: Int, toneId: Long): Any? {
    try {
      // 1. 获取音频播放URL
      val audioResponse = getAudioPlayURL(bookId, itemId, toneQuality, toneId)
      if (audioResponse is Throwable) {
        return audioResponse
      }

      // 2. 从响应中提取AudioPlayInfoData
      val data = Reflect.on(audioResponse).field("data").get<Any?>()
      if (data == null) {
        return Exception("音频播放响应data为空")
      }

      val mainUrl = Reflect.on(data).field("mainUrl").get<String?>()
      val backupUrl = Reflect.on(data).field("backupUrl").get<String?>()
      val isEncrypt = Reflect.on(data).field("isEncrypt").get<Boolean>()
      val encryptionKey = Reflect.on(data).field("encryptionKey").get<String?>()

      if (mainUrl == null || mainUrl.isEmpty()) {
        return Exception("音频播放URL为空")
      }

      // 3. 下载音频数据
      var audioData = downloadAudioData(mainUrl)
      if (audioData == null && backupUrl != null && backupUrl.isNotEmpty()) {
        audioData = downloadAudioData(backupUrl)
      }
      if (audioData == null) {
        return Exception("下载音频数据失败")
      }

      // 4. 如果不需要加密，直接返回
      if (!isEncrypt || encryptionKey == null || encryptionKey.isEmpty()) {
        return audioData
      }

      // 5. AES/CBC/PKCS5Padding解密
      val decryptedData = decryptAudioData(audioData, encryptionKey)
      if (decryptedData == null) {
        return Exception("音频解密失败")
      }

      return decryptedData
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getDecryptedAudio] 获取解密音频失败，bookId=" + bookId + ", itemId=" + itemId, t)
      return t
    }
  }

  /**
   * 下载音频数据
   * @param url 音频URL
   * @return 音频字节数据，失败返回null
   */
  private fun downloadAudioData(url: String): ByteArray? {
    try {
      val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
      connection.connectTimeout = 15000
      connection.readTimeout = 30000
      connection.requestMethod = "GET"

      val responseCode = connection.responseCode
      if (responseCode != 200) {
        LogUtils.logError("DragonService", "[DragonService.downloadAudioData] HTTP响应码: " + responseCode + ", URL: " + url, null)
        return null
      }

      connection.inputStream.use { inputStream ->
        java.io.ByteArrayOutputStream().use { outputStream ->
          val buffer = ByteArray(8192)
          var bytesRead: Int
          while (true) {
            bytesRead = inputStream.read(buffer)
            if (bytesRead == -1) break
            outputStream.write(buffer, 0, bytesRead)
          }
          return outputStream.toByteArray()
        }
      }
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.downloadAudioData] 下载音频失败, URL: " + url, t)
      return null
    }
  }

  /**
   * AES/CBC/PKCS5Padding解密音频数据
   * 对应 w85.i.k 方法的逻辑：
   *   1. Base64解码加密内容
   *   2. 前16字节作为IV
   *   3. 剩余字节作为密文
   *   4. AES/CBC/PKCS5Padding解密
   *
   * 密钥处理（对应 rk4.a.c）：
   *   - encryptionKey为hex字符串，转字节数组后填充/截断至16字节
   *
   * @param encryptedData 加密的音频数据
   * @param encryptionKey 加密密钥（hex字符串）
   * @return 解密后的字节数据，失败返回null
   */
  private fun decryptAudioData(encryptedData: ByteArray, encryptionKey: String): ByteArray? {
    try {
      // 1. Base64解码
      val decodedData = android.util.Base64.decode(encryptedData, android.util.Base64.NO_WRAP)

      // 2. 提取IV（前16字节）和密文（剩余字节）
      if (decodedData.size < 16) {
        LogUtils.logError("DragonService", "[DragonService.decryptAudioData] 解码后数据太短，长度: " + decodedData.size, null)
        return null
      }
      val iv = ByteArray(16)
      val ciphertext = ByteArray(decodedData.size - 16)
      System.arraycopy(decodedData, 0, iv, 0, 16)
      System.arraycopy(decodedData, 16, ciphertext, 0, ciphertext.size)

      // 3. 处理密钥：hex字符串转字节数组，填充/截断至16字节
      val keyBytes = hexStringToBytes(encryptionKey)
      val aesKey = ByteArray(16)
      if (keyBytes.size >= 16) {
        System.arraycopy(keyBytes, 0, aesKey, 0, 16)
      } else {
        System.arraycopy(keyBytes, 0, aesKey, 0, keyBytes.size)
        // 不足16字节补0
      }

      // 4. AES/CBC/PKCS5Padding解密
      val secretKeySpec = javax.crypto.spec.SecretKeySpec(aesKey, "AES")
      val ivParameterSpec = javax.crypto.spec.IvParameterSpec(iv)
      val cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding")
      cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
      return cipher.doFinal(ciphertext)
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.decryptAudioData] 解密失败", t)
      return null
    }
  }

  /**
   * hex字符串转字节数组
   * 对应 rk4.a.g 方法
   */
  private fun hexStringToBytes(hexString: String): ByteArray {
    if (hexString.length < 2) {
      return ByteArray(0)
    }
    val lowerHex = hexString.lowercase()
    val len = lowerHex.length / 2
    val result = ByteArray(len)
    for (i in 0 until len) {
      val index = i * 2
      result[i] = (Integer.parseInt(lowerHex.substring(index, index + 2), 16) and 0xFF).toByte()
    }
    return result
  }

  /**
   * 获取书籍目录
   *
   * @param bookId 书籍ID（字符串形式的数字）
   * @return 书籍目录结果或异常对象
   */
  @JvmStatic
  fun getCatalog(bookId: String): Any? {
    // 参数验证
    if (!isValidNumericId(bookId)) {
      val e = IllegalArgumentException("无效的bookId：" + bookId)
      LogUtils.logError("DragonService", "[DragonService.getCatalog] 书籍ID无效：" + bookId, e)
      return e
    }

    try {
      // 创建目录请求对象并设置书籍ID
      val directoryRequestClass = Class.forName(GET_DIRECTORY_REQUEST_CLASS, true, DragonGlobals.getDragonClassLoader())
      val directoryRequest = Reflect.onClass(directoryRequestClass).create().get<Any?>()
      Reflect.on(directoryRequest).set("bookId", bookId.toLong())

      // 调用目录接口
      return callFunction(rpcFacadeBClass(), directoryRequest, "K")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getCatalog] 获取书籍目录失败，bookId：" + bookId, t)
      return t
    }
  }

  /**
   * 获取书籍内容（单章节)
   * 修正：补充keyRegisterTs，集成现代解密流程
   * @param bookId 书籍ID
   * @param itemId 章节/条目ID
   * @return 解密后的书籍内容结果或异常对象
   */
  @JvmStatic
  fun getContent(bookId: String, itemId: String, novelTextType: Int): Any? {
    try {
      // 创建请求并补充关键字段
      val fullRequest = createFullRequest(bookId, itemId, novelTextType)

      // 调用接口获取原始响应（ro4.f.k 对应 FullRequest → FullResponse）
      val fullResponse = callFunction(rpcFacadeFClass(), fullRequest, "k")

      // 提取ItemContent
      val itemContent = Reflect.on(fullResponse).field("data").get<Any?>()
      if (itemContent == null) {
        throw RuntimeException("Content data is null")
      }

      // 解密流程
      val decryptedContent = decryptContent(itemContent, bookId, itemId)

      // 替换原数据
      Reflect.on(fullResponse).set("data", decryptedContent)
      return fullResponse

    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getContent] 获取内容失败，itemId：" + itemId, t)
      return t
    }
  }

  /**
   * 获取书籍内容（单章节)
   * @param bookId 书籍ID
   * @param itemId 章节/条目ID
   * @return 解密后的书籍内容结果或异常对象
   */
  @JvmStatic
  fun getContent(bookId: String, itemId: String): Any? {
    return getContent(bookId, itemId, 0)
  }

  /**
   * 批量获取书籍内容（包含解密逻辑）
   *
   * @param itemIds 章节/条目ID
   * @return 解密后的书籍内容结果或异常对象
   */
  @JvmStatic
  fun getBatchContent(bookId: String, itemIds: List<String>): Any? {
    try {
      // 修正类名：从 gn4.m0 改为 u56.a
      val batchRequestClass = Class.forName(BATCH_FULL_REQUEST_CLASS, true, DragonGlobals.getDragonClassLoader())
      val batchRequest = Reflect.onClass(batchRequestClass).create().get<Any?>()
      Reflect.on(batchRequest).set("itemIds", ListUtils.getQueryList(itemIds))

      // u56.a.h 对应 BatchFullRequest → BatchFullResponse
      val batchResponse = callFunction(CLASS_U56_A, batchRequest, "h")

      // 批量解密处理
      val data = Reflect.on(batchResponse).field("data").get<Any?>()
      if (data is Map<*, *>) {
        val mutableMap = HashMap<String, Any?>()
        for ((key, value) in data) {
          mutableMap[key as String] = value
        }
        for ((itemId, itemContent) in mutableMap) {
          try {
            val decrypted = decryptContent(itemContent!!, bookId, itemId)
            mutableMap[itemId] = decrypted
          } catch (e: Exception) {
            LogUtils.logError("DragonService", "[getBatchContent] 解密失败，itemId: " + itemId, e)
          }
        }
        Reflect.on(batchResponse).set("data", mutableMap)
      }
      return batchResponse

    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getBatchContent] 批量获取失败", t)
      return t
    }
  }

  /**
   * 获取书架信息
   *
   * @return 书架信息结果或异常对象
   */
  @JvmStatic
  fun getBookShelfInfo(): Any? {
    try {
      // 创建书架请求对象
      val shelfRequest = createRequestObject(GET_BOOK_SHELF_INFO_REQUEST_CLASS)
      // 调用书架接口
      return callFunction(rpcFacadeBClass(), shelfRequest, "z")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.getBookShelfInfo] 获取书架信息失败", t)
      return t
    }
  }

  /** 添加书架信息 */
  @JvmStatic
  fun addBookShelfInfo(bookId: String, type: String?): Any? {
    try {
      val shelfRequest = createRequestObject(ADD_BOOK_SHELF_INFO_REQUEST_CLASS)
      val bookIdList = ArrayList<String>()
      bookIdList.add(bookId)
      Reflect.on(shelfRequest).set("bookId", bookIdList)

      val addBookShelfSourceObj = Reflect.onClass(
        "com.dragon.read.rpc.model.AddBookShelfSource",
        DragonGlobals.getDragonClassLoader())
        .field("User")
        .get<Any?>()
      Reflect.on(shelfRequest).set("addBookSource", addBookShelfSourceObj)

      // 根据 type 选择阅读类型枚举
      val bookTypeField = when {
        "read" == type -> "Read"
        "listen" == type -> "Listen"
        else -> "Read"
      }
      val readingBookTypeObj = Reflect.onClass(
        "com.dragon.read.rpc.model.ReadingBookType",
        DragonGlobals.getDragonClassLoader())
        .field(bookTypeField)
        .get<Any?>()

      val bookShelfIdentifyData = createRequestObject("com.dragon.read.rpc.model.BookShelfIdentifyData")
      Reflect.on(bookShelfIdentifyData)
        .set("asterisked", false)
        .set("bookId", bookId)
        .set("bookType", readingBookTypeObj)
        .set("modifyTime", 0L)

      val identifyData = ArrayList<Any?>()
      identifyData.add(bookShelfIdentifyData)
      Reflect.on(shelfRequest).set("identifyData", identifyData)

      return callFunction(rpcFacadeBClass(), shelfRequest, "c")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.addBookShelfInfo] 添加书架信息失败", t)
      return t
    }
  }

  /**
   * 获取书城信息
   *
   * @param parameters 请求参数映射（键为字段名，值为参数值）
   * @return 书城信息结果或异常对象
   */
  @JvmStatic
  fun bookMall(parameters: Map<String, *>): Any? {
    try {
      // 创建书城请求对象并设置参数
      val mallRequest = Reflect.onClass(GET_BOOK_MALL_REQUEST_CLASS, DragonGlobals.getDragonClassLoader()).create().get<Any?>()
      setRequestParameters(mallRequest, parameters)
      // 调用书城接口
      return callFunction(rpcFacadeBClass(), mallRequest, "j")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.bookMall] 获取书城信息失败", t)
      return t
    }
  }

  /**
   * 获取新分类信息
   *
   * @param parameters 请求参数映射（键为字段名，值为参数值）
   * @return 新分类信息结果或异常对象
   */
  @JvmStatic
  fun newCategory(parameters: Map<String, *>): Any? {
    try {
      // 创建分类请求对象并设置参数
      val categoryRequestClass = Class.forName(GET_NEW_CATEGORY_REQUEST_CLASS, true, DragonGlobals.getDragonClassLoader())
      val categoryRequest = Reflect.onClass(categoryRequestClass).create().get<Any?>()
      setRequestParameters(categoryRequest, parameters)

      // 调用分类接口
      return callFunction(rpcFacadeBClass(), categoryRequest, "U")
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.newCategory] 获取新分类信息失败", t)
      return t
    }
  }

  // ============================= 辅助方法 =============================

  /**
   * 调用目标类的静态方法并阻塞获取第一个结果
   *
   * @param className 目标类名
   * @param param 方法参数
   * @param methodName 方法名
   * @return 方法返回的结果（Observable.blockingFirst()）或异常对象
   */
  private fun callFunction(className: String, param: Any?, methodName: String): Any? {
    try {
      // 调用静态方法获取Observable
      val observable = Reflect.onClass(className, DragonGlobals.getDragonClassLoader())
        .call(methodName, param)
        .get<Any?>()
      // 阻塞获取第一个结果
      return Reflect.on(observable).call("blockingFirst").get<Any?>()
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", 
        "[DragonService.callFunction] 调用目标方法失败，类：" + className + "，方法：" + methodName, t)
      return t
    }
  }

  /**
   * 创建RPC请求对象
   *
   * @param className 请求类的全限定名
   * @return 创建的请求对象
   */
  private fun createRequestObject(className: String): Any? {
    return Reflect.onClass(className, DragonGlobals.getDragonClassLoader()).create().get<Any?>()
  }

  /**
   * 为请求对象设置参数（处理列表参数取第一个值）
   *
   * @param request 请求对象
   * @param parameters 参数映射（键为字段名，值为参数值）
   */
  private fun setRequestParameters(request: Any, parameters: Map<String, *>) {
    for ((key, value) in parameters) {
      // 若参数是列表，取第一个元素作为值
      if (value is List<*>) {
        if (value.isNotEmpty()) {
          setField(request, key, value[0])
        }
      } else {
        setField(request, key, value)
      }
    }
  }

  /**
   * 根据字段类型自动转换并设置值
   *
   * @param obj 目标对象
   * @param fieldName 字段名（下划线形式，将转为驼峰）
   * @param value 要设置的值
   */
  private fun setField(obj: Any, fieldName: String, value: Any?) {
    try {
      // 下划线转驼峰（适配字段命名规范）
      val camelFieldName = FieldNameUtils.underlineToCamel(fieldName)
      // 获取字段当前值（用于判断类型）
      val fieldValue = Reflect.on(obj).get<Any?>(camelFieldName)
      val valueStr = value.toString()

      // 根据字段类型转换并设置值
      when {
        fieldValue is Short -> Reflect.on(obj).set(camelFieldName, valueStr.toShort())
        fieldValue is Int -> Reflect.on(obj).set(camelFieldName, valueStr.toInt())
        fieldValue is Long -> Reflect.on(obj).set(camelFieldName, valueStr.toLong())
        fieldValue is Float -> Reflect.on(obj).set(camelFieldName, valueStr.toFloat())
        fieldValue is Boolean -> Reflect.on(obj).set(camelFieldName, valueStr.toBoolean())
        fieldValue != null && fieldValue.javaClass.isEnum() -> {
          // 枚举类型通过findByValue方法设置
          val enumClass = fieldValue.javaClass
          val enumValue = Reflect.onClass(enumClass).call("findByValue", valueStr.toInt()).get<Any?>()
          Reflect.on(obj).set(camelFieldName, enumValue)
        }
        else -> {
          // 默认按字符串设置
          Reflect.on(obj).set(camelFieldName, valueStr)
        }
      }
    } catch (t: Throwable) {
      LogUtils.logError("DragonService", "[DragonService.setField] 设置字段失败，字段：" + fieldName + "，值：" + value, t)
    }
  }

  // ============================= 私有工具方法 =============================

  /** 获取搜索标签类型（枚举对象，默认3=Book） */
  private fun getSearchTabType(): Any? {
    val tabTypeClass = Class.forName(SEARCH_TAB_TYPE_CLASS, true, DragonGlobals.getDragonClassLoader())
    return Reflect.onClass(tabTypeClass).call("findByValue", 3).get<Any?>()
  }

  // ============================= 评论/子评论类名解析（DexKit 定位优先，兜底常量表） =============================

  private fun commentApiServiceClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_API_SERVICE.id, "com.dragon.read.saas.ugc.rpc.CommentApiService")

  private fun commentListRequestClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_LIST_REQUEST.id, "com.dragon.read.saas.ugc.model.GetCommentListRequest")

  private fun replyListRequestClass(): String =
    HookManager.getResolvedClassName(DragonTargets.REPLY_LIST_REQUEST.id, "com.dragon.read.saas.ugc.model.GetReplyListRequest")

  private fun commentGroupTypeClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_GROUP_TYPE.id, "com.dragon.read.saas.ugc.model.UgcCommentGroupTypeOutter")

  private fun commentSourceEnumClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_SOURCE_ENUM.id, "com.dragon.read.saas.ugc.model.UgcCommentSourceEnum")

  private fun commentRelativeTypeClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_RELATIVE_TYPE.id, "com.dragon.read.saas.ugc.model.UgcRelativeType")

  private fun commentChannelEnumClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_CHANNEL_ENUM.id, "com.dragon.read.saas.ugc.model.UgcCommentChannelEnum")

  private fun commentBusinessParamClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_BUSINESS_PARAM.id, "com.dragon.read.saas.ugc.model.CommentBusinessParam")

  private fun replyBusinessParamClass(): String =
    HookManager.getResolvedClassName(DragonTargets.REPLY_BUSINESS_PARAM.id, "com.dragon.read.saas.ugc.model.ReplyBusinessParam")

  private fun commentSortEnumClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_SORT_ENUM.id, "com.dragon.read.saas.ugc.model.UgcSortEnum")

  private fun commentFoldTypeClass(): String =
    HookManager.getResolvedClassName(DragonTargets.COMMENT_FOLD_TYPE.id, "com.dragon.read.saas.ugc.model.FoldType")

  /** 主 RPC 门面（搜索/详情/目录等，原 ro4.b，接入 DexKit 定位） */
  private fun rpcFacadeBClass(): String =
    HookManager.getResolvedClassName(DragonTargets.RPC_FACADE_B.id, CLASS_R63_A)

  /** 全量 RPC 门面（原 ro4.f，接入 DexKit 定位） */
  private fun rpcFacadeFClass(): String =
    HookManager.getResolvedClassName(DragonTargets.RPC_FACADE_F.id, CLASS_R63_D)

  /**
   * 验证ID是否为有效的数字字符串
   *
   * @param id 待验证的ID
   * @return true-有效，false-无效
   */
  private fun isValidNumericId(id: String?): Boolean {
    if (id == null || id.isEmpty()) {
      return false
    }
    return try {
      id.toLong()
      true
    } catch (e: NumberFormatException) {
      false
    }
  }

  /** 将原始内容对象转换为SAAS层内容对象 */
  private fun convertToSaasItemContent(itemContent: Any): Any? {
    val target = createRequestObject(SAAS_ITEM_CONTENT_CLASS)
    // 定位：
    // 包路径：com.dragon.read.util
    // 方法签名：public static final <T> T a(Object obj, Class<T> type)
    return Reflect.onClass("com.dragon.read.util.q", DragonGlobals.getDragonClassLoader())
      .call("a", itemContent, target?.javaClass) // 转换方法：(Object, Class) -> Object
      .get<Any?>()
  }

  /** 获取章节信息对象 */
  private fun getChapterInfo(saasItemContent: Any): Any? {
    return Reflect.onClass(
      "com.dragon.read.reader.download.ChapterInfo", DragonGlobals.getDragonClassLoader())
      .call("a", saasItemContent, false) // 转换方法：(SaasItemContent, boolean) -> ChapterInfo
      .get<Any?>()
  }

  // ============================= 解密流程 =============================

  /**
   * 解密流程
   * 流程：检查cryptStatus → 获取DecryptKey → 解密 → 解压
   */
  private fun decryptContent(itemContent: Any, bookId: String, chapterId: String): Any? {

    // 1. 提取关键字段
    val cryptStatus: Short = Reflect.on(itemContent).field("cryptStatus").get()
    val encryptedContent: String? = Reflect.on(itemContent).field("content").get()
    val keyVersion: Int = Reflect.on(itemContent).field("keyVersion").get()
    val compressStatus: Int = Reflect.on(itemContent).field("compressStatus").get()

    // 2. 根据cryptStatus分流处理
    return when (cryptStatus.toInt()) {
      1 -> // 明文，无需解密
        itemContent

      2 -> { // 密钥过期，需重新注册后重试（理论上应在调用层处理，此处简化）
        LogUtils.log("DragonService", "[modernDecryptContent] 密钥过期，尝试重新获取")
        // 重新获取密钥并解密（简化处理，实际应递归或重试）
        decryptWithFreshKey(itemContent, bookId, chapterId, keyVersion)
      }

      0 -> // 已加密，正常解密流程
        performDecryption(itemContent, encryptedContent, keyVersion, compressStatus, bookId, chapterId)

      else -> throw RuntimeException("未知的加密状态: " + cryptStatus)
    }
  }

  /**
   * 执行实际解密（使用w85.i和com.dragon.read.reader.utils.o）
   */
  private fun performDecryption(
    itemContent: Any, encryptedContent: String?,
    keyVersion: Int, compressStatus: Int, bookId: String, chapterId: String,
  ): Any? {

    // 1. 获取用户ID
    val userId = DragonUtils.getUserId()

    // 2. 获取DecryptKey（通过w85.i.o）
    val decryptKey: Any? = if (keyVersion == Int.MIN_VALUE) {
      // 明文章节（特殊情况）
      getPlainTextKey()
    } else {
      Reflect.onClass(CLASS_W85_I, DragonGlobals.getDragonClassLoader())
        .call("o", userId, keyVersion)
        .call("blockingGet")
        .get<Any?>()
    }
    LogUtils.log("DragonService", JsonUtils.toJson(decryptKey))

    // 3. 检查是否无需解密（notNeedDecrypt标记）
    val notNeedDecrypt: Boolean = Reflect.on(decryptKey).call("c").get()
    if (notNeedDecrypt) {
      Reflect.on(itemContent).set("content", encryptedContent)
      return itemContent
    }

    // 4. 执行解密（方式二选一）
    val decryptedText: String?

    // 方式A：通过 zm4.e (ReaderChapterService) - 推荐，自动处理压缩
    decryptedText = try {
      val chapterService = Reflect.onClass(CLASS_READER_SERVICE_API, DragonGlobals.getDragonClassLoader())
        .field("IMPL")
        .call("readerChapterService")
        .get<Any?>()

      Reflect.on(chapterService)
        .call("r", encryptedContent, decryptKey, compressStatus > 0, bookId, chapterId)
        .get<String?>()
    } catch (e: Exception) {
      // 方式B：通过 com.dragon.read.reader.utils.o.b（备用）
      LogUtils.logError("DragonService", "[performDecryption] Service方式失败，尝试Utils方式", e)
      Reflect.onClass(CLASS_DECRYPT_UTILS)
        .call("b", encryptedContent, decryptKey, compressStatus > 0, bookId, chapterId)
        .get<String?>()
    }

    // 5. 写回解密内容
    Reflect.on(itemContent).set("content", decryptedText)
    return itemContent
  }

  /**
   * 密钥过期后的重新获取与解密（处理cryptStatus=2）
   */
  private fun decryptWithFreshKey(
    itemContent: Any, bookId: String,
    chapterId: String, oldKeyVersion: Int,
  ): Any? {

    val userId = DragonUtils.getUserId()

    // 调用w85.i.F重新注册密钥
    val freshKey = Reflect.onClass(CLASS_W85_I)
      .call("F", userId) // F = 重新注册
      .call("blockingGet")
      .get<Any?>()

    LogUtils.log("DragonService", JsonUtils.toJson(freshKey))

    // 重新获取内容（理论上应重新请求Full，此处简化直接尝试解密）
    // 实际业务中应重新调用getContent，此处仅作示例
    val encryptedContent = Reflect.on(itemContent).field("content").get<String?>()
    val compressStatus = Reflect.on(itemContent).field("compressStatus").get<Int>()

    // 使用新密钥解密
    return performDecryption(itemContent, encryptedContent, oldKeyVersion, compressStatus, bookId, chapterId)
  }

  /**
   * 获取明文密钥（notNeedDecrypt=true）
   */
  private fun getPlainTextKey(): Any? {
    return Reflect.onClass("com.dragon.read.reader.DecryptKey")
      .field("f183364g")
      .call("b")
      .get<Any?>()
  }

  /**
   * 创建FullRequest并补充keyRegisterTs（关键修复）
   */
  private fun createFullRequest(bookId: String, itemId: String, novelTextType: Int): Any? {
    val fullRequestClass = Class.forName(FULL_REQUEST_CLASS, true, DragonGlobals.getDragonClassLoader())
    val request = Reflect.onClass(fullRequestClass).create().get<Any?>()

    // 设置基本字段
    Reflect.on(request)
      .set("bookId", bookId)
      .set("itemId", itemId)

    // novelTextType: 0=Normal, 1=RichText
    if (novelTextType > 0) {
      val novelTextTypeClass = Class.forName("com.dragon.read.rpc.model.NovelTextType", true, DragonGlobals.getDragonClassLoader())
      val textTypeEnum = Reflect.onClass(novelTextTypeClass).call("findByValue", novelTextType).get<Any?>()
      if (textTypeEnum != null) {
        Reflect.on(request).set("novelTextType", textTypeEnum)
      }
    }

    // 关键修复：设置keyRegisterTs（从w85.i.C获取）
    try {
      val userId = DragonUtils.getUserId()
      val keyRegisterTs: Int = Reflect.onClass(CLASS_W85_I, DragonGlobals.getDragonClassLoader())
        .call("C", userId)
        .get()
      Reflect.on(request).set("keyRegisterTs", keyRegisterTs)
    } catch (e: Exception) {
      LogUtils.logError("DragonService", "[createFullRequest] 设置keyRegisterTs失败，使用默认值0", e)
      Reflect.on(request).set("keyRegisterTs", 0)
    }

    return request
  }
}
