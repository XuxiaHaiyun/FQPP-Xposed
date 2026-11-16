# 番茄小说阅读器时长累计和奖励机制分析

## 概述

本文档分析番茄小说 Android 应用的阅读时长累计机制和奖励系统，包括时长采集、上报、任务管理和奖励发放等核心流程。

## 一、时长累计机制

### 1.1 核心类：ReadingTiming

**包路径**: `com.dragon.read.polaris.timing.ReadingTiming`

**功能**: 负责阅读时长的实时累计和上报

**核心机制**:

```java
public final class ReadingTiming {
    // 单例实例
    public static final ReadingTiming f182373a;
    
    // 定时器，每秒触发一次
    private static Timer f182377e;
    
    // 累计时长缓存
    private static final c<String, Long> f182375c;
    
    // 已记录页面ID集合（防止重复记录）
    private static final Lazy f182374b;
}
```

**关键常量**:
- `f182379g = 8`: 最大缓存页面数
- 定时器周期: 1000ms（每秒触发）
- 上报阈值: 10000ms（10秒上报一次）
- 单次最大累加: 30000ms（30秒）

### 1.2 时长累计流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    阅读时长累计流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. 页面进入                                                     │
│     └─▶ ReadingTiming.j(pageUniqueId)                           │
│         ├─ 检查页面是否已记录                                    │
│         ├─ 启动定时器 (1000ms周期)                               │
│         └─ 重置累计时长                                          │
│                                                                  │
│  2. 定时器触发 (每秒)                                            │
│     └─▶ ReadingTiming.a.run()                                   │
│         ├─ 获取当前页面ID                                        │
│         ├─ 检查是否已记录该页面                                   │
│         ├─ 累加时长 (每次+1000ms)                               │
│         └─ 调用 d(j17, false)                                   │
│                                                                  │
│  3. 时长上报 (累计达到10秒)                                      │
│     └─▶ ReadingTiming.d(j17, true)                             │
│         ├─ 检查累计时长 >= 10000ms                              │
│         ├─ 调用 ITimingService.addTime()                        │
│         └─ 重置累计时长                                          │
│                                                                  │
│  4. 页面退出                                                     │
│     └─▶ ReadingTiming.k()                                       │
│         ├─ 停止定时器                                            │
│         ├─ 上报剩余时长                                          │
│         └─ 清除缓存                                              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 核心代码逻辑

**启动定时器**:
```java
private final void i() {
    if (f182378f) return;  // 防止重复启动
    
    Timer timer = f182377e;
    if (timer != null) {
        timer.cancel();
    }
    
    PthreadTimer pthreadTimer = new PthreadTimer("ReadingTiming");
    // 每秒触发一次
    pthreadTimer.schedule(new a(), 1000L, 1000L);
    
    f182377e = pthreadTimer;
    f182378f = true;
    
    LogWrapper.info("growth", "ReadingTiming", "start timer", new Object[0]);
}
```

**定时器任务**:
```java
private static final void b() {
    String str = (String) f182375c.a();  // 当前页面ID
    long jLongValue = ((Number) f182375c.b()).longValue();  // 累计时长
    
    if (f().contains(str)) {
        return;  // 跳过已记录页面
    }
    
    long j17 = 1000;
    // 单次最大累加30秒
    if (jLongValue + 1000 > 30000) {
        j17 = 30000 - jLongValue;
    }
    
    if (j17 > 0) {
        f182375c.update(str, jLongValue + j17);
        readingTiming.d(j17, false);  // 累加时长
    }
}
```

**时长上报**:
```java
private final void d(long j17, boolean z17) {
    long j18;
    long j19 = f182376d + j17;
    f182376d = j19;
    
    // 累计达到10秒则上报
    if (j19 >= 10000) {
        j18 = 10000;
    } else {
        j18 = j19;
    }
    
    boolean z18 = (j19 < 10000 && (!z17 || j18 <= 0)) ? false : true;
    
    LogWrapper.debug("growth", "ReadingTiming", 
        "addReadTime, saveTime= %s, satisfySave= %b", 
        new Object[]{Long.valueOf(j18), Boolean.valueOf(z18)});
    
    if (z18) {
        f182376d = 0L;  // 重置
    }
    
    // 调用ITimingService上报
    g(0L, j17);
}
```

### 1.4 服务端上报

```java
private final void g(long j17, long j18) {
    ITimingService iTimingService = (ITimingService) gj1.b.c(ITimingService.class);
    if (iTimingService != null) {
        // 上报阅读场景时长
        iTimingService.addTime(TimingScene.NOVEL, j18);
    }
    
    // 本地记录
    x1.f182759a.a(j18);
}
```

**ITimingService接口**:
```java
public interface ITimingService extends IService {
    void addTime(TimingScene timingScene, long j17);
    
    void addPendantStatusListener(c cVar);
    
    AbsTimingType createTimingType(String str, String str2, 
        com.bytedance.ug.sdk.novel.base.cn.timing.b bVar, 
        com.bytedance.ug.sdk.novel.base.cn.timing.a aVar, 
        Map<String, String> map);
    
    void requestPendantConfig(String str, Map<String, String> map);
}
```

