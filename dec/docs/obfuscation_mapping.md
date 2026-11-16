# 混淆特征映射与跨版本适配指南

> 基于 **番茄免费小说 7.0.9.32** 与 **7.2.7** 双版本反编译对比验证（jadx 工程）
>
> 本文档用于指导跨版本适配：当目标应用升级后，混淆类名/方法名/字段名会变化，
> 需依据本文档提供的"特征签名"在新版本中重新定位，或后续接入 DexKit 实现动态查找。
>
> **7.2.7 对比结论**：特征签名定位策略有效——所有混淆目标均能通过特征在新版本中重新定位。
> 其中 4 项未变、5 项改名、2 项架构变更、1 项完全失效（详见第二节）。

## 一、适配总览

模块所有 Hook 点集中在 [HookManager.java](../../app/src/main/java/com/xxhy/fqpp/xposed/HookManager.java)，
具体拦截逻辑分布在 `xcallback/` 各 Hooker 类中。

混淆名分两类：
- **【稳定】**：未混淆的语义化名称（如 `com.dragon.read.util.BookUtils`、`isVip`），跨版本一般不变。
- **【混淆】**：单/双字母短名（如 `fd4.c`、`b2`、`M`），随版本变化，**升级后必须重新定位**。

## 二、版本对比速查表 (7.0.9.32 → 7.2.7)

> 以下通过 jadx-mcp-server 逐一验证。变化类型：✅未变 / 🔄改名 / 📦架构变更 / ❌失效

### 混淆类名对比

| 7.0.9.32 | 7.2.7 | 变化 | 定位特征（DexKit 查询条件） |
|---|---|---|---|
| `com.dragon.read.recyler.c` | `com.dragon.read.recyler.c` | ✅ | `abstract class extends RecyclerView.Adapter`，含 `getDataList()`+`setDataList()`，字段 `d`=List |
| `fd4.c` | `tr4.c` | 🔄 | `extends *.a.AbstractC*a`，含 6 个 CharSequence 字段(f/g/h/i/k/m)+AtomicBoolean+SwitchButtonV2.OnCheckedChangeListener |
| `fd4.a` | `tr4.a` | 🔄 | `interface`，唯一方法 `void a(View, c, int)` |
| `hd4.a`（基类） | `wr4.a` | 🔄 | fd4.c/tr4.c 的父类，含 AbstractC*a 抽象内部类 |
| `...settings.item.d` | `d` 已变为 Observer | 📦 | 7.2.7 中 d 不再 extends tr4.c；需搜索 settings.item 包内 extends tr4.c 的类 |
| `...settings.item.d$a` | `...settings.item.d0` | 📦 | 7.0.9: 内部类 implements fd4.a；7.2.7: 独立类，ctor(Activity)，含 `h(View, tr4.c, int)` 静态方法 |
| `uq4.q` | 未找到（包 uq4 不存在） | ❌ | 需通过调用 `UgcApiService.getCommentByBookIdRxJava` 的类重新定位 |

### 混淆方法名对比

| 方法 | 7.0.9.32 | 7.2.7 | 变化 | 定位特征 |
|---|---|---|---|---|
| SettingsActivity 列表构建1 | `b2` | `Q1` | 🔄 | `private List<tr4.c> X(recyler.c<tr4.c>)` — 按返回类型+参数类型定位 |
| SettingsActivity 列表构建2 | `c2` | `R1` | 🔄 | 同上，类中两个签名相同的 private 方法 |
| SettingsActivity 适配器getter | `X1` | `M1` | 🔄 | `static recyler.c X(SettingsActivity)` — 静态返回 recyler.c |
| 设置项点击回调 | `d$a.a` | `d0.h` | 📦 | 7.0.9: `void a(View, fd4.c, int)`；7.2.7: `void h(d0, View, tr4.c, int)` 静态方法 |
| AcctManager 单例 | `P` | `I` | 🔄 | `public static AcctManager X()` — 类中唯一 public static 返回自身类型 |
| AcctManager.M | `public boolean M()` | `private synthetic void M()` | ❌ | **完全失效**，7.2.7 中 M 改为 private synthetic void |
| ReadingTiming.g | `g(long, long)` | `g(long, long)` | ✅ | `private final void g(long, long)` — Kotlin，调用链 g→d→e |
| NpthCore.k | `k(Context, File)` | `k(Context, File)` | ✅ | `public static void k(Context, File)` |
| CrashReportConfig.b | `b()` | `b()` | ✅ | `public static final CrashReportConfig b()` — Kotlin 静态获取器 |
| uq4.q.q | `q(...)` | 未找到 | ❌ | 需通过调用 UgcApiService 的类重新定位 |

