# 番茄小说 VIP 特权判断和显示机制分析

## 概述

本文档分析番茄小说 Android 应用的 VIP 特权判断和显示机制，包括 VIP 用户状态、特权类型、特权数据结构、特权显示等核心流程。

## 一、VIP 用户状态

### 1.1 VIP 信息模型

**VipInfo** - VIP 用户信息

**位置**: `com.dragon.read.rpc.model.VipInfo`

```java
public class VipInfo implements Serializable {
    @SerializedName("is_vip")
    public String isVip;              // 是否VIP（String类型，"1"=是，"0"=否）

    @SerializedName("is_ad_vip")
    public boolean isAdVip;          // 是否广告VIP

    @SerializedName("is_union_vip")
    public boolean isUnionVip;        // 是否联合VIP

    @SerializedName("expire_time")
    public String expireTime;         // 到期时间

    @SerializedName("left_time")
    public String leftTime;           // 剩余时间

    @SerializedName("auto_renew")
    public boolean autoRenew;         // 是否自动续费

    @SerializedName("continue_month")
    public boolean continueMonth;     // 是否连续包月

    @SerializedName("continue_month_buy")
    public boolean continueMonthBuy;  // 是否连续购买

    @SerializedName("renew_type")
    public VipRenewType renewType;    // 续费类型

    @SerializedName("sub_type")
    public VipCommonSubType subType;  // VIP子类型

    @SerializedName("union_source")
    public int unionSource;           // 联合来源
}
```

### 1.2 VIP 续费类型

**VipRenewType** - VIP 续费类型枚举

```java
public enum VipRenewType {
    VipRenewNone(0),      // 不自动续费
    VipRenewMonth(1),     // 月卡自动续费
    VipRenewYear(2),      // 年卡自动续费
    VipRenewQuarter(3);   // 季卡自动续费
}
```

### 1.3 VIP 子类型

**VipCommonSubType** - VIP 子类型枚举

```java
public enum VipCommonSubType {
    Default(0),      // 默认
    Publish(1),      // 发布特权
    ShortStory(2),   // 短故事特权
    AdFree(3);       // 去广告特权
}
```

## 二、VIP 特权类型

### 2.1 特权类型枚举

**VipCommonPrivilegeType** - VIP 特权类型枚举

**位置**: `com.bytedance.kmp.reading.model.VipCommonPrivilegeType`

```java
public enum VipCommonPrivilegeType {
    AdFree(1),               // 免广告
    PubFree(2),              // 发布免费
    OfflineRead(3),          // 离线阅读
    AutoPageTurn(4),         // 自动翻页
    IdentifiedLogo(5),       // 认证标识
    ExclusiveSticker(6),      // 专属贴纸
    ReadingBackground(7),     // 阅读背景
    WebReading(8),           // 网页阅读
    ExclusiveCoupon(9),        // 专属优惠券
    StoryFree(10),           // 故事免费
    MemberUnion(11),          // 会员联合
    Spokesperson(12),        // 代言人
    ReadingAdFree(13),        // 阅读页去广告
    ListeningAdFree(14),      // 听书去广告
    WatchingAdFree(15),       // 观看去广告
    ExclusiveShopping(16),   // 专属购物
    SeriesFree(17),           // 系列免费
    Font(18);                // 专属字体
}
```

### 2.2 特权类型表

| 值 | 名称 | 说明 |
|----|------|------|
| 1 | AdFree | 免广告 |
| 2 | PubFree | 发布免费 |
| 3 | OfflineRead | 离线阅读 |
| 4 | AutoPageTurn | 自动翻页 |
| 5 | IdentifiedLogo | 认证标识 |
| 6 | ExclusiveSticker | 专属贴纸 |
| 7 | ReadingBackground | 阅读背景 |
| 8 | WebReading | 网页阅读 |
| 9 | ExclusiveCoupon | 专属优惠券 |
| 10 | StoryFree | 故事免费 |
| 11 | MemberUnion | 会员联合 |
| 12 | Spokesperson | 代言人 |
| 13 | ReadingAdFree | 阅读页去广告 |
| 14 | ListeningAdFree | 听书去广告 |
| 15 | WatchingAdFree | 观看去广告 |
| 16 | ExclusiveShopping | 专属购物 |
| 17 | SeriesFree | 系列免费 |
| 18 | Font | 专属字体 |

## 三、特权数据结构

### 3.1 特权信息

**VIPCommonPrivilegeInfo** - 通用特权信息

**位置**: `com.dragon.read.rpc.model.VIPCommonPrivilegeInfo`