## 二、奖励机制

### 2.1 奖励状态枚举

**AwardListItem状态定义**:

| 状态值 | 名称 | 说明 |
|--------|------|------|
| 0 | 未领取 | 奖励未到账 |
| 1 | 已领取 | 奖励已领取 |
| 2 | 待领取 | 可以领取 |
| 3 | 积攒中 | 正在进行积攒 |
| 4 | 未积攒 | 未开始积攒 |

```java
public final class AwardListItem {
    @SerializedName("collected_amount")
    private int collectedAmount;  // 已积攒数量
    
    @SerializedName("extra_amount")
    private int extraAmount;     // 额外数量
    
    @SerializedName("reward")
    private final int reward;    // 奖励数量
    
    @SerializedName("status")
    private int status;          // 状态
    
    @SerializedName("award_type")
    private final String awardType;  // 奖励类型
}
```

### 2.2 奖励数据结构

**AwardInfo**:
```java
public class AwardInfo implements Serializable {
    @SerializedName("action_type")
    public String actionType;      // 行动类型
    
    @SerializedName("action_url")
    public String actionUrl;       // 行动URL
    
    @SerializedName("bg_color")
    public String bgColor;        // 背景色
    
    @SerializedName("icon_url")
    public String iconUrl;        // 图标URL
    
    @SerializedName("text")
    public String text;           // 文案
    
    @SerializedName("text_color")
    public String textColor;      // 文字颜色
}
```

**TaskAwardData**:
```java
public class TaskAwardData implements Serializable {
    @SerializedName("gold_coin_num")
    public int goldCoinNum;       // 金币数量
}
```

### 2.3 任务模型

**SingleTaskModel任务类型**:

| 类型值 | 名称 | 说明 |
|--------|------|------|
| 10 | TYPE_AUDIO_TIME_TASK | 有声书时长任务 |
| 13 | TYPE_WEEKEND_DOUBLE_TASK | 周末双倍任务 |
| 14 | TYPE_LOCAL_BOOK_TASK | 本地书籍任务 |
| 28 | TYPE_WATCH_SHORT_VIDEO_TASK | 观看短视频任务 |

```java
public class SingleTaskModel implements Serializable {
    @SerializedName("action_desc")
    private String actionDesc;      // 行动描述
    
    @SerializedName("action_times")
    private int actionTimes;        // 行动次数
    
    @SerializedName("completed")
    private boolean completed;      // 是否完成
    
    @SerializedName("conf_extra")
    private String confExtra;       // 配置扩展
    
    @SerializedName("continue_comp_cnt")
    private int continueCompCnt;    // 连续完成次数
    
    @SerializedName("desc")
    private String desc;            // 任务描述
    
    @SerializedName("done_percent")
    private int donePercent;        // 完成百分比
    
    @SerializedName("key")
    private String key;             // 任务key
    
    @SerializedName("name")
    private String name;            // 任务名称
    
    @SerializedName("reward")
    private ArrayList<Reward> rewards;  // 奖励列表
    
    @SerializedName("status")
    private String status;          // 状态
}
```

### 2.4 任务模块类型

**GrowthTaskModuleType**:

```java
public enum GrowthTaskModuleType {
    Growing(1),           // 成长任务
    Viewing(2),          // 阅读任务
    PublishingContinuously(3),  // 持续发布任务
    Other(4);            // 其他任务
}
```

### 2.5 奖励Biz信息

**GrowthBizInfo**:
```java
public class GrowthBizInfo implements Serializable {
    @SerializedName("fav_style")
    public long favStyle;          // 收藏样式
    
    @SerializedName("is_app_new")
    public boolean isAppNew;       // 是否App新用户
    
    @SerializedName("is_mall_new")
    public boolean isMallNew;      // 是否商城新用户
    
    @SerializedName("newer_coupon_style")
    public long newerCouponStyle;   // 新人优惠券样式
}
```

## 三、任务管理

### 3.1 任务管理器

**PolarisComicTaskMgr**:
- 负责管理各类任务的执行和上报
- 处理阅读时长上报、节日活动时长上报等

### 3.2 任务上报机制

```java
public class PolarisComicTaskMgr {
    // 阅读时长上报
    public static void pageHasReportReadingTime(long duration) {
        // 触发任务进度更新
    }
    
    // 节日活动时长上报
    public static void pageHasReportFestivalReadingTime(long duration) {
        // 触发节日活动任务进度更新
    }
}
```

## 四、数据流图

### 4.1 时长累计数据流