### 混淆字段名对比

| 字段 | 7.0.9.32 | 7.2.7 | 变化 | 定位特征 |
|---|---|---|---|---|
| 设置项标题 | `fd4.c.f` | `tr4.c.f` | ✅ | 类中第一个 CharSequence 且被 ctor 赋值的 public 字段 |
| SettingsActivity 适配器 | `a` | `a` | ✅ | 类中 recyler.c 类型字段 |
| PrivilegeManager 单例 | `l` | `l` | ✅ | `private static volatile` 且类型为所属类 |
| PrivilegeManager VIP列表 | `j` | `j` | ✅ | `private List<VipInfoModel>` 类型字段 |
| PrivilegeManager 当前VIP | `vipInfoModel` | `vipInfoModel` | ✅ | 未混淆，跨版本稳定 |

### Covode Index 对比

| 类 | 7.0.9.32 | 7.2.7 |
|---|---|---|
| recyler.c | 604162 | 627847 |
| fd4.c / tr4.c | 597606 | 620902 |
| settings.item.d | 581974 | — (已变) |

## 三、混淆类名映射表（7.0.9.32 基准）

| 混淆名 | 真实语义 | 特征签名（用于跨版本定位） | Covode# |
|---|---|---|---|
| `com.dragon.read.recyler.c` | RecyclerView 抽象适配器基类 | `abstract class extends RecyclerView.Adapter<AbsRecyclerViewHolder<T>>`；含 `getDataList()/setDataList()/d3()/c3()/h3()/i3()/f3()/g3()/e3()` 方法；字段 `d`=List&lt;T&gt; | 604162 |
| `fd4.c` | 设置项模型基类 | `extends hd4.a.AbstractC3106a`；字段 `f`=CharSequence(标题)、`g/h/i/k/m`=CharSequence、`o`=fd4.a(监听器)、`p`=AtomicBoolean、`q`=SwitchButtonV2.OnCheckedChangeListener、`j/l/n/r/s/u/v`=boolean、`t`=List&lt;c&gt; | 597606 |
| `fd4.a` | 设置项点击监听接口 | `interface`，唯一方法 `void a(View, c, int)` | — |
| `com.dragon.read.component.biz.impl.mine.settings.item.d` | 清理缓存设置项 | `extends fd4.c`；ctor`(Context, recyler.c)`；内部类 `a` implements `fd4.a` | 581974 |
| `com.dragon.read.component.biz.impl.mine.settings.item.d$a` | 设置项点击监听器 | 外部类 `d` 的内部类 `a`；implements `fd4.a`；方法 `a(View, fd4.c, int)` | — |
| `uq4.q` | 书籍评论管理器 | 单例 `k()`；LogHelper tag=`"BookComment"`；导入 `GetCommentByBookIdRequest/Response`、`CommentListData` | 613664 |
| `uq4.q$a` | 评论游标持有者 | 内部类；字段 `a`=String cursor | 613665 |
| `uq4.q$b` | 评论查询参数 | 内部类；字段 `a`=boolean needCount、`b`=String cursor | 613666 |

### 稳定类名（跨版本不变）

| 类名 | 用途 |
|---|---|
| `com.dragon.read.util.BookUtils` | 书籍工具（isUnsafeBook / isOverallOffShelf） |
| `com.dragon.read.component.biz.impl.mine.settings.SettingsActivity` | 设置页 Activity |
| `com.dragon.read.app.MainApplication` | 主 Application |
| `com.ss.android.update.OnUpdateStatusChangedListener` | 更新状态监听接口 |
| `com.ss.android.update.UpdateServiceImpl` | 更新服务实现 |
| `com.dragon.read.rpc.model.VipCommonSubType` | VIP 子类型枚举（Default/ShortStory/Publish/AdFree） |
| `com.dragon.read.user.model.VipInfoModel` | VIP 信息模型 |
| `com.dragon.read.user.AcctManager` | 账户管理器 |
| `com.dragon.read.component.biz.impl.privilege.PrivilegeManager` | 权限管理器 |
| `com.dragon.read.polaris.timing.ReadingTiming` | 阅读时长统计（Kotlin） |
| `com.bytedance.crash.NpthCore` | 字节崩溃上报核心 |
| `com.dragon.read.base.ssconfig.template.CrashReportConfig` | 崩溃上报配置（Kotlin data class） |
| `com.dragon.read.saas.ugc.rpc.CommentApiService` | 评论 API 服务 |
| `com.dragon.read.rpc.model.VipInfo` | VIP 信息（RPC 模型） |
| `com.dragon.read.rpc.model.VipRenewType` | VIP 续费类型枚举 |
| `com.dragon.read.rpc.model.VipProfileShow` | VIP 资料展示 |