```java
public class VIPCommonPrivilegeInfo implements Serializable {
    public String id;                           // 特权ID

    public String title;                       // 特权标题

    public String desc;                        // 特权描述

    public String brief;                       // 特权简介

    @SerializedName("pic_url")
    public String picUrl;                      // 图片URL

    @SerializedName("night_pic_url")
    public String nightPicUrl;                 // 夜间模式图片

    @SerializedName("privilege_type")
    public VipCommonPrivilegeType privilegeType;  // 特权类型

    @SerializedName("related_link")
    public String relatedLink;                 // 关联链接

    @SerializedName("related_link_title")
    public String relatedLinkTitle;             // 关联链接标题

    @SerializedName("bubble_info")
    public VIPCommonPrivilegeBubble bubbleInfo;    // 气泡信息
}
```

### 3.2 特权气泡

**VIPCommonPrivilegeBubble** - 特权气泡信息

```java
public class VIPCommonPrivilegeBubble implements Serializable {
    @SerializedName("bubble_text")
    public String bubbleText;          // 气泡文字

    public boolean show;              // 是否显示气泡
}
```

### 3.3 特权数据

**VIPPrivilegeInfo** - 特权信息

```java
public class VIPPrivilegeInfo implements Serializable {
    public String id;                           // 特权ID

    public String title;                       // 特权标题

    public String desc;                        // 特权描述

    @SerializedName("pic_url")
    public String picUrl;                      // 图片URL

    @SerializedName("night_pic_url")
    public String nightPicUrl;                 // 夜间模式图片

    @SerializedName("related_link")
    public String relatedLink;                 // 关联链接

    @SerializedName("related_link_title")
    public String relatedLinkTitle;            // 关联链接标题
}
```

### 3.4 特权数据容器

**VIPPrivilegeInfoData** - 特权数据容器

```java
public class VIPPrivilegeInfoData implements Serializable {
    @SerializedName("privilege_info")
    public List<VIPCommonPrivilegeInfo> privilegeInfo;  // 特权信息列表
}
```

## 四、VIP Banner 数据

### 4.1 Banner 数据模型

**VipBannerData** - VIP Banner 数据

**位置**: `com.dragon.read.component.biz.api.data.VipBannerData`

```java
public final class VipBannerData extends AbsImpressionModel {
    private final boolean isVip;              // 是否VIP

    private final VipCommonSubType type;     // VIP子类型

    private String tvVipTitle;               // VIP标题

    private String tvVipPrivilege;            // VIP特权描述

    private String vipCouponEntranceText;    // VIP优惠券入口文案

    public VipBannerData(VipCommonSubType type) {
        this(type, false);
    }

    public VipBannerData(VipCommonSubType type, boolean isVip) {
        this.type = type;
        this.isVip = isVip;
        this.tvVipTitle = "";
        this.tvVipPrivilege = "";
    }

    public final boolean isVip() {
        return this.isVip;
    }

    public final VipCommonSubType getType() {
        return this.type;
    }

    public final String getTvVipTitle() {
        return this.tvVipTitle;
    }

    public final String getTvVipPrivilege() {
        return this.tvVipPrivilege;
    }

    public final String getVipCouponEntranceText() {
        return this.vipCouponEntranceText;
    }
}
```

## 五、VIP 配置服务

### 5.1 VIP 配置服务接口

**BsVipConfigService** - VIP 配置服务

**位置**: `com.dragon.read.component.biz.impl.brickservice.BsVipConfigService`

```java
public interface BsVipConfigService {
    // 获取章节解锁文案
    String getChapterUnlockWords(long chapterId, long bookId);

    // 获取锁章付费墙URL
    String getLockChapterPayWallUrl();

    // 获取VIP半页URL
    String getVipHalfPageUrl();

    // 获取VIP页面URL
    String getVipPageUrl();

    // 获取VIP页面URL Key
    String getVipPageUrlKey();

    // 上报阅读或音频完成
    void reportReaderOrAudioFinish(String str, String str2);
}
```

### 5.2 VIP 弹窗路径配置

**VipDialogPath** - VIP 弹窗路径配置

**位置**: `com.dragon.read.base.ssconfig.template.VipDialogPath`

```java
public final class VipDialogPath {
    @SerializedName("new_path")
    public final boolean newPath;  // 是否使用新路径

    static {
        // AB实验配置
        SsConfigMgr.prepareAB("vip_dialog_path_v553", VipDialogPath.class, IVipDialogPath.class);
    }
}
```

## 六、特权判断逻辑

