package com.xxhy.fqpp.xposed

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Display
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.xxhy.fqpp.xposed.global.XposedGlobals
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface
import java.text.SimpleDateFormat
import java.util.ArrayDeque
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ImGui风格的Xposed日志悬浮窗
 * 特点：
 * 1. 无需悬浮窗权限（TYPE_APPLICATION）
 * 2. 跨Activity显示，日志连贯
 * 3. 自动跟随Activity切换，无感迁移
 * 4. 收缩状态记忆（跨Activity保持）
 * 5. 横竖屏切换自动适配边界
 * 6. 暂停/恢复日志记录功能
 * 7. 关键词过滤功能
 * 8. 日志分级显示（DEBUG/INFO/WARN/ERROR），可按等级过滤
 * 9. MD3 深色风格，日志格式简洁
 */
object ImGuiOverlay {

    /** 日志级别（ordinal 即严重程度，用于等级过滤） */
    enum class LogLevel(val label: String, val color: Int) {
        DEBUG("D", COLOR_LOG_DEBUG),
        INFO("I", COLOR_LOG_INFO),
        WARN("W", COLOR_LOG_WARN),
        ERROR("E", COLOR_LOG_ERROR),
    }

    private class LogEntry(val time: String, val tag: String?, val message: String?, val level: LogLevel)

    private val globalLogList = ArrayDeque<LogEntry>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private const val MAX_LOG_LINES = 500 // 内存中存储的最大日志行数
    private const val UI_UPDATE_BATCH_DELAY_MS = 200 // UI 批量更新的延迟，例如100毫秒
    private val pendingUiUpdate = AtomicBoolean(false)
    private const val MAX_DISPLAY_LOG_LENGTH = 10000 // LogTextView 最大显示字符数

    @Volatile
    private var isLoggingPaused = false

    @Volatile
    private var currentKeywordFilter = "" // 空字符串表示无过滤

    /** 等级过滤下限（当前只显示 >= 该级别的日志；DEBUG 即显示全部） */
    @Volatile
    private var currentMinLevel = LogLevel.DEBUG

    private val levelOptions = listOf(LogLevel.DEBUG, LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR)

    private var relativeX = 0.05f
    private var relativeY = 0.15f
    private var isGloballyCollapsed = false
    private var lastOrientation = Configuration.ORIENTATION_UNDEFINED

    // 窗口尺寸：展开态为日志面板，折叠态为半透明悬浮球
    private val expandedWidth = dp(440)
    private val expandedHeight = dp(500)
    private val collapsedSize = dp(56)

    // MD3 深色调色板（Material 3 dark 语义色），背景 90% 不透明以便透出下方内容
    private const val COLOR_BG = 0xE61C1B1F.toInt() // surfaceContainerLowest
    private const val COLOR_TITLE_BG = 0xFF2B2930.toInt() // surfaceContainer
    private const val COLOR_TEXT = 0xFFE6E0E9.toInt() // onSurface
    private const val COLOR_TEXT_DIM = 0xFF938F99.toInt() // onSurfaceVariant
    private const val COLOR_LOG_DEBUG = 0xFF9AA0A6.toInt() // 中性灰蓝
    private const val COLOR_LOG_INFO = 0xFFA8C7FA.toInt() // primary80 浅蓝
    private const val COLOR_LOG_WARN = 0xFFFFC46B.toInt() // amber
    private const val COLOR_LOG_ERROR = 0xFFF2B8B5.toInt() // error80 浅红
    private const val COLOR_BUTTON = 0xFF37343B.toInt() // surfaceContainerHigh
    private const val COLOR_BORDER = 0xFF49454F.toInt() // outlineVariant

    private const val COLOR_BUTTON_ACTIVE = 0xFF4F3B7C.toInt() // secondaryContainer 深紫
    private const val COLOR_BUTTON_INACTIVE = COLOR_BUTTON

    private var context: Context? = null
    private var windowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null
    private var rootView: FrameLayout? = null
    private var logTextView: TextView? = null
    private var scrollView: ScrollView? = null
    private var contentView: FrameLayout? = null
    private var titleBar: LinearLayout? = null
    private var container: LinearLayout? = null // 最外层内容容器，将应用圆角和背景色
    private var collapseBtn: Button? = null
    private var pauseBtn: Button? = null
    private var filterBtn: Button? = null
    private var levelBtn: Button? = null
    private var copyBtn: Button? = null
    private var ballView: TextView? = null // 折叠态的 "_<" 悬浮球
    private var isCollapsed = false
    private var originalHeight = 0 // 展开时的高度
    private var windowWidth = 0 // 展开时的宽度
    private var currentActivity: Activity? = null
    private var screenWidth = 0
    private var screenHeight = 0

