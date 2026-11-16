# 章评接口调用获取流程分析

## 概述

章评（Chapter/Item Comment）是针对整章内容的评论功能，与段评（针对特定段落）不同，章评是用户针对整个章节发表的整体评论。本文详细分析章评接口的完整调用流程。

## 名词解释

- **章评（Item Comment）**：针对整章内容的评论
- **groupType**：关联类型，章评使用 `UgcRelativeType.Item`（值为15）
- **commentType**：评论类型，章评使用 `UgcCommentGroupTypeOutter.Item`（值为0）
- **itemVersion**：章节版本号，用于缓存管理

## 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI 层                                    │
│  ┌─────────────────┐  ┌─────────────────┐  ┌───────────────┐  │
│  │ 阅读页面         │  │ 章评列表页面      │  │ 书籍详情页     │  │
│  └────────┬────────┘  └────────┬────────┘  └───────┬───────┘  │
└───────────┼───────────────────┼───────────────────┼────────────┘
            │                   │                   │
            ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Presenter 层                                │
│  ┌─────────────────────────┐  ┌────────────────────────────┐  │
│  │ ItemCommentListPresenter │  │ yy1.y / a35.o              │  │
│  │ (通过e1.r()构建请求)     │  │ (短故事/名场面章评)          │  │
│  └────────────┬────────────┘  └─────────────┬──────────────┘  │
└───────────────┼──────────────────────────────┼─────────────────┘
                │                              │
                ▼                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      参数构建层                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ GetCommentListRequest + CommentBusinessParam             │    │
│  │ - commentSource: NovelItemComment (4)                   │    │
│  │ - groupType: UgcRelativeType.Item (15)                   │    │
│  │ - commentType: UgcCommentGroupTypeOutter.Item (0)        │    │
│  │ - serverChannel: NovelItemList (18)                     │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      RPC 层                                      │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ CommentApiService.getCommentListRxJava()                │    │
│  │ - 统一的评论列表获取接口                                  │    │
│  │ - 端点: /novel/commentapi/comment/list/:group_id/v1    │    │
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
| 1 | NovelBookComment | 书评（书籍级别） |
| 2 | NovelParaComment | 段评（段落级别） |
| 4 | NovelItemComment | **章评（章节级别）** |

### 关联类型 (UgcRelativeType)

| 值 | 枚举名 | 用途 |
|----|--------|------|
| 1 | Book | 书籍级别关联 |
| 15 | Item | **章节级别关联** |

### 评论类型 (UgcCommentGroupTypeOutter)

| 值 | 枚举名 | 用途 |
|----|--------|------|
| 0 | Item | **章评（章节评论）** |
| 1 | Paragraph | 段评（段落评论） |
| 2 | Book | 书评（书籍评论） |

### 服务端渠道 (UgcCommentChannelEnum)

| 值 | 枚举名 | 用途 |
|----|--------|------|
| 18 | NovelItemList | **章评列表** |

## 章评与段评对比

| 特性 | 章评 (Item) | 段评 (Paragraph) |
|------|-------------|------------------|
| commentSource | NovelItemComment (4) | NovelParaComment (2) |
| groupType | UgcRelativeType.Item (15) | UgcRelativeType.Item (15) |
| commentType | UgcCommentGroupTypeOutter.Item (0) | UgcCommentGroupTypeOutter.Paragraph (1) |
| serverChannel | NovelItemList (18) | NovelShortStoryParaList (39) |
| 业务参数 | 不需要 paraIndex | 需要 paraIndex、itemVersion |
| 定位方式 | 章节ID | 段落索引+文本偏移 |

## 核心代码分析

### 1. 渠道获取方法 (e1.r())

**文件**: `com.dragon.community.impl.e1`

```java
public final UgcCommentChannelEnum r(String type, Map<String, ? extends Object> extra,
                                    boolean z17, boolean z18) {
    // ...
    return UgcCommentChannelEnum.NovelItemList;  // 默认返回章评渠道
}
```

### 2. 章评请求构建

章评和段评共用 `getCommentListRxJava` 接口，通过不同的参数区分：

```java
// 章评请求
GetCommentListRequest request = new GetCommentListRequest();
request.commentSource = UgcCommentSourceEnum.NovelItemComment;    // 4
request.groupType = UgcRelativeType.Item;                         // 15
request.commentType = UgcCommentGroupTypeOutter.Item;             // 0
request.serverChannel = UgcCommentChannelEnum.NovelItemList;      // 18
request.groupID = chapterId;  // 章节ID作为groupID
```

### 3. 段评请求构建（对比）