## 四、混淆方法名映射表（7.0.9.32 基准）

| 混淆方法 | 所属类 | 签名 | 用途 | 适配要点 |
|---|---|---|---|---|
| `b2` | SettingsActivity | `private List<fd4.c> b2(recyler.c<fd4.c>)` | 构建设置页条目列表 | ⚠️ 已验证存在 |
| `c2` | SettingsActivity | `private List<fd4.c> c2(recyler.c<fd4.c>)` | 构建设置页条目列表 | ⚠️ 已验证存在 |
| `a` | fd4.a / d$a | `void a(View, fd4.c, int)` | 设置项点击回调 | 接口唯一方法 |
| `M` | AcctManager | `public boolean M()` | 未知（见下方版本漂移） | ⚠️ **见下方重要说明** |
| `P` | AcctManager | `public static AcctManager P()` | 单例获取器 | 类中唯一 public static 返回自身类型的无参方法 |
| `g` | ReadingTiming | `private final void g(long, long)` | 阅读时长上报 | Kotlin private final；调用链 g→d→e |
| `k` | NpthCore | `public static void k(Context, File)` | 崩溃监控初始化 | 接收 Context 和 File（dump 目录） |
| `b` | CrashReportConfig | `public static final CrashReportConfig b()` | 获取上报配置 | Kotlin 静态获取器；类含 a()/b() 两个 |
| `q` | uq4.q | `private Observable<BookComment> q(GetCommentByBookIdRequest, boolean, uq4.q$b, uq4.q$a)` | 获取书籍评论 | 调用 `com.dragon.community...comment.h.e(...)` |

### ⚠️ AcctManager.M() 版本漂移重要说明（7.0.9 + 7.2.7 双版本确认）

**问题**：在 7.0.9.32 中 jadx 显示 `public boolean M()`，返回值为 boolean。
但 [AcctManagerHooker.java](../../app/src/main/java/com/xxhy/fqpp/xposed/xcallback/AcctManagerHooker.java)
的 `afterHookedMethod` 把 `param.getResult()` 当作含 `userModel` 字段的对象使用：

```java
Object result = param.getResult();           // 若 M() 返回 boolean，此处为 Boolean
Object userModel = Reflect.on(result).field("userModel").get();  // 对 Boolean 取字段会抛异常
```

**7.2.7 确认**：`M()` 进一步变为 `private synthetic void M()` —— 完全不再是 hook 目标。
同时单例方法从 `P()` 改名为 `I()`。

**适配建议**：
1. 7.2.7 中应改 hook 返回用户信息对象的方法，候选：
   - `getUserInfo()` —— `public Object getUserInfo()` （返回 AcctUserModel）
   - `D()` —— `private AcctManager$AcctUserModel D()` （7.0.9 中为 G()）
   - `getVipInfo()` —— `public VipInfo getVipInfo()`
2. 或改为 hook `dispatchUpdateUserInfo()` 等用户信息更新方法。
3. 单例获取器 `P()` → `I()`，DragonUtils.java 中需同步更新。

## 五、混淆字段名映射表（7.0.9.32 基准）

