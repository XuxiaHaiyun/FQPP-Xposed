package com.xxhy.fqpp.xposed.xcallback

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.utils.LogUtils
import io.github.libxposed.api.XposedInterface
import java.util.ArrayDeque

/**
 * SettingsActivity 钩子：在设置页标题栏右上角注入 "FQ++" 入口。
 *
 * <p>背景：番茄设置页（{@code com.dragon.read...SettingsActivity}）使用自绘
 * {@code com.dragon.read.widget.CommonTitleBar}，无系统菜单机制（onCreateOptionsMenu
 * 不会被调用），因此改为 hook {@code onCreate(Bundle)}，在 {@code proceed()} 之后
 * 复用标题栏右侧动作文本（getRightActionText，番茄设置页未占用）作为入口按钮。
 *
 * <p>点击 "FQ++" 弹出配置弹窗：只读展示当前配置（远程 SP），可一键跳转模块 App 配置页。
 * 相比旧的"设置列表项注入"，入口不依赖设置项模型类结构，跨版本稳定。
 */
class SettingActivityHooker : XposedInterface.Hooker {

    companion object {
        /** CommonTitleBar 全限定名（跨版本稳定，自绘标题栏） */
        private const val COMMON_TITLE_BAR_CLASS = "com.dragon.read.widget.CommonTitleBar"
        /** 入口文本（同时用作幂等标记：标题栏已注入时跳过） */
        private const val ENTRY_TEXT = "FQ++"
    }

    override fun intercept(chain: XposedInterface.Chain): Any? {
        val result = chain.proceed()
        return try {
            val activity = chain.getThisObject() as? Activity
            if (activity != null) {
                injectTitleBarEntry(activity)
            }
            result
        } catch (t: Throwable) {
            // 注入异常不能影响番茄流程
            LogUtils.logWarn("SettingsMenu", "入口注入异常：${t.javaClass.simpleName}: ${t.message}")
            result
        }
    }

    /** 在标题栏右侧注入 "FQ++" 文本按钮（幂等）。 */
    private fun injectTitleBarEntry(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) return
        val decorView = activity.window?.decorView ?: return
        val titleBar = findCommonTitleBar(decorView) ?: return
        val actionText = invokeGetRightActionText(titleBar) ?: return

        // 幂等：已注入过（文本已是入口文本）则跳过
        if (ENTRY_TEXT == actionText.text?.toString()) {
            return
        }
        if (actionText.tag == ENTRY_TEXT) {
            return
        }

