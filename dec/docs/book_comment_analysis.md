# 书评接口调用获取流程分析

## 概述

书评（Book Comment）是针对整本书籍的评论功能，是三种评论类型（书评、章评、段评）中范围最广的一种。本文详细分析书评接口的完整调用流程。

## 名词解释

- **书评（Book Comment）**：针对整本书籍的评论
- **groupType**：关联类型，书评使用 `UgcRelativeType.Book`（值为1）
- **commentType**：评论类型，书评使用 `UgcCommentGroupTypeOutter.Book`（值为2）
- **tag**：话题标签，用于话题关联

## 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI 层                                    │
│  ┌─────────────────┐  ┌─────────────────┐  ┌───────────────┐  │
│  │ 书籍详情页       │  │ 书评列表页面      │  │ 其他业务页面   │  │
│  └────────┬────────┘  └────────┬────────┘  └───────┬───────┘  │
└───────────┼───────────────────┼───────────────────┼────────────┘
            │                   │                   │
            ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Presenter 层                                │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ q (BookCommentListPresenter)                           │    │
│  │ - 构造函数中初始化 GetCommentListRequest                │    │
│  │ - r() 方法：首次加载                                    │    │
│  │ - u() 方法：加载更多                                    │    │
│  └────────────┬──────────────────────────────────────────┘    │
└───────────────┼───────────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      参数构建层                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ GetCommentListRequest + CommentBusinessParam             │    │
│  │ - commentSource: NovelBookComment (1)                   │    │
│  │ - groupType: UgcRelativeType.Book (1)                  │    │
│  │ - commentType: UgcCommentGroupTypeOutter.Book (2)       │    │
│  │ - serverChannel: NovelBookInnerList 等                 │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      RPC 层                                      │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ CommentApiService.getCommentListRxJava()                │    │
│  │ - 统一的评论列表获取接口                                  │    │
│  │ - 端点: /novel/commentapi/comment/list/:group_id/v1   │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      服务端                                      │
│  返回 GetCommentListResponse → CommentListData                  │
└─────────────────────────────────────────────────────────────────┘
```

## 关键枚举值

### 评论来源 (UgcCommentSourceEnum)

| 值 | 枚举名 | 用途 |
|----|--------|------|
| 1 | NovelBookComment | **书评（书籍级别）** |
| 2 | NovelParaComment | 段评（段落级别） |
| 4 | NovelItemComment | 章评（章节级别） |

### 关联类型 (UgcRelativeType)

| 值 | 枚举名 | 用途 |
|----|--------|------|
| 1 | Book | **书籍级别关联** |
| 15 | Item | 章节级别关联 |

### 评论类型 (UgcCommentGroupTypeOutter)

| 值 | 枚举名 | 用途 |
|----|--------|------|
| 0 | Item | 章评（章节评论） |
| 1 | Paragraph | 段评（段落评论） |
| 2 | Book | **书评（书籍评论）** |

### 服务端渠道 (UgcCommentChannelEnum)

| 值 | 枚举名 | 用途 |
|----|--------|------|
| 1 | NovelBookInnerList | 书评-书籍内列表 |
| 2 | NovelBookListBookEnd | 书评-书籍列表末尾 |
| 3 | NovelBookListChapterEnd | 书评-章节末尾 |
| 4 | NovelBookExposedBookCover | 书评-曝光书籍封面 |
| 5 | NovelBookListBookCover | 书评-列表书籍封面 |
| 6 | NovelBookExposedBookDetail | 书评-曝光书籍详情 |
| 7 | NovelBookListBookDetail | 书评-列表书籍详情 |
| 8 | NovelBookExposedBookEnd | 书评-曝光书籍末尾 |
| 9 | NovelBookListDefault | 书评-列表默认 |
| 14 | NovelBookExposedChapterEnd | 书评-曝光章节末尾 |

## 三种评论类型对比

| 特性 | 书评 (Book) | 章评 (Item) | 段评 (Paragraph) |
|------|-------------|-------------|------------------|
| commentSource | NovelBookComment (1) | NovelItemComment (4) | NovelParaComment (2) |
| groupType | Book (1) | Item (15) | Item (15) |
| commentType | Book (2) | Item (0) | Paragraph (1) |
| 定位范围 | 整本书 | 整章 | 特定段落 |
| 业务参数 | tag（话题） | bookID | bookID, paraIndex, itemVersion |

## 核心代码分析

### 1. 书评Presenter构造函数

**文件**: `com.dragon.community.impl.list.content.q`

```java
/* JADX INFO: renamed from: q */
public final class q extends it1.a {

    /* 渠道来源 */
    private final UgcCommentChannelEnum serverChannel;

    /* 书籍ID */
    private final String bookId;

    /* 请求对象 */
    private final GetCommentListRequest f94410n;

    /* 排序类型 */
    private UgcSortEnum f94411o;