| 混淆字段 | 所属类 | 类型 | 用途 | 定位特征 |
|---|---|---|---|---|
| `f` | fd4.c | CharSequence | 设置项标题 | 类中第一个 CharSequence 且被 ctor 赋值的 public 字段 |
| `a` | SettingsActivity | recyler.c | RecyclerView 适配器 | 类中 recyler.c 类型的字段 |
| `l` | PrivilegeManager | private static volatile PrivilegeManager | 单例实例 | 类中唯一的 private static volatile 本类型字段 |
| `j` | PrivilegeManager | private List&lt;VipInfoModel&gt; | VIP 信息列表 | 类中唯一的 List&lt;VipInfoModel&gt; 类型字段 |
| `vipInfoModel` | PrivilegeManager | private VipInfoModel | 当前 VIP 信息 | **未混淆**，跨版本稳定 |

## 五、稳定反射字段（跨版本不变）

以下字段名来自 data class / RPC 模型，**未混淆**，跨版本相对稳定。

### VipInfo（`com.dragon.read.rpc.model.VipInfo`）
`expireTime:String`、`isVip:String`、`isAdVip:boolean`、`isUnionVip:boolean`、
`leftTime:String`、`autoRenew:boolean`、`renewType:VipRenewType`、
`continueMonth:boolean`、`continueMonthBuy:boolean`、`subType:VipCommonSubType`、`unionSource:int`

### VipInfoModel（`com.dragon.read.user.model.VipInfoModel`）
`expireTime:String`、`isVip:String`、`isAdVip:boolean`、`isAutoCharge:boolean`、
`isUnionVip:boolean`、`leftTime:String`、`subType:VipCommonSubType`、`unionSource:int`

> VipInfoModel 构造参数顺序：
> `(String expireTime, String isVip, String leftTime, boolean isAutoCharge, boolean isUnionVip, int unionSource, boolean isAdVip, VipCommonSubType subType)`

### AcctUserModel（`com.dragon.read.user.AcctManager$AcctUserModel`，通过 result.userModel 访问）
`userName`、`description`、`adVipAvailable`、`freeAd`、`freeAdDay`、`freeAdExpire`、`freeAdLeft`、
`vipInfo`、`vipInfoList`、`vipLastExpiredTime`、`vipProfileShow`

### VipProfileShow（`com.dragon.read.rpc.model.VipProfileShow`）
`couponStrategy:String`、`useLynx:boolean`

### 枚举值
- `VipCommonSubType`：`Default`、`ShortStory`、`Publish`、`AdFree`
- `VipRenewType`：`VipRenewYear`

## 六、各 Hook 点适配清单

| 功能 | Hook 入口 | 混淆依赖 | 适配优先级 |
|---|---|---|---|
| 设置页注入 FQ++ | `HookManager.hookSettingActivity` | `b2`/`c2`/`recyler.c`/`fd4.c`/`d`/`d$a` | 高（入口可见性） |
| FQ++ 点击弹窗 | `HookManager.hookSettingItem` | `d$a`/`a`/`fd4.c.f` | 高 |
| 本地会员（账户） | `HookManager.hookAccountManager` | `M`（⚠️版本漂移）/`islogin`/`P` | **最高** |
| 本地会员（权限） | `HookManager.hookPrivilegeManager` | `j`/`l`/`vipInfoModel` | 高 |
| 阻止更新 | `HookManager.hookUpdateManager` | 无混淆（全稳定） | 低 |
| 解封书籍 | `HookManager.hookBookUtils` | 无混淆（全稳定） | 低 |
| 禁用崩溃上报 | `HookManager.hookCrashReport` | `NpthCore.k`/`CrashReportConfig.b` | 中 |
| 阅读时长翻倍 | `HookManager.hookReadingTime` | `ReadingTiming.g` | 中 |
| 评论抓取（调试） | `HookManager.hookAll` 内联 | `uq4.q.q`/`CommentApiService.getCommentListRxJava` | 低（调试用） |

## 八、跨版本适配流程

1. **获取新版 APK**，用 jadx 打开，等待反编译完成。
2. **优先核查高危项**：
   - `AcctManager.M()` 返回类型（见第三节版本漂移说明）
   - `PrivilegeManager` 字段 `l`/`j` 是否仍为单例/VIP 列表
   - `SettingsActivity` 方法 `b2`/`c2` 是否仍返回 `List<fd4.c>`
3. **按特征签名定位**：参照第二/三/四节的特征签名，在新版 jadx 中搜索。
   - 类定位：用 `search_classes_by_keyword` 按导入类名/方法名搜索
   - 方法定位：用 `get_methods_of_class` 列出方法，按参数类型匹配
   - 字段定位：用 `get_fields_of_class` 列出字段，按类型匹配
