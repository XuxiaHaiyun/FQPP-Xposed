package com.xxhy.fqpp.xposed.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * [LogUtils] 单元测试。
 *
 * <p>测试范围：
 * <ul>
 *   <li>[LogUtils.LogLevel] 级别过滤逻辑</li>
 *   <li>[LogUtils.Environment] 环境到级别的映射</li>
 *   <li>[LogUtils.buildLogLine] 标准化格式构建</li>
 *   <li>[LogUtils.buildErrorLogLine] 错误日志含堆栈</li>
 *   <li>[LogUtils.getFullStackTrace] 堆栈提取</li>
 *   <li>级别管理 API（setMinLevel / setEnvironment / getMinLevel）</li>
 * </ul>
 *
 * <p>说明：{@code output} 等依赖 Android/Xposed 环境的方法不在此测试，
 * 因其需 instrumented 测试环境。纯格式化逻辑已抽取为 object 方法供 JVM 测试验证。
 */
class LogUtilsTest {

  private lateinit var savedMinLevel: LogUtils.LogLevel

  @Before
  fun setUp() {
    // 保存当前级别，测试后恢复，避免测试间相互影响
    savedMinLevel = LogUtils.getMinLevel()
    LogUtils.setMinLevel(LogUtils.LogLevel.DEBUG)
  }

  @After
  fun tearDown() {
    LogUtils.setMinLevel(savedMinLevel)
  }

  // ===== LogLevel.isLoggable 测试 =====

  @Test
  fun logLevel_isLoggable_sameLevel() {
    assertTrue("DEBUG should be loggable at min DEBUG",
      LogUtils.LogLevel.DEBUG.isLoggable(LogUtils.LogLevel.DEBUG))
    assertTrue("INFO should be loggable at min INFO",
      LogUtils.LogLevel.INFO.isLoggable(LogUtils.LogLevel.INFO))
    assertTrue("ERROR should be loggable at min ERROR",
      LogUtils.LogLevel.ERROR.isLoggable(LogUtils.LogLevel.ERROR))
  }

  @Test
  fun logLevel_isLoggable_higherLevelPasses() {
    assertTrue("INFO should be loggable at min DEBUG",
      LogUtils.LogLevel.INFO.isLoggable(LogUtils.LogLevel.DEBUG))
    assertTrue("WARN should be loggable at min DEBUG",
      LogUtils.LogLevel.WARN.isLoggable(LogUtils.LogLevel.DEBUG))
    assertTrue("ERROR should be loggable at min INFO",
      LogUtils.LogLevel.ERROR.isLoggable(LogUtils.LogLevel.INFO))
  }

  @Test
  fun logLevel_isLoggable_lowerLevelFails() {
    assertFalse("DEBUG should NOT be loggable at min INFO",
      LogUtils.LogLevel.DEBUG.isLoggable(LogUtils.LogLevel.INFO))
    assertFalse("DEBUG should NOT be loggable at min WARN",
      LogUtils.LogLevel.DEBUG.isLoggable(LogUtils.LogLevel.WARN))
    assertFalse("INFO should NOT be loggable at min ERROR",
      LogUtils.LogLevel.INFO.isLoggable(LogUtils.LogLevel.ERROR))
    assertFalse("WARN should NOT be loggable at min NONE",
      LogUtils.LogLevel.WARN.isLoggable(LogUtils.LogLevel.NONE))
  }

  @Test
  fun logLevel_isLoggable_noneBlocksAll() {
    for (level in LogUtils.LogLevel.values()) {
      if (level == LogUtils.LogLevel.NONE) continue
      assertFalse(level.name + " should NOT be loggable at min NONE",
        level.isLoggable(LogUtils.LogLevel.NONE))
    }
  }

  // ===== Environment 映射测试 =====

  @Test
  fun environment_developmentMapsToDebug() {
    assertEquals(LogUtils.LogLevel.DEBUG, LogUtils.Environment.DEVELOPMENT.minLevel)
  }

  @Test
  fun environment_testingMapsToInfo() {
    assertEquals(LogUtils.LogLevel.INFO, LogUtils.Environment.TESTING.minLevel)
  }

  @Test
  fun environment_productionMapsToWarn() {
    assertEquals(LogUtils.LogLevel.WARN, LogUtils.Environment.PRODUCTION.minLevel)
  }

