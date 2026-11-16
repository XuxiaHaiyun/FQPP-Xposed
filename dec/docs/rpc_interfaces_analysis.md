# 番茄小说 Android RPC 接口分析

## 概述

本文档分析番茄小说 Android 应用的各个 RPC（Remote Procedure Call，远程过程调用）接口，涵盖评论、书籍、短剧、搜索、支付、用户等核心功能模块。

## RPC 接口架构

### 接口分类

| 分类 | 主要接口 | 用途 |
|------|---------|------|
| 评论服务 | CommentApiService, CSSBookCommentApi, CSSParagraphCommentApi | 管理段评、章评、书评、短剧评论 |
| 书籍服务 | BookApiService, ReaderApiService | 书籍详情、章节内容、阅读记录 |
| 短剧服务 | NsShortVideoApi | 短剧视频播放、互动 |
| 搜索服务 | NsSearchApi | 搜索书籍、内容 |
| 用户服务 | UserApiService | 用户信息、偏好设置 |
| 商务服务 | CommerceApiService | 商业化、优惠券、广告 |
| 支付服务 | CJPayApiService | 支付相关功能 |
| 有声书服务 | INovelAudioApi | 有声书播放 |

## 1. 评论服务（Community API）

### 1.1 CommentApiService

**包路径**: `com.dragon.read.rpc.kmp.community.rpc.CommentApiService`

**描述**: 核心评论 RPC 服务，提供评论的增删改查功能

**方法列表**:

| 方法名 | 返回类型 | 请求参数 | 说明 |
|--------|---------|---------|------|
| getCommentList | Observable | GetCommentListRequest | 获取评论列表 |
| addComment | Object | AddCommentRequest | 添加评论 |
| addReply | Object | AddReplyRequest | 添加回复 |
| delComment | Object | DeleteCommentRequest | 删除评论 |

**请求模型**:

```java
public class GetCommentListRequest implements Serializable {
    public String groupID;                              // 组ID（章节ID/书籍ID）
    public CommentBusinessParam businessParam;          // 业务参数
    public int pageNum;                                 // 页码
    public int pageSize;                                // 每页数量
}
```

```java
public class CommentBusinessParam implements Serializable {
    @SerializedName("item_id")
    public String itemID;              // 章节ID

    @SerializedName("item_version")
    public String itemVersion;         // 章节版本号
}
```

**响应模型**:

```java
public class UgcComment implements Serializable {
    public String commentID;           // 评论ID
    public String content;             // 评论内容
    public String userID;              // 用户ID
    public long createTime;            // 创建时间
    public CommentExpand expand;       // 扩展信息
}
```

### 1.2 CSSParagraphCommentApi

**包路径**: `com.dragon.community.api.CSSParagraphCommentApi`

**描述**: 段评（段落评论）API，提供段评的列表、详情、编辑功能

**方法列表**:

| 方法名 | 参数 | 返回类型 | 说明 |
|--------|------|---------|------|
| init | InitParams, Config | void | 初始化 |
| openParagraphCommentListActivity | Context, Params | void | 打开段评列表页 |
| openParagraphCommentDetailActivity | Context, Params | void | 打开段评详情页 |
| openParagraphCommentEditor | Context, Params | void | 打开段评编辑器 |

### 1.3 CSSBookCommentApi

**包路径**: `com.dragon.community.api.CSSBookCommentApi`

**描述**: 书评（书籍评论）API，提供书评的列表、详情功能

**方法列表**:

| 方法名 | 参数 | 返回类型 | 说明 |
|--------|------|---------|------|
| init | InitParams, Config | void | 初始化 |
| openBookCommentListActivity | Context, Params | void | 打开书评列表页 |
| openBookCommentDetailActivity | Context, Params | void | 打开书评详情页 |

### 1.4 CSSPlayletCommentApi

**包路径**: `com.dragon.community.api.comment.playlet.CSSPlayletCommentApi`

**描述**: 短剧评论 API，提供短剧评论的同步、列表、编辑功能

**方法列表**:

| 方法名 | 参数 | 返回类型 | 说明 |
|--------|------|---------|------|
| init | InitParams, Config | void | 初始化 |
| addPlayletCommentSyncListener | SyncListener | void | 添加评论同步监听 |
| removePlayletCommentSyncListener | SyncListener | void | 移除评论同步监听 |
| openPlayletCommentListActivity | Context, Params | void | 打开短剧评论列表 |
| openPlayletCommentDetailActivity | Context, Params | void | 打开短剧评论详情 |
| openPlayletCommentEditor | Context, Params | void | 打开短剧评论编辑器 |
| getPredicateArgs | String | PredicateArgs | 获取谓词参数 |
| isMatchPredicateArgs | PredicateArgs, String | boolean | 匹配谓词参数 |

## 2. 书籍服务（Book API）

### 2.1 BookApiService

**包路径**: `com.bytedance.kmp.reading.rpc.BookApiService`

**描述**: 书籍核心 RPC 服务，提供书籍详情、书架、有声书等功能

**主要方法**:

| 方法名 | 返回类型 | 说明 |
|--------|---------|------|
| bookDetail | Observable | 获取书籍详情 |
| audioDetail | Observable | 获取有声书详情 |
| batchAudioPlayURL | Observable | 批量获取音频播放地址 |
| bookstoreTab | Observable | 获取书架Tab信息 |
| getBookShelfVideoInfo | Observable | 获取书架视频信息 |
| checkManualCategory | Observable | 检查手动分类 |
| commonAbResult | Observable | 通用AB实验结果 |

**书籍详情响应**:

```java
public class ApiItemInfo implements Serializable {
    @SerializedName("book_id")
    public String bookId;              // 书籍ID

    @SerializedName("item_id")
    public String itemId;              // 章节ID

    public String version;             // 章节版本号

    @SerializedName("content_md5")
    public String contentMd5;          // 内容MD5

    @SerializedName("content_picture")
    public String contentPicture;      // 内容图片

    public String title;               // 标题
    public String author;              // 作者
    public String genre;               // 类型
    public String category;           // 分类
}
```

### 2.2 ReaderApiService

**包路径**: `com.dragon.read.kmprpc.reader.saas.rpc.ReaderApiService`

**描述**: 阅读器 RPC 服务，提供章节内容获取

**主要方法**:

| 方法名 | 返回类型 | 说明 |
|--------|---------|------|
| getFull | Object | 获取完整章节内容 |
| getBatchFull | Object | 批量获取章节内容 |
| getReaderFontTypes | Object | 获取阅读器字体类型 |
| multiGetFullImage | Object | 批量获取完整图片 |

**请求模型**:

```java
public class RequestChapterContent {
    public String bookID;              // 书籍ID
    public String chapterID;           // 章节ID
    public String itemVersion;         // 章节版本号
}
```

**响应模型**:

```java
public class ItemContent implements Serializable {
    public NovelData novelData;        // 小说数据
    public AudioData audioData;       // 有声书数据
    public VideoData videoData;       // 视频数据
}
```

## 3. 短剧服务（Short Video API）

### 3.1 NsShortVideoApi

**包路径**: `com.dragon.read.component.shortvideo.api.NsShortVideoApi`

**描述**: 短剧视频 API，提供短剧播放、互动功能

**主要方法**:

| 方法名 | 参数 | 返回类型 | 说明 |
|--------|------|---------|------|
| addLikeModel | LikeModel | void | 添加点赞模型 |
| addStatReportData | StatData | void | 添加统计数据 |
| appendVideoWorkList | ArrayList, boolean, String | void | 追加视频作品列表 |
| attachLivePreviewController | RecyclerView, String, Observable | Controller | 附加直播预览控制器 |
| autoPlaySeries | - | Observable | 自动播放系列 |
| canShowVideoCommentNPSCard | String | boolean | 是否显示评论卡片 |
| enterVideoComment | Context, String | void | 进入视频评论 |
| getActivityEntranceInfo | - | ActivityEntranceInfo | 获取活动入口信息 |
| getCurrentSeriesInfo | - | SeriesInfo | 获取当前系列信息 |
| getDanmakuConfig | - | DanmakuConfig | 获取弹幕配置 |
| getFeedVideoInfo | String, String | VideoInfo | 获取Feed视频信息 |
| getPlayerConfig | - | PlayerConfig | 获取播放器配置 |
| getVideoDetail | String | VideoDetail | 获取视频详情 |
| getVideoInfoList | ArrayList | ArrayList | 获取视频信息列表 |
| isSeriesCompleted | String | boolean | 系列是否完成 |
| likeVideo | String | Observable | 点赞视频 |
| openVideoCommentList | Context, String | void | 打开视频评论列表 |
| preloadSeries | String, int | void | 预加载系列 |
| reportVideoEvent | VideoEvent | void | 上报视频事件 |
| requestSeriesList | String, int | Observable | 请求系列列表 |
| setAutoPlayNext | boolean | void | 设置自动播放下一个 |
| setSeriesAutoPlay | String, boolean | void | 设置系列自动播放 |