```
┌─────────────────────────────────────────────────────────────────┐
│                     阅读时长累计数据流                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐                                               │
│  │ 阅读器页面    │                                               │
│  └──────┬───────┘                                               │
│         │                                                        │
│         ▼                                                        │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ ReadingTiming                                            │   │
│  │ - j(pageUniqueId): 启动计时                             │   │
│  │ - a.run(): 每秒累加1000ms                              │   │
│  │ - d(j17, z17): 达到10秒触发上报                        │   │
│  └──────────────────────────┬─────────────────────────────┘   │
│                              │                                   │
│                              ▼                                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ ITimingService                                           │   │
│  │ - addTime(TimingScene.NOVEL, duration)                 │   │
│  │ - 创建TimingType并管理                                  │   │
│  └──────────────────────────┬─────────────────────────────┘   │
│                              │                                   │
│                              ▼                                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ 服务端                                                   │   │
│  │ - 累计用户阅读时长                                       │   │
│  │ - 更新任务进度                                           │   │
│  │ - 判断奖励是否可领取                                    │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 奖励发放数据流

```
┌─────────────────────────────────────────────────────────────────┐
│                      奖励发放数据流                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. 任务配置获取                                                 │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ TimerPendantConfig                                      │  │
│     │ - timerDuration: 定时时长                               │  │
│     │ - timerStartTime: 开始时间                               │  │
│     │ - timerEndTime: 结束时间                                 │  │
│     │ - timerInterval: 间隔时长                                 │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  2. 任务进度更新                                                 │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ SingleTaskModel                                         │  │
│     │ - donePercent: 完成百分比                                │  │
│     │ - status: 任务状态                                      │  │
│     │ - rewards: 奖励列表                                     │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  3. 奖励领取                                                     │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ AwardListItem                                          │  │
│     │ - status: 0=未领取, 1=已领取, 2=待领取                  │  │
│     │ - reward: 奖励数量                                     │  │
│     │ - collectedAmount: 已积攒数量                           │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  4. 金币到账                                                     │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ TaskAwardData                                          │  │
│     │ - goldCoinNum: 金币数量                                 │  │
│     └────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 五、关键配置

### 5.1 时长累计配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| 定时器周期 | 1000ms | 每秒触发一次 |
| 上报阈值 | 10000ms | 累计10秒上报 |
| 最大单次累加 | 30000ms | 防止异常 |
| 最大缓存页面 | 500 | 防止内存溢出 |

### 5.2 任务类型

| 任务类型 | 类型值 | 触发条件 |
|---------|--------|---------|
| 有声书时长 | 10 | 收听有声书 |
| 周末双倍 | 13 | 周末阅读 |
| 本地书籍 | 14 | 阅读本地书籍 |
| 短视频 | 28 | 观看短视频 |

### 5.3 奖励状态

| 状态 | 值 | 可领取 |
|------|-----|--------|
| 未领取 | 0 | 否 |
| 已领取 | 1 | 否 |
| 待领取 | 2 | 是 |
| 积攒中 | 3 | 否 |
| 未积攒 | 4 | 否 |

## 六、总结

### 6.1 时长累计机制特点

1. **实时累计**: 使用定时器每秒累加阅读时长
2. **智能上报**: 累计达到10秒自动上报，减少网络请求
3. **防重复**: 通过页面ID集合防止同一页面重复记录
4. **异常保护**: 单次最大累加30秒，防止异常情况
5. **内存保护**: 最大缓存500个页面ID

### 6.2 奖励机制特点

1. **多状态管理**: 奖励有明确的生命周期（未领取→待领取→已领取）
2. **多任务类型**: 支持阅读、有声书、短视频等多种任务
3. **持续激励**: 支持连续完成奖励、周末双倍等激励机制
4. **实时进度**: 提供完成百分比显示

### 6.3 核心流程

```
阅读页面 ──▶ 启动计时器 ──▶ 每秒累加时长 ──▶ 达到阈值上报
                                              │
                                              ▼
                                         服务端更新进度
                                              │
                                              ▼
                                      检查任务完成状态
                                              │
                                              ▼
                                      奖励可领取提示
                                              │
                                              ▼
                                        用户领取奖励
                                              │
                                              ▼
                                    TaskAwardData.goldCoinNum
```

## 七、附录：关键类清单

| 类名 | 路径 | 功能 |
|------|------|------|
| ReadingTiming | com.dragon.read.polaris.timing | 阅读时长累计 |
| ITimingService | com.bytedance.ug.sdk.novel.base.cn.service | 时长服务接口 |
| AwardListItem | com.bytedance.ug.sdk.novel.base.cn.task | 奖励列表项 |
| AwardInfo | com.dragon.read.model | 奖励信息 |
| SingleTaskModel | com.dragon.read.polaris.model | 单个任务模型 |
| PolarisComicTaskMgr | com.dragon.read.polaris.comic | 任务管理器 |
| GrowthTaskModuleType | com.dragon.read.rpc.model | 任务模块类型 |
| GrowthBizInfo | com.dragon.read.rpc.model | 成长业务信息 |
| StatData | com.dragon.read.rpc.model | 统计数据 |
