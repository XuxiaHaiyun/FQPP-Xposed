package com.xxhy.fqpp.xposed.web

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.utils.JsonUtils
import java.util.ArrayList

/**
 * Dragon控制器类 处理与书籍相关的各类Web请求，包括搜索、详情查询、目录获取、内容加载等功能 所有方法均为静态方法，通过调用DragonService完成业务逻辑处理
 */
object DragonController {

  /**
   * 从请求参数中获取指定key对应的第一个字符串值
   *
   * @param parameters 请求参数集合（key为参数名，value为参数值列表）
   * @param key 要获取的参数名
   * @return 参数值（若参数不存在或为空集合则返回null）
   */
  private fun getParameter(parameters: Map<String, List<String>>, key: String): String? {
    val values = parameters[key]
    if (values != null && values.isNotEmpty()) {
      return values[0]
    }
    return null
  }

  /**
   * 从请求参数中获取指定key对应的整数型值
   *
   * @param parameters 请求参数集合
   * @param key 要获取的参数名
   * @param defaultValue 参数不存在或解析失败时的默认值
   * @return 解析后的整数（若参数无效则返回默认值）
   */
  private fun getIntParameter(
    parameters: Map<String, List<String>>, key: String, defaultValue: Int,
  ): Int {
    val paramValue = getParameter(parameters, key)
    if (paramValue != null) {
      try {
        return paramValue.toInt()
      } catch (e: NumberFormatException) {
        // 解析失败时使用默认值
      }
    }
    return defaultValue
  }

  private fun getBoolParameter(
    parameters: Map<String, List<String>>, key: String, defaultValue: Boolean,
  ): Boolean {
    val paramValue = getParameter(parameters, key)
    if (paramValue != null) {
      return paramValue.toBoolean() || "1" == paramValue
    }
    return defaultValue
  }

  private fun getLongParameter(
    parameters: Map<String, List<String>>, key: String, defaultValue: Long,
  ): Long {
    val paramValue = getParameter(parameters, key)
    if (paramValue != null) {
      try {
        return paramValue.toLong()
      } catch (e: NumberFormatException) {
        // 解析失败时使用默认值
      }
    }
    return defaultValue
  }

  /**
   * 检查参数是否为空，为空则返回异常信息
   *
   * @param paramValue 参数值
   * @param paramName 参数名
   * @return 若为空则返回异常对象，否则返回null
   */
  private fun checkParamEmpty(paramValue: String?, paramName: String): Exception? {
    if (paramValue == null || paramValue.isEmpty()) {
      return Exception("参数 $paramName 不能为空")
    }
    return null
  }

  /**
   * 处理书籍搜索请求
   *
   * @param parameters 请求参数，包含：
   *   - query：搜索关键词（必填）
   *   - page：页码（可选，默认1）
   *   - count：每页数量（可选，默认10）
   *   - tab_type：搜索标签类型（可选，默认3=Book；1=Common, 2=Voice, 3=Book, 4=Topic, 5=Content, 8=Comic, 18=ShortStory）
   * @return 搜索结果的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun search(parameters: Map<String, List<String>>): Any? {
    // 获取搜索关键词并验证
    val keyword = getParameter(parameters, "query")
    val paramError = checkParamEmpty(keyword, "query")
    if (paramError != null) {
      return paramError
    }

    // 获取页码（默认第1页）
    val page = getIntParameter(parameters, "page", 1)
    val count = getIntParameter(parameters, "count", 10)
    val tabType = getIntParameter(parameters, "tab_type", 3)

    // 调用服务层执行搜索并返回JSON结果
    val result = JsonUtils.toJson(DragonService.search(keyword!!, page, count, tabType))
    // 将目标标签重排到 searchTabs 首位，保证书源 $.searchTabs[0].data 能取到书籍列表
    return reorderSearchTabs(result, tabType)
  }

  /** 搜索标签类型枚举值与响应中 tabType 名称的映射（1=Common, 2=Voice, 3=Book, 4=Topic, 5=Content, 8=Comic, 18=ShortStory） */
  private val SEARCH_TAB_NAMES = mapOf(
    1 to "Common",
    2 to "Voice",
    3 to "Book",
    4 to "Topic",
    5 to "Content",
    8 to "Comic",
    18 to "ShortStory",
  )