  @Test
  fun setEnvironment_updatesMinLevel() {
    LogUtils.setEnvironment(LogUtils.Environment.PRODUCTION)
    assertEquals(LogUtils.LogLevel.WARN, LogUtils.getMinLevel())

    LogUtils.setEnvironment(LogUtils.Environment.DEVELOPMENT)
    assertEquals(LogUtils.LogLevel.DEBUG, LogUtils.getMinLevel())

    LogUtils.setEnvironment(LogUtils.Environment.TESTING)
    assertEquals(LogUtils.LogLevel.INFO, LogUtils.getMinLevel())
  }

  @Test
  fun setEnvironment_nullDoesNotChangeLevel() {
    LogUtils.setMinLevel(LogUtils.LogLevel.ERROR)
    LogUtils.setEnvironment(null)
    assertEquals("null environment should not change level",
      LogUtils.LogLevel.ERROR, LogUtils.getMinLevel())
  }

  // ===== setMinLevel 测试 =====

  @Test
  fun setMinLevel_updatesLevel() {
    LogUtils.setMinLevel(LogUtils.LogLevel.WARN)
    assertEquals(LogUtils.LogLevel.WARN, LogUtils.getMinLevel())
  }

  @Test
  fun setMinLevel_nullDefaultsToDebug() {
    LogUtils.setMinLevel(null)
    assertEquals("null should default to DEBUG", LogUtils.LogLevel.DEBUG, LogUtils.getMinLevel())
  }

  // ===== buildLogLine 格式测试 =====

  @Test
  fun buildLogLine_containsAllComponents() {
    val ts = System.currentTimeMillis()
    val line = LogUtils.buildLogLine(
      LogUtils.LogLevel.INFO, "HttpServer", "Request received", "main", ts)

    assertTrue("Should contain timestamp brackets: $line", line.contains("[") && line.contains("]"))
    assertTrue("Should contain level INFO: $line", line.contains("[INFO]"))
    assertTrue("Should contain tag: $line", line.contains("[HttpServer]"))
    assertTrue("Should contain thread name: $line", line.contains("[main]"))
    assertTrue("Should contain message: $line", line.contains("Request received"))
  }

  @Test
  fun buildLogLine_exactFormat() {
    // 固定时间戳验证精确格式
    val ts = 1720760000000L // 2024-07-12 09:46:40.000 UTC（具体值取决于时区）
    val line = LogUtils.buildLogLine(
      LogUtils.LogLevel.WARN, "Module", "test message", "worker-1", ts)

    // 验证结构：[timestamp] [LEVEL] [TAG] [thread] message
    // 格式：5 个方括号组 + 空格分隔的消息
    val pattern = "^\\[[^]]+\\] \\[WARN\\] \\[Module\\] \\[worker-1\\] test message$"
    assertTrue("Line should match pattern. Actual: $line", line.matches(Regex(pattern)))
  }

  @Test
  fun buildLogLine_timestampFormat() {
    val ts = 1720760000000L
    val line = LogUtils.buildLogLine(
      LogUtils.LogLevel.DEBUG, "T", "msg", "t", ts)

    // 提取时间戳部分并验证格式 yyyy-MM-dd HH:mm:ss.SSS
    val tsPart = line.substring(1, line.indexOf("]"))
    assertTrue("Timestamp should match yyyy-MM-dd HH:mm:ss.SSS: $tsPart",
      tsPart.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}")))
  }

  @Test
  fun buildLogLine_nullTagUsesDefault() {
    val line = LogUtils.buildLogLine(
      LogUtils.LogLevel.INFO, null, "msg", "main", 0L)
    assertTrue("Null tag should use default 'Module': $line", line.contains("[Module]"))
  }

  @Test
  fun buildLogLine_nullMessageOutputsEmpty() {
    val line = LogUtils.buildLogLine(
      LogUtils.LogLevel.INFO, "T", null, "main", 0L)
    assertTrue("Null message should not produce NPE", line.endsWith("] "))
  }

  @Test
  fun buildLogLine_nullThreadUsesUnknown() {
    val line = LogUtils.buildLogLine(
      LogUtils.LogLevel.INFO, "T", "msg", null, 0L)
    assertTrue("Null thread should output 'unknown': $line", line.contains("[unknown]"))
  }

