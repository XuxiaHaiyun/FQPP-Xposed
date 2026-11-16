package com.xxhy.fqpp.xposed.utils

import android.util.Log
import com.xxhy.fqpp.xposed.ImGuiOverlay
import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.global.XposedGlobals
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 日志工具类 — 提供标准化的日志输出、多级别日志管理和文件写入功能。
 *
 * <h2>标准化输出格式</h2>
 * <pre>
 * [2026-07-12 10:30:45.123] [INFO] [Module] [main] Hello world
 * </pre>
 * 包含：时间戳、日志级别、TAG 标签、线程名、消息内容。ERROR 级别自动追加完整堆栈。
 *
 * <h2>日志级别管理</h2>
 * 通过 [LogLevel] 枚举控制输出 verbosity，支持运行时动态调整：
 * <ul>
 *   <li>[Environment.DEVELOPMENT] → 最低级别 DEBUG（全量输出）</li>
 *   <li>[Environment.TESTING] → 最低级别 INFO（屏蔽 DEBUG）</li>
 *   <li>[Environment.PRODUCTION] → 最低级别 WARN（仅 WARN + ERROR）</li>
 * </ul>
 * 调试模式（SP `debug_mode`）为总开关：关闭时所有日志均不输出。
 *
 * <h2>方法迁移指南</h2>
 * <table border="1">
 * <tr><th>旧方法（已废弃）</th><th>新方法</th><th>说明</th></tr>
 * <tr><td>[logI]</td><td>[log]</td><td>增加 TAG 参数，输出 INFO 级别</td></tr>
 * <tr><td>[logE]</td><td>[logError]</td><td>增加 TAG 参数，输出 ERROR 级别</td></tr>
 * </table>
 *
 * <h2>用法示例</h2>
 * <pre>{@code
 * LogUtils.setEnvironment(Environment.DEVELOPMENT);
 * LogUtils.logDebug("HttpServer", "Request received: " + uri);
 * LogUtils.log("DragonService", "Book detail fetched: " + bookId);
 * LogUtils.logWarn("PrivilegeManager", "VIP check fallback used");
 * LogUtils.logError("HookManager", "Hook failed for target", throwable);
 * }</pre>
 */
object LogUtils {

    // ===== 日志级别 =====

    /**
     * 日志级别枚举，按优先级递增。
     * 通过 [LogLevel.isLoggable] 判断某级别日志是否应被输出。
     */
    enum class LogLevel(val priority: Int) {
        /** 调试级别，最详细的日志，仅开发环境输出。 */
        DEBUG(1),
        /** 信息级别，常规运行信息。 */
        INFO(2),
        /** 警告级别，潜在问题或异常分支。 */
        WARN(3),
        /** 错误级别，功能异常、需关注的问题。 */
        ERROR(4),
        /** 特殊值：禁用所有日志输出。 */
        NONE(Int.MAX_VALUE);

        /**
         * 判断当前级别是否达到最低输出阈值。
         *
         * @param minLevel 配置的最低输出级别
         * @return true 表示当前级别 ≥ minLevel，应输出
         */
        fun isLoggable(minLevel: LogLevel): Boolean {
            return this.priority >= minLevel.priority
        }
    }

    /**
     * 运行环境枚举，映射到默认的最低日志级别。
     * 可通过 [setEnvironment] 快速切换。
     */
    enum class Environment(val minLevel: LogLevel) {
        /** 开发环境：输出 DEBUG 及以上所有日志。 */
        DEVELOPMENT(LogLevel.DEBUG),
        /** 测试环境：输出 INFO 及以上日志。 */
        TESTING(LogLevel.INFO),
        /** 生产环境：仅输出 WARN 和 ERROR。 */
        PRODUCTION(LogLevel.WARN);
    }

    /** 默认 TAG，用于废弃的旧方法委托。 */
    private const val DEFAULT_TAG = "Module"

    /** 当前最低日志级别，可运行时调整。 */
    @Volatile
    private var minLevel = LogLevel.DEBUG

    /** 时间戳格式。 */
    private const val TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS"

    // ===== 级别管理 =====

    /**
     * 设置最低日志级别。低于此级别的日志将被静默丢弃。
     *
     * @param level 最低输出级别（传 null 等同于 [LogLevel.DEBUG]）
     */
    @JvmStatic
    fun setMinLevel(level: LogLevel?) {
        minLevel = level ?: LogLevel.DEBUG
    }

    /**
     * 按运行环境设置最低日志级别。
     *
     * @param env 运行环境
     */
    @JvmStatic
    fun setEnvironment(env: Environment?) {
        if (env != null) {
            minLevel = env.minLevel
        }
    }