  /**
   * 将搜索响应中的目标标签（tabType）重排到 searchTabs 首位。
   *
   * 番茄小说聚合搜索响应中 searchTabs[0] 通常是空的"综合"标签，真实的书籍列表
   * 位于 Book 标签（tabType=3）中。书源规则依赖 $.searchTabs[0].data 获取列表，
   * 若目标标签不在首位则书源解析结果恒为空。因此需要把目标标签提到首位。
   *
   * @param json 搜索响应JSON字符串
   * @param tabType 请求的目标标签类型（默认3=Book）
   * @return 重排后的JSON字符串；解析失败或无需重排时返回原字符串
   */
  private fun reorderSearchTabs(json: String, tabType: Int): String {
    return try {
      val element = JsonParser.parseString(json)
      if (!element.isJsonObject) return json
      val root = element.asJsonObject
      val tabs = root.getAsJsonArray("searchTabs") ?: return json
      if (tabs.size() <= 1) return json
      val tabName = SEARCH_TAB_NAMES[tabType] ?: return json
      var targetIdx = -1
      for (i in 0 until tabs.size()) {
        val tab = tabs[i].asJsonObject
        val name = tab.get("tabType")?.takeIf { it.isJsonPrimitive }?.asString ?: continue
        if (name == tabName) {
          targetIdx = i
          break
        }
      }
      if (targetIdx <= 0) return json
      val target = tabs.remove(targetIdx)
      val newTabs = JsonArray()
      newTabs.add(target)
      for (i in 0 until tabs.size()) {
        newTabs.add(tabs[i])
      }
      root.add("searchTabs", newTabs)
      root.addProperty("selectedTabIdx", 0)
      root.toString()
    } catch (t: Throwable) {
      json
    }
  }

