# 短剧视频接口调用获取流程分析

## 概述

短剧（ShortSeries/Drama）是指短小精悍的连续剧内容，通常每集几分钟。本文档分析短剧视频源的获取、解密和播放流程，特别关注视频加密机制。

## 名词解释

- **ShortSeries**：短剧/微短剧
- **SerialDrama**：连续剧/系列剧
- **VideoContentType**：视频内容类型枚举
- **TTVideoEngine**：字节跳动视频播放引擎
- **PlayAddress**：播放地址（包含URL和加密信息）
- **VideoInfo**：视频基本信息
- **VideoInfoData**：视频URL数据（包含加密密钥）

## 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI 层                                    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ ShortSeriesPlayer / VideoPlayerViewModel               │    │
│  │ - 负责短剧播放页面的业务逻辑                            │    │
│  └──────────────────────┬──────────────────────────────────┘    │
└─────────────────────────┼────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      数据请求层                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ VideoInfoData / VideoInfo                              │    │
│  │ - videoId: 视频ID                                    │    │
│  │ - mainUrl: 主播放URL                                 │    │
│  │ - backupUrl: 备用播放URL                              │    │
│  │ - isEncrypt: 是否加密                                │    │
│  │ - encryptionKey: 加密密钥                            │    │
│  └─────────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ PlayAddress                                            │    │
│  │ - playUrl: 播放URL                                   │    │
│  │ - isEncrypt: 是否加密                                │    │
│  │ - encryptionKey: 加密密钥                            │    │
│  │ - playType: 播放类型 (0-URL, 1-本地文件, 2-模型)   │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────┼────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      播放引擎层                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ VideoEngineManager                                      │    │
│  │ - 视频引擎管理器                                       │    │
│  │ - 创建 TTVideoEngine 实例                             │    │
│  └─────────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ TTVideoEngineImpl                                      │    │
│  │ - 字节跳动视频播放引擎实现                             │    │
│  │ - 支持加密视频解密播放                                 │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

## 视频内容类型

**位置**: `com.bytedance.rpc.model.VideoContentType`

```java
public enum VideoContentType {
    ShortSeriesPlay(1),    // 短剧播放
    ScenePlay(2),          // 场景播放
    ExpertRecommendBook(3), // 专家荐书
    PPTRecommendBook(4),   // PPT荐书
    Movie(5),              // 电影
    TelePlay(6),           // 电视剧
    ShortPlay(7),          // 短剧
    DropMaterial(1000),    // 物料
    PUGC(1001),            // PUGC内容
    UnrealShortPlay(1002);  // 虚拟短剧
}
```

## 核心数据模型

### 1. VideoInfoData（视频URL数据）

**位置**: `com.dragon.read.rpc.model.VideoInfoData`

```java
public class VideoInfoData implements Serializable {
    @SerializedName("main_url")
    public String mainUrl;                 // 主播放URL

    @SerializedName("backup_url")
    public String backupUrl;               // 备用播放URL

    @SerializedName("is_encrypt")
    public boolean isEncrypt;              // 是否加密

    @SerializedName("encryption_key")
    public String encryptionKey;           // 加密密钥

    @SerializedName("url_expire")
    public long urlExpire;                // URL过期时间
}
```

### 2. VideoInfo（视频基本信息）

**位置**: `com.bytedance.rpc.model.VideoInfo`

```java
public class VideoInfo implements Serializable {
    @SerializedName("video_id")
    public String videoId;                 // 视频ID

    @SerializedName("video_url")
    public String videoUrl;                // 视频URL

    @SerializedName("video_model")
    public String videoModel;             // 视频模型

    @SerializedName("video_platform")
    public VideoPlatformType videoPlatform; // 视频平台

    @SerializedName("cover")
    public String cover;                   // 封面图

    @SerializedName("title")
    public String title;                   // 标题

    @SerializedName("sub_title")
    public String subTitle;                // 副标题

    @SerializedName("video_duration")
    public int videoDuration;              // 视频时长（秒）

    @SerializedName("episode_num")
    public int episodeNum;                 // 集数

    @SerializedName("series_id")
    public long seriesId;                  // 系列ID

    @SerializedName("related_book_id")
    public long relatedBookId;             // 关联书籍ID

    @SerializedName("type")
    public int type;                       // 类型
}
```

### 3. PlayAddress（播放地址）

**位置**: `com.xs.fm.player.base.play.address.PlayAddress`