    /** 获取当前最低日志级别。 */
    @JvmStatic
    fun getMinLevel(): LogLevel {
        return minLevel
    }

    // ===== 新日志方法 =====

    /**
     * 输出 INFO 级别日志。
     *
     * <p>适用场景：记录常规运行信息，如请求到达、方法调用成功等。
     *
     * <p>输出格式：
     * <pre>[yyyy-MM-dd HH:mm:ss.SSS] [INFO] [TAG] [thread] message</pre>
     *
     * @param tag 模块/功能标签，便于日志过滤与定位（如 "HttpServer"、"DragonService"）
     * @param message 日志消息
     */
    @JvmStatic
    fun log(tag: String, message: String) {
        output(LogLevel.INFO, tag, message, null)
    }

    /**
     * 输出 ERROR 级别日志，自动附带完整异常堆栈。
     *
     * <p>适用场景：功能异常、Hook 失败、不可恢复的错误。
     *
     * <p>输出格式：
     * <pre>
     * [yyyy-MM-dd HH:mm:ss.SSS] [ERROR] [TAG] [thread] message
     * java.lang.Exception: ...
     *     at com.xxhy.fqpp...(....)
     *     ...
     * </pre>
     *
     * @param tag 模块/功能标签
     * @param message 错误描述信息
     * @param throwable 异常对象（可为 null，此时不输出堆栈）
     */
    @JvmStatic
    fun logError(tag: String, message: String, throwable: Throwable?) {
        output(LogLevel.ERROR, tag, message, throwable)
    }

    /**
     * 输出 DEBUG 级别日志。
     *
     * <p>适用场景：开发调试期间的详细变量值、流程跟踪。生产环境通常被屏蔽。
     *
     * <p>输出格式：
     * <pre>[yyyy-MM-dd HH:mm:ss.SSS] [DEBUG] [TAG] [thread] message</pre>
     *
     * @param tag 模块/功能标签
     * @param message 调试信息
     */
    @JvmStatic
    fun logDebug(tag: String, message: String) {
        output(LogLevel.DEBUG, tag, message, null)
    }

    /**
     * 输出 WARN 级别日志。
     *
     * <p>适用场景：潜在问题、降级处理、非预期但可恢复的情况。
     *
     * <p>输出格式：
     * <pre>[yyyy-MM-dd HH:mm:ss.SSS] [WARN] [TAG] [thread] message</pre>
     *
     * @param tag 模块/功能标签
     * @param message 警告信息
     */
    @JvmStatic
    fun logWarn(tag: String, message: String) {
        output(LogLevel.WARN, tag, message, null)
    }

    // ===== 废弃的旧方法（向后兼容委托）=====

    /**
     * 打印普通信息日志。
     *
     * @param message 日志内容
     * @deprecated 使用 [log] 替代，以支持自定义 TAG。
     */
    @Deprecated("使用 log(tag, message) 替代，以支持自定义 TAG")
    @JvmStatic
    fun logI(message: String) {
        log(DEFAULT_TAG, message)
    }

    /**
     * 打印错误信息日志。
     *
     * @param message 错误描述信息
     * @param throwable 异常对象
     * @deprecated 使用 [logError] 替代，以支持自定义 TAG。
     */
    @Deprecated("使用 logError(tag, message, throwable) 替代，以支持自定义 TAG")
    @JvmStatic
    fun logE(message: String, throwable: Throwable?) {
        logError(DEFAULT_TAG, message, throwable)
    }

    // ===== 核心输出逻辑 =====

    /**
     * 核心日志输出方法。统一处理级别过滤、格式化、多通道分发。
     *
     * <p>输出通道：
     * <ol>
     *   <li>`XposedGlobals.getXposedEntryInstance().log` — Xposed 框架日志（LSPosed/Xposed 日志窗口可见）</li>
     *   <li>`ImGuiOverlay.log/logError` — 模块内置调试浮窗</li>
     * </ol>
     *
     * @param level 日志级别
     * @param tag 标签
     * @param message 消息
     * @param throwable 异常（可为 null）
     */
    private fun output(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        if (!level.isLoggable(minLevel)) return
        if (!isDebugMode()) return

        val logLine = buildLogLine(level, tag, message, Thread.currentThread().name,
            System.currentTimeMillis())
        val fullLog = if (throwable != null)
            "$logLine\n${getFullStackTrace(throwable)}"
        else logLine

        logToFramework(level, fullLog, throwable)

        // 悬浮窗按等级路由（DEBUG/INFO/WARN/ERROR 分级着色与过滤）
        val displayMsg =
            if (throwable != null) "$message\n${getFullStackTrace(throwable)}" else message
        when (level) {
            LogLevel.DEBUG -> ImGuiOverlay.logDebug(tag, displayMsg)
            LogLevel.WARN -> ImGuiOverlay.logWarn(tag, displayMsg)
            LogLevel.ERROR -> ImGuiOverlay.logError(tag, displayMsg)
            else -> ImGuiOverlay.log(tag, displayMsg)
        }
    }