  /**
   * 处理书籍详情请求
   *
   * @param parameters 请求参数，包含：
   *   - book_id：书籍ID（必填）
   *   - source：详情来源（可选，默认0=Default；1=Cover, 2=NewVideoTab, 4=VideoDetail, 6=AudioPlayer）
   *   - without_video：是否不包含视频信息（可选，默认false）
   * @return 书籍详情的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun detail(parameters: Map<String, List<String>>): Any? {
    // 获取书籍ID并验证
    val bookId = getParameter(parameters, "book_id")
    val paramError = checkParamEmpty(bookId, "book_id")
    if (paramError != null) {
      return paramError
    }

    val source = getIntParameter(parameters, "source", 0)
    val withoutVideo = getBoolParameter(parameters, "without_video", false)

    // 调用服务层获取详情并返回JSON结果
    return JsonUtils.toJson(DragonService.getDetail(bookId!!, source, withoutVideo))
  }

  /**
   * 处理多个书籍详情请求
   *
   * @param parameters 请求参数，包含： - book_id：书籍ID（必填）
   * @return 书籍详情的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun multiDetail(parameters: Map<String, List<String>>): Any? {
    // 获取书籍ID并验证
    val bookId = getParameter(parameters, "book_id")
    val paramError = checkParamEmpty(bookId, "book_id")
    if (paramError != null) {
      return paramError
    }

    // 调用服务层获取详情并返回JSON结果
    return JsonUtils.toJson(DragonService.getMultiDetail(bookId!!))
  }

  /**
   * 处理书籍目录请求
   *
   * @param parameters 请求参数，包含： - book_id：书籍ID（必填）
   * @return 书籍目录的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun catalog(parameters: Map<String, List<String>>): Any? {
    val bookId = getParameter(parameters, "book_id")
    val paramError = checkParamEmpty(bookId, "book_id")
    if (paramError != null) {
      return paramError
    }

    return JsonUtils.toJson(DragonService.getCatalog(bookId!!))
  }

  /**
   * 处理书籍内容请求
   *
   * @param parameters 请求参数，包含：
   *   - item_id：章节ID（必填）
   *   - book_id：书籍ID（可选）
   *   - novel_text_type：文本类型（可选，默认0=Normal；1=RichText）
   * @return 章节内容的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun content(parameters: Map<String, List<String>>): Any? {
    val bookId = getParameter(parameters, "book_id")
    val itemId = getParameter(parameters, "item_id")
    val paramError = checkParamEmpty(itemId, "item_id")
    if (paramError != null) {
      return paramError
    }

    val novelTextType = getIntParameter(parameters, "novel_text_type", 0)

    val content = DragonService.getContent(bookId!!, itemId!!, novelTextType)
    return JsonUtils.toJson(content)
  }

  /**
   * 添加书架信息添加书架信息
   *
   * @param parameters 请求参数，包含： - book_id：书籍ID（必填）
   * @return 章节内容的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun addBookToShelf(parameters: Map<String, List<String>>): Any? {
    val bookId = getParameter(parameters, "book_id")
    val type = getParameter(parameters, "type")
    val paramError = checkParamEmpty(bookId, "book_id")
    if (paramError != null) {
      return paramError
    }

    val content = DragonService.addBookShelfInfo(bookId!!, type)
    return JsonUtils.toJson(content)
  }

  /**
   * 批量处理书籍内容请求
   *
   * @param parameters 请求参数，包含：
   *                   - book_id：书籍ID（必填）
   *                   - item_ids：章节ID列表（必填）
   * @return 章节内容的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun batchContent(parameters: Map<String, List<String>>): Any? {
    // 验证 book_id 参数
    val bookIdList = parameters["book_id"]
    if (bookIdList == null || bookIdList.isEmpty()
      || bookIdList[0] == null || bookIdList[0].trim().isEmpty()
    ) {
      return Exception("参数 book_id 不能为空")
    }
    val bookId = bookIdList[0].trim()

    // 验证 item_ids 参数
    val itemIdList = parameters["item_ids"]
    if (itemIdList == null || itemIdList.isEmpty()) {
      return Exception("参数 item_ids 不能为空")
    }

    val itemIds = ArrayList<String>()
    for (itemId in itemIdList) {
      // 跳过空的 item_id 字符串
      if (itemId == null || itemId.trim().isEmpty()) {
        continue
      }
      itemIds.add(itemId)
    }

    if (itemIds.isEmpty()) {
      return Exception("参数 item_ids 中没有有效的章节ID")
    }

    val content = DragonService.getBatchContent(bookId, itemIds)
    return JsonUtils.toJson(content)
  }

  /**
   * 处理有声书播放地址请求
   *
   * @param parameters 请求参数，包含：
   *   - book_id：书籍ID（必填）
   *   - item_id：章节ID（必填）
   *   - tone_quality：音质（可选，默认128=HighQuality；32=LowQuality, 64=MediumQuality, 128=HighQuality）
   *   - tone_id：音色ID（可选，默认0使用默认80=多角色对话升级版）
   * @return 有声书播放地址的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun audioPlayURL(parameters: Map<String, List<String>>): Any? {
    // 验证书籍ID
    val bookId = getParameter(parameters, "book_id")
    var paramError = checkParamEmpty(bookId, "book_id")
    if (paramError != null) {
      return paramError
    }

    // 验证章节ID
    val itemId = getParameter(parameters, "item_id")
    paramError = checkParamEmpty(itemId, "item_id")
    if (paramError != null) {
      return paramError
    }

    val toneQuality = getIntParameter(parameters, "tone_quality", 128)
    val toneId = getLongParameter(parameters, "tone_id", 0)

    return JsonUtils.toJson(DragonService.getAudioPlayURL(bookId!!, itemId!!, toneQuality, toneId))
  }

  /**
   * 处理音频解密请求
   * 获取加密音频URL，下载并解密，返回解密后的音频二进制数据
   *
   * @param parameters 请求参数，包含：
   *   - book_id：书籍ID（必填）
   *   - item_id：章节ID（必填）
   *   - tone_quality：音质（可选，默认128=HighQuality；32=LowQuality, 64=MediumQuality, 128=HighQuality）
   *   - tone_id：音色ID（可选，默认0使用默认80=多角色对话升级版）
   * @return 解密后的音频字节数组（byte[]），或异常对象
   */
  @JvmStatic
  fun audioDecrypt(parameters: Map<String, List<String>>): Any? {
    // 验证书籍ID
    val bookId = getParameter(parameters, "book_id")
    var paramError = checkParamEmpty(bookId, "book_id")
    if (paramError != null) {
      return paramError
    }

    // 验证章节ID
    val itemId = getParameter(parameters, "item_id")
    paramError = checkParamEmpty(itemId, "item_id")
    if (paramError != null) {
      return paramError
    }

    val toneQuality = getIntParameter(parameters, "tone_quality", 128)
    val toneId = getLongParameter(parameters, "tone_id", 0)

    return DragonService.getDecryptedAudio(bookId!!, itemId!!, toneQuality, toneId)
  }