        actionText.text = ENTRY_TEXT
        // 纯文字样式：无背景边框，亮色加粗保证醒目
        actionText.background = null
        actionText.setTextColor(Color.parseColor("#8AB4FF")) // 亮蓝，深/浅主题均醒目
        actionText.setTypeface(android.graphics.Typeface.DEFAULT_BOLD)
        actionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        actionText.visibility = View.VISIBLE
        actionText.tag = ENTRY_TEXT
        actionText.setOnClickListener {
            showConfigDialog(activity)
        }
        LogUtils.log("SettingsMenu", "标题栏入口已注入：${activity.javaClass.name}")
    }

    /** BFS 遍历视图树查找 CommonTitleBar 实例。 */
    private fun findCommonTitleBar(root: View): View? {
        val queue = ArrayDeque<View>()
        queue.add(root)
        while (queue.isNotEmpty()) {
            val v = queue.poll()
            if (v.javaClass.name == COMMON_TITLE_BAR_CLASS) return v
            if (v is ViewGroup) {
                for (i in 0 until v.childCount) {
                    queue.add(v.getChildAt(i))
                }
            }
        }
        return null
    }

    /** 反射调用 CommonTitleBar.getRightActionText()。 */
    private fun invokeGetRightActionText(titleBar: View): TextView? {
        return runCatching {
            val method = titleBar.javaClass.getMethod("getRightActionText")
            method.invoke(titleBar) as? TextView
        }.getOrNull()
    }

    /**
     * 配置弹窗：从远程配置（模块 App SP，只读）读取当前配置展示，
     * 提供 "打开模块配置页" 跳转模块 App 进行修改。
     */
    private fun showConfigDialog(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) return
        val sp = ConfigManager.prefs()
        val port = sp.getString(SPConstants.PORT, SPConstants.DEFAULT_PORT) ?: SPConstants.DEFAULT_PORT
        val localVip = sp.getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)
        val blockUpdates = sp.getBoolean(SPConstants.BLOCK_UPDATES, SPConstants.DEFAULT_BLOCK_UPDATES)
        val unlockBooks = sp.getBoolean(SPConstants.UNLOCK_BOOKS, SPConstants.DEFAULT_UNLOCK_BOOKS)
        val readingTime = sp.getBoolean(SPConstants.READING_TIME, SPConstants.DEFAULT_READING_TIME)
        val blockCrash = sp.getBoolean(SPConstants.BLOCK_CRASH_REPORT, SPConstants.DEFAULT_BLOCK_CRASH_REPORT)
        val blockHotUpdate = sp.getBoolean(SPConstants.BLOCK_HOT_UPDATE, SPConstants.DEFAULT_BLOCK_HOT_UPDATE)
        val blockPluginLoad = sp.getBoolean(SPConstants.BLOCK_PLUGIN_LOAD, SPConstants.DEFAULT_BLOCK_PLUGIN_LOAD)
        val debug = sp.getBoolean(SPConstants.DEBUG_MODE, SPConstants.DEFAULT_DEBUG_MODE)
        val uiClean = sp.getBoolean(SPConstants.UI_CLEAN, SPConstants.DEFAULT_UI_CLEAN)
        val instantReward = sp.getBoolean(SPConstants.INSTANT_REWARD, SPConstants.DEFAULT_INSTANT_REWARD)
        val uiCleanOptions = ConfigManager.getUiCleanOptions()

        val lines = buildString {
            append("接口服务端口：$port\n")
            append("本地会员：${if (localVip) "已开启" else "已关闭"}\n")
            append("屏蔽更新：${if (blockUpdates) "已开启" else "已关闭"}\n")
            append("解锁书籍：${if (unlockBooks) "已开启" else "已关闭"}\n")
            append("阅读时长（5x）：${if (readingTime) "已开启" else "已关闭"}\n")
            append("崩溃上报（打开禁用）：${if (blockCrash) "已开启" else "已关闭"}\n")
            append("热更新（打开禁用）：${if (blockHotUpdate) "已开启" else "已关闭"}\n")
            append("加载插件（打开禁用）：${if (blockPluginLoad) "已开启" else "已关闭"}\n")
            append("界面净化：${if (uiClean) "已开启" else "已关闭"}")
            if (uiClean && uiCleanOptions.anyEnabled) {
                val enabledParts = buildList {
                    if (uiCleanOptions.searchWord) add("搜索框轮播")
                    if (uiCleanOptions.screenAd) add("全屏福利弹窗")
                    if (uiCleanOptions.vipCard) add("会员卡")
                    if (uiCleanOptions.functionBadge) add("功能区红点")
                    if (uiCleanOptions.miniGame) add("常玩游戏")
                    if (uiCleanOptions.tabBadge) add("TAB角标")
                    if (uiCleanOptions.mineFeed) add("我的页小说推荐")
                    if (uiCleanOptions.homeEarnTab) add("主页赚钱导航栏")
                    if (uiCleanOptions.homeSeriesTab) add("主页短剧导航栏")
                    if (uiCleanOptions.hidePublish) add("发表按钮")
                    if (uiCleanOptions.hideMineAsset) add("金币信息")
                    if (uiCleanOptions.hideMineHistory) add("我的页浏览历史外显")
                    if (uiCleanOptions.hideMineEarnPendant) add("点击赚钱挂件")
                    if (uiCleanOptions.searchResult) add("热搜榜/AI板块")
                    if (uiCleanOptions.searchAi) add("搜索AI入口")
                }
                append("（细分：${enabledParts.joinToString("、")}）")
            }
            append("\n")
            append("激励秒领：${if (instantReward) "已开启" else "已关闭"}\n")
            append("调试模块：${if (debug) "已开启" else "已关闭"}\n")
            append("\n修改配置请在模块 App 中操作。")
        }

        AlertDialog.Builder(activity)
            .setTitle("FQ++ 配置")
            .setMessage(lines)
            .setPositiveButton("打开模块配置页") { _, _ ->
                openModuleConfigPage(activity)
            }
            .setNegativeButton("关闭", null)
            .show()
    }

    private fun openModuleConfigPage(activity: Activity) {
        try {
            val intent = Intent()
            intent.component = ComponentName(
                "com.xxhy.fqpp", "com.xxhy.fqpp.ui.activity.ConfigActivity"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        } catch (t: Throwable) {
            LogUtils.logWarn("SettingsMenu", "跳转配置页失败：${t.message}")
        }
    }
}