    /** 最近一次出现的 Activity（hide/切页后仍保留），用于日志自动补挂浮窗。 */
    @Volatile
    private var lastKnownActivity: Activity? = null
    private val lazyAttachScheduled = AtomicBoolean(false)

    @JvmStatic
    fun inject(loadPackageParam: XposedModuleInterface.PackageLoadedParam) {
        try {
            XposedGlobals.getXposedEntryInstance()
                .hook(Activity::class.java.getDeclaredMethod("onResume"))
                .intercept { chain ->
                    val activity = chain.getThisObject() as Activity
                    ImGuiOverlay.show(activity)
                    chain.proceed()
                }

            XposedGlobals.getXposedEntryInstance()
                .hook(Activity::class.java.getDeclaredMethod("onConfigurationChanged", Configuration::class.java))
                .intercept { chain ->
                    val activity = chain.getThisObject() as Activity
                    val newConfig = chain.getArg(0) as Configuration
                    ImGuiOverlay.onConfigurationChanged(activity, newConfig)
                    chain.proceed()
                }

            // onDestroy：不再 hide()——TYPE_APPLICATION 窗口跨 Activity 保留，
            // 避免 Activity 切换（如 Splash→Main）瞬间 removeView 导致悬浮窗"闪一下就没了"；
            // 应用不可见时系统会自动移除该窗口，下次 show() 检测 parent==null 会重建。
            XposedGlobals.getXposedEntryInstance()
                .hook(Activity::class.java.getDeclaredMethod("onDestroy"))
                .intercept { chain -> chain.proceed() }

            ImGuiOverlay.log("Module", "Module loaded: " + loadPackageParam.getPackageName())
        } catch (t: Throwable) {
            logToFramework("ImGuiOverlay: Failed to inject hooks: " + t)
        }
    }

    /**
     * Hook android.util.Log 类，捕获所有系统日志
     */
    @JvmStatic
    fun hookSystemLog(loadPackageParam: XposedModuleInterface.PackageLoadedParam) {
        val logClass = "android.util.Log"

        try {
            val logHook = XposedInterface.Hooker { chain ->
                val tag = chain.getArg(0) as String
                val msg = chain.getArg(1) as String
                val tr = if (chain.getArgs().size > 2 && chain.getArg(2) is Throwable) chain.getArg(2) as Throwable else null
                val level: LogLevel

                val methodName = chain.getExecutable().name
                when (methodName) {
                    "v" -> level = LogLevel.DEBUG
                    "d" -> level = LogLevel.DEBUG
                    "i" -> level = LogLevel.INFO
                    "w" -> level = LogLevel.WARN
                    "e" -> level = LogLevel.ERROR
                    "wtf" -> level = LogLevel.ERROR
                    else -> level = LogLevel.INFO
                }

                var fullMessage = "$tag: $msg"
                if (tr != null) {
                    fullMessage += "\n" + getStackTraceString(tr)
                }
                addLogEntry(level, tag, fullMessage)
                chain.proceed()
            }

            val logMethods = arrayOf("v", "d", "i", "w", "e", "wtf")
            val classLoader = loadPackageParam.getDefaultClassLoader()
            val logClazz = Class.forName(logClass, false, classLoader)

            for (method in logMethods) {
                // Hook method (String, String)
                XposedGlobals.getXposedEntryInstance()
                    .hook(logClazz.getDeclaredMethod(method, String::class.java, String::class.java))
                    .intercept(logHook)
                // Hook method (String, String, Throwable)
                XposedGlobals.getXposedEntryInstance()
                    .hook(logClazz.getDeclaredMethod(method, String::class.java, String::class.java, Throwable::class.java))
                    .intercept(logHook)
            }

            // 特殊处理 Log.w (String, Throwable) 和 Log.wtf (String, Throwable)
            XposedGlobals.getXposedEntryInstance()
                .hook(logClazz.getDeclaredMethod("w", String::class.java, Throwable::class.java))
                .intercept { chain ->
                    val tag = chain.getArg(0) as String
                    val tr = chain.getArg(1) as Throwable
                    addLogEntry(LogLevel.WARN, tag, "$tag: " + getStackTraceString(tr))
                    chain.proceed()
                }

            XposedGlobals.getXposedEntryInstance()
                .hook(logClazz.getDeclaredMethod("wtf", String::class.java, Throwable::class.java))
                .intercept { chain ->
                    val tag = chain.getArg(0) as String
                    val tr = chain.getArg(1) as Throwable
                    addLogEntry(LogLevel.ERROR, tag, "$tag: " + getStackTraceString(tr))
                    chain.proceed()
                }

        } catch (e: Exception) {
            logToFramework("ImGuiOverlay: Hook System Log failed: " + e.message)
        }
    }