  /**
   * 处理书架信息请求
   *
   * @param parameters 无实际参数（预留）
   * @return 书架信息的JSON字符串
   */
  @JvmStatic
  fun bookshelfInfo(parameters: Map<String, List<String>>): Any? {
    val result = DragonService.getBookShelfInfo()
    return JsonUtils.toJson(result)
  }

  /**
   * 处理书城信息请求
   *
   * @param parameters 书城请求参数（具体参数由服务层定义）
   * @return 书城信息的JSON字符串
   */
  @JvmStatic
  fun bookMall(parameters: Map<String, List<String>>): Any? {
    return JsonUtils.toJson(DragonService.bookMall(parameters))
  }

  /**
   * 处理新分类信息请求
   *
   * @param parameters 分类请求参数（具体参数由服务层定义）
   * @return 分类信息的JSON字符串
   */
  @JvmStatic
  fun newCategory(parameters: Map<String, List<String>>): Any? {
    return JsonUtils.toJson(DragonService.newCategory(parameters))
  }

  /**
   * 处理评论列表请求（支持段评/章评/书评）
   *
   * @param parameters 请求参数，包含：
   *   - item_id：章节ID或书籍ID（必填）
   *   - book_id：书籍ID（段评/章评必填）
   *   - comment_type：评论类型（可选，默认1=段评；0=章评, 1=段评, 2=书评）
   *   - para_index：段落索引（段评时可选，默认0）
   *   - item_version：章节版本号（可选，不传则自动获取）
   *   - count：请求数量（可选，默认20）
   *   - cursor：分页游标（可选）
   *   - sort：排序方式（可选，默认1=SmartHot；2=TimeAsc, 3=TimeDesc）
   *   - fold_type：折叠类型（可选，默认0=Normal；1=Unfold, 2=Fold）
   *   - tag：话题标签（可选，书评时可用）
   *   - need_count：是否需要计数（可选，默认true）
   * @return 评论列表的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun commentList(parameters: Map<String, List<String>>): Any? {
    val itemId = getParameter(parameters, "item_id")
    val paramError = checkParamEmpty(itemId, "item_id")
    if (paramError != null) {
      return paramError
    }

    val bookId = getParameter(parameters, "book_id")
    val commentType = getIntParameter(parameters, "comment_type", 1) // 默认段评
    val paraIndex = getIntParameter(parameters, "para_index", 0)
    val itemVersion = getParameter(parameters, "item_version")
    val count = getIntParameter(parameters, "count", 20)
    val cursor = getParameter(parameters, "cursor")
    val sort = getIntParameter(parameters, "sort", 1)       // 默认SmartHot
    val foldType = getIntParameter(parameters, "fold_type", 0) // 默认Normal
    val tag = getParameter(parameters, "tag")
    val needCount = getBoolParameter(parameters, "need_count", true)

    val result = DragonService.getCommentList(
      itemId!!, bookId, commentType, paraIndex,
      itemVersion, count, cursor, sort, foldType, tag, needCount,
    )
    return JsonUtils.toJson(result)
  }

  /**
   * 处理评论气泡请求
   *
   * @param parameters 请求参数，包含：
   *   - item_id：章节ID（必填）
   *   - book_id：书籍ID（推荐传入，用于自动获取itemVersion）
   *   - item_version：章节版本号（可选，不传则通过book_id自动获取）
   * @return 评论气泡数据的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun commentBubble(parameters: Map<String, List<String>>): Any? {
    val itemId = getParameter(parameters, "item_id")
    val paramError = checkParamEmpty(itemId, "item_id")
    if (paramError != null) {
      return paramError
    }

    val bookId = getParameter(parameters, "book_id")
    val itemVersion = getParameter(parameters, "item_version")

    val result = DragonService.getCommentBubble(itemId!!, bookId, itemVersion)
    return JsonUtils.toJson(result)
  }

  /**
   * 处理子评论（回复）列表请求
   *
   * @param parameters 请求参数，包含：
   *   - comment_id：父评论ID（必填）
   *   - group_id：分组ID（章节ID/书籍ID，可选）
   *   - book_id：书籍ID（可选）
   *   - comment_type：评论类型（0=章评, 1=段评, 2=书评，默认0）
   *   - count：请求数量（默认20）
   *   - cursor：分页游标（可选）
   * @return 子评论列表数据的JSON字符串；若参数无效则返回异常对象
   */
  @JvmStatic
  fun replyList(parameters: Map<String, List<String>>): Any? {
    val commentId = getParameter(parameters, "comment_id")
    val paramError = checkParamEmpty(commentId, "comment_id")
    if (paramError != null) {
      return paramError
    }

    val groupId = getParameter(parameters, "group_id")
    val bookId = getParameter(parameters, "book_id")
    val commentType = getIntParameter(parameters, "comment_type", 0)
    val count = getIntParameter(parameters, "count", 20)
    val cursor = getParameter(parameters, "cursor")

    val result = DragonService.getReplyList(commentId!!, groupId, bookId, commentType, count, cursor)
    return JsonUtils.toJson(result)
  }