### 6.1 VIP 判断流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      VIP 判断流程                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. 获取VIP状态                                                 │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ VipInfo                                                │  │
│     │ - isVip: "1" / "0"                                   │  │
│     │ - isAdVip: true / false                               │  │
│     │ - isUnionVip: true / false                           │  │
│     │ - subType: AdFree / Publish / ShortStory / Default   │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  2. 判断VIP类型                                                 │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ switch (vipInfo.subType) {                           │  │
│     │   case AdFree:       // 去广告VIP                      │  │
│     │   case Publish:      // 发布VIP                       │  │
│     │   case ShortStory:   // 短故事VIP                    │  │
│     │   case Default:      // 默认VIP                       │  │
│     │ }                                                         │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  3. 判断特权是否可用                                            │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ VIPCommonPrivilegeInfo.privilegeType                  │  │
│     │ - AdFree(1):       isAdVip || isVip                  │  │
│     │ - OfflineRead(3):  isVip                             │  │
│     │ - ReadingAdFree(13): isAdVip || isVip                │  │
│     │ - ...                                                  │  │
│     └────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 特权显示判断

```java
public boolean canShowPrivilege(VipInfo vipInfo, VipCommonPrivilegeType type) {
    if (vipInfo == null || TextUtils.isEmpty(vipInfo.isVip)) {
        return false;
    }

    switch (type) {
        case AdFree:
            return vipInfo.isAdVip || "1".equals(vipInfo.isVip);

        case PubFree:
        case OfflineRead:
        case AutoPageTurn:
        case IdentifiedLogo:
        case ExclusiveSticker:
        case ReadingBackground:
        case WebReading:
        case ExclusiveCoupon:
        case StoryFree:
        case MemberUnion:
        case Spokesperson:
        case ExclusiveShopping:
        case SeriesFree:
        case Font:
            return "1".equals(vipInfo.isVip);

        case ReadingAdFree:
            return vipInfo.isAdVip || "1".equals(vipInfo.isVip);

        case ListeningAdFree:
            return vipInfo.isAdVip || "1".equals(vipInfo.isVip);

        case WatchingAdFree:
            return vipInfo.isAdVip || "1".equals(vipInfo.isVip);

        default:
            return false;
    }
}
```

## 七、特权显示组件

### 7.1 Banner 显示

```
┌─────────────────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ VipBannerData                                             │   │
│  │  ┌─────────────────────────────────────────────────┐     │   │
│  │  │ [VIP图标]  VIP尊享特权                          │     │   │
│  │  │ 畅享免广告、离线阅读等特权                      │     │   │
│  │  │                              [立即开通]          │     │   │
│  │  └─────────────────────────────────────────────────┘     │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 特权气泡显示

```java
// 气泡数据结构
VIPCommonPrivilegeBubble bubbleInfo = new VIPCommonPrivilegeBubble();
bubbleInfo.bubbleText = "VIP专享";
bubbleInfo.show = true;

// 显示条件
if (bubbleInfo.show && !TextUtils.isEmpty(bubbleInfo.bubbleText)) {
    // 显示气泡
    showBubble(bubbleInfo.bubbleText);
}
```

### 7.3 半页弹窗

```
┌─────────────────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                                                         │   │
│  │                    [关闭按钮]                           │   │
│  │                                                         │   │
│  │              🎉 恭喜获得VIP特权                         │   │
│  │                                                         │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐            │   │
│  │  │  免广告   │  │  离线阅读 │  │  专属字体 │            │   │
│  │  │  [图标]  │  │  [图标]  │  │  [图标]  │            │   │
│  │  └──────────┘  └──────────┘  └──────────┘            │   │
│  │                                                         │   │
│  │                   [立即开通VIP]                         │   │
│  │                                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## 八、VIP 页面配置

### 8.1 页面 URL 配置

```java
// VIP页面
String vipPageUrl = vipConfigService.getVipPageUrl();
// 示例: "VIP_PAGE_URL"

// VIP半页URL
String vipHalfPageUrl = vipConfigService.getVipHalfPageUrl();
// 示例: "VIP_HALF_PAGE_URL"

// 锁章付费墙URL
String lockChapterPayWallUrl = vipConfigService.getLockChapterPayWallUrl();
// 示例: "LOCK_CHAPTER_PAY_WALL_URL"
```

### 8.2 章节解锁文案

```java
// 获取章节解锁文案
String unlockWords = vipConfigService.getChapterUnlockWords(chapterId, bookId);
// 示例: "开通VIP免费读本章"
```

## 九、数据流图