    /** 通过 libxposed 框架日志接口输出（入口实例未就绪时静默降级）。 */
    private fun logToFramework(message: String) {
        try {
            XposedGlobals.getXposedEntryInstance()
                .log(Log.INFO, "ImGuiOverlay", message)
        } catch (ignored: Throwable) {
        }
    }

    /**
     * 将Throwable转换为字符串
     */
    @JvmStatic
    fun getStackTraceString(tr: Throwable): String {
        return Log.getStackTraceString(tr)
    }

    @JvmStatic
    fun show(activity: Activity?) {
        if (activity == null || activity.isFinishing || activity.isDestroyed) return
        lastKnownActivity = activity

        mainHandler.post {
            if (activity.isFinishing || activity.isDestroyed) return@post

            val currentOrientation = activity.resources.configuration.orientation
            val rotated = lastOrientation != Configuration.ORIENTATION_UNDEFINED
                && lastOrientation != currentOrientation
            lastOrientation = currentOrientation

            // 窗口已挂载（已 attach）：跨 Activity 保留，不重建（避免切换闪没）。
            // TYPE_APPLICATION 窗口由 WindowManagerGlobal 全局管理，可跨 Activity 存活。
            if (rootView != null && rootView!!.parent != null) {
                if (rotated) {
                    handleRotation()
                }
                if (rootView!!.visibility != View.VISIBLE) {
                    rootView!!.visibility = View.VISIBLE
                    try {
                        windowManager!!.updateViewLayout(rootView, params)
                    } catch (ignored: Exception) {
                        logToFramework("ImGuiOverlay: Error updating view visibility: " + ignored.message)
                    }
                }
                currentActivity = activity
                // 补刷日志：修复"日志列表为空、需切到其他界面才看到"（init 之外的累积日志
                // 因 hook 线程可见性问题未触发 UI 刷新时，这里兜底强制刷新）
                refreshLogViewContent()
                scrollToBottom()
                return@post
            }

            // rootView 存在但未 attach（addView 曾失败/应用不可见被系统移除）：清理后重建
            if (rootView != null && windowManager != null) {
                try {
                    saveRelativePosition()
                    windowManager!!.removeView(rootView)
                } catch (ignored: Exception) {
                    logToFramework("ImGuiOverlay: Error removing old view: " + ignored.message)
                }
                rootView = null
                currentActivity = null
            }

            init(activity)
        }
    }

    @JvmStatic
    fun onConfigurationChanged(activity: Activity, newConfig: Configuration) {
        if (currentActivity == activity) {
            handleRotation()
        }
    }

    @JvmStatic
    fun log(tag: String, message: String) {
        addLogEntry(LogLevel.INFO, tag, message)
    }

    @JvmStatic
    fun logError(tag: String, message: String) {
        addLogEntry(LogLevel.ERROR, tag, message)
    }

    @JvmStatic
    fun logWarn(tag: String, message: String) {
        addLogEntry(LogLevel.WARN, tag, message)
    }

    @JvmStatic
    fun logDebug(tag: String, message: String) {
        addLogEntry(LogLevel.DEBUG, tag, message)
    }

    private fun addLogEntry(level: LogLevel, tag: String, message: String?) {
        if (isLoggingPaused) {
            return
        }

        val MAX_LINE_LENGTH = 500
        var processedMsg = message
        if (message != null && message.length > MAX_LINE_LENGTH) {
            processedMsg = message.substring(0, MAX_LINE_LENGTH) + "..."
        }

        val time = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date()) // 简洁时间，不含毫秒
        val entry = LogEntry(time, tag, processedMsg, level)

        synchronized(globalLogList) {
            globalLogList.addLast(entry)
            if (globalLogList.size > MAX_LOG_LINES) {
                globalLogList.removeFirst()
            }
        }

