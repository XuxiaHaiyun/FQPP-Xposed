package com.xxhy.fqpp.xposed

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.global.XposedGlobals
import com.xxhy.fqpp.xposed.locate.DragonTargets
import com.xxhy.fqpp.xposed.locate.LocationCache
import com.xxhy.fqpp.xposed.locate.LocatorService
import com.xxhy.fqpp.xposed.locate.MethodHint
import com.xxhy.fqpp.xposed.utils.AppUtils
import com.xxhy.fqpp.xposed.utils.LogUtils
import com.xxhy.fqpp.xposed.xcallback.*
import io.github.libxposed.api.XposedInterface
import java.io.File
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.Collections

/**
 * Hook 注册总入口。
 *
 * <p>全部目标类名优先使用 DexKit 定位结果（{@link LocatorService}），
 * 定位失败的目标回退到常量表已知类名，再失败则跳过对应 hook 并记录日志。
 *
 * <p>延迟兜底：Application 早期定位可能因混淆版本变化失败，因此额外 hook
 * {@code Instrumentation.callActivityOnCreate}——进入设置页时类已加载，用运行时反射
 * 直接定位混淆方法并注入；进入首个业务 Activity（主页）时触发后台强制重定位 + 幂等重注入。
 */
object HookManager {

    // ===== 目标类名（定位失败时的最终兜底，来自 7.0.9.32 反编译验证） =====

    private const val TARGET_PACKAGE = "com.dragon.read"
    private const val CLASS_SETTINGS_ACTIVITY =
        "com.dragon.read.component.biz.impl.mine.settings.SettingsActivity"
    private const val CLASS_MAIN_APPLICATION = "com.dragon.read.app.MainApplication"
    private const val CLASS_UPDATE_STATUS_LISTENER =
        "com.ss.android.update.OnUpdateStatusChangedListener"
    private const val CLASS_UPDATE_SERVICE = "com.ss.android.update.UpdateServiceImpl"
    private const val CLASS_VIP_COMMON_SUB_TYPE = "com.dragon.read.rpc.model.VipCommonSubType"
    private const val CLASS_VIP_INFO_MODEL = "com.dragon.read.user.model.VipInfoModel"
    private const val CLASS_ACCOUNT_MANAGER = "com.dragon.read.user.AcctManager"
    private const val CLASS_PRIVILEGE_MANAGER =
        "com.dragon.read.component.biz.impl.privilege.PrivilegeManager"
    private const val CLASS_BOOK_UTILS = "com.dragon.read.util.BookUtils"
    private const val CLASS_READING_TIMING = "com.dragon.read.polaris.timing.ReadingTiming"
    private const val CLASS_TAB_BADGE_CONTROLLER = "bf4.c0"
    private const val CLASS_SIDEBAR_FUNC_ITEM = "i44.c"
    private const val CLASS_MINI_GAME_SERVICE = "e44.c"
    private const val CLASS_SCREEN_AD_MANAGER = "bd2.c"
    private const val CLASS_SEARCH_RESULT_BUILDER = "com.dragon.read.component.biz.impl.help.z"
    private const val CLASS_NPTH_CORE = "com.bytedance.crash.NpthCore"
    private const val CLASS_CRASH_REPORT_CONFIG =
        "com.dragon.read.base.ssconfig.template.CrashReportConfig"
    private const val CLASS_RPC_FACADE_B = "ro4.b"
    private const val CLASS_RPC_FACADE_F = "ro4.f"
    private const val CLASS_COMMENT_API_SERVICE = "com.dragon.read.saas.ugc.rpc.CommentApiService"
    private const val CLASS_COMMENT_LIST_REQUEST = "com.dragon.read.saas.ugc.model.GetCommentListRequest"
    private const val CLASS_REPLY_LIST_REQUEST = "com.dragon.read.saas.ugc.model.GetReplyListRequest"
    private const val CLASS_COMMENT_GROUP_TYPE = "com.dragon.read.saas.ugc.model.UgcCommentGroupTypeOutter"
    private const val CLASS_COMMENT_SOURCE_ENUM = "com.dragon.read.saas.ugc.model.UgcCommentSourceEnum"
    private const val CLASS_COMMENT_RELATIVE_TYPE = "com.dragon.read.saas.ugc.model.UgcRelativeType"
    private const val CLASS_COMMENT_CHANNEL_ENUM = "com.dragon.read.saas.ugc.model.UgcCommentChannelEnum"
    private const val CLASS_COMMENT_BUSINESS_PARAM = "com.dragon.read.saas.ugc.model.CommentBusinessParam"
    private const val CLASS_REPLY_BUSINESS_PARAM = "com.dragon.read.saas.ugc.model.ReplyBusinessParam"
    private const val CLASS_COMMENT_SORT_ENUM = "com.dragon.read.saas.ugc.model.UgcSortEnum"
    private const val CLASS_COMMENT_FOLD_TYPE = "com.dragon.read.saas.ugc.model.FoldType"

    // ===== 目标方法名（7.0.9.32 验证；混淆方法由 DexKit 二次定位） =====