```java
public class PlayAddress implements Serializable {
    public String playUrl;                 // 播放URL
    public String backUrl;                 // 备用URL
    public String playFile;                // 播放文件（本地路径）
    public String playVideoModel;          // 播放视频模型
    public String videoId;                // 视频ID
    public String tag;                    // 标签
    public String subTag;                // 子标签
    public String encryptionKey;           // 加密密钥
    public boolean isEncrypt;             // 是否加密

    public int playType = 0;              // 播放类型
                                            // 0: URL播放
                                            // 1: 本地文件播放
                                            // 2: 视频模型播放

    public boolean isFromDisk = false;   // 是否来自磁盘
    public boolean isFromCache = false;   // 是否来自缓存
    public int cacheType = 0;            // 缓存类型
    public long fetchTime = 0;           // 获取时间

    // 音量平衡相关
    public int volumeBalanceType = -1;
    public float volumeBalanceTargetLoudness = 0.0f;
    public float volumeBalanceSrcLoudness = 0.0f;
    public float volumeBalanceLoudPeak = 0.0f;

    // 片头片尾时间
    public int openingTime = -1;
    public int endingTime = -1;
}
```

### 4. VideoModel（视频模型）

**位置**: `com.ss.ttvideoengine.model.VideoModel`

视频模型是TTVideoEngine使用的内部模型，包含视频的所有播放信息。

### 5. ShortVideoData（短视频数据）

**位置**: `com.bytedance.rpc.model.ShortVideoData`

```java
public class ShortVideoData implements Serializable {
    public String f80180id;               // 视频ID（字段名有混淆）

    @SerializedName("vid")
    public String vid;                     // 视频vid

    public URL url;                       // 视频URL
    public URL addrH265;                  // H265编码URL
    public Cover cover;                   // 封面
    public ShortVideoAuthor author;        // 作者信息

    @SerializedName("is_ad")
    public boolean isAd;                   // 是否广告

    @SerializedName("digg_count")
    public long diggCount;                 // 点赞数

    @SerializedName("view_count")
    public long viewCount;                 // 浏览数

    @SerializedName("video_product")
    public ProductData videoProduct;       // 视频产品信息
}
```

## 视频加密机制

### 1. 加密标志判断

```java
public class VideoInfoData {
    @SerializedName("is_encrypt")
    public boolean isEncrypt;              // 是否加密

    @SerializedName("encryption_key")
    public String encryptionKey;           // 加密密钥
}
```

### 2. PlayAddress 加密信息

```java
public class PlayAddress {
    public String encryptionKey;           // 加密密钥
    public boolean isEncrypt;              // 是否加密

    // 比较两个播放地址是否相同（包括加密信息）
    public boolean isSameWith(PlayAddress playAddress) {
        // 比较playUrl, playFile, playVideoModel, tag, subTag
        // volumeBalanceType, volumeBalanceTargetLoudness, volumeBalanceLoudPeak
        // isEncrypt, encryptionKey, openingTime, endingTime
    }
}
```

### 3. 缓存过期判断

```java
public boolean isValidExpiredTime() {
    // 检查缓存是否过期
    // 如果当前时间 - fetchTime < 缓存有效期，返回true
    // 否则返回false
}
```

## 解密流程

### 1. TTVideoEngine 解密支持

**位置**: `com.ss.ttvideoengine.TTVideoEngineImpl`

视频引擎内部处理加密视频的解密，支持：
- 使用 `encryptionKey` 解密
- 支持多种加密方案

### 2. VideoEngineManager 视频引擎管理

**位置**: `com.ss.android.excitingvideo.video.VideoEngineManager`

```java
public final class VideoEngineManager {
    // 创建视频引擎实例
    public final TTVideoEngine b(
        Context context,
        VideoAd ad,
        String videoId,
        boolean needEncryption,
        int playerType
    ) {
        // 创建带有加密支持的视频引擎
    }

    // 处理视频解密
    private final boolean h(VideoAd videoAd) {
        // 检查视频是否需要解密
    }
}
```

### 3. 播放类型

```java
public class PlayAddress {
    public int playType = 0;

    // playType = 0: 使用 playUrl 进行网络播放
    // playType = 1: 使用 playFile 进行本地文件播放
    // playType = 2: 使用 playVideoModel 创建视频模型播放
}
```

## 关键类一览

| 类名 | 职责 |
|------|------|
| `VideoInfoData` | 视频URL数据（包含mainUrl、backupUrl、isEncrypt、encryptionKey） |
| `VideoInfo` | 视频基本信息模型 |
| `PlayAddress` | 播放地址（URL/文件路径/模型 + 加密信息） |
| `ShortVideoData` | 短视频数据模型 |
| `VideoContentType` | 视频内容类型枚举 |
| `TTVideoEngineImpl` | 字节跳动视频播放引擎 |
| `VideoEngineManager` | 视频引擎管理器 |
| `SeriesVideoInfo` | 系列视频信息 |

## 完整数据流时序图