**视频信息模型**:

```java
public class VideoInfoData implements Serializable {
    @SerializedName("is_encrypt")
    public boolean isEncrypt;              // 是否加密

    @SerializedName("encryption_key")
    public String encryptionKey;           // 加密密钥

    @SerializedName("play_url")
    public String playURL;                 // 播放地址

    @SerializedName("cover_url")
    public String coverURL;                // 封面地址

    public String title;                    // 标题
    public String author;                   // 作者
    public int duration;                   // 时长
}
```

## 4. 搜索服务（Search API）

### 4.1 NsSearchApi

**包路径**: `com.dragon.read.component.biz.api.NsSearchApi`

**描述**: 搜索 API，提供搜索功能、配置、路由等

**主要方法**:

| 方法名 | 参数 | 返回类型 | 说明 |
|--------|------|---------|------|
| abConfigService | - | ABConfigService | AB配置服务 |
| clearAllSearchRecord | - | void | 清除所有搜索记录 |
| configService | - | ConfigService | 配置服务 |
| dataService | - | DataService | 数据服务 |
| enableSearchFeedContainer | - | boolean | 是否启用搜索Feed容器 |
| enterAiBot | Context, PageRecorder, SearchSource, String | void | 进入AI机器人 |
| enterAiBot | Context, PageRecorder, SearchSource, String, String | void | 进入AI机器人（带额外参数） |
| obtainRouterInterceptor | - | RouterInterceptor | 获取路由拦截器 |
| reportImprImChatEntrance | ... | void | 上报聊天入口曝光 |
| routerService | - | RouterService | 路由服务 |
| uiService | - | UIService | UI服务 |

## 5. 用户服务（User API）

### 5.1 UserApiService

**包路径**: `com.bytedance.kmp.reading.rpc.UserApiService`

**描述**: 用户 RPC 服务，提供用户信息、偏好设置等

**主要方法**:

| 方法名 | 返回类型 | 说明 |
|--------|---------|------|
| getUserProfile | Object | 获取用户资料 |
| setProfile | Object | 设置用户资料 |
| userPreferenceInfo | Observable | 用户偏好信息 |
| userPreferenceSet | Object | 设置用户偏好 |
| userEventReport | Observable | 用户事件上报 |
| getMoreFollowUsers | Object | 获取更多关注用户 |
| getShareInfo | Object | 获取分享信息 |
| getAdFreePreSavePanel | Object | 获取去广告预存面板 |

## 6. 商务服务（Commerce API）

### 6.1 CommerceApiService

**包路径**: `com.bytedance.kmp.reading.rpc.CommerceApiService`

**描述**: 商业化 RPC 服务，提供商业化相关功能

**主要方法**:

| 方法名 | 返回类型 | 说明 |
|--------|---------|------|
| getCommerceTab | Observable | 获取商业Tab |
| getCommerceStatistics | Observable | 获取商业统计数据 |
| commerceStatisticsReport | Observable | 商业统计上报 |
| getCouponPopup | Observable | 获取优惠券弹窗 |
| getChapterEndResource | Object | 获取章节结尾资源 |
| reportImpression | Observable | 上报曝光 |
| lynxData | Observable | Lynx数据 |

## 7. 支付服务（Pay API）

### 7.1 CJPayApiService

**包路径**: `com.bytedance.caijing.sdk.biz.pay.api.CJPayApiService`

**描述**: 支付 API，提供支付工具获取

**主要方法**:

| 方法名 | 返回类型 | 说明 |
|--------|---------|------|
| getTTCJPayUtilsCompat | ITTCJPayUtilsCompat | 获取支付工具兼容版 |

## 8. 有声书服务（Audio API）

### 8.1 INovelAudioApi

**包路径**: `com.bytedance.novel.service.inter.INovelAudioApi`

