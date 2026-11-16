package com.xxhy.fqpp.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.xxhy.fqpp.R
import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants

/**
 * 模块配置页（模块 App 进程）。
 *
 * <p>配置统一保存到模块 App 自身 SharedPreferences，番茄进程通过
 * libxposed API 102 {@code getRemotePreferences()} 只读跨进程读取，
 * 不再向目标应用写入任何配置。
 */
class ConfigActivity : AppCompatActivity() {

  private lateinit var portInput: com.google.android.material.textfield.TextInputEditText
  private lateinit var startWithAppSwitch: SwitchMaterial
  private lateinit var blockUpdatesSwitch: SwitchMaterial
  private lateinit var localVipSwitch: SwitchMaterial
  private lateinit var unlockBooksSwitch: SwitchMaterial
  private lateinit var blockCrashReportSwitch: SwitchMaterial
  private lateinit var blockHotUpdateSwitch: SwitchMaterial
  private lateinit var blockPluginLoadSwitch: SwitchMaterial
  private lateinit var readingTimeX2Switch: SwitchMaterial
  private lateinit var uiCleanSwitch: SwitchMaterial
  private lateinit var uiCleanSearchWordSwitch: SwitchMaterial
  private lateinit var uiCleanScreenAdSwitch: SwitchMaterial
  private lateinit var uiCleanVipCardSwitch: SwitchMaterial
  private lateinit var uiCleanFunctionBadgeSwitch: SwitchMaterial
  private lateinit var uiCleanMiniGameSwitch: SwitchMaterial
  private lateinit var uiCleanTabBadgeSwitch: SwitchMaterial
  private lateinit var uiCleanMineFeedSwitch: SwitchMaterial
  private lateinit var uiCleanHomeEarnTabSwitch: SwitchMaterial
  private lateinit var uiCleanHomeSeriesTabSwitch: SwitchMaterial
  private lateinit var uiCleanHidePublishSwitch: SwitchMaterial
  private lateinit var uiCleanHideMineAssetSwitch: SwitchMaterial
  private lateinit var uiCleanHideMineHistorySwitch: SwitchMaterial
  private lateinit var uiCleanHideMineEarnPendantSwitch: SwitchMaterial
  private lateinit var uiCleanSearchResultSwitch: SwitchMaterial
  private lateinit var uiCleanSearchAiSwitch: SwitchMaterial
  private lateinit var instantRewardSwitch: SwitchMaterial
  private lateinit var debugModeSwitch: SwitchMaterial

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_config)
    setSupportActionBar(findViewById(R.id.configToolbar))
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val sp = ConfigManager.modulePrefs(this)

    portInput = findViewById(R.id.portInput)
    startWithAppSwitch = findViewById(R.id.startWithAppSwitch)
    blockUpdatesSwitch = findViewById(R.id.blockUpdatesSwitch)
    localVipSwitch = findViewById(R.id.localVipSwitch)
    unlockBooksSwitch = findViewById(R.id.unlockBooksSwitch)
    blockCrashReportSwitch = findViewById(R.id.blockCrashReportSwitch)
    blockHotUpdateSwitch = findViewById(R.id.blockHotUpdateSwitch)
    blockPluginLoadSwitch = findViewById(R.id.blockPluginLoadSwitch)
    readingTimeX2Switch = findViewById(R.id.readingTimeX2Switch)
    uiCleanSwitch = findViewById(R.id.uiCleanSwitch)
    uiCleanSearchWordSwitch = findViewById(R.id.uiCleanSearchWordSwitch)
    uiCleanScreenAdSwitch = findViewById(R.id.uiCleanScreenAdSwitch)
    uiCleanVipCardSwitch = findViewById(R.id.uiCleanVipCardSwitch)
    uiCleanFunctionBadgeSwitch = findViewById(R.id.uiCleanFunctionBadgeSwitch)
    uiCleanMiniGameSwitch = findViewById(R.id.uiCleanMiniGameSwitch)
    uiCleanTabBadgeSwitch = findViewById(R.id.uiCleanTabBadgeSwitch)
    uiCleanMineFeedSwitch = findViewById(R.id.uiCleanMineFeedSwitch)
    uiCleanHomeEarnTabSwitch = findViewById(R.id.uiCleanHomeEarnTabSwitch)
    uiCleanHomeSeriesTabSwitch = findViewById(R.id.uiCleanHomeSeriesTabSwitch)
    uiCleanHidePublishSwitch = findViewById(R.id.uiCleanHidePublishSwitch)
    uiCleanHideMineAssetSwitch = findViewById(R.id.uiCleanHideMineAssetSwitch)
    uiCleanHideMineHistorySwitch = findViewById(R.id.uiCleanHideMineHistorySwitch)
    uiCleanHideMineEarnPendantSwitch = findViewById(R.id.uiCleanHideMineEarnPendantSwitch)
    uiCleanSearchResultSwitch = findViewById(R.id.uiCleanSearchResultSwitch)
    uiCleanSearchAiSwitch = findViewById(R.id.uiCleanSearchAiSwitch)
    instantRewardSwitch = findViewById(R.id.instantRewardSwitch)
    debugModeSwitch = findViewById(R.id.debugModeSwitch)

    // 界面净化细分卡片：仅主开关开启时可见（关闭时整卡隐藏，避免误导）
    val uiCleanSubCard =
      findViewById<com.google.android.material.card.MaterialCardView>(R.id.uiCleanSubCard)
    uiCleanSubCard.visibility = if (uiCleanSwitch.isChecked) View.VISIBLE else View.GONE
    uiCleanSwitch.setOnCheckedChangeListener { _, checked ->
      uiCleanSubCard.visibility = if (checked) View.VISIBLE else View.GONE
    }

    // 读取当前配置填充 UI
    portInput.setText(sp.getString(SPConstants.PORT, SPConstants.DEFAULT_PORT))
    startWithAppSwitch.isChecked =
      sp.getBoolean(SPConstants.START_WITH_APP, SPConstants.DEFAULT_START_WITH_APP)
    blockUpdatesSwitch.isChecked =
      sp.getBoolean(SPConstants.BLOCK_UPDATES, SPConstants.DEFAULT_BLOCK_UPDATES)
    localVipSwitch.isChecked =
      sp.getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)
    unlockBooksSwitch.isChecked =
      sp.getBoolean(SPConstants.UNLOCK_BOOKS, SPConstants.DEFAULT_UNLOCK_BOOKS)
    blockCrashReportSwitch.isChecked =
      sp.getBoolean(SPConstants.BLOCK_CRASH_REPORT, SPConstants.DEFAULT_BLOCK_CRASH_REPORT)
    blockHotUpdateSwitch.isChecked =
      sp.getBoolean(SPConstants.BLOCK_HOT_UPDATE, SPConstants.DEFAULT_BLOCK_HOT_UPDATE)
    blockPluginLoadSwitch.isChecked =
      sp.getBoolean(SPConstants.BLOCK_PLUGIN_LOAD, SPConstants.DEFAULT_BLOCK_PLUGIN_LOAD)
    readingTimeX2Switch.isChecked =
      sp.getBoolean(SPConstants.READING_TIME, SPConstants.DEFAULT_READING_TIME)
    uiCleanSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN, SPConstants.DEFAULT_UI_CLEAN)
    uiCleanSearchWordSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_SEARCH_WORD, SPConstants.DEFAULT_UI_CLEAN_SEARCH_WORD)
    uiCleanScreenAdSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_SCREEN_AD, SPConstants.DEFAULT_UI_CLEAN_SCREEN_AD)
    uiCleanVipCardSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_VIP_CARD, SPConstants.DEFAULT_UI_CLEAN_VIP_CARD)
    uiCleanFunctionBadgeSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_FUNCTION_BADGE, SPConstants.DEFAULT_UI_CLEAN_FUNCTION_BADGE)
    uiCleanMiniGameSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_MINI_GAME, SPConstants.DEFAULT_UI_CLEAN_MINI_GAME)
    uiCleanTabBadgeSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_TAB_BADGE, SPConstants.DEFAULT_UI_CLEAN_TAB_BADGE)
    uiCleanMineFeedSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_MINE_FEED, SPConstants.DEFAULT_UI_CLEAN_MINE_FEED)
    uiCleanHomeEarnTabSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_HOME_EARN_TAB, SPConstants.DEFAULT_UI_CLEAN_HOME_EARN_TAB)
    uiCleanHomeSeriesTabSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_HOME_SERIES_TAB, SPConstants.DEFAULT_UI_CLEAN_HOME_SERIES_TAB)
    uiCleanHidePublishSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_HIDE_PUBLISH, SPConstants.DEFAULT_UI_CLEAN_HIDE_PUBLISH)
    uiCleanHideMineAssetSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_HIDE_MINE_ASSET, SPConstants.DEFAULT_UI_CLEAN_HIDE_MINE_ASSET)
    uiCleanHideMineHistorySwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_HIDE_MINE_HISTORY, SPConstants.DEFAULT_UI_CLEAN_HIDE_MINE_HISTORY)
    uiCleanHideMineEarnPendantSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_HIDE_MINE_EARN_PENDANT, SPConstants.DEFAULT_UI_CLEAN_HIDE_MINE_EARN_PENDANT)
    uiCleanSearchResultSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_SEARCH_RESULT, SPConstants.DEFAULT_UI_CLEAN_SEARCH_RESULT)
    uiCleanSearchAiSwitch.isChecked =
      sp.getBoolean(SPConstants.UI_CLEAN_SEARCH_AI, SPConstants.DEFAULT_UI_CLEAN_SEARCH_AI)
    instantRewardSwitch.isChecked =
      sp.getBoolean(SPConstants.INSTANT_REWARD, SPConstants.DEFAULT_INSTANT_REWARD)
    debugModeSwitch.isChecked =
      sp.getBoolean(SPConstants.DEBUG_MODE, SPConstants.DEFAULT_DEBUG_MODE)

    findViewById<com.google.android.material.button.MaterialButton>(R.id.saveButton)
      .setOnClickListener { saveConfig() }
    findViewById<com.google.android.material.button.MaterialButton>(R.id.importButton)
      .setOnClickListener { importBookSource() }
  }

  override fun onSupportNavigateUp(): Boolean {
    finish()
    return true
  }

  private fun saveConfig() {
    val port = portInput.text?.toString()?.trim() ?: ""
    if (TextUtils.isEmpty(port)) {
      Toast.makeText(this, "端口号不能为空", Toast.LENGTH_SHORT).show()
      return
    }
    val portRegex =
      "^(0|[1-9]\\d{0,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$"
    if (!port.matches(Regex(portRegex))) {
      Toast.makeText(this, "端口号无效，请输入0-65535之间的整数", Toast.LENGTH_SHORT).show()
      return
    }

    ConfigManager.save(
      this,
      port = port,
      startWithApp = startWithAppSwitch.isChecked,
      blockUpdates = blockUpdatesSwitch.isChecked,
      localVip = localVipSwitch.isChecked,
      debugMode = debugModeSwitch.isChecked,
      unlockBooks = unlockBooksSwitch.isChecked,
      blockCrashReport = blockCrashReportSwitch.isChecked,
      blockHotUpdate = blockHotUpdateSwitch.isChecked,
      blockPluginLoad = blockPluginLoadSwitch.isChecked,
      readingTime = readingTimeX2Switch.isChecked,
      uiClean = uiCleanSwitch.isChecked,
      uiCleanOptions = ConfigManager.UiCleanOptions(
        searchWord = uiCleanSearchWordSwitch.isChecked,
        screenAd = uiCleanScreenAdSwitch.isChecked,
        vipCard = uiCleanVipCardSwitch.isChecked,
        functionBadge = uiCleanFunctionBadgeSwitch.isChecked,
        miniGame = uiCleanMiniGameSwitch.isChecked,
        tabBadge = uiCleanTabBadgeSwitch.isChecked,
        mineFeed = uiCleanMineFeedSwitch.isChecked,
        homeEarnTab = uiCleanHomeEarnTabSwitch.isChecked,
        homeSeriesTab = uiCleanHomeSeriesTabSwitch.isChecked,
        hidePublish = uiCleanHidePublishSwitch.isChecked,
        hideMineAsset = uiCleanHideMineAssetSwitch.isChecked,
        hideMineHistory = uiCleanHideMineHistorySwitch.isChecked,
        hideMineEarnPendant = uiCleanHideMineEarnPendantSwitch.isChecked,
        searchResult = uiCleanSearchResultSwitch.isChecked,
        searchAi = uiCleanSearchAiSwitch.isChecked,
      ),
      instantReward = instantRewardSwitch.isChecked,
    )
    MaterialAlertDialogBuilder(this)
      .setTitle("配置已保存")
      .setMessage("配置已写入模块数据目录，番茄小说进程会自动读取。\n\n请重启番茄小说，使全部配置生效。若仍未生效，请在 LSPosed 中确认本模块已启用。")
      .setPositiveButton("知道了", null)
      .show()
    if (debugModeSwitch.isChecked) {
      MaterialAlertDialogBuilder(this)
        .setTitle("提示")
        .setMessage("启用调试后，会输出更详细的日志，接口响应会以格式化 JSON 返回，可能影响传输速度。")
        .setPositiveButton("知道了", null)
        .show()
    }
  }

  /**
   * 导入书源：生成书源导入链接并跳转阅读应用。
   *
   * <p>链接指向设备 localhost 的本地 HTTP 服务（运行于番茄进程）。
   * 若服务未运行（如未开启"随应用启动"且番茄未启动），导入会失败。
   * 不做安装校验，直接尝试拉起处理链接的应用。
   */
  private fun importBookSource() {
    val portStr = ConfigManager.modulePrefs(this)
      .getString(SPConstants.PORT, SPConstants.DEFAULT_PORT) ?: SPConstants.DEFAULT_PORT
    val importUrl = "legado://booksource/importonline?src=http://localhost:$portStr/booksource"
    val uri = Uri.parse(importUrl)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
      startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      Toast.makeText(this, "未找到可处理书源导入链接的应用（如阅读 App）", Toast.LENGTH_SHORT).show()
    }
  }
}
