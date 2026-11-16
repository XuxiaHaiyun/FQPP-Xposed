# 小说内容与段评接口 itemVersion 对比分析

## 概述

本文档分析小说内容接口和段评接口中 `itemVersion`（或 `version`）字段的区别、来源和使用场景。

## 核心结论

**小说内容和段评接口的 itemVersion 是同一个概念，来源相同，用途相同。**

两者都表示**章节内容的版本号**，用于：
1. 确保段评与章节内容的一致性
2. 当章节内容更新时，旧版本的段评可能失效
3. 在请求段评列表时携带此版本号，服务端可以据此返回正确的段评数据

## 数据来源对比

### 1. 小说内容接口中的 version

**来源链路**：
```
API响应 → ItemContent.novelData.version → ChapterInfo.version
```

**关键类**：

#### ApiItemInfo（小说章节信息）

**位置**: `readersaas.com.dragon.read.saas.rpc.model.ApiItemInfo`

```java
public class ApiItemInfo implements Serializable {
    @SerializedName("item_id")
    public String itemId;              // 章节ID

    public String version;             // 章节版本号（无SerializedName注解）

    @SerializedName("content_md5")
    public String contentMd5;          // 内容MD5

    @SerializedName("book_id")
    public String bookId;              // 书籍ID
}
```

#### ChapterInfo（章节信息）

**位置**: `com.dragon.read.reader.download.ChapterInfo`

```java
public class ChapterInfo {
    @SerializedName("chapter_version")
    public String version;             // 章节版本号（来自 ApiItemInfo.version）

    public static ChapterInfo a(ItemContent itemContent, boolean z17) {
        // ...
        chapterInfo.version = apiItemInfo3.version;  // 从 novelData.version 获取
        // ...
    }
}
```

### 2. 段评接口中的 itemVersion

**来源链路**：
```
阅读器 → v1.b(client, chapterId) → ParaTextBlock.chapterVersion → CommentBusinessParam.itemVersion
```

**关键类**：

#### ParaTextBlock（段评文本块）

**位置**: `com.dragon.read.social.model.ParaTextBlock`

```java
public class ParaTextBlock extends TargetTextBlock {
    private final String bookId;
    private final String chapterId;
    private final String chapterVersion;    // 章节版本号

    public final String getChapterVersion() {
        return this.chapterVersion;
    }
}
```

#### CommentBusinessParam（评论业务参数）

**位置**: `com.dragon.read.saas.ugc.model.CommentBusinessParam`

```java
public class CommentBusinessParam implements Serializable {
    @SerializedName("item_id")
    public String itemID;              // 章节ID

    @SerializedName("item_version")
    public String itemVersion;         // 章节版本号（用于段评请求）
}
```

#### CommentExpand（评论扩展信息）

**位置**: `com.dragon.read.saas.ugc.model.CommentExpand`

```java
public class CommentExpand implements Serializable {
    @SerializedName("item_version")
    public String itemVersion;         // 章节版本号（来自段评数据）
}
```

## 字段名称对比

| 接口类型 | 类名 | 字段名 | SerializedName | 说明 |
|---------|------|--------|----------------|------|
| 小说内容 | ApiItemInfo | version | 无注解 | 服务端返回的章节版本 |
| 小说内容 | ChapterInfo | version | "chapter_version" | 本地存储的章节版本 |
| 段评请求 | CommentBusinessParam | itemVersion | "item_version" | 段评请求携带的版本 |
| 段评响应 | CommentExpand | itemVersion | "item_version" | 段评数据中的版本 |
| 段评本地 | ParaTextBlock | chapterVersion | 无 | 本地创建段评时的版本 |

## 数据流对比图

### 小说内容获取流程

```
┌─────────────────────────────────────────────────────────────────┐
│ 服务端                                                           │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ ItemContent                                              │    │
│  │ - novelData.version = "123456"                          │    │
│  └──────────────────────┬──────────────────────────────────┘    │
└─────────────────────────┼────────────────────────────────────────┘
                          │ API响应
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│ 客户端                                                           │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ ChapterInfo                                              │    │
│  │ - version = "123456"                                    │    │
│  │ - chapterId = "7123456789"                              │    │
│  └─────────────────────────────────────────────────────────┘    │
│                          │                                     │
│                          ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ 阅读器显示章节内容                                       │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### 段评创建流程

```
┌─────────────────────────────────────────────────────────────────┐
│ 阅读器                                                           │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ ReaderClient                                             │    │
│  │ - getBookProviderProxy().getBookId()                    │    │
│  │ - v1.b(client, chapterId) → "123456"                    │    │
│  └──────────────────────┬──────────────────────────────────┘    │
└─────────────────────────┼────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│ 段评创建                                                         │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ ParaTextBlock                                            │    │
│  │ - bookId = "7123456789"                                 │    │
│  │ - chapterId = "7123456789"                              │    │
│  │ - chapterVersion = "123456"                             │    │
│  │ - selectedText = "选中文字..."                           │    │
│  └──────────────────────┬──────────────────────────────────┘    │
└─────────────────────────┼────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│ 段评请求                                                         │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ GetCommentListRequest                                    │    │
│  │ - groupID = chapterId                                   │    │
│  │ - businessParam.itemVersion = "123456"                  │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