    private const val METHOD_IS_UNSAFE_BOOK = "isUnsafeBook"
    private const val METHOD_IS_OVERALLOFFSHELF = "isOverallOffShelf"
    private const val METHOD_APP_CREATE_ALWAYS = "onCreateAlways"
    private const val METHOD_UPDATE_CHECK = "checkUpdate"
    private const val METHOD_ACCOUNT_M = "M"
    private const val METHOD_ACCOUNT_SINGLETON = "P"
    private const val METHOD_ACCOUNT_IS_LOGIN = "islogin"
    private const val METHOD_CHECK_VIP_RELATIONAL = "canShowVipRelational"
    private const val METHOD_CHECK_NO_AD_ALL_SCENE = "hasNoAdFollAllScene"
    private const val METHOD_CHECK_NO_AD_SHORT_SERIES = "hasNoAdForShortSeries"
    private const val METHOD_CHECK_NO_AD_PRIVILEGE = "hasNoAdPrivilege"
    private const val METHOD_CHECK_NO_AD_CONSUMPTION = "hasNoAdReadConsumptionPrivilege"
    private const val METHOD_CHECK_FOREVER_NO_AD = "isForeverNoAd"
    private const val METHOD_CHECK_NO_AD = "isNoAd"
    private const val METHOD_CHECK_IS_VIP = "isVip"
    private const val METHOD_CHECK_PAY_VIP_ENTRANCE = "showPayVipEntranceInChapterEnd"
    private const val METHOD_CHECK_PRIVILEGE = "hasPrivilege"
    private const val METHOD_GET_PRIVILEGE_INSTANCE = "getInstance"
    private const val METHOD_UPDATE_VIP_INFO = "updateVipInfo"
    private const val METHOD_CHECK_BOOK_AD_FREE = "isBookAdFree"
    private const val METHOD_READING_TIME_ADD = "g"

    /** 定位结果：targetId -> 类全限定名（DexKit 优先，兜底常量表） */
    @Volatile
    private var resolvedClasses: Map<String, String> = emptyMap()

    /**
     * 由 XposedEntry 在 onPackageLoaded 后调用：优先使用持久化定位缓存。
     *
     * <p>策略：
     * 1. 上次已定位成功且目标版本匹配 → 直接使用映射表（毫秒级，不初始化 DexKit）；
     * 2. 无缓存/版本变化 → 等待 XposedEntry 已触发的 DexKit 异步扫描结果。
     */
    fun ensureLocated() {
        // 1. 缓存优先：版本匹配且已有定位结果 → 直接用映射表
        val context = DragonGlobals.getDragonApplication() ?: AppUtils.createAppContext()
        if (context != null) {
            val cache = LocationCache.getInstance(context)
            val versionCode = runCatching {
                context.packageManager.getPackageInfo(TARGET_PACKAGE, 0).versionCode.toInt()
            }.getOrNull()
            if (versionCode != null && cache.targetVersionCode == versionCode) {
                val cached = cache.getAllCached()
                if (cached.isNotEmpty()) {
                    resolvedClasses = mergeMaps(cached, knownFallback)
                    LogUtils.log("HookManager", "定位缓存命中（versionCode=$versionCode，${cached.size} 项），直接使用映射表")
                    return
                }
            }
        }
        // 2. 无缓存/版本变化 → 等待 DexKit 异步扫描
        val summary = LocatorService.await() ?: return
        resolvedClasses = mergeMaps(summary.classNameMap, knownFallback)
    }

    /** 合并定位结果与常量表兜底（定位成功优先）。 */
    private fun mergeMaps(
        primary: Map<String, String>,
        fallback: Map<String, String>,
    ): Map<String, String> {
        val merged = mutableMapOf<String, String>()
        merged.putAll(primary)
        fallback.forEach { (targetId, className) ->
            if (targetId !in merged) merged[targetId] = className
        }
        return merged
    }

