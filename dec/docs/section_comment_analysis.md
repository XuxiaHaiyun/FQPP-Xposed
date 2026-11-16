# 段评接口调用获取流程分析

## 概述

段评（Paragraph Comment）是网文阅读应用中的核心功能，允许用户在阅读过程中针对特定段落进行评论和互动。本文详细分析段评接口的完整调用流程，从用户触发到数据返回的全过程。

## 名词解释

- **段评**：针对阅读内容中特定段落的评论
- **itemVersion**：章节版本号，用于标识章节内容的版本
- **ParaTextBlock**：段落文本块，包含段落的定位信息和元数据

## 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI 层                                    │
│  ┌─────────────────┐  ┌─────────────────┐  ┌───────────────┐  │
│  │ 阅读页面         │  │ 段评列表页面     │  │ 其他业务页面   │  │
│  └────────┬────────┘  └────────┬────────┘  └───────┬───────┘  │
└───────────┼───────────────────┼───────────────────┼────────────┘
            │                   │                   │
            ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Presenter 层                                │
│  ┌─────────────────────────┐  ┌────────────────────────────┐  │
│  │ ParaCommentListPresenter │  │ yy1.y / a35.o              │  │
│  └────────────┬────────────┘  └─────────────┬──────────────┘  │
└───────────────┼──────────────────────────────┼─────────────────┘
                │                              │
                ▼                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      参数构建层                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ GetCommentListRequest + CommentBusinessParam             │    │
│  │ - itemVersion: 从 ParaTextBlock 获取                     │    │
│  │ - bookId, chapterId, paraIndex 等                      │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      RPC 层                                      │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ CommentApiService.getCommentListRxJava()                │    │
│  │ - 接收完整的 GetCommentListRequest                       │    │
│  │ - 序列化为 JSON                                          │    │
│  │ - 发送到后端 API: /novel/commentapi/comment/list/:id/v1 │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      服务端                                      │
│  返回 GetCommentListResponse → CommentListData                  │
└─────────────────────────────────────────────────────────────────┘
```

## 数据模型

### 1. ParaTextBlock（段落文本块）

**位置**: `com.dragon.read.social.model.ParaTextBlock`

包含用户选中的段落信息和相关元数据：

```java
public class ParaTextBlock extends TargetTextBlock {
    private final String bookId;           // 书籍ID
    private final String chapterId;        // 章节ID
    private final String chapterVersion;   // 章节版本号
    private final String imageUrl;        // 图片URL
    private String selectedText;           // 选中的文本
    
    // 其他定位信息（继承自 TargetTextBlock）
    // - startParaId, endParaId: 段落索引
    // - startOffsetInPara, endOffsetInPara: 段落内偏移
    // - markingInterval: 标记区间
}
```

### 2. GetCommentListRequest（请求对象）

**位置**: `com.dragon.read.saas.ugc.model.GetCommentListRequest`

```java
public class GetCommentListRequest implements Serializable {
    @SerializedName("aid")
    @RpcField(FieldType.QUERY)
    public int appID;                          // 应用ID
    
    @SerializedName("group_id")
    public String groupID;                     // 组ID（章节ID）
    
    @SerializedName("group_type")
    public UgcRelativeType groupType;          // 组类型（Item）
    
    @SerializedName("comment_type")
    public UgcCommentGroupTypeOutter commentType;  // 评论类型（Paragraph）
    
    @SerializedName("comment_source")
    public UgcCommentSourceEnum commentSource;  // 评论来源
    
    @SerializedName("server_channel")
    public UgcCommentChannelEnum serverChannel;  // 服务端渠道
    
    @SerializedName("business_param")
    public CommentBusinessParam businessParam;  // 业务参数
    
    @SerializedName("sort")
    public UgcSortEnum sort;                   // 排序方式
    
    public int count;                          // 请求数量
    public String cursor;                       // 分页游标
}
```

### 3. CommentBusinessParam（业务参数）

**位置**: `com.dragon.read.saas.ugc.model.CommentBusinessParam`

```java
public class CommentBusinessParam implements Serializable {
    @SerializedName("item_version")
    public String itemVersion;                  // 章节版本号
    
    @SerializedName("book_id")
    public String bookID;                      // 书籍ID
    
    @SerializedName("para_index")
    public int paraIndex;                      // 段落索引
    
    @SerializedName("author_user_id")
    public String authorUserID;                // 作者用户ID
    
    @SerializedName("famous_scene_id")
    public String famousSceneID;               // 名场面ID
    
    @SerializedName("ref_comment_id")
    public String refCommentID;                // 引用的评论ID
    
    @SerializedName("insert_comment_ids")
    public List<String> insertCommentIDs;      // 插入的评论ID列表
    
    @SerializedName("need_count")
    public boolean needCount;                  // 是否需要计数
    
    @SerializedName("fold_type")
    public FoldType foldType;                 // 折叠类型
    
