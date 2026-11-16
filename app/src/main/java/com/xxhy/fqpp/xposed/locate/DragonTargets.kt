package com.xxhy.fqpp.xposed.locate

/**
 * 全部 hook 目标的定位规格（基于 7.0.9.32 反编译验证，见 HookManager 注释与 jadx 分析）。
 *
 * 每个目标：已知类名（7.0.9.32）+ DexKit 特征（跨版本兜底）。
 */
object DragonTargets {

    // ===== 混淆类目标 =====

    /** 底部 TAB 角标气泡控制器（bf4.c0，单例，7.0.9.32：方法 F0/G0/h0/c0） */
    val TAB_BADGE_CONTROLLER = TargetSpec(
        id = "tab_badge_controller",
        description = "底部TAB角标控制器",
        knownNames = listOf("bf4.c0"),
        query = DexQuery(
            covodeIndex = 600663,
            searchPackage = "bf4",
            methodHints = listOf(
                MethodHint(name = "F0"),
                MethodHint(name = "G0", returnType = "boolean"),
                MethodHint(name = "h0"),
            ),
        ),
    )

    /** 滑栏功能项数据类（i44.c = SidebarFuncItemData，7.0.9.32：showRedDot 字段/f() 方法） */
    val SIDEBAR_FUNC_ITEM = TargetSpec(
        id = "sidebar_func_item",
        description = "滑栏功能项数据类",
        knownNames = listOf("i44.c"),
        query = DexQuery(
            covodeIndex = 592747,
            searchPackage = "i44",
            methodHints = listOf(
                MethodHint(name = "f", returnType = "boolean"),
            ),
        ),
    )

    /** 迷你游戏开关服务（e44.c = 单例，7.0.9.32：a() 汇总 hideMiniGame） */
    val MINI_GAME_SERVICE = TargetSpec(
        id = "mini_game_service",
        description = "迷你游戏开关服务",
        knownNames = listOf("e44.c"),
        query = DexQuery(
            covodeIndex = 592686,
            searchPackage = "e44",
            methodHints = listOf(
                MethodHint(name = "a", returnType = "boolean"),
            ),
        ),
    )

    /** 全屏福利广告管理器（bd2.c implements IActivityScreenAdManager，7.0.9.32） */
    val SCREEN_AD_MANAGER = TargetSpec(
        id = "screen_ad_manager",
        description = "全屏福利广告管理器",
        knownNames = listOf("bd2.c"),
        query = DexQuery(
            covodeIndex = 565239,
            interfaces = listOf("com.dragon.read.ad.screen.IActivityScreenAdManager"),
            methodHints = listOf(
                MethodHint(name = "b", returnType = "boolean"),
                MethodHint(name = "c"),
                MethodHint(name = "onScreenAdDialogShow", returnType = "boolean"),
            ),
        ),
    )

    /** 搜索页板块数据构建器（help.z，7.0.9.32：S/I/M/R/T 构造层） */
    val SEARCH_RESULT_BUILDER = TargetSpec(
        id = "search_result_builder",
        description = "搜索页板块数据构建器",
        knownNames = listOf("com.dragon.read.component.biz.impl.help.z"),
        query = DexQuery(
            covodeIndex = 579958,
            searchPackage = "com.dragon.read.component.biz.impl.help",
            methodHints = listOf(
                MethodHint(name = "z"),
                MethodHint(name = "R"),
            ),
        ),
    )

    /** RecyclerView 抽象适配器基类（设置页列表适配器） */
    val RECYCLER_VIEW_ADAPTER = TargetSpec(
        id = "recyler_adapter",
        description = "RecyclerView 抽象适配器",
        knownNames = listOf("com.dragon.read.recyler.c"),
        query = DexQuery(
            superClass = "androidx.recyclerview.widget.RecyclerView\$Adapter",
            covodeIndex = 604162,
            searchPackage = "com.dragon.read",
            classModifiers = java.lang.reflect.Modifier.ABSTRACT,
            methodHints = listOf(
                MethodHint(name = "getDataList"),
                MethodHint(name = "setDataList"),
            ),
        ),
    )

    /** 设置项模型基类（fd4.c extends hd4.a.AbstractC3106a） */
    val ITEM_MODEL = TargetSpec(
        id = "item_model",
        description = "设置项模型基类",
        knownNames = listOf("fd4.c"),
        query = DexQuery(
            superClass = "hd4.a",
            covodeIndex = 597606,
            searchPackage = "fd4",
            fieldHints = listOf(
                FieldHint(type = "java.lang.CharSequence"),
            ),
        ),
    )