    private val knownFallback: Map<String, String> = mapOf(
        DragonTargets.TAB_BADGE_CONTROLLER.id to CLASS_TAB_BADGE_CONTROLLER,
        DragonTargets.SIDEBAR_FUNC_ITEM.id to CLASS_SIDEBAR_FUNC_ITEM,
        DragonTargets.MINI_GAME_SERVICE.id to CLASS_MINI_GAME_SERVICE,
        DragonTargets.SCREEN_AD_MANAGER.id to CLASS_SCREEN_AD_MANAGER,
        DragonTargets.SEARCH_RESULT_BUILDER.id to CLASS_SEARCH_RESULT_BUILDER,
        DragonTargets.SETTINGS_ACTIVITY.id to CLASS_SETTINGS_ACTIVITY,
        DragonTargets.MAIN_APPLICATION.id to CLASS_MAIN_APPLICATION,
        DragonTargets.UPDATE_STATUS_LISTENER.id to CLASS_UPDATE_STATUS_LISTENER,
        DragonTargets.UPDATE_SERVICE.id to CLASS_UPDATE_SERVICE,
        DragonTargets.VIP_COMMON_SUB_TYPE.id to CLASS_VIP_COMMON_SUB_TYPE,
        DragonTargets.VIP_INFO_MODEL.id to CLASS_VIP_INFO_MODEL,
        DragonTargets.ACCOUNT_MANAGER.id to CLASS_ACCOUNT_MANAGER,
        DragonTargets.PRIVILEGE_MANAGER.id to CLASS_PRIVILEGE_MANAGER,
        DragonTargets.BOOK_UTILS.id to CLASS_BOOK_UTILS,
        DragonTargets.READING_TIMING.id to CLASS_READING_TIMING,
        DragonTargets.NPTH_CORE.id to CLASS_NPTH_CORE,
        DragonTargets.CRASH_REPORT_CONFIG.id to CLASS_CRASH_REPORT_CONFIG,
        DragonTargets.RPC_FACADE_B.id to CLASS_RPC_FACADE_B,
        DragonTargets.RPC_FACADE_F.id to CLASS_RPC_FACADE_F,
        DragonTargets.COMMENT_API_SERVICE.id to CLASS_COMMENT_API_SERVICE,
        DragonTargets.COMMENT_LIST_REQUEST.id to CLASS_COMMENT_LIST_REQUEST,
        DragonTargets.REPLY_LIST_REQUEST.id to CLASS_REPLY_LIST_REQUEST,
        DragonTargets.COMMENT_GROUP_TYPE.id to CLASS_COMMENT_GROUP_TYPE,
        DragonTargets.COMMENT_SOURCE_ENUM.id to CLASS_COMMENT_SOURCE_ENUM,
        DragonTargets.COMMENT_RELATIVE_TYPE.id to CLASS_COMMENT_RELATIVE_TYPE,
        DragonTargets.COMMENT_CHANNEL_ENUM.id to CLASS_COMMENT_CHANNEL_ENUM,
        DragonTargets.COMMENT_BUSINESS_PARAM.id to CLASS_COMMENT_BUSINESS_PARAM,
        DragonTargets.REPLY_BUSINESS_PARAM.id to CLASS_REPLY_BUSINESS_PARAM,
        DragonTargets.COMMENT_SORT_ENUM.id to CLASS_COMMENT_SORT_ENUM,
        DragonTargets.COMMENT_FOLD_TYPE.id to CLASS_COMMENT_FOLD_TYPE,
    )

    /**
     * 获取定位后的类名（无定位结果时回退常量表）。
     */
    private fun className(targetId: String, fallback: String): String {
        return resolvedClasses[targetId] ?: fallback
    }

    /**
     * 供模块外（如 DragonService）获取定位后的类名：
     * DexKit 定位结果优先，未定位/定位失败时回退调用方提供的已知类名。
     */
    @JvmStatic
    fun getResolvedClassName(targetId: String, fallback: String): String {
        return resolvedClasses[targetId] ?: fallback
    }

    @JvmStatic
    fun hookAll() {
        // 调试浮窗（混淆查找弹窗）立即注入：先读配置再注入，确保定位日志实时可见
        val sp = ConfigManager.prefs()
        val debugMode = sp.getBoolean(SPConstants.DEBUG_MODE, SPConstants.DEFAULT_DEBUG_MODE)
        if (debugMode) {
            runCatching { ImGuiOverlay.inject(XposedGlobals.getPackageLoadedParam()) }
        }

        ensureLocated()
        hookActivityLifecycle()

        hookSettingActivity()

        // 开关判断集中在 hookAll：关闭时根本不挂载对应 hook（避免空挂载开销）
        val localVip = sp.getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)
        if (localVip) {
            // 本地会员：用户信息拦截（AcctManager）+ 会员特权（PrivilegeManager）+
            // VIP 卡片/Banner 展示（NsVipImpl 4 方法 → true）
            hookAccountManager()
            hookPrivilegeManager()
            hookVipShowForLocalVip()
        }
        val blockUpdates = sp.getBoolean(SPConstants.BLOCK_UPDATES, SPConstants.DEFAULT_BLOCK_UPDATES)
        if (blockUpdates) {
            hookUpdateManager()
        }