```
用户                  播放页面          ViewModel           RPC层             服务端           视频引擎         解密模块
 │                      │                  │                │                │                │                │
 │ 开始播放短剧          │                  │                │                │                │                │
 ├─────────────────────>│                  │                │                │                │                │
 │                      │                  │                │                │                │                │
 │                      │ 构建视频请求      │                │                │                │                │
 │                      │ - videoId       │                │                │                │                │
 │                      │ - contentType   │                │                │                │                │
 │                      │                  │                │                │                │                │
 │                      │ 发送请求         │                │                │                │                │
 │                      ├───────────────>│                │                │                │                │
 │                      │                │ RPC调用         │                │                │                │
 │                      │                ├────────────────────────────────>│                │                │
 │                      │                │                │                │                │                │
 │                      │                │                │ 返回VideoInfoData               │                │
 │                      │                │                │                │                │                │
 │                      │                │                │ 返回响应        │                │                │
 │                      │                │                <────────────────────────────────┤                │
 │                      │                │                │                │                │                │
 │                      │ 解析响应        │                │                │                │                │
 │                      │ - mainUrl     │                │                │                │                │
 │                      │ - backupUrl   │                │                │                │                │
 │                      │ - isEncrypt   │                │                │                │                │
 │                      │ - encryptionKey                │                │                │                │
 │                      │                  │                │                │                │                │
 │                      │ 创建PlayAddress │                │                │                │                │
 │                      │ - playUrl      │                │                │                │                │
 │                      │ - isEncrypt    │                │                │                │                │
 │                      │ - encryptionKey│                │                │                │                │
 │                      │                  │                │                │                │                │
 │                      │ 开始播放        │                │                │                │                │
 │                      │                │                │                │                │                │
 │                      │                │                │                │ TTVideoEngine │                │
 │                      │                │                │                ├───────────────>│                │
 │                      │                │                │                │                │                │
 │                      │                │                │                │ 检查isEncrypt │                │
 │                      │                │                │                │                │                │
 │                      │                │                │                │ 如果需要解密  │                │
 │                      │                │                │                │                │                │
 │                      │                │                │                │ 使用encryptionKey解密              │
 │                      │                │                │                │                │                │
 │                      │                │                │                │ 播放解密后视频│                │
 │                      │                │                │                │<───────────────┤                │
 │                      │                │                │                │                │                │
 │ 显示播放界面         │                │                │                │                │                │
 └──────────────────────┴────────────────┴────────────────┴────────────────┴────────────────┴────────────────┘
```

## 音频与视频加密对比

| 特性 | 音频 | 视频 |
|------|------|------|
| 加密标志 | `isEncrypt` | `isEncrypt` |
| 加密密钥 | `encryptionKey` | `encryptionKey` |
| 主URL | `mainUrl` | `mainUrl` |
| 备用URL | `backupUrl` | `backupUrl` |
| 播放引擎 | TTAudioEngineImpl | TTVideoEngineImpl |
| 分段下载器 | SegmentDownloader | VideoEngineManager |
| 解密密钥版本 | v1/v2 | - |
| 密钥来源 | Source (SERVER/CACHE/NONE) | - |

## 缓存策略

### PlayAddress 缓存

```java
public boolean isValidExpiredTime() {
    // 检查缓存是否过期
    if (System.currentTimeMillis() - this.fetchTime < 缓存有效期) {
        return true;  // 缓存有效
    }
    return false;     // 缓存过期
}
```

### 缓存类型

```java
public class PlayAddress {
    public int cacheType = 0;  // 缓存类型
    public boolean isFromCache = false;  // 是否来自缓存
}
```

## 错误处理

### 1. URL路由失败

```java
public interface VideoURLRouteListener {
    void onVideoURLRouteFailed(String vid, int errorCode);
}
```

### 2. 播放错误

```java
VideoEngineManager$setVideoEngineCallback$1.onError(int what, int extra) {
    // 处理视频播放错误
    // what: 错误类型
    // extra: 额外错误码
}
```

### 3. 备用URL切换

```java
if (!TextUtils.isEmpty(playInfo.backupUrl)) {
    // 主URL失败时使用备用URL
    playUrl = playInfo.backupUrl;
}
```

## 总结

1. **完整流程**：
   - 请求VideoInfoData → 获取视频URL和加密信息 → 创建PlayAddress → 传入TTVideoEngine → 如需解密则使用encryptionKey解密 → 播放视频

2. **核心组件**：
   - `VideoInfoData`：视频URL数据（包含加密信息）
   - `PlayAddress`：播放地址（统一URL/文件/模型三种播放方式）
   - `TTVideoEngineImpl`：字节跳动视频播放引擎
   - `VideoEngineManager`：视频引擎管理器

3. **加密机制**：
   - 使用 `isEncrypt` 标志判断是否需要解密
   - 通过 `encryptionKey` 进行解密
   - 与音频加密机制类似

4. **播放类型**：
   - URL播放（网络）
   - 本地文件播放
   - 视频模型播放

5. **容错设计**：
   - 主备URL切换
   - 缓存过期机制
   - 多码率支持