**描述**: 有声书 API，提供有声书初始化、跳转功能

**主要方法**:

| 方法名 | 参数 | 返回类型 | 说明 |
|--------|------|---------|------|
| init | - | void | 初始化有声书 |
| openAudioPlayer | String, Activity | void | 打开音频播放器 |

## 接口调用流程

### 评论获取流程

```
┌─────────────────────────────────────────────────────────────────┐
│ Presenter                                                       │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ ParaCommentListPresenter                                  │  │
│  │ - paraTextBlock.getChapterVersion() → itemVersion       │  │
│  │ - new GetCommentListRequest()                            │  │
│  └─────────────────────────┬────────────────────────────────┘  │
└────────────────────────────┼────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ RPC Service                                                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ CommentApiService                                         │  │
│  │ - getCommentListRxJava(request, header)                   │  │
│  │ - Observable<UgcCommentList>                              │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ Response Handler                                                │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ UgcCommentList                                            │  │
│  │ - comments: List<UgcComment>                            │  │
│  │ - CommentExpand.expand.itemVersion                       │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 书籍内容获取流程

```
┌─────────────────────────────────────────────────────────────────┐
│ Request                                                         │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ RequestChapterContent                                     │  │
│  │ - bookID = "7123456789"                                  │  │
│  │ - chapterID = "8123456789"                               │  │
│  │ - itemVersion = "123456"                                 │  │
│  └─────────────────────────┬────────────────────────────────┘  │
└────────────────────────────┼────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ RPC Service                                                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ ReaderApiService                                          │  │
│  │ - getFull(request, header)                               │  │
│  │ - ItemContent                                             │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ Response                                                         │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ ItemContent                                               │  │
│  │ - novelData.version = "123456"                           │  │
│  │ - novelData.content = "小说内容..."                        │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## 加密机制

### 有声书加密

- **加密类型**: 对称加密（AES）
- **密钥获取**: 通过特定的密钥交换协议
- **解密流程**: 下载加密内容 → 使用密钥解密 → 播放

### 短剧视频加密

- **加密类型**: 对称加密
- **密钥来源**: `VideoInfoData.encryptionKey`
- **解密流程**: 获取视频信息 → 检查 `isEncrypt` → 使用密钥解密 → 播放

### 小说内容加密

- **加密类型**: DH/ECDH 非对称加密
- **密钥交换**: 
  1. 客户端生成公钥
  2. 服务端使用公钥加密对称密钥
  3. 客户端使用私钥解密获得对称密钥
  4. 使用对称密钥解密内容

## 总结

| 服务分类 | 接口名称 | 核心功能 | 加密方式 |
|---------|---------|---------|---------|
| 评论服务 | CommentApiService | 评论增删改查 | 无 |
| 书籍服务 | BookApiService | 书籍详情 | 无 |
| 阅读服务 | ReaderApiService | 章节内容 | DH/ECDH |
| 短剧服务 | NsShortVideoApi | 视频播放 | 对称加密 |
| 搜索服务 | NsSearchApi | 内容搜索 | 无 |
| 用户服务 | UserApiService | 用户信息 | 无 |
| 商务服务 | CommerceApiService | 商业化 | 无 |
| 支付服务 | CJPayApiService | 支付 | 无 |
| 有声书服务 | INovelAudioApi | 音频播放 | 对称加密 |

## 附录：接口依赖关系

```
┌─────────────────────────────────────────────────────────────────┐
│                      依赖关系图                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌──────────────┐                                               │
│   │   App        │                                               │
│   └──────┬───────┘                                               │
│          │                                                        │
│          ▼                                                        │
│   ┌──────────────┐     ┌──────────────┐     ┌──────────────┐  │
│   │ BookApi      │────▶│ReaderApi     │     │ CommentApi   │  │
│   └──────────────┘     └──────────────┘     └──────────────┘  │
│          │                   │                    │               │
│          ▼                   ▼                    ▼               │
│   ┌──────────────┐     ┌──────────────┐     ┌──────────────┐  │
│   │ItemContent   │     │ChapterInfo   │     │UgcComment    │  │
│   │-novelData   │     │-version      │     │-expand       │  │
│   │-audioData   │     └──────────────┘     │-itemVersion  │  │
│   │-videoData   │                           └──────────────┘  │
│   └──────────────┘                                               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```