        val unlockBooks = sp.getBoolean(SPConstants.UNLOCK_BOOKS, SPConstants.DEFAULT_UNLOCK_BOOKS)
        if (unlockBooks) {
            hookBookUtils()
        }
        val blockCrashReport =
            sp.getBoolean(SPConstants.BLOCK_CRASH_REPORT, SPConstants.DEFAULT_BLOCK_CRASH_REPORT)
        if (blockCrashReport) {
            hookCrashReport()
        }
        val blockHotUpdate =
            sp.getBoolean(SPConstants.BLOCK_HOT_UPDATE, SPConstants.DEFAULT_BLOCK_HOT_UPDATE)
        if (blockHotUpdate) {
            hookHotUpdate()
        }
        val blockPluginLoad =
            sp.getBoolean(SPConstants.BLOCK_PLUGIN_LOAD, SPConstants.DEFAULT_BLOCK_PLUGIN_LOAD)
        if (blockPluginLoad) {
            hookPluginLoad()
        }
        val readingTime = sp.getBoolean(SPConstants.READING_TIME, SPConstants.DEFAULT_READING_TIME)
        if (readingTime) {
            hookReadingTime()
        }
        val uiClean = sp.getBoolean(SPConstants.UI_CLEAN, SPConstants.DEFAULT_UI_CLEAN)
        if (uiClean) {
            UiCleanHooker.installUiClean()
        }
        val instantReward = sp.getBoolean(SPConstants.INSTANT_REWARD, SPConstants.DEFAULT_INSTANT_REWARD)
        if (instantReward) {
            InstantRewardHooker.installInstantReward()
        }
    }

    fun hookReadingTime() {
        val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
        try {
            val cls = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.READING_TIMING.id, CLASS_READING_TIMING)
            )
            // 【混淆】g(long, long)：私有实例方法，特征为仅含两个 long 参数
            val method = runCatching {
                cls.getDeclaredMethod(METHOD_READING_TIME_ADD, Long::class.javaPrimitiveType, Long::class.javaPrimitiveType)
            }.getOrElse {
                locateMethod(
                    cls.name,
                    DragonTargets.READING_TIME_ADD_METHOD
                )?.let { m -> cls.getDeclaredMethod(m, Long::class.javaPrimitiveType, Long::class.javaPrimitiveType) }
                    ?: throw it
            }
            if (!hookedMethods.add(method)) return
            XposedGlobals.getXposedEntryInstance().hook(method).intercept { chain ->
                val original = chain.getArg(1) as Long
                val multiplied = original * 5
                val args: Array<Any> = chain.getArgs().toTypedArray().also { it[1] = multiplied }
                chain.proceed(args)
            }
        } catch (e: Throwable) {
            LogUtils.logWarn("ReadingDoubler", "g() hook failed: ${e.message}")
        }
    }

    fun hookCrashReport() {
        val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
        // 1. 阻断崩溃监控初始化
        try {
            val npthCoreClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.NPTH_CORE.id, CLASS_NPTH_CORE)
            )
            val kMethod = npthCoreClass.getDeclaredMethod("k", Context::class.java, File::class.java)
            if (hookedMethods.add(kMethod)) {
                XposedGlobals.getXposedEntryInstance().hook(kMethod).intercept { null }
                LogUtils.log("BlockCrashReport", "NpthCore.k 已阻断")
            }
        } catch (_: Throwable) {
        }

        // 2. 强制上报配置永远返回 false
        try {
            val configClazz = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.CRASH_REPORT_CONFIG.id, CLASS_CRASH_REPORT_CONFIG)
            )
            val bMethod = configClazz.getDeclaredMethod("b")
            if (hookedMethods.add(bMethod)) {
                XposedGlobals.getXposedEntryInstance().hook(bMethod).intercept { chain ->
                    try {
                        val ctor: Constructor<*> = configClazz.declaredConstructors[0]
                        ctor.isAccessible = true
                        ctor.newInstance(false, java.util.Collections.emptyList<Any>())
                    } catch (_: Throwable) {
                        chain.proceed()
                    }
                }
                LogUtils.log("BlockCrashReport", "CrashReportConfig.b() 已强制 enable=false")
            }
        } catch (_: Throwable) {
        }
    }

    /**
     * 热更新（打开禁用）：阻止 Reparo 热修复框架初始化与远程补丁加载。
     *
     * <p>7.0.9.32 反编译实证：{@code com.dragon.read.base.hotfix.ReparoHotFixInitServiceImpl}
     * 为番茄 Reparo（字节热修）入口：
     * <ul>
     *   <li>{@code initialize(Application)}：初始化入口，内部 {@code doInit} →
     *       {@code f.e().h(config)} 启动框架（每小时轮询补丁）+ 注册回调；</li>
     *   <li>{@code loadRemotePatch()}：拉取并加载远程补丁（{@code f.e().l()}）；</li>
     *   <li>内部配置类 {@code ReparoHotFixInitServiceImpl$c}（IReparoConfig）的
     *       {@code enable()} 返回 true（框架级启用开关）。</li>
     * </ul>
     * 三层 hook 全部短路：不初始化、不拉补丁、框架判定禁用。
     */
    fun hookHotUpdate() {
        val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
        val clazz = "com.dragon.read.base.hotfix.ReparoHotFixInitServiceImpl"
        try {
            // 1. 初始化入口短路：Reparo 完全不启动
            val initCls = loadTargetClass(targetAppClassLoader, clazz)
            val initMethod = initCls.getDeclaredMethod("initialize", Application::class.java)
            if (hookedMethods.add(initMethod)) {
                XposedGlobals.getXposedEntryInstance().hook(initMethod).intercept { null }
                LogUtils.log("BlockHotUpdate", "Reparo.initialize 已阻断")
            }
        } catch (_: Throwable) {
        }
        try {
            // 2. 远程补丁加载短路
            val initCls = loadTargetClass(targetAppClassLoader, clazz)
            val patchMethod = initCls.getDeclaredMethod("loadRemotePatch")
            if (hookedMethods.add(patchMethod)) {
                XposedGlobals.getXposedEntryInstance().hook(patchMethod).intercept { null }
                LogUtils.log("BlockHotUpdate", "Reparo.loadRemotePatch 已阻断")
            }
        } catch (_: Throwable) {
        }
        try {
            // 3. 框架级启用开关：enable() → false
            val configCls = loadTargetClass(
                targetAppClassLoader,
                "com.dragon.read.base.hotfix.ReparoHotFixInitServiceImpl\$c"
            )
            val enableMethod = configCls.getDeclaredMethod("enable")
            if (hookedMethods.add(enableMethod)) {
                XposedGlobals.getXposedEntryInstance().hook(enableMethod).intercept { false }
                LogUtils.log("BlockHotUpdate", "ReparoConfig.enable() 已强制 false")
            }
        } catch (_: Throwable) {
        }
    }

    /**
     * 加载插件（打开禁用）：禁止加载 files/plugins/ 下的 Mira 插件。
     *
     * <p>7.0.9.32 反编译实证：{@code com.dragon.read.base.plugin.MiraPluginInitServiceImpl}
     * 为番茄 Mira（字节插件化框架）入口，从 files/plugins/ 加载
     * player/lynx/live/im/offlinetts 等插件：
     * <ul>
     *   <li>{@code initialize(Application)}：插件环境初始化 → {@code doInit} →
     *       {@code PluginServiceManager.ins().init} + 预加载已装插件 + 远程插件下载；</li>
     *   <li>{@code initMira(Application)}：Mira SDK 初始化（安装 ClassLoader/Instrumentation hook）；</li>
     *   <li>{@code loadRemotePlugin()}：触发远程插件下载（MiraMorpheusHelper.c）。</li>
     * </ul>
     * 三层 hook 全部短路：插件环境不初始化、Mira SDK 不启动、远程插件不下载。
     */
    fun hookPluginLoad() {
        val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
        val clazz = "com.dragon.read.base.plugin.MiraPluginInitServiceImpl"
        try {
            val cls = loadTargetClass(targetAppClassLoader, clazz)
            // 1. 插件环境初始化短路（doInit 不执行 → 不预加载/不下载插件）
            val initMethod = cls.getDeclaredMethod("initialize", Application::class.java)
            if (hookedMethods.add(initMethod)) {
                XposedGlobals.getXposedEntryInstance().hook(initMethod).intercept { null }
                LogUtils.log("BlockPluginLoad", "Mira.initialize 已阻断")
            }
        } catch (_: Throwable) {
        }
        try {
            val cls = loadTargetClass(targetAppClassLoader, clazz)
            // 2. Mira SDK 初始化短路（ClassLoader hook 不安装）
            val miraMethod = cls.getDeclaredMethod("initMira", Application::class.java)
            if (hookedMethods.add(miraMethod)) {
                XposedGlobals.getXposedEntryInstance().hook(miraMethod).intercept { null }
                LogUtils.log("BlockPluginLoad", "Mira.initMira 已阻断")
            }
        } catch (_: Throwable) {
        }
        try {
            val cls = loadTargetClass(targetAppClassLoader, clazz)
            // 3. 远程插件下载短路
            val remoteMethod = cls.getDeclaredMethod("loadRemotePlugin")
            if (hookedMethods.add(remoteMethod)) {
                XposedGlobals.getXposedEntryInstance().hook(remoteMethod).intercept { null }
                LogUtils.log("BlockPluginLoad", "Mira.loadRemotePlugin 已阻断")
            }
        } catch (_: Throwable) {
        }
    }

    /**
     * 本地会员：展示 VIP 卡片/Banner 相关入口（NsVipImpl 方法 → true）。
     *
     * <p>7.0.9.32 实证：willShowNativeBanner/canShowVipCenter/willShowLynxBanner/
     * canShowMulVip 决定 VIP 卡片/Banner 的展示，本地会员开启时强制 true 展示；
     * 界面净化（vipCard）不再 hook 这些（避免与本地会员冲突）。
     */
    fun hookVipShowForLocalVip() {
        try {
            val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
            val vipClazz = loadTargetClass(targetAppClassLoader, "com.dragon.read.component.biz.impl.NsVipImpl")
            listOf("willShowNativeBanner", "canShowVipCenter", "willShowLynxBanner", "canShowMulVip")
                .forEach { name ->
                    runCatching {
                        val m = vipClazz.getDeclaredMethod(name)
                        if (hookedMethods.add(m)) {
                            XposedGlobals.getXposedEntryInstance().hook(m).intercept { true }
                            LogUtils.log("LocalVip", "VIP展示 $name → true")
                        }
                    }
                }
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "挂钩 VIP 展示失败: ${t.message}")
        }
    }

    fun hookBookUtils() {
        try {
            val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
            val aClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.BOOK_UTILS.id, CLASS_BOOK_UTILS)
            )
            val method = aClass.getDeclaredMethod(METHOD_IS_OVERALLOFFSHELF, Any::class.java)
            val method2 = aClass.getDeclaredMethod(METHOD_IS_UNSAFE_BOOK, Any::class.java)
            if (hookedMethods.add(method)) {
                XposedGlobals.getXposedEntryInstance().hook(method).intercept(BookUtilsHooker())
            }
            if (hookedMethods.add(method2)) {
                XposedGlobals.getXposedEntryInstance().hook(method2).intercept(BookUtilsHooker())
            }
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "挂钩 BookUtils 失败: ${t.message}")
        }
    }

    /**
     * 挂钩设置页标题栏右上角入口（hook onCreate，注入后由 SettingActivityHooker
     * 在标题栏添加 "FQ++" 按钮）。沿继承链向上查找番茄自己的 onCreate 实现
     * （不触碰 androidx/系统基类，避免全局 hook）。
     */
    fun hookSettingActivity() {
        try {
            val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
            val settingsActivityClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.SETTINGS_ACTIVITY.id, CLASS_SETTINGS_ACTIVITY)
            )
            val hooked = hookSettingsCreateMethod(settingsActivityClass)
            if (hooked > 0) {
                LogUtils.log("HookManager", "设置页标题栏入口已挂钩")
            } else {
                LogUtils.logWarn("HookManager", "设置页入口定位失败，等待进入设置页时运行时重试")
            }
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "挂钩设置界面失败: ${t.message}")
        }
    }

    /**
     * 沿继承链查找番茄自己的 onCreate(Bundle) 并 hook。
     *
     * <p>仅 hook {@code com.dragon.read} 包内的类：若 SettingsActivity 未重写该方法
     * （onCreate 在父类中声明），则向上查找；到达 androidx/系统基类即停止，
     * 避免对所有 Activity 全局注入。
     */
    private fun hookSettingsCreateMethod(activityClass: Class<*>): Int {
        var clazz: Class<*>? = activityClass
        while (clazz != null && clazz.name.startsWith(TARGET_PACKAGE)) {
            runCatching {
                val m = clazz.getDeclaredMethod("onCreate", android.os.Bundle::class.java)
                if (hookedMethods.add(m)) {
                    XposedGlobals.getXposedEntryInstance().hook(m).intercept(SettingActivityHooker())
                    return 1
                }
            }
            clazz = clazz.superclass
        }
        return 0
    }

    // ===== 运行时延迟定位与注入（进入主页/设置页后触发） =====

    /** 已 hook 的目标方法/构造器集合（幂等防重复注册） */
    private val hookedMethods = Collections.synchronizedSet(mutableSetOf<Any>())

    @Volatile
    private var activityLifecycleHooked = false

    @Volatile
    private var relocateTriggered = false

    /**
     * Hook Instrumentation.callActivityOnCreate：在每个 Activity 创建前拦截。
     * - 设置页 → 运行时反射定位混淆方法并注入（此时类已加载，成功率最高）；
     * - 首个业务 Activity（主页）→ 后台强制重定位 + 幂等重注入。
     */
    private fun hookActivityLifecycle() {
        if (activityLifecycleHooked) return
        synchronized(hookedMethods) {
            if (activityLifecycleHooked) return
            runCatching {
                val classLoader = XposedGlobals.getPackageLoadedParam().getDefaultClassLoader()
                val instrumentationClass = Class.forName(
                    "android.app.Instrumentation", false, classLoader)
                val callMethod = instrumentationClass.getDeclaredMethod(
                    "callActivityOnCreate", Activity::class.java, Bundle::class.java)
                XposedGlobals.getXposedEntryInstance().hook(callMethod).intercept { chain ->
                    val activity = runCatching { chain.getArg(0) as? Activity }.getOrNull()
                    if (activity != null) {
                        onActivityCreated(activity)
                    }
                    chain.proceed()
                }
                activityLifecycleHooked = true
                LogUtils.log("HookManager", "Activity 生命周期钩子已安装")
            }.onFailure {
                LogUtils.logError("HookManager", "安装 Activity 生命周期钩子失败", it)
            }
        }
    }

    private fun onActivityCreated(activity: Activity) {
        val className = activity.javaClass.name
        if (className.contains("settings", ignoreCase = true)) {
            installSettingsPageHooks(activity)
        } else if (!relocateTriggered) {
            relocateTriggered = true
            LogUtils.log("HookManager", "首次业务 Activity（$className），后台触发混淆重定位")
            Thread({ runCatching { relocateAndRehook() } }, "FQPP-Relocate").start()
        }
        // 界面净化/激励秒领目标类多在业务页面加载后才存在：
        // 每个 Activity 创建时幂等重装，已挂钩方法自动跳过，此前未加载的类随页面打开自然生效
        retryUiCleanHooks()
    }

    /**
     * 幂等重装界面净化/激励秒领 hook。
     *
     * <p>installUiClean/installInstantReward 内部有 hookedMethods 集合保证
     * 同一方法只挂一次；类未加载导致的失败会在后续 Activity 创建时自动重试。
     */
    private fun retryUiCleanHooks() {
        val sp = ConfigManager.prefs()
        if (sp.getBoolean(SPConstants.UI_CLEAN, SPConstants.DEFAULT_UI_CLEAN)) {
            UiCleanHooker.installUiClean()
        }
        if (sp.getBoolean(SPConstants.INSTANT_REWARD, SPConstants.DEFAULT_INSTANT_REWARD)) {
            InstantRewardHooker.installInstantReward()
        }
    }

    /**
     * 设置页 Activity 已创建（onCreate 执行前）：运行时定位并注入标题栏入口。
     * 每次进入均重试（hookedMethods 保证幂等，反射遍历开销可忽略）。
     */
    private fun installSettingsPageHooks(activity: Activity) {
        try {
            val settingsClass = activity.javaClass
            val hooked = hookSettingsCreateMethod(settingsClass)
            LogUtils.log("HookManager", "设置页标题栏入口注入：hooked=$hooked")
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "设置页运行时注入失败: ${t.message}")
        }
    }

    /**
     * 进入主页后：强制重跑 DexKit 定位并幂等重注入全部 hook。
     * 此时目标类已全部加载，定位成功率最高。
     */
    private fun relocateAndRehook() {
        val service = LocatorService.getInstance() ?: return
        try {
            val summary = service.resolveAll()
            val merged = mutableMapOf<String, String>()
            merged.putAll(summary.classNameMap)
            knownFallback.forEach { (targetId, className) ->
                if (targetId !in merged) merged[targetId] = className
            }
            resolvedClasses = merged
            LogUtils.log(
                "HookManager",
                "主页重定位完成：成功 ${summary.classNameMap.size}/${summary.total}"
            )

            hookSettingActivity()
            hookVipInfoModel()
            hookUpdateManager()
            hookAccountManager()
            hookPrivilegeManager()
            hookBookUtils()
            hookCrashReport()
            hookReadingTime()
            retryUiCleanHooks()
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "主页重定位失败: ${t.message}")
        }
    }

    @JvmStatic
    fun hookMainApplication() {
        try {
            val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
            val mainApplicationClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.MAIN_APPLICATION.id, CLASS_MAIN_APPLICATION)
            )
            val appCreateMethod = mainApplicationClass.getDeclaredMethod(METHOD_APP_CREATE_ALWAYS)
            XposedGlobals.getXposedEntryInstance().hook(appCreateMethod).intercept(MainApplicationHooker())
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "挂钩主应用类失败: ${t.message}")
        }
    }

    fun hookUpdateManager() {
        try {
            val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
            val updateStatusListenerClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.UPDATE_STATUS_LISTENER.id, CLASS_UPDATE_STATUS_LISTENER)
            )
            val updateServiceClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.UPDATE_SERVICE.id, CLASS_UPDATE_SERVICE)
            )

            val updateCheckMethod1 = updateServiceClass.getDeclaredMethod(
                METHOD_UPDATE_CHECK, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType,
                updateStatusListenerClass, Boolean::class.javaPrimitiveType
            )
            val updateCheckMethod2 = updateServiceClass.getDeclaredMethod(
                METHOD_UPDATE_CHECK, Int::class.javaPrimitiveType, updateStatusListenerClass
            )
            val updateCheckMethod3 = updateServiceClass.getDeclaredMethod(
                METHOD_UPDATE_CHECK, Int::class.javaPrimitiveType, updateStatusListenerClass,
                Boolean::class.javaPrimitiveType
            )

            listOf(updateCheckMethod1, updateCheckMethod2, updateCheckMethod3).forEach { m ->
                if (hookedMethods.add(m)) {
                    XposedGlobals.getXposedEntryInstance().hook(m).intercept(UpdateManagerHooker())
                }
            }
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "挂钩更新管理器失败: ${t.message}")
        }
    }

    fun hookVipInfoModel() {
        try {
            val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
            val vipCommonSubTypeClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.VIP_COMMON_SUB_TYPE.id, CLASS_VIP_COMMON_SUB_TYPE)
            )
            val vipInfoModelClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.VIP_INFO_MODEL.id, CLASS_VIP_INFO_MODEL)
            )

            val vipInfoModelConstructor: Constructor<*> = vipInfoModelClass.getConstructor(
                String::class.java, String::class.java, String::class.java,
                Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType,
                Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType,
                vipCommonSubTypeClass
            )
            if (hookedMethods.add(vipInfoModelConstructor)) {
                XposedGlobals.getXposedEntryInstance().hook(vipInfoModelConstructor).intercept(VipInfoModelHooker())
            }        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "挂钩VIP信息模型失败: ${t.message}")
        }
    }

    fun hookAccountManager() {
        try {
            val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
            val accountManagerClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.ACCOUNT_MANAGER.id, CLASS_ACCOUNT_MANAGER)
            )

            // 底层字段级拦截：P() 单例方法 → 反射修改实例 userModel 字段，
            // 所有读该字段的 getter 自动生效（getUserInfo/getUserName/getVipInfo 等）
            runCatching {
                val m = accountManagerClass.getDeclaredMethod("P")
                if (hookedMethods.add(m)) {
                    XposedGlobals.getXposedEntryInstance().hook(m).intercept { chain ->
                        val result = chain.proceed()
                        if (ConfigManager.prefs().getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)) {
                            AcctManagerHooker.applyUserModel(result)
                        }
                        result
                    }
                }
            }
            // 用户信息拦截：getUserInfo()（返回 AcctUserModel）兜底路径；
            // 其余用户信息 getter 直接返回模拟值兜底（防个别 getter 不读模型）。
            // 旧版 hook 的 M() 返回 boolean（登录判断），对 Boolean 取 userModel
            // 字段必抛异常 → 拦截从未生效。
            val mainHook = runCatching { accountManagerClass.getDeclaredMethod("getUserInfo") }.getOrNull()
            if (mainHook != null && hookedMethods.add(mainHook)) {
                XposedGlobals.getXposedEntryInstance().hook(mainHook).intercept(AcctManagerHooker())
            }
            // 标量 getter：直接返回模拟值
            val scalarGetters = mapOf(
                "getUserName" to { _: XposedInterface.Chain -> "XuxiaHaiyun" as Any? },
                "getDescription" to { _: XposedInterface.Chain -> "用户信息拦截成功" as Any? },
                "isFreeAd" to { _: XposedInterface.Chain -> true as Any? },
                "adVipAvailable" to { _: XposedInterface.Chain -> true as Any? },
                "getFreeAdDay" to { _: XposedInterface.Chain -> 100000f as Any? },
                "getFreeAdExpire" to { _: XposedInterface.Chain -> 218342534400L as Any? },
                "getFreeAdLeft" to { _: XposedInterface.Chain -> 218342534400L as Any? },
            )
            scalarGetters.forEach { (name, handler) ->
                runCatching {
                    val m = accountManagerClass.getDeclaredMethod(name)
                    if (hookedMethods.add(m)) {
                        XposedGlobals.getXposedEntryInstance().hook(m).intercept { chain ->
                            if (ConfigManager.prefs().getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)) {
                                handler(chain)
                            } else {
                                chain.proceed()
                            }
                        }
                    }
                }
            }
            // 对象 getter：返回构建的模拟 VIP 对象
            runCatching {
                val m = accountManagerClass.getDeclaredMethod("getVipInfo")
                if (hookedMethods.add(m)) {
                    XposedGlobals.getXposedEntryInstance().hook(m).intercept { chain ->
                        if (ConfigManager.prefs().getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)) {
                            AcctManagerHooker.buildVipInfo() ?: chain.proceed()
                        } else {
                            chain.proceed()
                        }
                    }
                }
            }
            runCatching {
                val m = accountManagerClass.getDeclaredMethod("getVipProfileShow")
                if (hookedMethods.add(m)) {
                    XposedGlobals.getXposedEntryInstance().hook(m).intercept { chain ->
                        if (ConfigManager.prefs().getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)) {
                            AcctManagerHooker.buildVipProfileShow() ?: chain.proceed()
                        } else {
                            chain.proceed()
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "挂钩账户管理器失败: ${t.message}")
        }
    }

    fun hookPrivilegeManager() {
        try {
            val targetAppClassLoader = DragonGlobals.getDragonClassLoader()
            val privilegeManagerClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.PRIVILEGE_MANAGER.id, CLASS_PRIVILEGE_MANAGER)
            )
            val vipInfoModelClass = loadTargetClass(
                targetAppClassLoader,
                className(DragonTargets.VIP_INFO_MODEL.id, CLASS_VIP_INFO_MODEL)
            )

            val methods = listOf(
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_VIP_RELATIONAL),
                privilegeManagerClass.getDeclaredMethod("isAnyVip"), // 非 native，可 hook；VIP 卡片/身份判定核心
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_NO_AD_ALL_SCENE),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_NO_AD_SHORT_SERIES),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_NO_AD_PRIVILEGE),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_NO_AD_CONSUMPTION),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_FOREVER_NO_AD),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_NO_AD, String::class.java),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_IS_VIP),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_PAY_VIP_ENTRANCE),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_PRIVILEGE, String::class.java),
                privilegeManagerClass.getDeclaredMethod(METHOD_GET_PRIVILEGE_INSTANCE),
                privilegeManagerClass.getDeclaredMethod(METHOD_UPDATE_VIP_INFO, vipInfoModelClass, Boolean::class.javaPrimitiveType),
                privilegeManagerClass.getDeclaredMethod(METHOD_CHECK_BOOK_AD_FREE, String::class.java),
            )
            methods.forEach { m ->
                if (hookedMethods.add(m)) {
                    XposedGlobals.getXposedEntryInstance().hook(m).intercept(PrivilegeManagerHooker())
                }
            }
        } catch (t: Throwable) {
            LogUtils.logWarn("HookManager", "挂钩权限管理器失败: ${t.message}")
        }
    }

    /**
     * 使用 DexKit 定位混淆方法名。
     */
    private fun locateMethod(ownerClassName: String, hints: List<MethodHint>): String? {
        return runCatching {
            LocatorService.getInstance()?.resolveMethod(ownerClassName, hints)
        }.getOrNull()
    }

    /**
     * 使用 DexKit 定位混淆方法名（返回全部候选，用于同签名多方法场景）。
     */
    private fun locateMethods(ownerClassName: String, hints: List<MethodHint>): List<String> {
        return runCatching {
            LocatorService.getInstance()?.resolveMethods(ownerClassName, hints) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    /**
     * 加载目标类的工具方法。
     */
    private fun loadTargetClass(classLoader: ClassLoader, className: String): Class<*> =
        classLoader.loadClass(className)
}