4. **更新源码常量**：修改 [HookManager.java](../../app/src/main/java/com/xxhy/fqpp/xposed/HookManager.java) 中的 `CLASS_*`/`METHOD_*` 常量。
5. **更新本映射表**：同步更新本文档与各 Hooker 类的 Javadoc 注释。

## 九、DexKit 动态查找规划（暂未实现）

后续可接入 [DexKit](https://github.com/luckydoger/DexKit) 实现运行时动态定位，替代硬编码混淆名。
基于 7.0.9 + 7.2.7 双版本对比验证，以下查询条件跨版本稳定：

### 类查找（DexKit ClassQuery）

| 目标 | 查询条件 | 跨版本稳定性 |
|---|---|---|
| recyler.c（适配器） | `superClass=RecyclerView.Adapter` + `isAbstract=true` + `methodName=getDataList` + `methodName=setDataList` | ✅ 类名+方法名均稳定 |
| fd4.c/tr4.c（设置项模型） | `fieldCount(CharSequence)>=6` + `fieldType(AtomicBoolean)=true` + `fieldType(SwitchButtonV2$OnCheckedChangeListener)=true` | ✅ 字段结构稳定，类名变 |
| fd4.a/tr4.a（点击接口） | `isInterface=true` + `methodCount=1` + `methodParamTypes=[View, *, int]` | ✅ 接口签名稳定 |
| d0（7.2.7监听器） | `methodName=h` + `methodParamTypes=[*, View, *, int]` + `constructorParamTypes=[Activity]` | 📦 7.2.7 新结构 |

### 方法查找（DexKit MethodQuery）

| 目标 | 查询条件 | 跨版本稳定性 |
|---|---|---|
| SettingsActivity 列表构建 | `returnType=List` + `paramTypes=[recyler.c]` + `isPrivate=true` | ✅ 签名稳定，方法名变 |
| ReadingTiming.g | `paramTypes=[long, long]` + `isPrivate=true` + `declaredClass=ReadingTiming` | ✅ 完全稳定 |
| NpthCore.k | `paramTypes=[Context, File]` + `isStatic=true` + `declaredClass=NpthCore` | ✅ 完全稳定 |
| CrashReportConfig.b | `returnType=CrashReportConfig` + `isStatic=true` + `paramCount=0` | ✅ 完全稳定 |
| AcctManager 单例 | `returnType=AcctManager` + `isStatic=true` + `isPublic=true` + `paramCount=0` | ✅ 签名稳定，方法名变(P→I) |

### 字段查找（DexKit FieldQuery）

| 目标 | 查询条件 | 跨版本稳定性 |
|---|---|---|
| fd4.c.f/tr4.c.f（标题） | `type=CharSequence` + `declaredClass=设置项模型类` + `fieldIndex=0` | ✅ 完全稳定 |
| SettingsActivity.a（适配器） | `type=recyler.c` + `declaredClass=SettingsActivity` | ✅ 完全稳定 |
| PrivilegeManager.l（单例） | `type=PrivilegeManager` + `isStatic=true` + `isVolatile=true` + `isPrivate=true` | ✅ 完全稳定 |
| PrivilegeManager.j（VIP列表） | `type=List<VipInfoModel>` + `declaredClass=PrivilegeManager` | ✅ 完全稳定 |

### 缓存与降级策略
1. DexKit 查询结果缓存到 SP，首次启动查找。
2. 查找失败降级到硬编码默认值（按版本号选择 7.0.9 或 7.2.7 的值）。
3. 优先用完全稳定的条件（✅），其次用签名稳定但改名的条件。

## 十、相关文件索引

- Hook 总入口：[HookManager.java](../../app/src/main/java/com/xxhy/fqpp/xposed/HookManager.java)
- Xposed 入口：[XposedEntry.java](../../app/src/main/java/com/xxhy/fqpp/xposed/XposedEntry.java)
- Hooker 实现：[xcallback/](../../app/src/main/java/com/xxhy/fqpp/xposed/xcallback/)
- 反射工具：[DragonUtils.java](../../app/src/main/java/com/xxhy/fqpp/xposed/utils/DragonUtils.java)
- 反编译工程：`dec/番茄免费小说_7.0.9.32.apk.jadx`
- 其他分析文档：[dec/docs/](./)