    /** 通过 libxposed 框架日志接口输出（入口实例未就绪时静默降级）。 */
    private fun logToFramework(level: LogLevel, fullLog: String, throwable: Throwable?) {
        try {
            val priority = when (level) {
                LogLevel.DEBUG -> Log.DEBUG
                LogLevel.WARN -> Log.WARN
                LogLevel.ERROR -> Log.ERROR
                else -> Log.INFO
            }
            XposedGlobals.getXposedEntryInstance()
                .log(priority, DEFAULT_TAG, fullLog, throwable)
        } catch (ignored: Throwable) {
        }
    }

    /** 读取 SP 中的调试模式开关。 */
    private fun isDebugMode(): Boolean {
        val sp = ConfigManager.prefs()
        return sp.getBoolean(SPConstants.DEBUG_MODE, SPConstants.DEFAULT_DEBUG_MODE)
    }

    // ===== 格式化（纯函数，可在 JVM 单元测试中验证）=====

    /**
     * 构建标准化的日志行。纯函数，不依赖 Android/Xposed 环境。
     *
     * <p>格式：{@code [yyyy-MM-dd HH:mm:ss.SSS] [LEVEL] [TAG] [thread] message}
     *
     * @param level 日志级别
     * @param tag 标签（null 时使用 [DEFAULT_TAG]）
     * @param message 消息（null 时输出空字符串）
     * @param threadName 线程名（null 时输出 "unknown"）
     * @param timestampMs 时间戳（毫秒）
     * @return 格式化后的日志行
     */
    fun buildLogLine(level: LogLevel, tag: String?, message: String?,
                     threadName: String?, timestampMs: Long): String {
        val sdf = SimpleDateFormat(TIMESTAMP_PATTERN, Locale.US)
        val timestamp = sdf.format(Date(timestampMs))
        val safeTag = tag ?: DEFAULT_TAG
        val safeMsg = message ?: ""
        val safeThread = threadName ?: "unknown"
        return String.format(Locale.US, "[%s] [%s] [%s] [%s] %s",
            timestamp, level.name, safeTag, safeThread, safeMsg)
    }

    /**
     * 构建包含异常堆栈的完整错误日志行。纯函数。
     *
     * @param level 日志级别（通常为 ERROR）
     * @param tag 标签
     * @param message 错误描述
     * @param threadName 线程名
     * @param timestampMs 时间戳
     * @param throwable 异常对象（null 时不追加堆栈）
     * @return 格式化后的完整日志（可能包含多行堆栈）
     */
    fun buildErrorLogLine(level: LogLevel, tag: String?, message: String?,
                          threadName: String?, timestampMs: Long, throwable: Throwable?): String {
        val base = buildLogLine(level, tag, message, threadName, timestampMs)
        return if (throwable != null) {
            "$base\n${getFullStackTrace(throwable)}"
        } else {
            base
        }
    }

    // ===== 工具方法 =====

    /**
     * 获取异常的完整堆栈信息。
     *
     * @param t 异常对象
     * @return 包含完整堆栈信息的字符串
     */
    @JvmStatic
    fun getFullStackTrace(t: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        t.printStackTrace(printWriter)
        printWriter.close()
        return stringWriter.toString()
    }

    /**
     * 将日志内容追加写入到文件。
     *
     * @param text 要写入的日志内容（原始文本，不做格式化）
     *     <p>日志文件路径为应用数据目录下的 "fqpp.log"，采用同步方式写入避免并发问题。
     *     仅在调试模式开启时写入。
     */
    @JvmStatic
    fun appendLogToFile(text: String) {
        if (!isDebugMode()) return

        val logFile = File(DragonGlobals.getDragonApplication()!!.dataDir, "fqpp.log")
        val line = "$text\n\n"

        // 同步代码块：保证多线程环境下日志写入的线程安全
        synchronized(LogUtils) {
            try {
                FileWriter(logFile, true).use { fw ->
                    fw.write(line)
                    fw.flush()
                }
            } catch (e: IOException) {
                logE("写日志失败", e)
            }
        }
    }
}