## 关键代码分析

### 1. 小说内容版本获取

**位置**: `com.dragon.read.reader.download.ChapterInfo`

```java
public static ChapterInfo a(ItemContent itemContent, boolean z17) {
    ApiItemInfo apiItemInfo = itemContent.novelData;
    ChapterInfo chapterInfo = new ChapterInfo(apiItemInfo.bookId, apiItemInfo.itemId);

    // 从 novelData.version 获取章节版本
    chapterInfo.version = apiItemInfo.version;

    // 其他信息
    chapterInfo.contentMd5 = apiItemInfo.contentMd5;
    chapterInfo.bookName = apiItemInfo.bookName;

    return chapterInfo;
}
```

### 2. 段评版本获取

**位置**: `du4.g`

```java
public static final ParaTextBlock c(
    ReaderClient client,
    String chapterId,
    String selectedText,
    IDragonParagraph parentParagraph,
    String str
) {
    // 获取书籍ID
    String bookId = client.getBookProviderProxy().getBookId();

    // 获取章节版本号
    String strB = v1.b(client, chapterId);
    Intrinsics.checkNotNullExpressionValue(strB, "getItemVersion(...)");

    // 创建 ParaTextBlock
    return new ParaTextBlock(
        bookId,
        chapterId,
        selectedText,
        id6, 0, id7, length,
        markingIntervalE,
        PARAGRAPH,
        strB,    // chapterVersion
        str2
    );
}
```

### 3. 段评请求携带版本

**位置**: `com.dragon.community.impl.list.content.ParaCommentListPresenter`

```java
public void a(List<ParaTextBlock> list) {
    GetCommentListRequest request = new GetCommentListRequest();
    request.groupID = paraTextBlock.getChapterId();

    CommentBusinessParam businessParam = new CommentBusinessParam();
    businessParam.itemID = paraTextBlock.getChapterId();
    businessParam.itemVersion = paraTextBlock.getChapterVersion();  // 携带版本号

    request.businessParam = businessParam;
}
```

## 版本号的作用

### 1. 内容一致性校验

当用户阅读章节内容时：
- 章节内容有版本号 `version`
- 用户创建段评时，段评携带 `itemVersion`
- 服务端存储段评时，记录当时的章节版本

### 2. 版本更新检测

当章节内容更新时：
- 服务端更新章节内容，生成新的版本号
- 旧版本号对应的段评可能失效（段落位置变化）
- 用户请求段评时携带当前版本号，服务端可以：
  - 返回当前版本的段评
  - 标记旧版本段评为"历史版本"
  - 提示用户"内容已更新，段评可能失效"

### 3. 搜索结果中的版本

**位置**: `com.dragon.read.rpc.model.SearchChapterContentData`

```java
public class SearchChapterContentData implements Serializable {
    @SerializedName("item_id")
    public String itemId;              // 章节ID

    public String version;             // 章节版本号

    public String content;             // 章节内容
}
```

搜索结果也携带版本号，用于：
- 确保搜索结果与当前内容一致
- 搜索片段定位时使用正确的版本

## 总结

| 对比项 | 小说内容接口 | 段评接口 |
|--------|-------------|---------|
| 字段名 | version / chapter_version | itemVersion / chapterVersion |
| 来源 | ApiItemInfo.version | v1.b(client, chapterId) |
| 本质 | 章节内容的版本号 | 章节内容的版本号 |
| 用途 | 内容版本标识 | 确保段评与内容一致性 |
| 关系 | **相同概念** | **相同概念** |

**结论**：
- 小说内容和段评接口的 `itemVersion` 是**同一个概念**
- 都表示**章节内容的版本号**
- 来源相同（服务端返回的章节版本）
- 用途相同（确保内容一致性）
- 只是字段命名略有不同（version vs itemVersion）

## 版本号生成机制（推测）

服务端可能使用以下方式生成版本号：
1. 内容MD5哈希
2. 时间戳
3. 内容更新计数器
4. 组合方案（如 MD5 + 时间戳）

客户端不生成版本号，只从服务端获取并传递。