    /** 设置项点击监听接口（fd4.a: void a(View, fd4.c, int)） */
    val ITEM_CLICK_LISTENER_INTERFACE = TargetSpec(
        id = "item_click_listener_interface",
        description = "设置项点击监听接口",
        knownNames = listOf("fd4.a"),
        query = DexQuery(
            searchPackage = "fd4",
            methodHints = listOf(
                MethodHint(
                    name = "a",
                    returnType = "void",
                    paramTypes = listOf("android.view.View", "fd4.c", "int"),
                ),
            ),
        ),
    )

    /** 清理缓存设置项（settings.item.d extends fd4.c） */
    val CLEAR_CACHE_ITEM = TargetSpec(
        id = "clear_cache_item",
        description = "清理缓存设置项",
        knownNames = listOf("com.dragon.read.component.biz.impl.mine.settings.item.d"),
        query = DexQuery(
            superClass = "fd4.c",
            covodeIndex = 581974,
            searchPackage = "com.dragon.read.component.biz.impl.mine.settings",
        ),
    )

    /** 书籍评论管理器（uq4.q，单例 k()） */
    val BOOK_COMMENT_MANAGER = TargetSpec(
        id = "book_comment_manager",
        description = "书籍评论管理器",
        knownNames = listOf("uq4.q"),
        query = DexQuery(
            covodeIndex = 613664,
            searchPackage = "uq4",
            usingStrings = listOf("BookComment"),
        ),
    )

    /** RPC 门面类（ro4.b，100 个静态请求门面） */
    val RPC_FACADE_B = TargetSpec(
        id = "rpc_facade_b",
        description = "RPC 请求门面",
        knownNames = listOf("ro4.b"),
        query = DexQuery(
            covodeIndex = 612424,
            searchPackage = "ro4",
            methodHints = listOf(
                MethodHint(name = "s", returnType = "java.lang.Class"),
            ),
        ),
    )

    /** RPC 全量请求门面（ro4.f，方法 k: FullRequest→FullResponse） */
    val RPC_FACADE_F = TargetSpec(
        id = "rpc_facade_f",
        description = "RPC 全量请求门面",
        knownNames = listOf("ro4.f"),
        query = DexQuery(
            covodeIndex = 612442,
            searchPackage = "ro4",
            methodHints = listOf(
                MethodHint(
                    name = "k",
                    paramTypes = listOf("com.dragon.read.rpc.model.FullRequest"),
                    returnType = "io.reactivex.Observable",
                ),
            ),
        ),
    )

    /** 评论 RPC 门面（未混淆：com.dragon.read.saas.ugc.rpc.CommentApiService，7.0.9.32 验证） */
    val COMMENT_API_SERVICE = TargetSpec(
        id = "comment_api_service",
        description = "评论 RPC 门面",
        knownNames = listOf("com.dragon.read.saas.ugc.rpc.CommentApiService"),
        query = DexQuery(
            usingStrings = listOf("commentapi"),
            methodHints = listOf(
                MethodHint(name = "getCommentListRxJava"),
            ),
        ),
    )

    /** 评论列表请求模型（未混淆，7.0.9.32 验证） */
    val COMMENT_LIST_REQUEST = TargetSpec(
        id = "comment_list_request",
        description = "评论列表请求模型",
        knownNames = listOf("com.dragon.read.saas.ugc.model.GetCommentListRequest"),
        query = DexQuery(
            fieldHints = listOf(
                FieldHint(name = "commentSource"),
                FieldHint(name = "groupType"),
            ),
        ),
    )

    /** 子评论列表请求模型（未混淆，7.0.9.32 验证） */
    val REPLY_LIST_REQUEST = TargetSpec(
        id = "reply_list_request",
        description = "子评论列表请求模型",
        knownNames = listOf("com.dragon.read.saas.ugc.model.GetReplyListRequest"),
        query = DexQuery(
            fieldHints = listOf(
                FieldHint(name = "commentID"),
                FieldHint(name = "commentSource"),
            ),
        ),
    )

