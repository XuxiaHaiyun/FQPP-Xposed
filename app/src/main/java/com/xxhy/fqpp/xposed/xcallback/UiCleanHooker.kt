package com.xxhy.fqpp.xposed.xcallback

import android.view.View
import android.view.ViewGroup
import com.xxhy.fqpp.xposed.utils.LogUtils
import io.github.libxposed.api.XposedInterface

/**
 * 界面净化 Hook 集合（7.0.9.32 反编译验证）。
 *
 * <p>覆盖目标（全部来自 7.0.9.32 jadx 定位）：
 * <ul>
 *   <li>主页搜索框轮播提示：{@code com.dragon.read.kmp.bookmall.search.SearchWordDisplayViewKMP}（d/e/h 列表置空）；</li>
 *   <li>全屏福利广告：{@code bd2.c}（IActivityScreenAdManager 实现，拦截展示）；</li>
 *   <li>我的页会员特权卡：{@code NsVipImpl.willShowNativeBanner/canShowVipCenter}→false；</li>
 *   <li>功能区角标：{@code i44.c.f}→false（false 时不画红色角标）；</li>
 *   <li>侧滑"常玩游戏"：{@code e44.c.a}（汇总自 IBsMineTabCellService.hideMiniGame）→true；</li>
 *   <li>底部 TAB 角标：{@code bf4.c0} 展示总闸 + 数据端全链拦截；</li>
 *   <li>搜索页热搜榜/AI 板块：{@code help.z}（S/I/M/R/T 构造层返回 null，列表自然跳过）；</li>
 *   <li>搜索页 AI 横幅/搜索框/FAB：{@code SearchActivity.G3/H3/I3}→false + 两个模板类静态工厂返回默认实例；</li>
 * </ul>
 *
 * <p>激励秒领已拆分为独立类 {@link InstantRewardHooker}（本类不再包含其 hook）。
 *
 * <p>所有目标方法名均为 7.0.9.32 的最终混淆名，与模块声明版本绑定；
 * 任何 hook 失败均单独捕获并日志输出，不影响其余 hook。
 */
object UiCleanHooker {

    /** 日志场景 TAG：界面净化（全类统一，便于浮窗过滤）。 */
    private const val TAG = "UiClean"

    /** DexKit 定位类名（7.0.9.32 常量兜底）：无定位结果时回退嵌入的混淆名。 */
    private fun resolve(targetId: String, fallback: String): String =
        com.xxhy.fqpp.xposed.HookManager.getResolvedClassName(targetId, fallback)

    // ===== 界面净化 =====

    /** 主页搜索框轮播提示：清空词列表（d/e/h 三个公开 setter，均接收 List<dz3.a>）。 */
    private fun hookSearchWordHint() {
        val clazz = "com.dragon.read.kmp.bookmall.search.SearchWordDisplayViewKMP"
        val listParam = arrayOf("java.util.List")
        for (name in arrayOf("d", "e", "h")) {
            HookSupport.safeHook(TAG, clazz, name, listParam, extraLog = "搜索框轮播提示") { chain ->
                val args = chain.getArgs().toTypedArray()
                if (args.isNotEmpty() && args[0] is List<*>) {
                    args[0] = java.util.ArrayList<Any>()
                }
                chain.proceed(args)
            }
        }
    }