```java
// 段评请求
GetCommentListRequest request = new GetCommentListRequest();
request.commentSource = UgcCommentSourceEnum.NovelParaComment;    // 2
request.groupType = UgcRelativeType.Item;                         // 15
request.commentType = UgcCommentGroupTypeOutter.Paragraph;       // 1
request.serverChannel = UgcCommentChannelEnum.NovelShortStoryParaList;  // 39
request.groupID = chapterId;

// 业务参数
CommentBusinessParam businessParam = new CommentBusinessParam();
businessParam.paraIndex = paraIndex;        // 段落索引
businessParam.itemVersion = chapterVersion;  // 章节版本号
businessParam.bookID = bookId;
```

## 完整数据流时序图

```
用户                  章评列表页面           e1/e1.r()          API调用方         CommentApiService     服务端
 │                      │                    │                    │                    │                    │
 │ 打开章评列表          │                    │                    │                    │                    │
 ├─────────────────────>│                    │                    │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │ 确定渠道类型        │                    │                    │                    │
 │                      ├───────────────────>│                    │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │ 返回NovelItemList  │                    │                    │                    │
 │                      <────────────────────┤                    │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │ 构建GetCommentListRequest              │                    │                    │
 │                      │ - commentSource = NovelItemComment    │                    │                    │
 │                      │ - groupType = Item                    │                    │                    │
 │                      │ - commentType = Item                  │                    │                    │
 │                      │ - channel = NovelItemList             │                    │                    │
 │                      │ - groupID = chapterId                 │                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │ 发送请求            │                    │                    │
 │                      │                    ├──────────────────>│                    │                    │
 │                      │                    │                    │                    │                    │
 │                      │                    │ getCommentListRxJava                   │                    │
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
 │                      │ 显示章评数据       │                    │                    │                    │
 └──────────────────────┴────────────────────┴────────────────────┴──────────────────┘                    │
```

## 章评接口请求参数

### GetCommentListRequest 关键字段

| 字段 | 类型 | 章评值 | 说明 |
|------|------|--------|------|
| appID | int | 自动填充 | 应用ID |
| groupID | String | chapterId | 章节ID |
| groupType | UgcRelativeType | Item (15) | 关联类型 |
| commentType | UgcCommentGroupTypeOutter | Item (0) | 评论类型 |
| commentSource | UgcCommentSourceEnum | NovelItemComment (4) | 评论来源 |
| serverChannel | UgcCommentChannelEnum | NovelItemList (18) | 服务端渠道 |
| sort | UgcSortEnum | SmartHot | 排序方式 |
| count | int | 20 | 请求数量 |
| cursor | String | 分页游标 | 分页用 |
| businessParam | CommentBusinessParam | 见下文 | 业务参数 |

### CommentBusinessParam 关键字段

| 字段 | 类型 | 说明 |
|------|------|------|
| bookID | String | 书籍ID |
| needCount | boolean | 是否需要计数 |
| insertCommentIDs | List<String> | 需要插入的评论ID列表 |
| tag | String | 话题标签 |
| foldType | FoldType | 折叠类型 |
| itemVersion | String | **章评不需要此字段** |

## 相关类一览

| 类名 | 职责 |
|------|------|
| `com.dragon.community.impl.e1` | 渠道类型判断，默认返回NovelItemList |
| `com.dragon.community.impl.list.content.q` | 书评列表Presenter（对比参考） |
| `com.dragon.community.impl.list.content.ParaCommentListPresenter` | 段评列表Presenter（对比参考） |
| `yy1.y` | 名场面/短故事章评请求构建 |
| `a35.o` | 短故事段评请求构建 |
| `CommentApiService` | RPC服务接口 |
| `GetCommentListRequest` | 请求对象模型 |
| `CommentBusinessParam` | 业务参数模型 |

## 关键差异说明

### 1. 章评 vs 段评 - 业务参数差异

**章评**不需要以下段评特有的字段：
- `paraIndex`：段落索引（章评针对整章）
- `itemVersion`：章节版本号（章评不涉及段落定位）

**章评**关注的是：
- `bookID`：书籍ID
- `tag`：话题标签（用于话题评论）

### 2. 统一的RPC接口

无论是书评、章评还是段评，都使用同一个RPC接口：

```java
// CommentApiService.java
public static Observable<GetCommentListResponse> getCommentListRxJava(
    GetCommentListRequest getCommentListRequest) {
    return getApi().f(getCommentListRequest);
}
```

服务端通过 `commentSource`、`groupType`、`commentType`、`serverChannel` 这几个字段的组合来区分不同的评论类型。

## 总结

1. **章评定义清晰**：使用 `UgcCommentSourceEnum.NovelItemComment` + `UgcRelativeType.Item` + `UgcCommentGroupTypeOutter.Item`
2. **渠道专一**：使用 `UgcCommentChannelEnum.NovelItemList`
3. **参数简化**：相比段评，章评不需要段落定位相关的参数
4. **统一接口**：与书评、段评共用同一个RPC接口，通过参数区分
5. **设计优势**：良好的扩展性，新增评论类型只需添加新的枚举值组合