    /** 评论枚举模型（UgcCommentGroupTypeOutter，未混淆，7.0.9.32 验证） */
    val COMMENT_GROUP_TYPE = TargetSpec(
        id = "comment_group_type",
        description = "评论分组类型枚举",
        knownNames = listOf("com.dragon.read.saas.ugc.model.UgcCommentGroupTypeOutter"),
    )

    /** 评论来源枚举（UgcCommentSourceEnum，未混淆，7.0.9.32 验证） */
    val COMMENT_SOURCE_ENUM = TargetSpec(
        id = "comment_source_enum",
        description = "评论来源枚举",
        knownNames = listOf("com.dragon.read.saas.ugc.model.UgcCommentSourceEnum"),
    )

    /** 评论相对类型枚举（UgcRelativeType，未混淆，7.0.9.32 验证） */
    val COMMENT_RELATIVE_TYPE = TargetSpec(
        id = "comment_relative_type",
        description = "评论相对类型枚举",
        knownNames = listOf("com.dragon.read.saas.ugc.model.UgcRelativeType"),
    )

    /** 评论频道枚举（UgcCommentChannelEnum，未混淆，7.0.9.32 验证） */
    val COMMENT_CHANNEL_ENUM = TargetSpec(
        id = "comment_channel_enum",
        description = "评论频道枚举",
        knownNames = listOf("com.dragon.read.saas.ugc.model.UgcCommentChannelEnum"),
    )

    /** 评论业务参数（CommentBusinessParam，未混淆，7.0.9.32 验证） */
    val COMMENT_BUSINESS_PARAM = TargetSpec(
        id = "comment_business_param",
        description = "评论业务参数",
        knownNames = listOf("com.dragon.read.saas.ugc.model.CommentBusinessParam"),
    )

    /** 子评论业务参数（ReplyBusinessParam，未混淆，7.0.9.32 验证） */
    val REPLY_BUSINESS_PARAM = TargetSpec(
        id = "reply_business_param",
        description = "子评论业务参数",
        knownNames = listOf("com.dragon.read.saas.ugc.model.ReplyBusinessParam"),
    )

    /** 排序枚举（UgcSortEnum，未混淆，7.0.9.32 验证） */
    val COMMENT_SORT_ENUM = TargetSpec(
        id = "comment_sort_enum",
        description = "评论排序枚举",
        knownNames = listOf("com.dragon.read.saas.ugc.model.UgcSortEnum"),
    )

    /** 折叠类型枚举（FoldType，未混淆，7.0.9.32 验证） */
    val COMMENT_FOLD_TYPE = TargetSpec(
        id = "comment_fold_type",
        description = "评论折叠类型枚举",
        knownNames = listOf("com.dragon.read.saas.ugc.model.FoldType"),
    )

    // ===== 稳定类目标（类名未混淆，仍纳入定位便于未来升级兜底） =====

    /** 设置页 Activity */
    val SETTINGS_ACTIVITY = TargetSpec(
        id = "settings_activity",
        description = "设置页 Activity",
        knownNames = listOf("com.dragon.read.component.biz.impl.mine.settings.SettingsActivity"),
    )

    /** 主 Application */
    val MAIN_APPLICATION = TargetSpec(
        id = "main_application",
        description = "主 Application",
        knownNames = listOf("com.dragon.read.app.MainApplication"),
    )

    /** 更新状态监听接口 */
    val UPDATE_STATUS_LISTENER = TargetSpec(
        id = "update_status_listener",
        description = "更新状态监听接口",
        knownNames = listOf("com.ss.android.update.OnUpdateStatusChangedListener"),
    )

    /** 更新服务实现 */
    val UPDATE_SERVICE = TargetSpec(
        id = "update_service",
        description = "更新服务实现",
        knownNames = listOf("com.ss.android.update.UpdateServiceImpl"),
    )

    /** VIP 子类型枚举 */
    val VIP_COMMON_SUB_TYPE = TargetSpec(
        id = "vip_common_sub_type",
        description = "VIP 子类型枚举",
        knownNames = listOf("com.dragon.read.rpc.model.VipCommonSubType"),
    )

    /** VIP 信息模型 */
    val VIP_INFO_MODEL = TargetSpec(
        id = "vip_info_model",
        description = "VIP 信息模型",
        knownNames = listOf("com.dragon.read.user.model.VipInfoModel"),
    )