        scheduleLogUpdate()
        tryLazyAttach()
    }

    /**
     * 浮窗尚未挂载时（模块加载早期/切页间隙），尝试用最近一次 Activity 自动补挂，
     * 避免"要跳转一个页面才能看到日志"。幂等：仅当 rootView 为空且存在可用的上一次 Activity 时触发。
     */
    private fun tryLazyAttach() {
        if (rootView != null) return
        if (!lazyAttachScheduled.compareAndSet(false, true)) return
        mainHandler.post {
            lazyAttachScheduled.set(false)
            val act = lastKnownActivity ?: return@post
            if (act.isFinishing || act.isDestroyed) return@post
            show(act)
        }
    }

    private val sRefreshLogsRunnable = Runnable {
        try {
            if (logTextView != null) {
                refreshLogViewContent()
                scrollToBottom()
            }
        } catch (t: Throwable) {
            logToFramework("ImGuiOverlay: Log UI refresh error: " + t.message)
        }
        pendingUiUpdate.set(false)
    }

    private fun scheduleLogUpdate() {
        // 无条件调度：由 sRefreshLogsRunnable 内部判断 logTextView 是否就绪。
        // 此前在此检查 logTextView（hook 线程读到未同步的旧值 null 直接 return），
        // 导致挂载后的新日志永不刷新 UI（表现为"日志列表为空，需切界面才看到"）。
        mainHandler.removeCallbacks(sRefreshLogsRunnable)
        mainHandler.postDelayed(sRefreshLogsRunnable, UI_UPDATE_BATCH_DELAY_MS.toLong())
    }

    private fun init(activity: Activity) {
        this.currentActivity = activity
        this.context = activity
        this.windowManager = activity.windowManager
        // 恢复记忆的位置与折叠状态（进程重启后仍保留）
        loadState()
        this.isCollapsed = isGloballyCollapsed
        this.windowWidth = if (isCollapsed) collapsedSize else expandedWidth
        this.originalHeight = expandedHeight

        updateScreenSize()
        createViews()
        setupWindowParams()
        setupDragLogic()
        applyRelativePosition() // 应用相对位置时会用到 windowWidth 和 originalHeight

        try {
            windowManager!!.addView(rootView, params)
            updatePauseButtonState()
            updateFilterButtonState()
            updateLevelButtonState()

            refreshLogViewContent()
            scrollToBottom()

            logToFramework("ImGuiOverlay: Attached to: " + activity.javaClass.simpleName +
                (if (isCollapsed) " [Collapsed]" else " [Expanded]"))

        } catch (e: Exception) {
            Log.e("ImGuiOverlay", "Failed to add view: " + e.message, e)
            // 清理状态，避免残留 rootView/currentActivity 导致后续 show() 走"已挂载"分支而不再重建
            rootView = null
            currentActivity = null
        }
    }

    private fun updateScreenSize() {
        val metrics = DisplayMetrics()
        val display: Display = windowManager!!.defaultDisplay
        display.getRealMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
    }

    private fun handleRotation() {
        if (rootView == null || windowManager == null) return

        saveRelativePosition()
        updateScreenSize()
        applyRelativePosition()

        if (rootView!!.parent != null) {
            try {
                windowManager!!.updateViewLayout(rootView, params)
            } catch (e: IllegalArgumentException) {
                logToFramework("ImGuiOverlay: handleRotation failed, view not attached: " + e.message)
            }
        }
    }

    private fun applyRelativePosition() {
        val windowHeight = if (isCollapsed) collapsedSize else originalHeight

        val maxX = Math.max(0, screenWidth - windowWidth)
        val maxY = Math.max(0, screenHeight - windowHeight)

        params!!.x = (relativeX * maxX).toInt()
        params!!.y = (relativeY * maxY).toInt()

        ensureInBounds()
    }

    private fun saveRelativePosition() {
        val windowHeight = if (isCollapsed) collapsedSize else originalHeight

        val maxX = screenWidth - windowWidth
        val maxY = screenHeight - windowHeight

        relativeX = if (maxX > 0) params!!.x.toFloat() / maxX else 0f
        relativeY = if (maxY > 0) params!!.y.toFloat() / maxY else 0f

        relativeX = Math.max(0f, Math.min(1f, relativeX))
        relativeY = Math.max(0f, Math.min(1f, relativeY))
    }

    private fun ensureInBounds() {
        val windowHeight = if (isCollapsed) collapsedSize else originalHeight

        params!!.x = Math.max(0, Math.min(params!!.x, screenWidth - windowWidth))
        params!!.y = Math.max(0, Math.min(params!!.y, screenHeight - windowHeight))
    }

    private fun createViews() {
        rootView = FrameLayout(context!!)
        // rootView 不再直接设置背景，而是由其子视图 `container` 接管圆角和背景色
        // rootView.setBackground(createBackground()); // 移除此行

        container = LinearLayout(context)
        container!!.orientation = LinearLayout.VERTICAL
        // 为 container 设置圆角背景
        container!!.background = createBackground()
        // 启用剪裁，确保内容在圆角范围内显示 (API 21+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            container!!.clipToOutline = true
        }

        rootView!!.addView(container, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        // 折叠悬浮球：半透明圆球显示 "_<"，点击展开
        ballView = TextView(context)
        ballView!!.setText("_<")
        ballView!!.setTextColor(COLOR_TEXT_DIM)
        ballView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        ballView!!.setTypeface(Typeface.DEFAULT_BOLD)
        ballView!!.gravity = Gravity.CENTER
        ballView!!.background = createBallBackground()
        // 点击/拖动由 setupDragLogic 统一处理（轻触展开，拖动移动窗口）
        ballView!!.visibility = if (isCollapsed) View.VISIBLE else View.GONE
        rootView!!.addView(ballView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        titleBar = LinearLayout(context)
        titleBar!!.orientation = LinearLayout.HORIZONTAL
        titleBar!!.setBackgroundColor(COLOR_TITLE_BG)
        titleBar!!.setPadding(dp(6), dp(4), dp(6), dp(4))
        titleBar!!.gravity = Gravity.CENTER_VERTICAL
        titleBar!!.visibility = if (isCollapsed) View.GONE else View.VISIBLE

        val titleText = TextView(context)
        titleText.setText("FQ++")
        titleText.setTextColor(COLOR_TEXT_DIM)
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        titleText.setTypeface(Typeface.MONOSPACE)
        val titleParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        titleBar!!.addView(titleText, titleParams)

        pauseBtn = createButton(if (isLoggingPaused) "▶" else "||", 44, 26)
        pauseBtn!!.setOnClickListener { togglePauseLogging() }
        titleBar!!.addView(pauseBtn)

        levelBtn = createButton(levelFilterLabel(), 48, 26)
        levelBtn!!.setOnClickListener { toggleLevelFilter() }
        titleBar!!.addView(levelBtn)

        filterBtn = createButton(if (currentKeywordFilter.isEmpty()) "F" else "×F", 40, 26)
        filterBtn!!.setOnClickListener { toggleKeywordFilter() }
        titleBar!!.addView(filterBtn)

        copyBtn = createButton("CP", 44, 26)
        copyBtn!!.setOnClickListener { copyLogs() }
        titleBar!!.addView(copyBtn)

        val clearBtn = createButton("CLR", 44, 26)
        clearBtn.setOnClickListener { clearLogs() }
        titleBar!!.addView(clearBtn)

        collapseBtn = createButton("—", 36, 26)
        collapseBtn!!.setOnClickListener { toggleCollapse() }
        titleBar!!.addView(collapseBtn)

        container!!.addView(titleBar, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(38)
        ))

        contentView = FrameLayout(context!!)
        val contentParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            if (isCollapsed) 0 else ViewGroup.LayoutParams.MATCH_PARENT,
            if (isCollapsed) 0f else 1f
        )
        contentView!!.layoutParams = contentParams

        if (isCollapsed) {
            contentView!!.visibility = View.GONE
        }

        container!!.addView(contentView)

        scrollView = ScrollView(context)
        // ScrollView 背景设为透明，以便下面的 container 背景可见
        scrollView!!.setBackgroundColor(Color.TRANSPARENT)
        contentView!!.addView(scrollView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        logTextView = TextView(context)
        logTextView!!.setTextColor(COLOR_LOG_INFO)
        logTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        logTextView!!.setTypeface(Typeface.MONOSPACE)
        logTextView!!.setPadding(dp(8), dp(8), dp(8), dp(8))
        scrollView!!.addView(logTextView)
    }

    private fun updatePauseButtonState() {
        if (pauseBtn != null) {
            pauseBtn!!.setText(if (isLoggingPaused) "▶ RESUME" else "|| PAUSE")
            pauseBtn!!.setBackgroundColor(if (isLoggingPaused) COLOR_BUTTON_ACTIVE else COLOR_BUTTON_INACTIVE)
        }
    }

    private fun updateFilterButtonState() {
        if (filterBtn != null) {
            if (currentKeywordFilter.isEmpty()) {
                filterBtn!!.setText("F FILTER")
                filterBtn!!.setBackgroundColor(COLOR_BUTTON_INACTIVE)
            } else {
                filterBtn!!.setText("X CLEAR (" + currentKeywordFilter + ")")
                filterBtn!!.setBackgroundColor(COLOR_BUTTON_ACTIVE)
            }
        }
    }

    private fun levelFilterLabel(): String {
        return when (currentMinLevel) {
            LogLevel.DEBUG -> "ALL"
            LogLevel.INFO -> "I+"
            LogLevel.WARN -> "W+"
            LogLevel.ERROR -> "E+"
        }
    }

    private fun updateLevelButtonState() {
        if (levelBtn != null) {
            levelBtn!!.setText(levelFilterLabel())
            val active = currentMinLevel != LogLevel.DEBUG
            levelBtn!!.setBackgroundColor(if (active) COLOR_BUTTON_ACTIVE else COLOR_BUTTON_INACTIVE)
        }
    }

    private fun toggleLevelFilter() {
        val next = levelOptions[(levelOptions.indexOf(currentMinLevel) + 1) % levelOptions.size]
        currentMinLevel = next
        updateLevelButtonState()
        refreshLogViewContent()
        scrollToBottom()
        logToFramework("ImGuiOverlay: Level filter set to: " + levelFilterLabel())
    }

    private fun togglePauseLogging() {
        isLoggingPaused = !isLoggingPaused
        updatePauseButtonState()

        if (!isLoggingPaused) {
            scheduleLogUpdate()
            logToFramework("ImGuiOverlay,Logging resumed.")
        } else {
            logToFramework("ImGuiOverlay,Logging paused.")
        }
    }

    private fun toggleKeywordFilter() {
        if (currentKeywordFilter.isEmpty()) {
            showFilterInputDialog()
        } else {
            currentKeywordFilter = ""
            updateFilterButtonState()
            refreshLogViewContent()
            scrollToBottom()
            logToFramework("ImGuiOverlay,Keyword filter cleared.")
        }
    }

    private fun showFilterInputDialog() {
        if (currentActivity == null || currentActivity!!.isFinishing || currentActivity!!.isDestroyed) {
            logToFramework("ImGuiOverlay,Cannot show filter dialog, activity is not valid.")
            return
        }

        mainHandler.post {
            if (currentActivity == null || currentActivity!!.isFinishing || currentActivity!!.isDestroyed) {
                return@post
            }

            val input = EditText(currentActivity)
            input.setSingleLine(true)
            input.setText(currentKeywordFilter)

            AlertDialog.Builder(currentActivity, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Filter Logs by Keyword")
                .setMessage("Enter keyword to filter logs (case insensitive):")
                .setView(input)
                .setPositiveButton("Apply") { _, _ ->
                    val newKeyword = input.text.toString().trim()
                    if (!newKeyword.equals(currentKeywordFilter, ignoreCase = true)) {
                        currentKeywordFilter = newKeyword
                        updateFilterButtonState()
                        refreshLogViewContent()
                        scrollToBottom()
                        if (currentKeywordFilter.isEmpty()) {
                            logToFramework("ImGuiOverlay,Keyword filter cleared via dialog.")
                        } else {
                            logToFramework("ImGuiOverlay,Keyword filter set to: '" + currentKeywordFilter + "'")
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                .show()
        }
    }

    private fun createButton(text: String, widthDp: Int, heightDp: Int): Button {
        val btn = Button(context)
        btn.text = text
        btn.setTextColor(COLOR_TEXT)
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        btn.setBackgroundColor(COLOR_BUTTON)
        btn.setPadding(0, 0, 0, 0)
        btn.gravity = Gravity.CENTER
        val params = LinearLayout.LayoutParams(dp(widthDp), dp(heightDp))
        params.leftMargin = dp(4)
        btn.layoutParams = params
        return btn
    }

    private fun createBackground(): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(COLOR_BG)
        drawable.cornerRadius = dp(14).toFloat() // MD3 大圆角
        drawable.setStroke(dp(1), COLOR_BORDER)
        return drawable
    }

    /** 折叠悬浮球：正圆 + 半透明深色 + 细描边 */
    private fun createBallBackground(): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(0xB31C1B1F.toInt()) // 70% 不透明
        drawable.cornerRadius = dp(28).toFloat() // 正圆（球径 56dp）
        drawable.setStroke(dp(1), COLOR_BORDER)
        return drawable
    }

    private fun setupWindowParams() {
        params = WindowManager.LayoutParams(
            windowWidth, // 使用更新后的宽度
            if (isGloballyCollapsed) dp(40) else originalHeight, // 使用更新后的高度
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT // 保持半透明像素格式，以便COLOR_BG的透明度生效
        )
        params!!.gravity = Gravity.TOP or Gravity.LEFT
        params!!.alpha = 1.0f // WindowManager的alpha设为1.0，因为背景色COLOR_BG已含有透明度

        // 移除了 Android 12 (API 31) 及以上版本对背景模糊的设置，避免模糊主界面
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        //     params.flags |= WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        //     params.blurBehindRadius = dp(30);
        // }
    }

    private fun setupDragLogic() {
        attachDrag(titleBar!!) { toggleCollapse() }
        attachDrag(ballView!!) { toggleCollapse() }
    }

    /**
     * 通用拖动 + 轻触点击：按住拖动窗口（带相对位置记忆），
     * 位移超过 touchSlop 视为拖动，否则视为轻触触发 clickAction。
     */
    private fun attachDrag(view: View, clickAction: () -> Unit) {
        val touchSlop = ViewConfiguration.get(context!!).scaledTouchSlop
        view.setOnTouchListener(object : View.OnTouchListener {
            private var startX = 0f
            private var startY = 0f
            private var initialX = 0
            private var initialY = 0
            private var moved = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.rawX
                        startY = event.rawY
                        initialX = params!!.x
                        initialY = params!!.y
                        moved = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - startX
                        val dy = event.rawY - startY
                        if (Math.abs(dx) > touchSlop || Math.abs(dy) > touchSlop) {
                            moved = true
                        }
                        if (moved) {
                            params!!.x = initialX + dx.toInt()
                            params!!.y = initialY + dy.toInt()

                            ensureInBounds()

                            if (rootView != null && rootView!!.parent != null) {
                                try {
                                    windowManager!!.updateViewLayout(rootView, params)
                                } catch (e: IllegalArgumentException) {
                                    logToFramework("ImGuiOverlay: Dragging failed, view not attached: " + e.message)
                                }
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (moved) {
                            saveRelativePosition()
                            saveState()
                        } else {
                            clickAction()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    // ===== 位置与折叠状态持久化（进程重启后恢复记忆） =====

    private const val STATE_SP_NAME = "fqpp_overlay_state"

    private fun saveState() {
        runCatching {
            val sp = context?.getSharedPreferences(STATE_SP_NAME, Context.MODE_PRIVATE) ?: return
            sp.edit()
                .putFloat("relX", relativeX)
                .putFloat("relY", relativeY)
                .putBoolean("collapsed", isGloballyCollapsed)
                .apply()
        }
    }

    private fun loadState() {
        runCatching {
            val sp = context?.getSharedPreferences(STATE_SP_NAME, Context.MODE_PRIVATE) ?: return
            relativeX = sp.getFloat("relX", 0.05f)
            relativeY = sp.getFloat("relY", 0.15f)
            isGloballyCollapsed = sp.getBoolean("collapsed", false)
        }
    }

    private fun toggleCollapse() {
        isCollapsed = !isCollapsed
        isGloballyCollapsed = isCollapsed
        saveRelativePosition() // 确保折叠状态下的位置也被保存

        if (isCollapsed) {
            // 折叠为悬浮球
            windowWidth = collapsedSize
            contentView!!.visibility = View.GONE
            titleBar!!.visibility = View.GONE
            ballView!!.visibility = View.VISIBLE
            params!!.width = collapsedSize
            params!!.height = collapsedSize
            container!!.background = createBallBackground()
        } else {
            // 展开为日志面板
            windowWidth = expandedWidth
            titleBar!!.visibility = View.VISIBLE
            ballView!!.visibility = View.GONE
            contentView!!.visibility = View.VISIBLE
            params!!.width = expandedWidth
            params!!.height = originalHeight
            container!!.background = createBackground()
        }

        if (collapseBtn != null) {
            collapseBtn!!.setText("—")
        }

        if (rootView != null && rootView!!.parent != null) {
            container!!.requestLayout()
            try {
                windowManager!!.updateViewLayout(rootView, params)
            } catch (e: IllegalArgumentException) {
                logToFramework("ImGuiOverlay: toggleCollapse failed, view not attached: " + e.message)
            }
        }
        saveState() // 持久化折叠状态
        if (!isCollapsed) {
            scrollToBottom()
        }
    }

    private fun clearLogs() {
        synchronized(globalLogList) {
            globalLogList.clear()
        }
        mainHandler.post {
            if (logTextView != null) {
                logTextView!!.setText("")
            }
            pendingUiUpdate.set(false)
        }
        logToFramework("ImGuiOverlay,All logs cleared.")
    }

    /** 复制当前可见（过滤后）日志到剪贴板。 */
    private fun copyLogs() {
        val sb = StringBuilder()
        val filterLowerCase = currentKeywordFilter.lowercase(Locale.US)
        synchronized(globalLogList) {
            for (entry in globalLogList) {
                if (entry.level.ordinal < currentMinLevel.ordinal) continue
                if (filterLowerCase.isNotEmpty()) {
                    val msgMatch = entry.message?.lowercase(Locale.US)?.contains(filterLowerCase) == true
                    val tagMatch = entry.tag?.lowercase(Locale.US)?.contains(filterLowerCase) == true
                    if (!msgMatch && !tagMatch) continue
                }
                sb.append(formatLogEntry(entry))
            }
        }
        if (sb.isEmpty()) {
            logToFramework("ImGuiOverlay: No logs to copy.")
            return
        }
        runCatching {
            val ctx = context
            if (ctx != null) {
                val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                cm.setPrimaryClip(android.content.ClipData.newPlainText("FQ++ Logs", sb.toString()))
                logToFramework("ImGuiOverlay: Logs copied (" + sb.length + " chars)")
            }
        }
    }

    /**
     * 从 globalLogList 重新构建整个 log TextView 内容。
     * 由批量 UI 更新任务或过滤器/暂停状态改变时调用。
     */
    private fun refreshLogViewContent() {
        if (logTextView == null) return

        val ssb = SpannableStringBuilder()
        val filterLowerCase = currentKeywordFilter.lowercase(Locale.US)

        synchronized(globalLogList) {
            for (entry in globalLogList) {
                // 等级过滤：只显示 >= 当前等级下限的日志
                if (entry.level.ordinal < currentMinLevel.ordinal) {
                    continue
                }
                // 关键词过滤（与等级过滤叠加）
                if (filterLowerCase.isNotEmpty()) {
                    val msgMatch = entry.message?.lowercase(Locale.US)?.contains(filterLowerCase) == true
                    val tagMatch = entry.tag?.lowercase(Locale.US)?.contains(filterLowerCase) == true
                    if (!msgMatch && !tagMatch) {
                        continue
                    }
                }

                ssb.append(formatLogEntry(entry))
                if (ssb.length > MAX_DISPLAY_LOG_LENGTH) {
                    logToFramework("ImGuiOverlay: Log text content exceeded MAX_DISPLAY_LOG_LENGTH, truncating display.")
                    // 找到最后一个换行符，从那里开始截断，保留更多完整行
                    val lastLineBreak = ssb.toString().lastIndexOf('\n', ssb.length - (MAX_DISPLAY_LOG_LENGTH / 2))
                    if (lastLineBreak != -1) {
                        ssb.delete(0, lastLineBreak + 1)
                    } else {
                        // 如果没有换行符或太靠前，则直接从中间截取
                        ssb.delete(0, ssb.length - (MAX_DISPLAY_LOG_LENGTH / 2))
                    }
                    ssb.insert(0, "... (truncated)\n")
                }
            }
        }
        logTextView!!.setText(ssb)
    }

    /**
     * 简洁 MD3 格式：`12:34:56 I tag: message`
     * 时间与标签用中性色，级别标记与正文按级别着色。
     */
    private fun formatLogEntry(entry: LogEntry): SpannableString {
        val text = String.format(
            Locale.US, "%s %s %s: %s\n",
            entry.time, entry.level.label, entry.tag, entry.message
        )
        val spannable = SpannableString(text)
        val prefixLen = entry.time.length + 1
        val levelLen = entry.level.label.length + 1
        // 时间与标签用中性色（先设置，级别 span 后设置优先绘制）
        spannable.setSpan(
            ForegroundColorSpan(COLOR_TEXT_DIM),
            0, prefixLen + levelLen,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // 级别标记用级别色
        spannable.setSpan(
            ForegroundColorSpan(entry.level.color),
            prefixLen, prefixLen + levelLen,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // 消息正文用级别色
        spannable.setSpan(
            ForegroundColorSpan(entry.level.color),
            prefixLen + levelLen, text.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun scrollToBottom() {
        if (scrollView != null && !isCollapsed && contentView!!.visibility == View.VISIBLE) {
            scrollView!!.post { scrollView!!.fullScroll(View.FOCUS_DOWN) }
        }
    }

    private fun dp(px: Int): Int {
        if (context == null) { // 在某些Xposed初始化阶段context可能为空，此时需要一个默认值
            return (px * 2.0f).toInt() // 假设一个中等密度的设备
        }
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            px.toFloat(),
            context!!.resources.displayMetrics
        ).toInt()
    }

    @JvmStatic
    fun getCurrentActivity(): Activity? {
        return currentActivity
    }

    @JvmStatic
    fun isCollapsed(): Boolean {
        return isGloballyCollapsed
    }

    @JvmStatic
    fun destroy() {
        mainHandler.post {
            mainHandler.removeCallbacks(sRefreshLogsRunnable)
            if (rootView != null && windowManager != null) {
                try {
                    windowManager!!.removeView(rootView)
                } catch (ignored: Exception) {
                    logToFramework("ImGuiOverlay: Error destroying view: " + ignored.message)
                }
            }
            synchronized(globalLogList) {
                globalLogList.clear()
            }
            isGloballyCollapsed = false
            isLoggingPaused = false
            currentKeywordFilter = ""
            currentMinLevel = LogLevel.DEBUG
            relativeX = 0.05f
            relativeY = 0.15f
            lastOrientation = Configuration.ORIENTATION_UNDEFINED
            pendingUiUpdate.set(false)
            rootView = null
            currentActivity = null
            lastKnownActivity = null
            windowManager = null
            context = null
            logTextView = null
            scrollView = null
            contentView = null
            titleBar = null
            container = null
            collapseBtn = null
            pauseBtn = null
            filterBtn = null
            levelBtn = null
            copyBtn = null
            ballView = null
            params = null
            isCollapsed = false
            originalHeight = 0
            windowWidth = 0
            screenWidth = 0
            screenHeight = 0
        }
    }

    @JvmStatic
    fun hide() {
        mainHandler.post {
            mainHandler.removeCallbacks(sRefreshLogsRunnable)
            if (rootView != null && windowManager != null) {
                try {
                    saveRelativePosition()
                    windowManager!!.removeView(rootView)
                } catch (ignored: Exception) {
                    logToFramework("ImGuiOverlay: Error hiding view: " + ignored.message)
                }
            }
            rootView = null
            currentActivity = null
            pendingUiUpdate.set(false)
        }
    }
}