  @Test
  fun buildLogLine_allLevelsRenderCorrectly() {
    for (level in LogUtils.LogLevel.values()) {
      if (level == LogUtils.LogLevel.NONE) continue
      val line = LogUtils.buildLogLine(level, "T", "msg", "t", 0L)
      assertTrue("Level ${level.name} should appear in output: $line",
        line.contains("[${level.name}]"))
    }
  }

  @Test
  fun buildLogLine_emptyTagAndMessage() {
    val line = LogUtils.buildLogLine(
      LogUtils.LogLevel.INFO, "", "", "main", 0L)
    assertNotNull("Empty strings should not cause NPE", line)
    assertTrue("Empty tag should still render brackets: $line", line.contains("[]"))
  }

  // ===== buildErrorLogLine 测试 =====

  @Test
  fun buildErrorLogLine_includesStackTrace() {
    val t = RuntimeException("test exception")
    val line = LogUtils.buildErrorLogLine(
      LogUtils.LogLevel.ERROR, "HookManager", "Hook failed", "main", 0L, t)

    assertTrue("Should contain ERROR level: $line", line.contains("[ERROR]"))
    assertTrue("Should contain message: $line", line.contains("Hook failed"))
    assertTrue("Should contain exception class name: $line",
      line.contains("java.lang.RuntimeException"))
    assertTrue("Should contain exception message: $line", line.contains("test exception"))
    assertTrue("Should contain 'at' stack frame: $line", line.contains("at "))
  }

  @Test
  fun buildErrorLogLine_nullThrowableNoStackTrace() {
    val line = LogUtils.buildErrorLogLine(
      LogUtils.LogLevel.ERROR, "T", "msg", "main", 0L, null)

    assertTrue("Should contain message: $line", line.contains("msg"))
    assertFalse("Should NOT contain 'at ' stack frame without throwable: $line",
      line.contains("\n\tat "))
  }

  @Test
  fun buildErrorLogLine_multilineOutput() {
    val t = IllegalStateException("multi")
    val line = LogUtils.buildErrorLogLine(
      LogUtils.LogLevel.ERROR, "T", "msg", "main", 0L, t)

    // 错误日志应包含多行（第一行是格式化行，后续是堆栈）
    assertTrue("Error log should be multiline", line.contains("\n"))
    val lines = line.split("\n")
    assertTrue("Should have at least 2 lines", lines.size >= 2)
    assertTrue("First line should contain [ERROR]", lines[0].contains("[ERROR]"))
  }

  // ===== getFullStackTrace 测试 =====

  @Test
  fun getFullStackTrace_containsExceptionInfo() {
    val t = NullPointerException("npe test")
    val stack = LogUtils.getFullStackTrace(t)

    assertNotNull(stack)
    assertTrue("Should contain exception class: $stack",
      stack.contains("java.lang.NullPointerException"))
    assertTrue("Should contain message: $stack", stack.contains("npe test"))
    assertTrue("Should contain stack frame 'at': $stack", stack.contains("at "))
  }

  @Test
  fun getFullStackTrace_containsCauseChain() {
    val cause = IllegalArgumentException("root cause")
    val t = RuntimeException("wrapper", cause)
    val stack = LogUtils.getFullStackTrace(t)

    assertTrue("Should contain wrapper: $stack", stack.contains("RuntimeException"))
    assertTrue("Should contain wrapper message: $stack", stack.contains("wrapper"))
    assertTrue("Should contain caused by: $stack", stack.contains("Caused by:"))
    assertTrue("Should contain root cause: $stack", stack.contains("IllegalArgumentException"))
    assertTrue("Should contain root message: $stack", stack.contains("root cause"))
  }

  @Test
  fun getFullStackTrace_nestedException() {
    try {
      try {
        throw NumberFormatException("bad number")
      } catch (e: NumberFormatException) {
        throw IllegalStateException("parse failed", e)
      }
    } catch (e: IllegalStateException) {
      val stack = LogUtils.getFullStackTrace(e)
      assertTrue("Should contain outer exception", stack.contains("IllegalStateException"))
      assertTrue("Should contain inner exception via Caused by", stack.contains("Caused by:"))
      assertTrue("Should contain inner exception type", stack.contains("NumberFormatException"))
    }
  }
}
