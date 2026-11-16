package com.xxhy.fqpp.xposed.constant

/**
 * SharedPreferences相关常量类
 * 存储SP文件名、各配置项的键名及默认值，统一管理本地配置常量
 */
object SPConstants {
    const val SP_NAME = "fqpp"

    const val PORT = "port"
    const val DEFAULT_PORT = "9999"

    const val START_WITH_APP = "start_with_app"
    const val DEFAULT_START_WITH_APP = false

    const val BLOCK_UPDATES = "block_updates"
    const val DEFAULT_BLOCK_UPDATES = false

    const val MAPPING = "mapping"
    const val DEFAULT_MAPPING = "{}"

    const val DEBUG_MODE = "debug_mode"
    const val DEFAULT_DEBUG_MODE = false

    const val LOCAL_VIP = "local_vip"
    const val DEFAULT_LOCAL_VIP = false

    const val UNLOCK_BOOKS = "unlock_books"
    const val DEFAULT_UNLOCK_BOOKS = false

    const val BLOCK_CRASH_REPORT = "block_crash_report"
    const val DEFAULT_BLOCK_CRASH_REPORT = false

    /** 热更新（打开禁用）：阻止 Reparo 热修复框架初始化/加载补丁 */
    const val BLOCK_HOT_UPDATE = "block_hot_update"
    const val DEFAULT_BLOCK_HOT_UPDATE = false

    /** 加载插件（打开禁用）：禁止加载 files/plugins/ 下的 Mira 插件 */
    const val BLOCK_PLUGIN_LOAD = "block_plugin_load"
    const val DEFAULT_BLOCK_PLUGIN_LOAD = false

    const val READING_TIME = "reading_time"
    const val DEFAULT_READING_TIME = false

    const val UI_CLEAN = "ui_clean"
    const val DEFAULT_UI_CLEAN = false

    // ===== 界面净化-细分开关（主开关 UI_CLEAN 开启后按需生效） =====

    /** 主页搜索框轮播提示（默认开启） */
    const val UI_CLEAN_SEARCH_WORD = "ui_clean_search_word"
    const val DEFAULT_UI_CLEAN_SEARCH_WORD = true
    /** 全屏福利广告弹窗（默认开启） */
    const val UI_CLEAN_SCREEN_AD = "ui_clean_screen_ad"
    const val DEFAULT_UI_CLEAN_SCREEN_AD = true
    /** 我的页会员特权卡/会员中心入口（默认关闭：本地会员时默认展示卡片，主动开启才隐藏） */
    const val UI_CLEAN_VIP_CARD = "ui_clean_vip_card"
    const val DEFAULT_UI_CLEAN_VIP_CARD = false
    /** 功能区/右侧滑栏红点角标（默认开启） */
    const val UI_CLEAN_FUNCTION_BADGE = "ui_clean_function_badge"
    const val DEFAULT_UI_CLEAN_FUNCTION_BADGE = true
    /** 侧滑"常玩游戏"区块（默认开启） */
    const val UI_CLEAN_MINI_GAME = "ui_clean_mini_game"
    const val DEFAULT_UI_CLEAN_MINI_GAME = true
    /** 底部 TAB 红点/文字角标（默认开启） */
    const val UI_CLEAN_TAB_BADGE = "ui_clean_tab_badge"
    const val DEFAULT_UI_CLEAN_TAB_BADGE = true
    /** 我的页底部"小说推荐"feed（默认开启，与其他细分一致） */
    const val UI_CLEAN_MINE_FEED = "ui_clean_mine_feed"
    const val DEFAULT_UI_CLEAN_MINE_FEED = true
    /** 隐藏主页"赚钱"导航栏（底部 TAB LuckyBenefit，默认开启） */
    const val UI_CLEAN_HOME_EARN_TAB = "ui_clean_home_earn_tab"
    const val DEFAULT_UI_CLEAN_HOME_EARN_TAB = true
    /** 隐藏主页"短剧"导航栏（底部 TAB VideoSeriesFeedTab，默认开启） */
    const val UI_CLEAN_HOME_SERIES_TAB = "ui_clean_home_series_tab"
    const val DEFAULT_UI_CLEAN_HOME_SERIES_TAB = true
    /** 隐藏发表帖子/话题/书单按钮（编辑器发布入口 triggerLayout=idd，书城/我的页显示，默认开启） */
    const val UI_CLEAN_HIDE_PUBLISH = "ui_clean_hide_publish"
    const val DEFAULT_UI_CLEAN_HIDE_PUBLISH = true
    /** 隐藏我的页金币信息板块（金币余额/现金余额/提现，view id=ez1，官方埋点名"金币信息"，默认开启） */
    const val UI_CLEAN_HIDE_MINE_ASSET = "ui_clean_hide_mine_asset"
    const val DEFAULT_UI_CLEAN_HIDE_MINE_ASSET = true
    /** 隐藏我的页"上次观看记录"（历史卡，view id=hq，默认关闭：仅用户主动开启时隐藏） */
    const val UI_CLEAN_HIDE_MINE_HISTORY = "ui_clean_hide_mine_history"
    const val DEFAULT_UI_CLEAN_HIDE_MINE_HISTORY = false
    /** 主页"点击赚钱"挂件（GoldCoinBoxTextView，Lottie+关闭按钮 jvm，默认关闭） */
    const val UI_CLEAN_HIDE_MINE_EARN_PENDANT = "ui_clean_hide_mine_earn_pendant"
    const val DEFAULT_UI_CLEAN_HIDE_MINE_EARN_PENDANT = false
    /** 搜索页热搜榜/AI 板块（默认开启） */
    const val UI_CLEAN_SEARCH_RESULT = "ui_clean_search_result"
    const val DEFAULT_UI_CLEAN_SEARCH_RESULT = true
    /** 搜索页 AI 入口（悬浮按钮/横幅/搜索框，默认开启） */
    const val UI_CLEAN_SEARCH_AI = "ui_clean_search_ai"
    const val DEFAULT_UI_CLEAN_SEARCH_AI = true

    const val INSTANT_REWARD = "instant_reward"
    const val DEFAULT_INSTANT_REWARD = false
}