### 9.1 VIP 状态获取流程

```
┌─────────────────────────────────────────────────────────────────┐
│                  VIP 状态获取流程                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. 初始化                                                       │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ BsVipConfigService                                     │  │
│     │ - getVipPageUrl(): 获取VIP页面URL                      │  │
│     │ - getVipHalfPageUrl(): 获取VIP半页URL                  │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  2. 获取VIP信息                                                 │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ VipInfo                                               │  │
│     │ - isVip: "1"                                         │  │
│     │ - subType: AdFree                                    │  │
│     │ - expireTime: "2025-12-31"                          │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  3. 获取特权列表                                                 │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ VIPPrivilegeInfoData                                  │  │
│     │ - privilegeInfo: List<VIPCommonPrivilegeInfo>         │  │
│     └────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 9.2 特权显示判断流程

```
┌─────────────────────────────────────────────────────────────────┐
│                  特权显示判断流程                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐                                               │
│  │ VipInfo      │                                               │
│  │ isVip = "1"  │                                               │
│  └──────┬───────┘                                               │
│         │                                                        │
│         ▼                                                        │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ 遍历特权列表 VIPCommonPrivilegeInfo                       │   │
│  │  ┌────────────────────────────────────────────────────┐  │   │
│  │  │ privilegeType = VipCommonPrivilegeType.AdFree     │  │   │
│  │  │                                                     │  │   │
│  │  │ 判断条件:                                           │  │   │
│  │  │ isAdVip || "1".equals(isVip) = true                │  │   │
│  │  │                                                     │  │   │
│  │  │ 显示结果: ✅ 显示此特权                             │  │   │
│  │  └────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────┬───────────────────────────────┘   │
│                             │                                     │
│                             ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ VipBannerData                                           │   │
│  │ - isVip = true                                          │   │
│  │ - type = VipCommonSubType.AdFree                        │   │
│  │ - tvVipTitle = "VIP尊享特权"                            │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 十、总结

### 10.1 VIP 状态类型

| 状态类型 | 字段 | 说明 |
|---------|------|------|
| 普通VIP | isVip = "1" | 完整VIP会员 |
| 广告VIP | isAdVip = true | 仅去广告特权 |
| 联合VIP | isUnionVip = true | 联合会员 |

### 10.2 特权显示逻辑

1. **基础判断**: 检查 `isVip` 是否为 "1"
2. **类型判断**: 根据 `subType` 判断VIP子类型
3. **特权判断**: 遍历 `privilegeInfo` 列表
4. **条件匹配**: 根据特权类型匹配用户VIP状态

### 10.3 核心判断方法

```java
// 判断用户是否为VIP
boolean isVip = "1".equals(vipInfo.isVip);

// 判断用户是否有某特权
boolean hasPrivilege = checkPrivilege(vipInfo, privilegeType);

// 显示Banner
VipBannerData banner = new VipBannerData(subType, isVip);
banner.setTvVipTitle("VIP尊享特权");
banner.setTvVipPrivilege("畅享免广告、离线阅读等特权");
```

### 10.4 关键配置

| 配置项 | 说明 |
|-------|------|
| vip_dialog_path_v553 | VIP弹窗路径AB实验 |
| getVipPageUrl() | VIP页面URL |
| getVipHalfPageUrl() | VIP半页URL |
| getLockChapterPayWallUrl() | 锁章付费墙URL |
| getChapterUnlockWords() | 章节解锁文案 |

## 附录：关键类清单

| 类名 | 路径 | 功能 |
|------|------|------|
| VipInfo | com.dragon.read.rpc.model | VIP用户信息 |
| VipRenewType | com.dragon.read.rpc.model | VIP续费类型 |
| VipCommonSubType | com.bytedance.kmp.reading.model | VIP子类型 |
| VipCommonPrivilegeType | com.bytedance.kmp.reading.model | 特权类型枚举 |
| VIPCommonPrivilegeInfo | com.dragon.read.rpc.model | 通用特权信息 |
| VIPCommonPrivilegeBubble | com.dragon.read.rpc.model | 特权气泡信息 |
| VIPPrivilegeInfo | com.dragon.read.rpc.model | 特权信息 |
| VIPPrivilegeInfoData | com.dragon.read.rpc.model | 特权数据容器 |
| VipBannerData | com.dragon.read.component.biz.api.data | VIP Banner数据 |
| BsVipConfigService | com.dragon.read.component.biz.impl.brickservice | VIP配置服务 |
| VipDialogPath | com.dragon.read.base.ssconfig.template | VIP弹窗路径配置 |