    /** 账户管理器 */
    val ACCOUNT_MANAGER = TargetSpec(
        id = "account_manager",
        description = "账户管理器",
        knownNames = listOf("com.dragon.read.user.AcctManager"),
    )

    /** 权限管理器 */
    val PRIVILEGE_MANAGER = TargetSpec(
        id = "privilege_manager",
        description = "权限管理器",
        knownNames = listOf("com.dragon.read.component.biz.impl.privilege.PrivilegeManager"),
    )

    /** 书籍工具类 */
    val BOOK_UTILS = TargetSpec(
        id = "book_utils",
        description = "书籍工具类",
        knownNames = listOf("com.dragon.read.util.BookUtils"),
    )

    /** 阅读时长统计（Kotlin 类） */
    val READING_TIMING = TargetSpec(
        id = "reading_timing",
        description = "阅读时长统计",
        knownNames = listOf("com.dragon.read.polaris.timing.ReadingTiming"),
        query = DexQuery(
            covodeIndex = 601541,
            searchPackage = "com.dragon.read.polaris",
        ),
    )

    /** 崩溃监控核心（bytedance crash SDK） */
    val NPTH_CORE = TargetSpec(
        id = "npth_core",
        description = "崩溃监控核心",
        knownNames = listOf("com.bytedance.crash.NpthCore"),
    )

    /** 崩溃上报配置 */
    val CRASH_REPORT_CONFIG = TargetSpec(
        id = "crash_report_config",
        description = "崩溃上报配置",
        knownNames = listOf("com.dragon.read.base.ssconfig.template.CrashReportConfig"),
    )

    // ===== 全量清单 =====

    val ALL: List<TargetSpec> = listOf(
        TAB_BADGE_CONTROLLER,
        SIDEBAR_FUNC_ITEM,
        MINI_GAME_SERVICE,
        SCREEN_AD_MANAGER,
        SEARCH_RESULT_BUILDER,
        RECYCLER_VIEW_ADAPTER,
        ITEM_MODEL,
        ITEM_CLICK_LISTENER_INTERFACE,
        CLEAR_CACHE_ITEM,
        BOOK_COMMENT_MANAGER,
        RPC_FACADE_B,
        RPC_FACADE_F,
        COMMENT_API_SERVICE,
        COMMENT_LIST_REQUEST,
        REPLY_LIST_REQUEST,
        COMMENT_GROUP_TYPE,
        COMMENT_SOURCE_ENUM,
        COMMENT_RELATIVE_TYPE,
        COMMENT_CHANNEL_ENUM,
        COMMENT_BUSINESS_PARAM,
        REPLY_BUSINESS_PARAM,
        COMMENT_SORT_ENUM,
        COMMENT_FOLD_TYPE,
        SETTINGS_ACTIVITY,
        MAIN_APPLICATION,
        UPDATE_STATUS_LISTENER,
        UPDATE_SERVICE,
        VIP_COMMON_SUB_TYPE,
        VIP_INFO_MODEL,
        ACCOUNT_MANAGER,
        PRIVILEGE_MANAGER,
        BOOK_UTILS,
        READING_TIMING,
        NPTH_CORE,
        CRASH_REPORT_CONFIG,
    )

    // ===== 混淆方法目标 =====

    /**
     * SettingsActivity 构建设置项列表的私有方法（7.0.9: b2/c2 → 7.2.7: Q1/R1）。
     * 特征：private List<fd4.c> X(recyler.c<fd4.c>)。
     */
    val SETTINGS_BUILD_LIST_METHODS = listOf(
        MethodHint(
            returnType = "java.util.List",
            paramTypes = listOf("com.dragon.read.recyler.c"),
            usingStrings = listOf("fqpp"),
        ),
    )

    /** ReadingTiming 上报方法（private final void g(long, long)） */
    val READING_TIME_ADD_METHOD = listOf(
        MethodHint(
            returnType = "void",
            paramTypes = listOf("long", "long"),
        ),
    )

    /** AcctManager 单例获取器（public static AcctManager X()） */
    val ACCOUNT_SINGLETON_METHOD = listOf(
        MethodHint(
            name = "P",
            returnType = "com.dragon.read.user.AcctManager",
        ),
    )

    /** AcctManager 登录状态（public boolean islogin()） */
    val ACCOUNT_IS_LOGIN_METHOD = listOf(
        MethodHint(
            name = "islogin",
            returnType = "boolean",
        ),
    )
}
