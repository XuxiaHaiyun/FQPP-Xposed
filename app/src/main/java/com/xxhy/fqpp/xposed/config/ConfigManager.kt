package com.xxhy.fqpp.xposed.config

import android.content.Context
import android.content.SharedPreferences
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.global.XposedGlobals

/**
 * 配置管理器：配置统一保存在【模块 App 自身】的 SharedPreferences 中。
 *
 * <p>架构（libxposed API 102）：
 * <ul>
 *   <li><b>模块 App 进程</b>：通过 [modulePrefs] 读写配置（唯一写入口，配置页 ConfigActivity）；</li>
 *   <li><b>Hook 进程（com.dragon.read）</b>：通过 [prefs] 只读配置——
 *       libxposed {@code XposedModule.getRemotePreferences()} 跨进程读取模块 App 的 SP
 *       （API 102 特性），无需再向目标应用写入任何配置；</li>
 *   <li><b>回退</b>：远程配置不可用（旧框架/异常）时回退到目标应用进程内同名 SP（旧方案），保证兼容。</li>
 * </ul>
 */
object ConfigManager {

    /** 配置文件名（与模块 App 内 SharedPreferences 一致） */
    const val SP_NAME = SPConstants.SP_NAME

    /** 进程内无可读 SP 时的兜底（空实现，所有读取返回默认值）。 */
    private val EMPTY_PREFERENCES: SharedPreferences = object : SharedPreferences {
        override fun getAll(): MutableMap<String, Any?> = mutableMapOf<String, Any?>()
        override fun getString(key: String?, defValue: String?): String? = defValue
        override fun getStringSet(
            key: String?, defValues: MutableSet<String>?,
        ): MutableSet<String>? = defValues
        override fun getInt(key: String?, defValue: Int): Int = defValue
        override fun getLong(key: String?, defValue: Long): Long = defValue
        override fun getFloat(key: String?, defValue: Float): Float = defValue
        override fun getBoolean(key: String?, defValue: Boolean): Boolean = defValue
        override fun contains(key: String?): Boolean = false
        override fun edit(): SharedPreferences.Editor {
            throw UnsupportedOperationException("EMPTY_PREFERENCES is read-only")
        }
        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?,
        ) = Unit
        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?,
        ) = Unit
    }

    /**
     * 模块 App 进程：配置读写入口（唯一写入口）。
     *
     * <p>优先使用 libxposed service 库的远程偏好（经框架 Binder 读写模块 App
     * 数据目录下的 SP 文件，与 hook 进程 {@code getRemotePreferences()} 指向同一存储，
     * 且无需模块自身进程被注入）；框架服务不可用时回退本地 SP 文件
     * （hook 进程仍可通过远程偏好读取到该文件）。
     *
     * @param context 模块 App 上下文
     */
    fun modulePrefs(context: Context): SharedPreferences {
        val service = com.xxhy.fqpp.App.service
        if (service != null) {
            runCatching { return service.getRemotePreferences(SP_NAME) }
        }
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Hook 进程：只读配置。
     *
     * <p>读取顺序：
     * <ol>
     *   <li>API 102 远程配置（模块 App 的 SP，实时跨进程读取）——模块自身进程被注入时可用；</li>
     *   <li>回退目标应用进程内同名 SP（旧方案数据，兼容旧框架/模块 App 从未保存过配置）；</li>
     *   <li>进程内无可读 SP 时返回空实现（全部默认值）。</li>
     * </ol>
     */
    fun prefs(): SharedPreferences {
        val remote = runCatching {
            XposedGlobals.getXposedEntryInstance()?.getRemotePreferences(SP_NAME)
        }.getOrNull()
        if (remote != null && remote.all.isNotEmpty()) return remote
        if (remote != null) {
            frameworkLog("远程配置为空（模块 App 从未保存配置），回退目标应用进程内 SP")
        } else {
            frameworkLog("远程配置不可用（模块自身进程未注入/框架不支持），回退目标应用进程内 SP")
        }
        return runCatching {
            DragonGlobals.getDragonApplication()
                .getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        }.getOrNull() ?: EMPTY_PREFERENCES
    }

    /** 输出框架日志（LSPosed 日志窗口可见），用于排查配置链路。 */
    private fun frameworkLog(message: String) {
        runCatching {
            XposedGlobals.getXposedEntryInstance()
                ?.log(android.util.Log.WARN, "FQPP-Config", message)
        }
    }

    // ===== 类型化读取（hook 进程与模块 App 共用） =====

    fun getPort(): String =
        prefs().getString(SPConstants.PORT, SPConstants.DEFAULT_PORT) ?: SPConstants.DEFAULT_PORT

    fun getStartWithApp(): Boolean =
        prefs().getBoolean(SPConstants.START_WITH_APP, SPConstants.DEFAULT_START_WITH_APP)

    fun getBlockUpdates(): Boolean =
        prefs().getBoolean(SPConstants.BLOCK_UPDATES, SPConstants.DEFAULT_BLOCK_UPDATES)

    fun getLocalVip(): Boolean =
        prefs().getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)

    fun getDebugMode(): Boolean =
        prefs().getBoolean(SPConstants.DEBUG_MODE, SPConstants.DEFAULT_DEBUG_MODE)

    fun getUnlockBooks(): Boolean =
        prefs().getBoolean(SPConstants.UNLOCK_BOOKS, SPConstants.DEFAULT_UNLOCK_BOOKS)

    fun getBlockCrashReport(): Boolean =
        prefs().getBoolean(SPConstants.BLOCK_CRASH_REPORT, SPConstants.DEFAULT_BLOCK_CRASH_REPORT)

    fun getBlockHotUpdate(): Boolean =
        prefs().getBoolean(SPConstants.BLOCK_HOT_UPDATE, SPConstants.DEFAULT_BLOCK_HOT_UPDATE)

    fun getBlockPluginLoad(): Boolean =
        prefs().getBoolean(SPConstants.BLOCK_PLUGIN_LOAD, SPConstants.DEFAULT_BLOCK_PLUGIN_LOAD)

    fun getReadingTime(): Boolean =
        prefs().getBoolean(SPConstants.READING_TIME, SPConstants.DEFAULT_READING_TIME)

    fun getUiClean(): Boolean =
        prefs().getBoolean(SPConstants.UI_CLEAN, SPConstants.DEFAULT_UI_CLEAN)

    fun getInstantReward(): Boolean =
        prefs().getBoolean(SPConstants.INSTANT_REWARD, SPConstants.DEFAULT_INSTANT_REWARD)

    // ===== 界面净化-细分开关（分层读，逐一兜底） =====

    /** 界面净化细分配置：主开关 UI_CLEAN 关闭时一律 false，开启时取各自开关值。 */
    data class UiCleanOptions(
        val searchWord: Boolean,
        val screenAd: Boolean,
        val vipCard: Boolean,
        val functionBadge: Boolean,
        val miniGame: Boolean,
        val tabBadge: Boolean,
        val mineFeed: Boolean,
        val homeEarnTab: Boolean,
        val homeSeriesTab: Boolean,
        val hidePublish: Boolean,
        val hideMineAsset: Boolean,
        val hideMineHistory: Boolean,
        val hideMineEarnPendant: Boolean,
        val searchResult: Boolean,
        val searchAi: Boolean,
    ) {
        val anyEnabled: Boolean
            get() = searchWord || screenAd || vipCard || functionBadge ||
                miniGame || tabBadge || mineFeed ||
                homeEarnTab || homeSeriesTab || hidePublish || hideMineAsset ||
                hideMineHistory || hideMineEarnPendant || searchResult || searchAi
    }

    fun getUiCleanOptions(): UiCleanOptions {
        val sp = prefs()
        return UiCleanOptions(
            searchWord = sp.getBoolean(SPConstants.UI_CLEAN_SEARCH_WORD, SPConstants.DEFAULT_UI_CLEAN_SEARCH_WORD),
            screenAd = sp.getBoolean(SPConstants.UI_CLEAN_SCREEN_AD, SPConstants.DEFAULT_UI_CLEAN_SCREEN_AD),
            vipCard = sp.getBoolean(SPConstants.UI_CLEAN_VIP_CARD, SPConstants.DEFAULT_UI_CLEAN_VIP_CARD),
            functionBadge = sp.getBoolean(SPConstants.UI_CLEAN_FUNCTION_BADGE, SPConstants.DEFAULT_UI_CLEAN_FUNCTION_BADGE),
            miniGame = sp.getBoolean(SPConstants.UI_CLEAN_MINI_GAME, SPConstants.DEFAULT_UI_CLEAN_MINI_GAME),
            tabBadge = sp.getBoolean(SPConstants.UI_CLEAN_TAB_BADGE, SPConstants.DEFAULT_UI_CLEAN_TAB_BADGE),
            mineFeed = sp.getBoolean(SPConstants.UI_CLEAN_MINE_FEED, SPConstants.DEFAULT_UI_CLEAN_MINE_FEED),
            homeEarnTab = sp.getBoolean(SPConstants.UI_CLEAN_HOME_EARN_TAB, SPConstants.DEFAULT_UI_CLEAN_HOME_EARN_TAB),
            homeSeriesTab = sp.getBoolean(SPConstants.UI_CLEAN_HOME_SERIES_TAB, SPConstants.DEFAULT_UI_CLEAN_HOME_SERIES_TAB),
            hidePublish = sp.getBoolean(SPConstants.UI_CLEAN_HIDE_PUBLISH, SPConstants.DEFAULT_UI_CLEAN_HIDE_PUBLISH),
            hideMineAsset = sp.getBoolean(SPConstants.UI_CLEAN_HIDE_MINE_ASSET, SPConstants.DEFAULT_UI_CLEAN_HIDE_MINE_ASSET),
            hideMineHistory = sp.getBoolean(SPConstants.UI_CLEAN_HIDE_MINE_HISTORY, SPConstants.DEFAULT_UI_CLEAN_HIDE_MINE_HISTORY),
            hideMineEarnPendant = sp.getBoolean(SPConstants.UI_CLEAN_HIDE_MINE_EARN_PENDANT, SPConstants.DEFAULT_UI_CLEAN_HIDE_MINE_EARN_PENDANT),
            searchResult = sp.getBoolean(SPConstants.UI_CLEAN_SEARCH_RESULT, SPConstants.DEFAULT_UI_CLEAN_SEARCH_RESULT),
            searchAi = sp.getBoolean(SPConstants.UI_CLEAN_SEARCH_AI, SPConstants.DEFAULT_UI_CLEAN_SEARCH_AI),
        )
    }

    // ===== 写入（仅模块 App 进程调用） =====

    fun save(
        context: Context,
        port: String,
        startWithApp: Boolean,
        blockUpdates: Boolean,
        localVip: Boolean,
        debugMode: Boolean,
        unlockBooks: Boolean,
        blockCrashReport: Boolean,
        blockHotUpdate: Boolean,
        blockPluginLoad: Boolean,
        readingTime: Boolean,
        uiClean: Boolean,
        uiCleanOptions: UiCleanOptions = UiCleanOptions(true, true, false, true, true, true, true, true, true, true, true, false, false, true, true),
        instantReward: Boolean,
    ) {
        modulePrefs(context).edit()
            .putString(SPConstants.PORT, port)
            .putBoolean(SPConstants.START_WITH_APP, startWithApp)
            .putBoolean(SPConstants.BLOCK_UPDATES, blockUpdates)
            .putBoolean(SPConstants.LOCAL_VIP, localVip)
            .putBoolean(SPConstants.DEBUG_MODE, debugMode)
            .putBoolean(SPConstants.UNLOCK_BOOKS, unlockBooks)
            .putBoolean(SPConstants.BLOCK_CRASH_REPORT, blockCrashReport)
            .putBoolean(SPConstants.BLOCK_HOT_UPDATE, blockHotUpdate)
            .putBoolean(SPConstants.BLOCK_PLUGIN_LOAD, blockPluginLoad)
            .putBoolean(SPConstants.READING_TIME, readingTime)
            .putBoolean(SPConstants.UI_CLEAN, uiClean)
            .putBoolean(SPConstants.UI_CLEAN_SEARCH_WORD, uiCleanOptions.searchWord)
            .putBoolean(SPConstants.UI_CLEAN_SCREEN_AD, uiCleanOptions.screenAd)
            .putBoolean(SPConstants.UI_CLEAN_VIP_CARD, uiCleanOptions.vipCard)
            .putBoolean(SPConstants.UI_CLEAN_FUNCTION_BADGE, uiCleanOptions.functionBadge)
            .putBoolean(SPConstants.UI_CLEAN_MINI_GAME, uiCleanOptions.miniGame)
            .putBoolean(SPConstants.UI_CLEAN_TAB_BADGE, uiCleanOptions.tabBadge)
            .putBoolean(SPConstants.UI_CLEAN_MINE_FEED, uiCleanOptions.mineFeed)
            .putBoolean(SPConstants.UI_CLEAN_HOME_EARN_TAB, uiCleanOptions.homeEarnTab)
            .putBoolean(SPConstants.UI_CLEAN_HOME_SERIES_TAB, uiCleanOptions.homeSeriesTab)
            .putBoolean(SPConstants.UI_CLEAN_HIDE_PUBLISH, uiCleanOptions.hidePublish)
            .putBoolean(SPConstants.UI_CLEAN_HIDE_MINE_ASSET, uiCleanOptions.hideMineAsset)
            .putBoolean(SPConstants.UI_CLEAN_HIDE_MINE_HISTORY, uiCleanOptions.hideMineHistory)
            .putBoolean(SPConstants.UI_CLEAN_HIDE_MINE_EARN_PENDANT, uiCleanOptions.hideMineEarnPendant)
            .putBoolean(SPConstants.UI_CLEAN_SEARCH_RESULT, uiCleanOptions.searchResult)
            .putBoolean(SPConstants.UI_CLEAN_SEARCH_AI, uiCleanOptions.searchAi)
            .putBoolean(SPConstants.INSTANT_REWARD, instantReward)
            .apply()
    }
}