    @SerializedName("client_ab_params")
    public String clientAbParams;              // 客户端AB参数
}
```

## 核心调用链路

### 1. ParaTextBlock 创建流程

**文件**: `com.dragon.read.social.model.ParaTextBlock`

```
用户选择文本
    ↓
创建 ParaTextBlock 对象
    ↓
du4.g.c() 方法被调用
    ↓
v1.b(readerClient, chapterId) 获取章节版本
    ↓
返回 ParaTextBlock 实例
```

**关键代码**:
```java
// du4.g.c() 方法
public static final ParaTextBlock c(ReaderClient client, String chapterId, 
                                     String selectedText, IDragonParagraph parentParagraph, 
                                     String str) {
    // 获取书籍ID
    String bookId = client.getBookProviderProxy().getBookId();
    
    // 获取标记区间
    MarkingInterval markingIntervalE = MarkingInterval.Companion.e(parentParagraph);
    
    // 获取章节版本号
    String chapterVersion = v1.b(client, chapterId);
    
    // 创建 ParaTextBlock
    return new ParaTextBlock(bookId, chapterId, selectedText, 
                            id6, 0, id7, length, 
                            markingIntervalE, PARAGRAPH, chapterVersion, str2);
}
```

### 2. 章节版本号获取（v1.b）

**文件**: `com.dragon.read.reader.utils.v1`

```java
public static String b(ReaderClient readerClient, String str) {
    if (readerClient == null || TextUtils.isEmpty(str)) {
        return "";
    }
    
    // 优先从内存中的 Chapter 对象获取版本号
    Chapter chapterG = ue5.t.f320913c.c(readerClient).g(str);
    if (chapterG != null && !TextUtils.isEmpty(chapterG.getVersion())) {
        return chapterG.getVersion();
    }
    
    // 兜底：从 CatalogProvider 获取版本号
    ChapterItem chapterItemE0 = readerClient.getCatalogProvider().e0(str);
    if (chapterItemE0 == null) {
        return "";
    }
    
    // 记录日志用于排查
    if (!g2.i()) {
        LogWrapper.info("experience", "ParagraphUtil", 
                        "拿不到ChapterInfo的version，用CatalogData的version兜底");
    }
    
    return chapterItemE0.getVersion();
}
```

**获取优先级**:
1. **优先**: 从内存中的 `Chapter` 对象获取 `getVersion()`
2. **兜底**: 从 `CatalogProvider` 获取 `ChapterItem.getVersion()`

### 3. ParaCommentListPresenter 构建请求

**文件**: `com.dragon.community.impl.list.content.ParaCommentListPresenter`

```java
public ParaCommentListPresenter(e1 e1Var, g2 g2Var, j0 listParam) {
    // 创建 GetCommentListRequest
    GetCommentListRequest getCommentListRequest = new GetCommentListRequest();
    this.f94233n = getCommentListRequest;
    
    // 设置基础参数
    getCommentListRequest.commentSource = UgcCommentSourceEnum.NovelParaComment;
    getCommentListRequest.groupID = listParam.h().getChapterId();
    getCommentListRequest.groupType = UgcRelativeType.Item;
    getCommentListRequest.commentType = UgcCommentGroupTypeOutter.Paragraph;
    getCommentListRequest.sort = UgcSortEnum.SmartHot;
    getCommentListRequest.count = 20;
    
    // 设置业务参数
    CommentBusinessParam commentBusinessParam = new CommentBusinessParam();
    getCommentListRequest.businessParam = commentBusinessParam;
    
    commentBusinessParam.needCount = true;
    commentBusinessParam.insertCommentIDs = listParam.f();
    commentBusinessParam.paraIndex = listParam.h().getEndParaId();
    commentBusinessParam.itemVersion = listParam.h().getChapterVersion();  // 关键！
    commentBusinessParam.bookID = listParam.h().getBookId();
    commentBusinessParam.authorUserID = listParam.q();
}
```

### 4. 调用 RPC 接口

**文件**: `com.dragon.read.saas.ugc.rpc.CommentApiService`

```java
public static Observable<GetCommentListResponse> getCommentListRxJava(
    GetCommentListRequest getCommentListRequest) {
    return getApi().f(getCommentListRequest);
}
```

**内部实现**:
```java
// a.f() 方法（RCP框架生成）
@RpcOperation("$POST /novel/commentapi/comment/list/:group_id/v1/")
@RpcParams(true)
@RpcSerializer(SerializeType.JSON)
Observable<GetCommentListResponse> f(GetCommentListRequest getCommentListRequest);
```

## 完整数据流时序图

```
用户                  阅读页面              ParaTextBlock         ParaCommentListPresenter    CommentApiService           服务端
 │                      │                      │                      │                      │                      │
 │  选择文本             │                      │                      │                      │                      │
 ├──────────────────────>│                      │                      │                      │                      │
 │                      │                      │                      │                      │                      │
 │                      │ 创建ParaTextBlock     │                      │                      │                      │
 │                      ├─────────────────────>│                      │                      │                      │
 │                      │                      │                      │                      │                      │
 │                      │                      │ 获取chapterVersion   │                      │                      │
 │                      │                      ├─────────────────────>│                      │                      │
 │                      │                      │                      │                      │                      │
 │                      │                      │                      │ v1.b() 获取版本号     │                      │
 │                      │                      │                      ├─────────────────────>│                      │
 │                      │                      │                      │                      │                      │
 │                      │                      │                      │ 返回版本号            │                      │
 │                      │                      │                      <─────────────────────┤                      │
 │                      │                      │                      │                      │                      │
 │                      │ 返回ParaTextBlock     │                      │                      │                      │
 │                      <──────────────────────┤                      │                      │                      │
 │                      │                      │                      │                      │                      │
 │                      │                      │                      │                      │                      │
 │                      │ 显示段评列表          │                      │                      │                      │
 │                      ├──────────────────────┤                      │                      │                      │
 │                      │                      │                      │                      │                      │
 │                      │                      │                      │ 构建GetCommentListRequest │                      │
 │                      │                      │                      ├──────────────────────>│                      │
 │                      │                      │                      │                      │                      │
 │                      │                      │                      │ 调用getCommentListRxJava │                      │
 │                      │                      │                      ├──────────────────────>│                      │
 │                      │                      │                      │                      │                      │
 │                      │                      │                      │ 发送HTTP POST请求       │                      │
 │                      │                      │                      ├───────────────────────────────────────────────>│
 │                      │                      │                      │                      │                      │
 │                      │                      │                      │                      │ 返回JSON响应           │
 │                      │                      │                      │                      <───────────────────────────────────────────────┤
 │                      │                      │                      │                      │                      │
 │                      │                      │                      │ 返回Observable        │                      │
 │                      │                      │                      <──────────────────────┤                      │
 │                      │                      │                      │                      │                      │
 │                      │ 显示段评数据         │                      │                      │                      │
 └──────────────────────┴──────────────────────┴──────────────────────┴──────────────────────┘                      │