  /**
   * 处理书籍源导入配置请求 生成包含当前服务端口的书籍源配置JSON（用于客户端导入）
   *
   * @param parameters 无实际参数（预留）
   * @return 替换端口后的书籍源配置JSON
   */
  @JvmStatic
  fun bookSource(parameters: Map<String, List<String>>): Any? {
    // 书籍源配置模板（包含端口占位符）
    val json = "[\n  {\n    \"bookSourceGroup\": \"番茄\",\n    \"bookSourceName\": \"FQ Helper Simple\",\n    \"bookSourceType\": 0,\n    \"bookSourceUrl\": \"http://localhost:###port####FQHelperSimple\",\n    \"bookUrlPattern\": \"https?://.*(fqnovel|snssdk|fanqienovel|changdunovel)\\\\.com/.*[0-9]{19}.*\",\n    \"customOrder\": 0,\n    \"enabled\": true,\n    \"enabledCookieJar\": true,\n    \"enabledExplore\": true,\n    \"exploreUrl\": \"书架::/reading/bookapi/bookshelf/info\",\n    \"jsLib\": \"// 封面处理函数\\nlet replaceCover = (u) => {\\n    if (!u) return \'\';\\n    if (u.startsWith(\\\"https://\\\")) u = u.substring(8);\\n    else if (u.startsWith(\\\"http://\\\")) u = u.substring(7);\\n    let uArr = u.split(\\\"/\\\");\\n    uArr[0] = \\\"https://p6-novel.byteimg.com/origin\\\";\\n    let uArr2 = [];\\n    uArr.forEach((x) => {\\n        if (!x.includes(\\\"?\\\") && !x.includes(\\\"~\\\")) uArr2.push(x);\\n        else uArr2.push(x.split(\\\"~\\\")[0]);\\n    });\\n    return uArr2.join(\\\"/\\\");\\n};\",\n    \"lastUpdateTime\": 1772188499031,\n    \"loginUrl\": \"https://fanqienovel.com\",\n    \"respondTime\": 180000,\n    \"ruleBookInfo\": {\n      \"author\": \"\$.author\",\n      \"coverUrl\": \"\$.thumbUrl\\n@js:replaceCover(result)\",\n      \"init\": \"data\",\n      \"intro\": \"&nbsp;&nbsp;\\n原名：{{\$.originalBookName}}\\n别名：{{\$.aliasName}}\\n评分：{{\$.score}}\\n来源：{{\$.source}}\\n开坑：{{\$.createTime##T|\\\\+.*## }}\\n标签：{{\$.tags}}\\n主角：{{\$.role##\\\\[|\\\\\\\"|\\\\]}}\\n在线：{{\$.readCount}}人在读\\n简介：{{\$.bookAbstract}}\\n版权：{{\$.copyrightInfo##，.*##。}}\\n@js:result.replace(/.+：(人在读)?\\\\n/g,\\\"\\\")\",\n      \"kind\": \"\$.category\",\n      \"lastChapter\": \"\$.lastChapterTitle\",\n      \"name\": \"\$.bookName\",\n      \"tocUrl\": \"/reading/bookapi/directory/all_items?book_id={{\$.bookId}}\",\n      \"wordCount\": \"\$.wordNumber\"\n    },\n    \"ruleContent\": {\n      \"content\": \"@js:\\nlet content = JSON.parse(result).data.content;\\ncontent.replace(/<[?!][^>]+>/g,\\\"\\\").replace(/<style>.*<\\\\/style>/gs, \\\"\\\");\"\n    },\n    \"ruleExplore\": {\n      \"author\": \"author\",\n      \"bookList\": \"@js:\\n(function() {\\n    var startTime = Date.now();\\n    var logLevel = \\\"INFO\\\"; // 可配置: DEBUG, INFO, WARN, ERROR\\n    \\n    function log(level, icon, msg) {\\n        var levels = { DEBUG: 0, INFO: 1, WARN: 2, ERROR: 3 };\\n        if (levels[level] >= levels[logLevel]) {\\n            java.log(icon + \\\" \\\" + msg);\\n        }\\n    }\\n    \\n    try {\\n        // ========== 阶段1: 解析书架数据 ==========\\n        log(\\\"INFO\\\", \\\"🚀\\\", \\\"[1/6] 开始解析书架数据...\\\");\\n        var data = JSON.parse(result);\\n        var shelf = data.data && data.data.bookShelfInfo;\\n        \\n        if (!shelf || !(shelf instanceof Array) || shelf.length === 0) {\\n            log(\\\"ERROR\\\", \\\"❌\\\", \\\"书架为空或格式错误\\\");\\n            return [];\\n        }\\n        log(\\\"INFO\\\", \\\"✅\\\", \\\"[1/6] 书架解析成功 | 共 \\\" + shelf.length + \\\" 本书\\\");\\n        \\n        // ========== 阶段2: 提取ID ==========\\n        log(\\\"INFO\\\", \\\"🔄\\\", \\\"[2/6] 提取书籍ID...\\\");\\n        var ids = [];\\n        for (var i = 0; i < shelf.length; i++) {\\n            if (shelf[i].bookId) ids.push(shelf[i].bookId);\\n        }\\n        log(\\\"INFO\\\", \\\"✅\\\", \\\"[2/6] ID提取完成 | 有效ID: \\\" + ids.length + \\\"/\\\" + shelf.length);\\n        \\n        // ========== 阶段3: 分批获取详情 ==========\\n        var batchSize = 200;\\n        var totalBatch = Math.ceil(ids.length / batchSize);\\n        log(\\\"INFO\\\", \\\"🔄\\\", \\\"[3/6] 分批获取详情 | 共\\\" + totalBatch + \\\"批，每批≤\\\" + batchSize + \\\"本\\\");\\n        \\n        var details = {};\\n        var successBatches = 0;\\n        var failBatches = 0;\\n        \\n        for (var start = 0; start < ids.length; start += batchSize) {\\n            var batch = ids.slice(start, start + batchSize);\\n            var batchNum = Math.floor(start / batchSize) + 1;\\n            var progress = \\\"[\\\" + batchNum + \\\"/\\\" + totalBatch + \\\"]\\\";\\n            \\n            log(\\\"DEBUG\\\", \\\"🔄\\\", progress + \\\" 正在请求 \\\" + batch.length + \\\" 本...\\\");\\n            \\n            try {\\n                var url = \\\"http://localhost:###port###/reading/bookapi/multi-detail?book_id=\\\" + batch.join(\\\",\\\");\\n                var res = java.ajax(url);\\n                \\n                if (!res) {\\n                    log(\\\"WARN\\\", \\\"⚠️\\\", progress + \\\" 返回空数据\\\");\\n                    failBatches++;\\n                    continue;\\n                }\\n                \\n                var detailData;\\n                try {\\n                    detailData = JSON.parse(res.trim());\\n                } catch (parseErr) {\\n                    log(\\\"ERROR\\\", \\\"❌\\\", progress + \\\" JSON解析失败: \\\" + parseErr.message);\\n                    failBatches++;\\n                    continue;\\n                }\\n                \\n                if (detailData.code !== \\\"SUCCESS\\\") {\\n                    log(\\\"ERROR\\\", \\\"❌\\\", progress + \\\" 接口错误: \\\" + detailData.message);\\n                    failBatches++;\\n                    continue;\\n                }\\n                \\n                if (detailData.data && detailData.data instanceof Array) {\\n                    var batchSuccess = 0;\\n                    for (var j = 0; j < detailData.data.length; j++) {\\n                        var item = detailData.data[j];\\n                        if (item && item.bookId) {\\n                            details[item.bookId] = item;\\n                            batchSuccess++;\\n                        }\\n                    }\\n                    log(\\\"INFO\\\", \\\"✅\\\", progress + \\\" 成功获取 \\\" + batchSuccess + \\\"/\\\" + batch.length + \\\" 本\\\");\\n                    successBatches++;\\n                } else {\\n                    log(\\\"WARN\\\", \\\"⚠️\\\", progress + \\\" 数据格式异常\\\");\\n                    failBatches++;\\n                }\\n            } catch (e) {\\n                log(\\\"ERROR\\\", \\\"❌\\\", progress + \\\" 请求异常: \\\" + e.message);\\n                failBatches++;\\n            }\\n        }\\n        \\n        var detailKeys = Object.keys(details);\\n        log(\\\"INFO\\\", \\\"📊\\\", \\\"[4/6] 详情获取统计 | 成功: \\\" + detailKeys.length + \\\"/\\\" + ids.length + \\n            \\\" | 批次: \\\" + successBatches + \\\"成功/\\\" + failBatches + \\\"失败\\\");\\n        \\n        // ========== 阶段4: 组装数据 ==========\\n        log(\\\"INFO\\\", \\\"🔄\\\", \\\"[5/6] 组装书籍数据...\\\");\\n        var books = [];\\n        var missingDetails = 0;\\n        \\n        for (var i = 0; i < shelf.length; i++) {\\n            var s = shelf[i];\\n            if (!s.bookId) continue;\\n            \\n            var d = details[s.bookId] || {};\\n            if (!details[s.bookId]) missingDetails++;\\n            \\n            // 字数格式化\\n            var wordNum = d.wordNumber || s.wordNumber || 0;\\n            var wordCount = \\\"\\\";\\n            if (wordNum) {\\n                var num = parseInt(wordNum);\\n                wordCount = num >= 10000 ? (num / 10000).toFixed(1) + \\\"万字\\\" : num + \\\"字\\\";\\n            }\\n            \\n            // 连载状态\\n            var status = \\\"\\\";\\n            var cs = d.creationStatus || s.creationStatus;\\n            if (cs !== undefined && cs !== null) {\\n                status = cs.toString() === \\\"0\\\" ? \\\"连载中\\\" : cs.toString() === \\\"1\\\" ? \\\"已完结\\\" : \\\"未知\\\";\\n            }\\n            \\n            // 封面URL补全\\n            var cover = replaceCover(d.thumbUrl) || replaceCover(s.thumbUrl) || \\\"\\\";\\n            \\n            books.push({\\n                name: d.bookName || s.bookName || \\\"未知书名\\\",\\n                author: d.author || s.author || \\\"未知作者\\\",\\n                coverUrl: cover,\\n                intro: d.bookAbstract || s.bookAbstract || \\\"\\\",\\n                kind: d.category || s.category || \\\"\\\",\\n                lastChapterTitle: d.lastChapterTitle || s.lastChapterTitle || \\\"\\\",\\n                wordCount: wordCount,\\n                status: status,\\n                bookUrl: \\\"/reading/bookapi/detail?book_id=\\\" + s.bookId\\n            });\\n        }\\n        \\n        log(\\\"INFO\\\", \\\"✅\\\", \\\"[5/6] 组装完成 | 成功: \\\" + books.length + \\\"本 | 缺失详情: \\\" + missingDetails + \\\"本\\\");\\n        \\n        // ========== 阶段5: 调试信息 ==========\\n        log(\\\"INFO\\\", \\\"🔍\\\", \\\"[6/6] 数据详情\\\");\\n        \\n        if (books.length > 0) {\\n            var first = books[0];\\n            log(\\\"DEBUG\\\", \\\"📖\\\", \\\"首本书籍信息:\\\");\\n            log(\\\"DEBUG\\\", \\\"   \\\", \\\"  书名: \\\" + first.name);\\n            log(\\\"DEBUG\\\", \\\"   \\\", \\\"  作者: \\\" + first.author);\\n            log(\\\"DEBUG\\\", \\\"   \\\", \\\"  状态: \\\" + first.status);\\n            log(\\\"DEBUG\\\", \\\"   \\\", \\\"  字数: \\\" + first.wordCount);\\n            log(\\\"DEBUG\\\", \\\"   \\\", \\\"  分类: \\\" + first.kind);\\n            log(\\\"DEBUG\\\", \\\"   \\\", \\\"  URL:  \\\" + first.bookUrl.substring(0, 40) + \\\"...\\\");\\n            \\n            // 字段统计\\n            var fields = Object.keys(first);\\n            log(\\\"DEBUG\\\", \\\"📋\\\", \\\"字段列表(\\\" + fields.length + \\\"个): \\\" + fields.join(\\\" | \\\"));\\n            \\n            // 数据样例（截断）\\n            var sample = JSON.stringify(first);\\n            var maxLen = 200;\\n            var display = sample.length > maxLen ? sample.substring(0, maxLen) + \\\"... (共\\\" + sample.length + \\\"字符)\\\" : sample;\\n            log(\\\"DEBUG\\\", \\\"📝\\\", \\\"数据样例: \\\" + display);\\n        } else {\\n            log(\\\"WARN\\\", \\\"⚠️\\\", \\\"警告: 书籍列表为空\\\");\\n        }\\n        \\n        // 最终统计\\n        var duration = Date.now() - startTime;\\n        log(\\\"INFO\\\", \\\"🎉\\\", \\\"处理完成 | 总耗时: \\\" + duration + \\\"ms | 返回: \\\" + books.length + \\\"本\\\");\\n        \\n        return books;\\n        \\n    } catch (e) {\\n        log(\\\"ERROR\\\", \\\"❌\\\", \\\"严重错误: \\\" + e.message);\\n        log(\\\"DEBUG\\\", \\\"🔍\\\", \\\"错误堆栈: \\\" + (e.stack || \\\"无\\\"));\\n        return [];\\n    }\\n})();\\n\",\n      \"bookUrl\": \"bookUrl\",\n      \"coverUrl\": \"coverUrl\",\n      \"intro\": \"intro\",\n      \"kind\": \"kind\",\n      \"lastChapter\": \"lastChapterTitle\",\n      \"name\": \"name\",\n      \"wordCount\": \"wordCount\"\n    },\n    \"ruleSearch\": {\n      \"author\": \"\$.bookData[0].author\",\n      \"bookList\": \"@js:(function(){var tabs=JSON.parse(result).searchTabs;for(var i=0;i<tabs.length;i++){if(tabs[i].tabType==='Book'){return tabs[i].data||[];}}return [];})()\",\n      \"bookUrl\": \"/reading/bookapi/detail?book_id={{\$.bookData[0].bookId}}\",\n      \"checkKeyWord\": \"SUCCESS\",\n      \"coverUrl\": \"\$.bookData[0].thumbUrl\\n@js:replaceCover(result)\",\n      \"intro\": \"\$.bookData[0].bookAbstract\",\n      \"kind\": \"\$.bookData[0].category\",\n      \"lastChapter\": \"\$.bookData[0].lastChapterTitle\",\n      \"name\": \"\$.bookData[0].bookName\",\n      \"wordCount\": \"\$.bookData[0].wordNumber\"\n    },\n    \"ruleToc\": {\n      \"chapterList\": \"\$.data.itemDataList || \$.data.catalogData\",\n      \"chapterName\": \"\$.title || \$.catalogTitle\",\n      \"chapterUrl\": \"@js:\\n(function() {\\n    let bookUrl = book.bookUrl || \'\';\\n    let bookId = \'\';\\n    let match = bookUrl.match(/book_id=(\\\\d+)/);\\n    if (match) {\\n        bookId = match[1];\\n    } else {\\n        bookId = book.durChapterPage || \'\';\\n    }\\n    let itemId = result.itemId || result.id || \'\';\\n    if (!bookId || !itemId) return \'\';\\n    return `/reading/reader/full?book_id=\${bookId}&item_id=\${itemId}`;\\n})();\",\n      \"isPay\": \"\$.needUnlock\",\n      \"isVip\": \"\$.showVipTag\",\n      \"preUpdateJs\": \"java.refreshTocUrl();\"\n    },\n    \"searchUrl\": \"/reading/bookapi/search/tab?query={{key}}&page={{page}}\",\n    \"weight\": 0\n  }\n]"
    // 从配置中获取服务端口，替换模板中的占位符
    val sp = ConfigManager.prefs()
    val portStr = sp.getString(SPConstants.PORT, SPConstants.DEFAULT_PORT)
        ?: SPConstants.DEFAULT_PORT
    return json.replace("###port###", portStr)
  }
}