    /* 标签 */
    private String f94412p;

    public q(com.dragon.community.common.contentlist.content.comment.i listner,
             com.dragon.community.impl.list.page.n listParam) {
        // ...
        GetCommentListRequest getCommentListRequest = new GetCommentListRequest();
        this.f94410n = getCommentListRequest;

        // 设置书评参数
        getCommentListRequest.commentSource = UgcCommentSourceEnum.NovelBookComment;     // 1
        getCommentListRequest.serverChannel = listParam.k();                            // 渠道
        getCommentListRequest.groupID = listParam.d();                                  // 书籍ID
        getCommentListRequest.groupType = UgcRelativeType.Book;                         // 1
        getCommentListRequest.commentType = UgcCommentGroupTypeOutter.Book;             // 2
        getCommentListRequest.sort = UgcSortEnum.SmartHot;
        getCommentListRequest.count = 20;

        CommentBusinessParam commentBusinessParam = new CommentBusinessParam();
        getCommentListRequest.businessParam = commentBusinessParam;
        commentBusinessParam.needCount = true;
        commentBusinessParam.bookID = listParam.d();  // 书籍ID
    }
}
```

### 2. 首次加载方法 (r)

```java
@Override
public Disposable r(final Function1<? super com.dragon.community.common.contentlist.content.base.h, Unit> onSuccess,
                    final Function1<? super Throwable, Unit> onError) {
    // 处理标签
    boolean z17;
    List<String> listListOf;

    GetCommentListRequest getCommentListRequest = this.f94410n;
    this.f94411o = getCommentListRequest.sort;
    CommentBusinessParam commentBusinessParam = getCommentListRequest.businessParam;
    String str = commentBusinessParam.tag;
    this.f94412p = str;

    // 如果没有标签且有默认话题，则使用默认话题
    if (str != null && str.length() != 0) {
        z17 = false;
    } else {
        z17 = true;
    }
    if (z17 && n32.g.a(this.f94408l.j())) {
        listListOf = CollectionsKt__CollectionsJVMKt.listOf(this.f94408l.j());
    } else {
        listListOf = null;
    }
    commentBusinessParam.insertCommentIDs = listListOf;

    // 设置分页游标
    this.f94410n.cursor = f();

    // 设置折叠类型
    GetCommentListRequest getCommentListRequest2 = this.f94410n;
    CommentBusinessParam commentBusinessParam2 = getCommentListRequest2.businessParam;
    FoldType foldTypeZ;
    if (getCommentListRequest2.sort == UgcSortEnum.SmartHot) {
        foldTypeZ = z();  // 智能热门排序
    } else {
        foldTypeZ = FoldType.Normal;
    }
    commentBusinessParam2.foldType = foldTypeZ;
    this.f94410n.businessParam.needCount = true;

    // 调用API
    Single<CommentListData> singleE =
        com.dragon.community.common.contentlist.content.comment.h.e(this.f94410n);

    // 订阅处理...
    return disposableSubscribe;
}
```

### 3. 加载更多方法 (u)

```java
@Override
public Disposable u(final Function1<? super com.dragon.community.common.contentlist.content.base.h, Unit> onSuccess,
                    final Function1<? super Throwable, Unit> onError) {
    // 设置分页游标
    this.f94410n.cursor = f();
    this.f94410n.businessParam.foldType = z();

    // 加载更多时不需要计数
    CommentBusinessParam commentBusinessParam = this.f94410n.businessParam;
    commentBusinessParam.needCount = false;
    commentBusinessParam.insertCommentIDs = null;

    // 调用API
    Single<CommentListData> singleE =
        com.dragon.community.common.contentlist.content.comment.h.e(this.f94410n);

    // 订阅处理...
    return disposableSubscribe;
}
```

## 完整数据流时序图

```
用户                  书评列表页面           q (Presenter)        h.e()             CommentApiService     服务端
 │                      │                    │                    │                    │                    │
 │ 打开书评列表          │                    │                    │                    │                    │
 ├─────────────────────>│                    │                    │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │ 创建q Presenter   │                    │                    │                    │
 │                      │ - 设置请求参数    │                    │                    │                    │
 │                      │   commentSource   │                    │                    │                    │
 │                      │   = NovelBookComment                    │                    │                    │
 │                      │   groupType      │                    │                    │                    │
 │                      │   = Book         │                    │                    │                    │
 │                      │   commentType    │                    │                    │                    │
 │                      │   = Book         │                    │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │ 调用 r() 首次加载 │                    │                    │                    │
 │                      ├──────────────────>│                    │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │ 构建完整请求        │                    │                    │
 │                      │                    │ - 设置cursor       │                    │                    │
 │                      │                    │ - 设置foldType     │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │ 调用 h.e()         │                    │                    │
 │                      │                    ├──────────────────>│                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │                    │ getCommentListRxJava                   │
 │                      │                    │                    ├──────────────────>│                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │                    │ 发送HTTP POST      │                    │
 │                      │                    │                    ├─────────────────────────────────────────>│
 │                      │                    │                    │                    │                    │
 │                      │                    │                    │                    │ 返回JSON响应       │
 │                      │                    │                    │                    <─────────────────────────────────────────┤
 │                      │                    │                    │                    │                    │
 │                      │                    │                    │ 返回Observable    │                    │
 │                      │                    │                    <──────────────────┤                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │ 返回Disposable   │                    │                    │
 │                      │                    <──────────────────┤                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │ 显示书评列表       │                    │                    │                    │
 └──────────────────────┴────────────────────┴────────────────────┴──────────────────┘                    │