    /** 全屏福利广告：bd2.c 直接短路展示/查询 + 根闸口 canShowScreenAd 全链拦截。 */
    private fun hookScreenAdManager() {
        val clazz = resolve(com.xxhy.fqpp.xposed.locate.DragonTargets.SCREEN_AD_MANAGER.id, "bd2.c")
        HookSupport.safeHook(TAG, clazz, "b") { false } // 是否应展示福利弹窗
        HookSupport.safeHook(TAG, clazz, "c") { null }   // 触发展示
        HookSupport.safeHook(TAG, clazz, "onScreenAdDialogShow") { false }
        // 根闸口：bd2.c.i(Object) 先调 NsUtilsDepend.IMPL.canShowScreenAd(obj) 再弹窗，
        // 7.0.9.32 实证 NsUtilsDependImpl.canShowScreenAd → ScreenAdManager.canShowScreenAd。
        // 钩这两层即可拦截全部全屏广告（含"赚钱"页/阅读页弹窗），旧 b/c 钩仅作兜底。
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.msg.ScreenAdManager",
            "canShowScreenAd",
            arrayOf("java.lang.Object"),
            extraLog = "全屏广告(根闸canShowScreenAd)",
        ) { false }
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.NsUtilsDependImpl",
            "canShowScreenAd",
            arrayOf("java.lang.Object"),
            extraLog = "全屏广告(NsUtilsDependImpl闸)",
        ) { false }
    }

    /** 我的页会员特权卡（原生 Banner）+ 会员中心入口。
     *  经 7.0.9.32 反编译验证（FanqieMineFragmentV2.oh）：
     *  卡片根视图 F 仅在 isDisableVipInGoogle()==true 或非会员且无入口时隐藏。
     *  <b>注意</b>：willShowNativeBanner/canShowVipCenter/willShowLynxBanner/canShowMulVip
     *  由"本地会员"（LOCAL_VIP）hook 返回 true 控制展示，界面净化<b>不要</b>动它们。
     *  这里只做：isDisableVipInGoogle→true（整卡隐藏开关）+ oh() 构建跳过。 */
    private fun hookVipEntrances() {
        val clazz = "com.dragon.read.component.biz.impl.NsVipImpl"
        // 官方"Google 地区禁用会员"开关：强制 true 使 oh() 直接隐藏整张会员卡
        HookSupport.safeHook(TAG, clazz, "isDisableVipInGoogle") { true }
        // 主路径：hook FanqieMineFragmentV2.oh()（VIP 卡片构建，7.0.9.32 实证）直接跳过
        // ——不受本地会员（willShowNativeBanner 等→true 使卡片展示）冲突影响，vipCard 开启即彻底隐藏
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.mine.FanqieMineFragmentV2",
            "oh",
            extraLog = "会员卡(跳过构建oh)",
        ) { null }
    }

    /** 我的功能区 + 右侧滑栏共用角标（红点/角标）。
     *
     *  <ul>
     *    <li>{@code i44.c}（i44.SidebarFuncItemData）唯一 boolean 字段 {@code f258411f} 在
     *        数据类 {@code toString()} 中是 {@code showRedDot}，{@code f()} 原样返回；</li>
     *    <li>Compose 绘制端（kmp.mine.sidebar.u0.w）为
     *        {@code if (!funcItemData.f()) { 绘制红底圆形角标 }}，即 f()==true 时<b>显示</b>红点；</li>
     *    <li>因此必须返回 <b>false</b>（旧代码 true 被实机确认：滑栏功能项全部出现红点）。</li>
     *  </ul>
     *  双保险：构造器强制 showRedDot=false + 读方法返回 false。
     *
     *  <p>我的页功能区（商城/浏览历史/我的消息/成为作家/我的预约）角标渲染器
     *  {@code h53.c.k2()} 走 {@code MineRedDotManager.n().l(areEqual(cqy, data.f253118j))}，
     *  l/m 返回 true 表示"显示角标"（默认 true），强制 false 即全部不画。
     *  7.0.9.32 已验证 MineRedDotManager.l(boolean)/m(String)、NsMineImpl.enableShowRedDot(String)。
     */
    private fun hookFunctionBadge() {
        val clazz = resolve(com.xxhy.fqpp.xposed.locate.DragonTargets.SIDEBAR_FUNC_ITEM.id, "i44.c")
        HookSupport.safeHookCtor(
            TAG,
            clazz,
            arrayOf("int", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String", "boolean"),
            extraLog = "功能区角标(构造器置false)",
        ) { chain ->
            val args = chain.getArgs().toTypedArray()
            if (args.size >= 6 && args[5] is Boolean) {
                args[5] = false
            }
            chain.proceed(args)
        }
        HookSupport.safeHook(TAG, clazz, "f") { false }
        // 我的页功能区角标（h53.c.k2 数据闸）：l(boolean)/m(String) 全返回 false
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.mine.reddot.MineRedDotManager",
            "l",
            arrayOf("boolean"),
            extraLog = "我的功能区角标(l闸)",
        ) { false }
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.mine.reddot.MineRedDotManager",
            "m",
            arrayOf("java.lang.String"),
            extraLog = "我的功能区角标(m闸)",
        ) { false }
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.NsMineImpl",
            "enableShowRedDot",
            arrayOf("java.lang.String"),
            extraLog = "我的功能区角标(NsMineImpl闸)",
        ) { false }
    }

    /** 侧滑"常玩游戏"区块隐藏。 */
    private fun hookMiniGame() {
        val clazz = resolve(com.xxhy.fqpp.xposed.locate.DragonTargets.MINI_GAME_SERVICE.id, "e44.c")
        HookSupport.safeHook(TAG, clazz, "a") { true }
    }

    /** 底部 TAB 角标（PolarisTabBadgeHelper 全链路拦截）。
     *
     *  <p>7.0.9.32 反编译验证（bf4.c0 = c0 类，单例 f18120a）：
     *  <ul>
     *    <li>数据落地：服务器回包 → {@code h0(String, GetTabBubbleResp)}（static，List 存于字段 f18123d）；</li>
     *    <li>本地缓存：{@code c0(GetTabBubbleResult)} 过滤 + 静态块从 SP 恢复 f18123d；</li>
     *    <li>展示总入口：{@code F0(db5.h, String)} → E0/x0 逐条遍历 → {@code G0} 决策；
     *        真正绘制为 {@code d3.E0(true, taskKey, bubble)}（红点）/ C0/D0（文字角标）。</li>
     *  </ul>
     *  因此用一个"展示总闸"（F0 短路）+ 两个数据端（c0 缓存恢复清零、h0 网络落地清零）即
     *  彻底禁止所有 TAB 红点/文字角标；E0/x0/G0/V/T 保留多重兜底。
     */
    private fun hookTabBadge() {
        val badgeClazz = resolve(com.xxhy.fqpp.xposed.locate.DragonTargets.TAB_BADGE_CONTROLLER.id, "bf4.c0")
        val str = arrayOf("java.lang.String")
        // 数据端 1：缓存恢复（static init 也从这里走）——清零传入的 bubbleList
        HookSupport.safeHook(
            TAG, badgeClazz, "c0", arrayOf("com.dragon.read.model.GetTabBubbleResult"),
            extraLog = "TAB角标(缓存清零)",
        ) { chain ->
            val args = chain.getArgs().toTypedArray()
            if (args.isNotEmpty()) {
                val r = args[0]
                val f = runCatching { r.javaClass.getDeclaredField("bubbleList") }.getOrNull()
                if (f != null) {
                    f.isAccessible = true
                    f.set(r, java.util.Collections.emptyList<Any>())
                }
            }
            chain.proceed(args)
        }
        // 数据端 2：服务器回包落地 → 直接丢弃
        HookSupport.safeHook(
            TAG, badgeClazz, "h0",
            arrayOf("java.lang.String", "com.dragon.read.model.GetTabBubbleResp"),
            extraLog = "TAB角标(回包丢弃)",
        ) { null }
        // 展示总闸：F0 短路（含冷启动 cold_start 与 updatePolarisTabBadge 两条入口）
        HookSupport.safeHook(
            TAG, badgeClazz, "F0", arrayOf("db5.h", "java.lang.String"),
            extraLog = "TAB角标(总闸F0)",
        ) { null }
        // 遍历/dot决策兜底
        HookSupport.safeHook(TAG, badgeClazz, "g0", str) { null }
        HookSupport.safeHook(TAG, badgeClazz, "l0", str) { null }
        HookSupport.safeHook(
            TAG, badgeClazz, "G0",
            arrayOf("android.app.Activity", "th4.d3", "com.dragon.read.model.TabBubble"),
            extraLog = "TAB角标(单点否决G0)",
        ) { false }
        // 旧语义兜底：T 为单个气泡"可展示"判定
        HookSupport.safeHook(TAG, badgeClazz, "T", arrayOf("com.dragon.read.model.TabBubble"), extraLog = "TAB角标(T判定)") { false }
    }

    /**
     * 我的页"小说推荐"feed 隐藏（场景闸，仅目标场景生效）。
     *
     * <p>7.0.9.32 反编译实证（完整链路）：
     * <ul>
     *   <li>我的页 P3 = 原生 {@code StaggeredFeedLayout}（iw2.k.C → new StaggeredFeedLayout），
     *       scene 配置 f134378a = FanqieMineFragmentV2$a（j63.b.getScene()=FeedScene.MINE）；</li>
     *   <li>数据路径：{@code P3.dataService().g(a0,z)} → dataService() 返回 this →
     *       {@code g(a0,z)}（gt3.k 主入口）→ {@code q(a0,z)} 发请求 → 响应 → {@code I0} 下发
     *       （dispatchDataUpdate 更新 adapter）；另有外部 b/e 直传 List 入口；</li>
     *   <li>历史卡经 {@code k()}→{@code A4()} 独立插入，不经过上述数据路径。</li>
     * </ul>
     * <b>重写要点</b>：开关（mineFeed）<b>单次挂载</b>（hookedMethods 幂等去重），
     * targets 为 MINE；拦截 g/q/b/e/I0 五条路径 + KMP 版 b/F；
     * scene 判定三级兜底（字段名 → gt3.r 类型 → 宿主类名）。
     */
    private fun installFeedSceneGate() {
        val options = com.xxhy.fqpp.xposed.config.ConfigManager.getUiCleanOptions()
feedTargets = buildSet {
            if (options.mineFeed) add("MINE")
            // 注意：书城 tab（含榜单等）不在此场景闸拦截，避免误伤。
        }
        if (feedTargets.isEmpty()) return
        // 我的页"加载中"（view id=k5，布局 fez）隐藏
        if (options.mineFeed) {
            blockK5 = true
            hookViewIdHider()
        }
        val clazz = "com.dragon.read.component.biz.impl.bookmall.holder.staggeredinfinite.container.StaggeredFeedLayout"
        val listParam = arrayOf("java.util.List")
        // g(a0,z)：V2 主数据入口（P3.dataService().g → this.g）
        hookByName(clazz, "g", 2, "g主入口") { c -> feedSceneGate(c as XposedInterface.Chain, "g") }
        // q(a0,z)：请求发起（g 内部），拦截更早
        hookByName(clazz, "q", 2, "q请求") { c -> feedSceneGate(c as XposedInterface.Chain, "q") }
        // b/e：外部直接传 List 的数据入口
        HookSupport.safeHook(TAG, clazz, "b", listParam, extraLog = "推荐feed(场景闸b)") { chain ->
            feedSceneGate(chain, "b")
        }
        HookSupport.safeHook(TAG, clazz, "e", listParam, extraLog = "推荐feed(场景闸e)") { chain ->
            feedSceneGate(chain, "e")
        }
        // 响应数据下发核心（原 I0）：实机方法名非 "I0"（jadx 反编译名），
        // 按特征查找：static + 5 参 + 第一个参数类型为 StaggeredFeedLayout
        hookFeedResponseGate(clazz)
        // KMP 版（书城 KMP 页等）
        installKmpFeedSceneGate()
        LogUtils.log(TAG, "推荐feed场景闸已安装 (targets=$feedTargets)")
    }


    /** 按特征定位请求响应数据下发核心（static、5~6 参、第一参即 StaggeredFeedLayout）。 */
    private fun hookFeedResponseGate(clazz: String) {
        try {
            val cls = HookSupport.dragonLoader().loadClass(clazz)
            val method = cls.declaredMethods.firstOrNull {
                java.lang.reflect.Modifier.isStatic(it.modifiers) &&
                    it.parameterCount in 5..6 && it.parameterTypes[0] == cls
            }
            if (method == null) {
                LogUtils.logWarn(TAG, "推荐feed(响应下发) 未找到: $clazz static 5/6参")
            } else if (HookSupport.hookedMethods.add(method)) {
                com.xxhy.fqpp.xposed.global.XposedGlobals.getXposedEntryInstance()
                    .hook(method).intercept { c ->
                        feedSceneGate(c as XposedInterface.Chain, "resp")
                    }
                LogUtils.log(TAG, "已挂钩 推荐feed(响应下发) $clazz.${method.name}")
            }
        } catch (t: Throwable) {
            LogUtils.logWarn(TAG, "响应下发 hook 失败: ${t.message}")
        }
    }

    /** 目标场景（mineFeed → MINE）。 */
    private var feedTargets: Set<String> = emptySet()

    /** 按方法名+参数个数动态查找并 hook（规避混淆参数类型不可预知）。 */
    private fun hookByName(
        clazz: String,
        name: String,
        paramCount: Int,
        desc: String,
        interceptor: (XposedInterface.Chain) -> Any?,
    ) {
        try {
            val cls = HookSupport.dragonLoader().loadClass(clazz)
            val method = cls.declaredMethods.firstOrNull { it.name == name && it.parameterCount == paramCount }
            if (method == null) {
                LogUtils.logWarn(TAG, "推荐feed($desc) 方法未找到: $clazz.$name(${paramCount}参)")
            } else if (HookSupport.hookedMethods.add(method)) {
                com.xxhy.fqpp.xposed.global.XposedGlobals.getXposedEntryInstance()
                    .hook(method).intercept(interceptor)
                LogUtils.log(TAG, "已挂钩 推荐feed($desc) $clazz.$name")
            }
        } catch (t: Throwable) {
            LogUtils.logWarn(TAG, "推荐feed($desc) hook 失败: ${t.message}")
        }
    }

    /** KMP 版场景闸：hook StaggeredFeedLayoutViewModel.b/F（书城 KMP 页等，单次挂载）。 */
    private fun installKmpFeedSceneGate() {
        val clazz = "com.dragon.read.kmp.common_feed.staggeredfeed.StaggeredFeedLayoutViewModel"
        // b(List<? extends m24.b>)：数据更新入口（实例方法）
        HookSupport.safeHook(TAG, clazz, "b", arrayOf("java.util.List"), extraLog = "推荐feed(KMP场景闸b)") { chain ->
            kmpFeedSceneGate(chain, "b")
        }
        // F(StaggeredFeedLayoutViewModel, o24.b, boolean, gt3.z, o24.d)：loadData 成功回调（static，5 参）
        hookByName(clazz, "F", 5, "KMP-F下发") { c -> kmpFeedSceneGate(c as XposedInterface.Chain, "F") }
    }

    /** KMP 场景闸：scene 命中时跳过数据更新并收掉 loading，其余放行。 */
    private fun kmpFeedSceneGate(chain: XposedInterface.Chain, entry: String): Any? {
        return try {
            val args = chain.getArgs().toTypedArray()
            // b：getThisObject 是 ViewModel；F：static，args[0] 是 ViewModel
            val vm = chain.getThisObject() ?: args.getOrNull(0)
            if (vm == null) return chain.proceed()
            val scene = kmpScene(vm)
            val hit = scene in feedTargets
            if (feedGateLogCount++ < 20) {
                LogUtils.log(TAG, "推荐feed(KMP) 场景判定 ($entry, scene='$scene', hit=$hit, vm=${vm.javaClass.simpleName})")
            }
            if (hit) {
                // 收尾：显示内容（隐藏 loading/error），列表保持空（推荐不下发）
                runCatching { vm.javaClass.getMethod("P").invoke(vm) }
                null
            } else {
                chain.proceed()
            }
        } catch (t: Throwable) {
            chain.proceed()
        }
    }

    /** KMP ViewModel 的 scene：f162764a（k24.j depend）→ getScene() → FeedScene.name。 */
    private fun kmpScene(vm: Any): String {
        val depend = runCatching {
            vm.javaClass.getDeclaredField("f162764a").apply { isAccessible = true }.get(vm)
        }.getOrNull() ?: return ""
        val scene = runCatching {
            depend.javaClass.getMethod("getScene").invoke(depend)
        }.getOrNull() ?: return ""
        return (scene as? Enum<*>)?.name ?: ""
    }

    /** 场景配置字段 f134378a（gt3.r）的反射缓存。 */
    private var sceneCfgField: java.lang.reflect.Field? = null
    /** getScene() Method 缓存（按实现类缓存，避免多实现类误用）。 */
    private var sceneGetter: java.lang.reflect.Method? = null
    private var sceneGetterOwner = ""
    /** a1() showContent 缓存。 */
    private var showContentMethod: java.lang.reflect.Method? = null
    private var feedGateLogCount = 0

    /** 场景闸：命中合并目标场景时跳过推荐列表下发并收掉 loading/error，其余放行。 */
    private fun feedSceneGate(chain: XposedInterface.Chain, entry: String): Any? {
        return try {
            val args = chain.getArgs().toTypedArray()
            // 实例方法（g/q/b/e）用 getThisObject；static 方法（I0）第一参数即 StaggeredFeedLayout 实例
            val layout = chain.getThisObject() ?: args.getOrNull(0)
            if (layout == null) return chain.proceed()
            val scene = currentFeedScene(layout)
            val hit = scene in feedTargets
            if (feedGateLogCount++ < 20) {
                LogUtils.log(TAG, "推荐feed 场景判定 ($entry, scene='$scene', hit=$hit, layout=${layout.javaClass.simpleName})")
            }
            if (hit) {
                showFeedContent(layout)
                // 延迟重复隐藏 loading（覆盖拦截后仍显示的"加载中"字样）
                if (layout is View) {
                    val handler = layout.handler
                    if (handler != null) {
                        for (delay in longArrayOf(300, 1000, 2000)) {
                            handler.postDelayed({ runCatching { showFeedContent(layout) } }, delay)
                        }
                    }
                }
                null
            } else {
                chain.proceed()
            }
        } catch (t: Throwable) {
            chain.proceed()
        }
    }

    private fun currentFeedScene(layout: Any): String {
        var f = sceneCfgField
        if (f == null) {
            // 字段名 f134378a 优先；找不到则按 gt3.r 接口类型匹配（防 jadx/混淆差异）
            f = runCatching { layout.javaClass.getDeclaredField("f134378a") }.getOrNull()
                ?: layout.javaClass.declaredFields.firstOrNull { isGt3r(it.type) }
            f?.isAccessible = true
            sceneCfgField = f
        }
        val cfg = f?.get(layout) ?: return hostSceneFallback(layout)
        var getter = sceneGetter
        val owner = cfg.javaClass.name
        if (getter == null || sceneGetterOwner != owner) {
            getter = runCatching { cfg.javaClass.getMethod("getScene") }.getOrNull()
            getter?.isAccessible = true
            sceneGetter = getter
            sceneGetterOwner = owner
        }
        val scene = getter?.invoke(cfg)
        val name = (scene as? Enum<*>)?.name
        if (!name.isNullOrEmpty()) return name
        return hostSceneFallback(layout)
    }

    /** gt3.r 接口类型判定（含包名后缀 ".r" 且为接口）。 */
    private fun isGt3r(type: Class<*>): Boolean {
        return type.name == "gt3.r" || (type.name.endsWith(".r") && type.isInterface)
    }

    /** 宿主兜底：scene 配置对象的类名含 Mine/BookMall 特征时直接判定场景。 */
    private fun hostSceneFallback(layout: Any): String {
        val clsName = runCatching {
            val f = layout.javaClass.declaredFields.firstOrNull { isGt3r(it.type) }
            f?.apply { isAccessible = true }?.get(layout)?.javaClass?.name
        }.getOrNull() ?: ""
        return when {
            clsName.contains("FanqieMineFragment") -> "MINE"
            else -> ""
        }
    }

    private fun showFeedContent(layout: Any) {
        var m = showContentMethod
        if (m == null) {
            m = layout.javaClass.getMethod("a1")
            m.isAccessible = true
            showContentMethod = m
        }
        m.invoke(layout)
    }

    /**
     * 隐藏主页"赚钱"/"短剧"导航栏（底部 TAB）。
     *
     * <p>7.0.9.32 反编译实证：
     * <ul>
     *   <li>底部 tab 数据源为 {@code NsCommonDependImpl.getMainTabBarItems()}
     *       （List&lt;BottomTabBarItemType&gt;）与 {@code getBottomTabBarItemDataList()}
     *       （List&lt;BottomTabBarItemData&gt;，含 tabType 字段）；db5.i（tab ViewHolder）、
     *       k63.a、th4.d3 等渲染处均读取该数据源；</li>
     *   <li>BottomTabBarItemType 枚举：LuckyBenefit(2)="赚钱"（幸运福利）、
     *       VideoSeriesFeedTab(7)="短剧"（剧集 feed），从数据源过滤即可隐藏对应 tab；</li>
     *   <li>渲染端 {@code MainFragmentActivity.f5()} 遍历 {@code u2.f176199a.z()} 的 tab 列表，
     *       switch 中为 tab 项 View 动态 setId：赚钱→{@code R.id.aot}、短剧→{@code R.id.aou}，
     *       随后 {@code BottomTabBarLayout.O1(db5.h)} 添加。故再补一层 view 兜底：
     *       O1 添加后按 id 隐藏，双保险。</li>
     * </ul>
     */
    private fun hookHomeTabBar() {
        val options = com.xxhy.fqpp.xposed.config.ConfigManager.getUiCleanOptions()
        val blockEarn = options.homeEarnTab
        val blockSeries = options.homeSeriesTab
        if (!blockEarn && !blockSeries) return
        val clazz = "com.dragon.read.component.NsCommonDependImpl"
        HookSupport.safeHook(
            TAG, clazz, "getMainTabBarItems",
            extraLog = "主页导航栏(类型过滤)",
        ) { chain ->
            val result = chain.proceed()
            if (result is List<*>) filterTabTypes(result, blockEarn, blockSeries) else result
        }
        HookSupport.safeHook(
            TAG, clazz, "getBottomTabBarItemDataList",
            extraLog = "主页导航栏(数据过滤)",
        ) { chain ->
            val result = chain.proceed()
            if (result is List<*>) filterTabData(result, blockEarn, blockSeries) else result
        }
        // view 层兜底：渲染添加 tab 项后按 id（aot=赚钱/aou=短剧）隐藏
        HookSupport.safeHook(
            TAG, "com.dragon.read.widget.BottomTabBarLayout", "O1", arrayOf("db5.h"),
            extraLog = "主页导航栏(view层隐藏)",
        ) { chain ->
            val r = chain.proceed()
            val holder = chain.getArgs().toTypedArray().getOrNull(0)
            hideTabViewIfNeeded(holder, blockEarn, blockSeries)
            r
        }
        // 渲染数据源：MainFragmentActivity.f5() 遍历 u2.f176199a.z()（List<BottomTabBarItemType>）
        // 与 u2.f176199a.m()（List<BottomTabBarItemData>）创建 tab——这才是底部导航栏真正来源
        HookSupport.safeHook(
            TAG, "com.dragon.read.pages.main.u2", "z",
            extraLog = "主页导航栏(渲染源z过滤)",
        ) { chain ->
            val result = chain.proceed()
            if (result is List<*>) filterTabTypes(result, blockEarn, blockSeries) else result
        }
        HookSupport.safeHook(
            TAG, "com.dragon.read.pages.main.u2", "m",
            extraLog = "主页导航栏(渲染源m过滤)",
        ) { chain ->
            val result = chain.proceed()
            if (result is List<*>) filterTabData(result, blockEarn, blockSeries) else result
        }
    }

    /** 赚钱 tab 项 view id（R.id.aot）缓存。 */
    private var tabAotId = 0
    /** 短剧 tab 项 view id（R.id.aou）缓存。 */
    private var tabAouId = 0

    /** 按 id 隐藏赚钱/短剧 tab 项视图（O1 添加后调用）。 */
    private fun hideTabViewIfNeeded(holder: Any?, blockEarn: Boolean, blockSeries: Boolean) {
        if (holder == null) return
        val view = runCatching {
            holder.javaClass.getMethod("getView").invoke(holder) as? View
        }.getOrNull() ?: return
        if (tabAotId == 0) {
            tabAotId = runCatching { view.resources.getIdentifier("aot", "id", "com.dragon.read") }.getOrDefault(0)
        }
        if (tabAouId == 0) {
            tabAouId = runCatching { view.resources.getIdentifier("aou", "id", "com.dragon.read") }.getOrDefault(0)
        }
        val hide = (blockEarn && tabAotId != 0 && view.id == tabAotId) ||
            (blockSeries && tabAouId != 0 && view.id == tabAouId)
        if (hide && view.visibility != View.GONE) {
            view.visibility = View.GONE
        }
    }

    /** 过滤 BottomTabBarItemType 列表：排除赚钱(LuckyBenefit)/短剧(VideoSeriesFeedTab)。 */
    private fun filterTabTypes(list: List<*>, blockEarn: Boolean, blockSeries: Boolean): List<*> {
        return list.filter { item ->
            val name = (item as? Enum<*>)?.name ?: return@filter true
            !(blockEarn && name == "LuckyBenefit") && !(blockSeries && name == "VideoSeriesFeedTab")
        }
    }

    /** 过滤 BottomTabBarItemData 列表：按 tabType 字段排除。 */
    private fun filterTabData(list: List<*>, blockEarn: Boolean, blockSeries: Boolean): List<*> {
        return list.filter { item ->
            val tabType = runCatching {
                item?.javaClass?.getField("tabType")?.get(item)
            }.getOrNull()
            val name = (tabType as? Enum<*>)?.name ?: return@filter true
            !(blockEarn && name == "LuckyBenefit") && !(blockSeries && name == "VideoSeriesFeedTab")
        }
    }

    /**
     * 隐藏"发表帖子/话题/书单"按钮（编辑器发布入口，书城页/我的页显示）。
     *
     * <p>7.0.9.32 反编译实证：{@code com.dragon.read.component.biz.impl.bookmall.videoseriespost.entrance.m}
     * （FrameLayout）即发布入口组件，构造时 inflate R.layout.bxu 并
     * {@code findViewById(R.id.idd)} 得到 triggerLayout（发布按钮本体）。
     * 双保险：onAttachedToWindow 后自身 GONE + {@code isShown()} 强制 false
     * （m 已 override isShown 由数据驱动，hook 返回 false 后 View 树判定不绘制）。
     */
    private fun hookPublishButton() {
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.bookmall.videoseriespost.entrance.m",
            "onAttachedToWindow",
            extraLog = "发表按钮隐藏(attach后GONE)",
        ) { chain ->
            val r = chain.proceed()
            runCatching {
                val v = chain.getThisObject() as? View
                if (v != null && v.visibility != View.GONE) {
                    v.visibility = View.GONE
                }
            }
            r
        }
        // isShown() 强制 false：View 树 dispatchDraw 跳过该视图（更可靠，防后续 setVisibility 复位）
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.bookmall.videoseriespost.entrance.m",
            "isShown",
            extraLog = "发表按钮隐藏(isShown=false)",
        ) { false }
        // view id 兜底：idd/ide/ok（用户 uiautomator 实测），限主页（书城/我的页）
        blockPublish = true
        hookViewIdHider()
    }

    /**
     * 隐藏主页"点击赚钱"挂件。
     *
     * <p>7.0.9.32 实证（用户提供创建调用堆栈）：
     * {@code PlanPendantServiceAdapter.dealPlanData → v() → d.b → new InnerPendantView}、
     * {@code manager.b$b.run() → new widget.b}（widget.b/InnerPendantView 为挂件根视图，
     * extends ConstraintLayout，内含"点击赚钱"文本、Lottie 动画与关闭按钮 jvm）。
     * 拦截：
     * 1) 创建入口 {@code manager.b$b.run()} / {@code PlanPendantServiceAdapter.v()} 跳过；
     * 2) widget.b / InnerPendantView / GoldCoinBoxTextView 的 onAttachedToWindow 是
     *    <b>继承方法</b>（getDeclaredMethod 找不到，日志报 hook 失败），改由
     *    hookViewIdHider 按类名判断 GONE（blockPendant，仅主页）。
     */
    private fun hookMineEarnPendant() {
        // 1) 创建入口 A：manager.b$b.run()（用户第二次堆栈实证：b$b.run → new widget.b）
        hookByName(
            "com.bytedance.ug.sdk.novel.pendant.manager.b\$b",
            "run",
            0,
            "点击赚钱挂件(创建入口b\$b)",
        ) { null }
        // 1b) 创建入口 B：PlanPendantServiceAdapter.v()——7.0.9.32 实机已无此方法
        //     （log6 实证"方法未找到"），b$b.run 已覆盖创建入口，不再尝试
        // 2) 视图层兜底：widget.b/InnerPendantView/GoldCoinBoxTextView 按类名 GONE
        //    （onAttachedToWindow 为 View 继承方法，统一在 hookViewIdHider 按类判断）
        blockPendant = true
        hookViewIdHider()
    }

    /**
     * 隐藏我的页"金币信息"板块（金币余额/现金余额/提现）。
     *
     * <p>7.0.9.32 反编译实证：{@code VariantMineFragment}（我的页<b>原生变体</b>，未启用）中
     * {@code findViewById(R.id.f108400ez1)} 即该板块视图，{@code qg()} 显示、{@code wf()} 隐藏；
     * 而实机"我的页"由 {@code FanqieMineFragmentProvider.create()} 创建
     * {@code FanqieMineFragmentV2}（KMP Compose 渲染），qg() 不会被调用。
     * 因此除保留 qg() 拦截外，另按 view id（ez1，用户 uiautomator 实测）在
     * onAttachedToWindow 时精确 GONE，覆盖 V2 渲染路径。
     */
    private fun hookMineAsset() {
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.mine.VariantMineFragment",
            "qg",
            extraLog = "我的页金币信息(跳过显示)",
        ) { null }
        blockEz1 = true
        hookViewIdHider()
    }

    /**
     * 隐藏我的页"浏览历史外显"。
     *
     * <p>7.0.9.32 反编译实证：历史外显共四条链路，全部拦截：
     * <ul>
     *   <li>SSO 总闸：{@code o53.h0.r()}（private final boolean）= "我的tab浏览历史外露"条件
     *       （读 MyTabExposuredHistory/MyTabExposuredHistoryV597New 的 AB 配置），强制 false
     *       即外露条（view m0，高 50dp）永不出现；</li>
     *   <li>服务端卡片：{@code BasicExperienceCardProvider.parseModel(CellViewData)} 在
     *       groupIdType == CandidateDataType.ReadHistory 时产出 StaggeredHistoryModel 等卡，
     *       拦截返回空列表即不渲染；</li>
     *   <li>本地插入：{@code FanqieMineFragmentV2.handleInsertHistoryCard(o53.z)} 内
       *       {@code this.P3.k(zVar.a(), 0)} 把历史卡插入 feed 位置 0，直接跳过；</li>
     *   <li>兜底渲染：MineHistoryCard（外部展示卡）V(boolean)/构造器根 GONE + view id 隐藏。</li>
     * </ul>
     */
    private fun hookMineHistoryCard() {
        // ① SSO 外显总闸：o53.h0.r() = false（"我的tab浏览历史外露"条不复存在）
        // 注意：o53 是包名，类名是 h0（反编译包结构 o53/h0），二进制名= "o53.h0"
        HookSupport.safeHook(
            TAG,
            "o53.h0",
            "r",
            emptyArray(),
            extraLog = "我的页浏览历史(外露条总闸)",
        ) { false }
        // ② 服务端历史卡：BasicExperienceCardProvider.parseModel 对 ReadHistory 返回空
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.mine.highfreq.history.BasicExperienceCardProvider",
            "parseModel",
            arrayOf("com.dragon.read.rpc.model.CellViewData"),
            extraLog = "我的页浏览历史(服务端卡拦截)",
        ) { chain ->
            val args = chain.getArgs().toTypedArray()
            val cell = args.getOrNull(0)
            if (cell != null) {
                val gid = runCatching {
                    // 字段 public CellViewData.groupIdType，直接用字段读（getter 名为防混淆，不依赖）
                    cell.javaClass.getField("groupIdType").get(cell)
                }.getOrNull()
                if (gid is Enum<*> && gid.name == "ReadHistory") {
                    return@safeHook java.util.Collections.emptyList<Any>()
                }
            }
            chain.proceed(args)
        }
        // 本地插入路径：handleInsertHistoryCard 无参跳过（事件类 o53.z = 包.类，非内部类）
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.mine.FanqieMineFragmentV2",
            "handleInsertHistoryCard",
            arrayOf("o53.z"),
            extraLog = "我的页浏览历史(跳过插入)",
        ) { null }
        // 底层插入路径：k(Object,int)，仅我的页(MINE)跳过
        hookByName(
            "com.dragon.read.component.biz.impl.bookmall.holder.staggeredinfinite.container.StaggeredFeedLayout",
            "k",
            2,
            "历史卡插入(k)",
        ) { c ->
            val chain = c as XposedInterface.Chain
            try {
                val layout = chain.getThisObject()
                if (layout != null && currentFeedScene(layout) == "MINE") null else chain.proceed()
            } catch (t: Throwable) {
                chain.proceed()
            }
        }
        // 主渲染：MineHistoryCard（"浏览历史"外部展示卡，埋点 module_name="浏览历史"、
        // is_outside_show）——V(boolean) true 时显示卡片；强制 false 不显示
        HookSupport.safeHook(
            TAG,
            "com.dragon.read.component.biz.impl.mine.card.model.MineHistoryCard",
            "V",
            arrayOf("boolean"),
            extraLog = "我的页浏览历史(强制隐藏V)",
        ) { chain ->
            val args = chain.getArgs().toTypedArray()
            if (args.isNotEmpty() && args[0] == true) {
                args[0] = false
            }
            chain.proceed(args)
        }
        // 最彻底兜底：hook 构造器，构造后根视图 h()（inflate R.layout.c5t）直接 GONE——
        // 不依赖 V(boolean) 参数是否被调用
        HookSupport.safeHookCtor(
            TAG,
            "com.dragon.read.component.biz.impl.mine.card.model.MineHistoryCard",
            arrayOf("com.dragon.read.base.AbsFragment"),
            extraLog = "我的页浏览历史(构造后根GONE)",
        ) { chain ->
            val r = chain.proceed()
            runCatching {
                val card = chain.getThisObject()
                val root = card.javaClass.getMethod("h").invoke(card) as? View
                if (root != null) {
                    root.visibility = View.GONE
                    if (viewIdLogCount++ < 10) {
                        LogUtils.log(TAG, "我的页浏览历史卡根视图已 GONE (class=${root.javaClass.name})")
                    }
                }
            }
            r
        }
        blockHq = true
        hookViewIdHider()
    }

    /** 是否隐藏金币信息板块（ez1）。 */
    private var blockEz1 = false
    /** 是否隐藏历史卡（hq/ihq/iht/j4o）。 */
    private var blockHq = false
    /** 是否隐藏我的页推荐"加载中"（k5）。 */
    private var blockK5 = false
    /** 是否隐藏发表按钮（idd/ide/ok，限主页）。 */
    private var blockPublish = false
    /** 是否隐藏我的页会员特权卡（jzp，限主页）。 */
    private var blockVipCard = false
    /** 是否隐藏主页"点击赚钱"挂件（widget.b/InnerPendantView/GoldCoinBoxTextView）。 */
    private var blockPendant = false
    /** 目标 view id 解析缓存（getIdentifier 运行时解析，短 id 数值稳定）。 */
    private var viewIdEz1 = 0
    private var viewIdHq = 0
    private var viewIdIhq = 0
    private var viewIdIht = 0
    private var viewIdJ4o = 0
    private var viewIdK5 = 0
    private var viewIdIde = 0
    private var viewIdIdd = 0
    private var viewIdOk = 0
    private var viewIdJzp = 0
    private var viewIdLogCount = 0
    private var histDiagCount = 0
    /** 历史卡（容器特征）隐藏命中计数（限 3 次）。 */
    private var histHideCount = 0

    /**
     * 按 view id 精确隐藏（onAttachedToWindow）。
     *
     * <p>短混淆 id 全局复用严重（如 ihq 46 处、hq 21 处），<b>不能</b>全局按 id 隐藏。
     * 历史卡（hq/ihq/iht/j4o）与我的页"加载中"（k5）仅在<b>我的页推荐 feed 内</b>命中
     * （isInMineFeed：父链含 StaggeredFeedLayout 且 scene==MINE）；发表按钮（idd/ide/ok）
     * 与会员特权卡（jzp）限主页（书城/我的页 tab，isInMainActivity）；金币信息 ez1 专属不限；
     * 主页"点击赚钱"挂件（widget.b/InnerPendantView/GoldCoinBoxTextView）按类判断限主页
     * （onAttachedToWindow 为继承方法，getDeclaredMethod 无法 hook，统一在此处理）。
     */
    private fun hookViewIdHider() {
        HookSupport.safeHook(
            TAG,
            "android.view.View",
            "onAttachedToWindow",
            extraLog = "板块view id隐藏(ez1/hq/k5/发表/会员卡/挂件)",
        ) { chain ->
            val r = chain.proceed()
            runCatching {
                val v = chain.getThisObject() as? View ?: return@runCatching
                if (v.id == 0) return@runCatching
                val res = v.resources
                if (viewIdEz1 == 0) viewIdEz1 = runCatching { res.getIdentifier("ez1", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdHq == 0) viewIdHq = runCatching { res.getIdentifier("hq", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdIhq == 0) viewIdIhq = runCatching { res.getIdentifier("ihq", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdIht == 0) viewIdIht = runCatching { res.getIdentifier("iht", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdJ4o == 0) viewIdJ4o = runCatching { res.getIdentifier("j4o", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdK5 == 0) viewIdK5 = runCatching { res.getIdentifier("k5", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdIde == 0) viewIdIde = runCatching { res.getIdentifier("ide", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdIdd == 0) viewIdIdd = runCatching { res.getIdentifier("idd", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdOk == 0) viewIdOk = runCatching { res.getIdentifier("ok", "id", "com.dragon.read") }.getOrDefault(0)
                if (viewIdJzp == 0) viewIdJzp = runCatching { res.getIdentifier("jzp", "id", "com.dragon.read") }.getOrDefault(0)
                // 先做低成本的 id 初筛，命中候选才遍历父链（避免高频开销）
                val isHistItem = blockHq && (v.id == viewIdHq || v.id == viewIdIhq || v.id == viewIdIht || v.id == viewIdJ4o)
                val isLoading = blockK5 && viewIdK5 != 0 && v.id == viewIdK5
                val isPublish = blockPublish && (v.id == viewIdIde || v.id == viewIdIdd || v.id == viewIdOk) && isInMainActivity(v)
                val isVipCard = blockVipCard && viewIdJzp != 0 && v.id == viewIdJzp && isInMainActivity(v)
                // 主页点击赚钱挂件：按类名判断（onAttachedToWindow 继承自 View，无法 getDeclaredMethod）
                val clsName = v.javaClass.name
                val isPendant = blockPendant && isInMainActivity(v) &&
                    (clsName.contains("pendant.widget.b") || clsName.contains("InnerPendantView") || clsName.contains("GoldCoinBoxTextView"))
                val hide = (blockEz1 && viewIdEz1 != 0 && v.id == viewIdEz1) ||
                    ((isHistItem || isLoading) && isInMineFeed(v)) ||
                    isPublish || isVipCard || isPendant
                if (hide && v.visibility != View.GONE) {
                    v.visibility = View.GONE
                    if (viewIdLogCount++ < 15) {
                        val resName = runCatching { res.getResourceEntryName(v.id) }.getOrNull()
                        LogUtils.log(TAG, "板块已隐藏 (viewId=${v.id}/${resName}, class=${v.javaClass.name})")
                    }
                }
                // 历史卡诊断：blockHq 时记录我的页 attach 的容器。注意 onAttachedToWindow
                // 触发时视图尚未布局（height=0），必须 post 到布局完成后检查尺寸才有意义。
                if (blockHq && isInMainActivity(v) && v is ViewGroup &&
                    (v is android.widget.FrameLayout || v is androidx.constraintlayout.widget.ConstraintLayout || v is android.widget.LinearLayout) &&
                    v.childCount >= 2 && histDiagCount < 12
                ) {
                    val target = v
                    target.post {
                        val density = target.resources.displayMetrics.density
                        if (target.height < 60 * density || target.height > 600 * density) return@post
                        val resName = runCatching { target.resources.getResourceEntryName(target.id) }.getOrNull()
                        val hDp = (target.height / density).toInt()
                        val wDp = (target.width / density).toInt()
                        LogUtils.log(
                            TAG,
                            "历史卡诊断: attach 容器 (class=${target.javaClass.name}, id=${target.id}/${resName}, w=${wDp}dp h=${hDp}dp, children=${target.childCount})"
                        )
                        histDiagCount++
                    }
                }
                // 历史卡精准定位（log7 实证）：我的页"浏览历史"卡 = FrameLayout/LinearLayout、
                // id=NO_ID(-1)、布局后 w 250-340dp × h 280-380dp、children≤3 的大容器，
                // 内容条目为 178×66dp 横条卡（封面 cek 44×62）。按容器特征直接 GONE。
                if (blockHq && isInMainActivity(v) && !isInHybridContainer(v) &&
                    (v is android.widget.FrameLayout || v is android.widget.LinearLayout) &&
                    v.id == View.NO_ID && v.childCount <= 3 && histHideCount < 3
                ) {
                    val target = v
                    target.post {
                        val density = target.resources.displayMetrics.density
                        val wDp = target.width / density
                        val hDp = target.height / density
                        if (wDp in 250f..340f && hDp in 280f..380f && target.visibility != View.GONE) {
                            target.visibility = View.GONE
                            histHideCount++
                            LogUtils.log(
                                TAG,
                                "历史卡(容器特征)已隐藏 (${wDp.toInt()}dp×${hDp.toInt()}dp, class=${target.javaClass.name}, children=${target.childCount})"
                            )
                        }
                    }
                }
            }
            r
        }
    }

    /** 判断 view 是否位于书城 Lynx 混合容器（hybrid.ui.j）内——书城内容不按此特征隐藏。 */
    private fun isInHybridContainer(v: View): Boolean {
        var p: android.view.ViewParent? = v.parent
        while (p is View) {
            if (p.javaClass.name.startsWith("com.dragon.read.component.biz.impl.hybrid.ui")) return true
            p = p.parent
        }
        return false
    }

    /** 判断 view 是否在我的页推荐 feed（父链含 StaggeredFeedLayout 且 scene==MINE）。 */
    private fun isInMineFeed(v: View): Boolean {
        var p: android.view.ViewParent? = v.parent
        while (p is View) {
            if (p.javaClass.name.contains("StaggeredFeedLayout")) {
                return currentFeedScene(p) == "MINE"
            }
            p = p.parent
        }
        return false
    }

    /** 判断 view 是否位于主页（MainFragmentActivity：书城/我的页 tab）。 */
    private fun isInMainActivity(v: View): Boolean {
        var ctx: android.content.Context? = v.context
        var guard = 0
        while (ctx is android.content.ContextWrapper && guard++ < 8) {
            if (ctx is android.app.Activity) {
                return ctx.javaClass.name.contains("MainFragmentActivity")
            }
            ctx = ctx.baseContext
        }
        return false
    }

    /** 搜索页热搜榜/AI搜索板块：数据构建层返回 null，列表自然跳过该类型。 */
    private fun hookSearchResultClean() {
        val clazz = resolve(com.xxhy.fqpp.xposed.locate.DragonTargets.SEARCH_RESULT_BUILDER.id, "com.dragon.read.component.biz.impl.help.z")
        val cellViewData = arrayOf("com.dragon.read.rpc.model.CellViewData")
        // 热搜榜（Rank models）
        HookSupport.safeHook(TAG, clazz, "S", cellViewData) { null }
        HookSupport.safeHook(TAG, clazz, "M", cellViewData) { null }
        // 热搜词
        HookSupport.safeHook(TAG, clazz, "I", cellViewData) { null }
        // 中部 AI 搜索区 / AI 机器人入口
        HookSupport.safeHook(TAG, clazz, "R", cellViewData) { null }
        HookSupport.safeHook(TAG, clazz, "T", cellViewData) { null }
    }

    /** 搜索页 AI 入口：Activity 三开关 + 模板静态工厂返回默认（false,false）。 */
    private fun hookSearchAiEntrances() {
        val activity = "com.dragon.read.component.biz.impl.SearchActivity"
        HookSupport.safeHook(TAG, activity, "G3") { false } // AI 悬浮按钮
        HookSupport.safeHook(TAG, activity, "H3") { false } // 搜索横幅 AI
        HookSupport.safeHook(TAG, activity, "I3") { false } // 搜索框 AI
        hookStaticFactory(
            "com.dragon.read.base.ssconfig.template.SearchResultPageAiFloatButton"
        )
        hookStaticFactory(
            "com.dragon.read.base.ssconfig.template.SearchPageAiEntranceV675"
        )
    }

    private fun hookStaticFactory(clazz: String) {
        try {
            val cls = HookSupport.dragonLoader().loadClass(clazz)
            val method = cls.getDeclaredMethod("b")
            if (!HookSupport.hookedMethods.add(method)) return
            com.xxhy.fqpp.xposed.global.XposedGlobals.getXposedEntryInstance()
                .hook(method).intercept { cls.getConstructor().newInstance() }
            LogUtils.log(TAG, "静态工厂 $clazz.b() 已替换为默认实例")
        } catch (t: Throwable) {
            LogUtils.logError(TAG, "$clazz.b() hook 失败: ${t.message}", null)
        }
    }

    /** 安装全部界面净化 hook。 */
    @JvmStatic
    fun installUiClean() {
        val options = com.xxhy.fqpp.xposed.config.ConfigManager.getUiCleanOptions()
        if (options.searchWord) hookSearchWordHint()
        if (options.screenAd) hookScreenAdManager()
        if (options.vipCard) {
            hookVipEntrances()
            // view id 兜底：会员特权卡（jzp）attach 后 GONE（限主页），
            // 解决数据层 hook（isDisableVipInGoogle 等）在本地会员开启时未生效的问题
            blockVipCard = true
            hookViewIdHider()
            // 底层拦截：jzp 会员卡 / hq 系列历史卡 任何 setVisibility(VISIBLE) 都强制 GONE
            // （比 onAttachedToWindow 更可靠，覆盖动态刷新/重新显示）
            HookSupport.safeHook(
                TAG,
                "android.view.View",
                "setVisibility",
                arrayOf("int"),
                extraLog = "会员卡/历史卡(setVisibility拦截)",
            ) { chain ->
                val args = chain.getArgs().toTypedArray()
                runCatching {
                    val v = chain.getThisObject() as? View
                    if (v != null && args.isNotEmpty() && args[0] != View.GONE) {
                        val res = v.resources
                        val hide = when {
                            blockVipCard -> {
                                if (viewIdJzp == 0) viewIdJzp = runCatching { res.getIdentifier("jzp", "id", "com.dragon.read") }.getOrDefault(0)
                                viewIdJzp != 0 && v.id == viewIdJzp
                            }
                            blockHq -> {
                                if (viewIdHq == 0) viewIdHq = runCatching { res.getIdentifier("hq", "id", "com.dragon.read") }.getOrDefault(0)
                                if (viewIdIhq == 0) viewIdIhq = runCatching { res.getIdentifier("ihq", "id", "com.dragon.read") }.getOrDefault(0)
                                if (viewIdIht == 0) viewIdIht = runCatching { res.getIdentifier("iht", "id", "com.dragon.read") }.getOrDefault(0)
                                if (viewIdJ4o == 0) viewIdJ4o = runCatching { res.getIdentifier("j4o", "id", "com.dragon.read") }.getOrDefault(0)
                                v.id == viewIdHq || v.id == viewIdIhq || v.id == viewIdIht || v.id == viewIdJ4o
                            }
                            else -> false
                        }
                        if (hide) {
                            args[0] = View.GONE
                            if (viewIdLogCount++ < 15) {
                                val resName = runCatching { res.getResourceEntryName(v.id) }.getOrNull()
                                LogUtils.log(TAG, "板块 setVisibility 强制 GONE (viewId=${v.id}/${resName}, class=${v.javaClass.name})")
                            }
                        }
                    }
                }
                chain.proceed(args)
            }
        }
        if (options.functionBadge) hookFunctionBadge()
        if (options.miniGame) hookMiniGame()
        if (options.tabBadge) hookTabBadge()
        if (options.mineFeed) installFeedSceneGate()
        if (options.homeEarnTab || options.homeSeriesTab) hookHomeTabBar()
        if (options.hidePublish) hookPublishButton()
        if (options.hideMineAsset) hookMineAsset()
        if (options.hideMineHistory) hookMineHistoryCard()
        if (options.hideMineEarnPendant) hookMineEarnPendant()
        if (options.searchResult) hookSearchResultClean()
        if (options.searchAi) hookSearchAiEntrances()
        LogUtils.log(TAG, "界面净化 hook 全部安装（细分开关: ${options.anyEnabled}）")
    }
}