```

## itemVersion 的作用

### 1. 缓存管理

`itemVersion`（章节版本号）主要用于：

1. **内容版本标识**：标识当前章节的内容版本
2. **评论关联**：确保评论与特定版本的章节内容关联
3. **缓存策略**：服务器可以根据版本号判断是否需要返回新的评论数据

### 2. 数据一致性

当章节内容发生更新时（如作者修改章节），版本号会发生变化，从而：

1. **区分新旧评论**：新的版本号下可能需要展示不同的评论
2. **触发数据刷新**：客户端检测到版本变化时重新拉取评论
3. **历史数据保留**：旧版本的评论数据仍然被保留

### 3. 降级策略

如果无法获取章节版本号，系统采用兜底策略：

```java
// v1.b() 方法中的兜底逻辑
if (chapterG != null && !TextUtils.isEmpty(chapterG.getVersion())) {
    return chapterG.getVersion();
}
// 兜底：使用 CatalogProvider 中的版本号
ChapterItem chapterItemE0 = readerClient.getCatalogProvider().e0(str);
return chapterItemE0.getVersion();
```

## 相关类一览

| 类名 | 职责 |
|------|------|
| `ParaTextBlock` | 段落文本块的数据模型 |
| `CSSParaTextBlock` | ParaTextBlock 的包装类 |
| `ICSSParaTextBlock` | 段落文本块的接口定义 |
| `du4.g` | ParaTextBlock 的工厂类 |
| `v1` | 章节信息获取工具类 |
| `j0` | 段评列表参数封装 |
| `ParaCommentListPresenter` | 段评列表的 Presenter |
| `CommentApiService` | RPC 服务接口 |
| `GetCommentListRequest` | 请求对象 |
| `CommentBusinessParam` | 业务参数对象 |

## 关键方法说明

### 获取章节版本号

```java
// 入口方法
String chapterVersion = v1.b(readerClient, chapterId);

// 内部实现
// 1. 优先从内存中的 Chapter 对象获取
Chapter chapter = ue5.t.f320913c.c(readerClient).g(chapterId);
if (chapter != null && !TextUtils.isEmpty(chapter.getVersion())) {
    return chapter.getVersion();
}

// 2. 兜底从 CatalogProvider 获取
ChapterItem chapterItem = readerClient.getCatalogProvider().e0(chapterId);
return chapterItem.getVersion();
```

### 构建评论请求

```java
// 创建请求对象
GetCommentListRequest request = new GetCommentListRequest();

// 设置请求参数
request.groupID = chapterId;
request.businessParam.itemVersion = chapterVersion;
request.businessParam.bookID = bookId;

// 调用API
Observable<GetCommentListResponse> observable = 
    CommentApiService.getCommentListRxJava(request);
```

## 总结

1. **数据来源清晰**：itemVersion 最终来源于 `Chapter.getVersion()` 或 `ChapterItem.getVersion()`
2. **获取优先级明确**：优先使用内存中的数据，失败后使用持久化数据作为兜底
3. **设计目的**：确保评论数据与特定版本的章节内容正确关联
4. **降级机制完善**：即使无法获取版本号，也能通过兜底策略正常工作