```

## 书评接口请求参数

### GetCommentListRequest 关键字段

| 字段 | 类型 | 书评值 | 说明 |
|------|------|--------|------|
| appID | int | 自动填充 | 应用ID |
| groupID | String | bookId | 书籍ID |
| groupType | UgcRelativeType | Book (1) | 关联类型 |
| commentType | UgcCommentGroupTypeOutter | Book (2) | 评论类型 |
| commentSource | UgcCommentSourceEnum | NovelBookComment (1) | 评论来源 |
| serverChannel | UgcCommentChannelEnum | NovelBookInnerList 等 | 服务端渠道 |
| sort | UgcSortEnum | SmartHot | 排序方式 |
| count | int | 20 | 请求数量 |
| cursor | String | 分页游标 | 分页用 |
| businessParam | CommentBusinessParam | 见下文 | 业务参数 |

### CommentBusinessParam 关键字段

| 字段 | 类型 | 书评值 | 说明 |
|------|------|--------|------|
| bookID | String | bookId | 书籍ID |
| needCount | boolean | true(首次)/false(加载更多) | 是否需要计数 |
| insertCommentIDs | List<String> | null 或默认话题ID | 需要插入的评论ID列表 |
| tag | String | 话题标签 | 话题关联 |
| foldType | FoldType | Normal/Hot | 折叠类型 |
| itemVersion | String | **不需要** | 书评不涉及章节版本 |

## 话题标签处理

书评支持话题标签功能：

```java
// 如果没有指定标签，且存在默认话题，则使用默认话题
if (z17 && n32.g.a(this.f94408l.j())) {
    listListOf = CollectionsKt__CollectionsJVMKt.listOf(this.f94408l.j());
} else {
    listListOf = null;
}
commentBusinessParam.insertCommentIDs = listListOf;

// 设置标签
public final void R(UgcSortEnum ugcSortEnum, String str) {
    this.f94410n.businessParam.tag = str;
    // ...
}
```

## 相关类一览

| 类名 | 职责 |
|------|------|
| `com.dragon.community.impl.list.content.q` | 书评列表Presenter，负责请求构建和数据处理 |
| `com.dragon.community.impl.list.page.n` | 书评列表参数封装 |
| `com.dragon.community.common.contentlist.content.comment.h` | 评论列表API调用封装 |
| `CommentApiService` | RPC服务接口 |
| `GetCommentListRequest` | 请求对象模型 |
| `CommentBusinessParam` | 业务参数模型 |
| `n32.g` | 话题判断工具类 |

## 设计特点

### 1. 统一接口，参数区分

无论是书评、章评还是段评，都使用同一个RPC接口：

```java
public static Observable<GetCommentListResponse> getCommentListRxJava(
    GetCommentListRequest getCommentListRequest) {
    return getApi().f(getCommentListRequest);
}
```

服务端通过以下参数组合区分：
- `commentSource`：1=书评, 2=段评, 4=章评
- `groupType`：1=书籍, 15=章节
- `commentType`：0=章评, 1=段评, 2=书评

### 2. 话题支持

书评支持话题标签功能，可以针对特定话题进行评论筛选。

### 3. 折叠策略

书评列表支持折叠功能：
- `SmartHot` 排序时使用智能折叠
- 其他排序使用普通模式

### 4. 加载策略

- 首次加载：需要计数 (`needCount=true`)
- 加载更多：不需要计数 (`needCount=false`)

## 总结

1. **书评参数明确**：
   - `commentSource = NovelBookComment (1)`
   - `groupType = Book (1)`
   - `commentType = Book (2)`

2. **不需要章节相关信息**：
   - 无需 `itemVersion`
   - 无需 `paraIndex`
   - 只需要 `bookID`

3. **支持话题功能**：
   - 可通过 `tag` 字段筛选话题
   - 可通过 `insertCommentIDs` 插入话题相关评论

4. **统一的RPC接口**：
   - 与章评、段评共用 `getCommentListRxJava`
   - 通过枚举值组合区分不同评论类型

5. **良好的扩展性**：
   - 新增评论类型只需添加新的枚举值组合
   - 复用现有的请求/响应模型
