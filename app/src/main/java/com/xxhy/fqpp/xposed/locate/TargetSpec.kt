package com.xxhy.fqpp.xposed.locate

/**
 * 单个 hook 目标的定位规格。
 *
 * 定位流程分两级：
 * 1. 已知类名快路径：直接用 [knownNames] 尝试 ClassLoader 加载（零成本）；
 * 2. DexKit 特征路径：全部已知名加载失败时，用 [query] 按特征定位。
 */
data class TargetSpec(
    /** 稳定标识，同时作为缓存键的一部分 */
    val id: String,
    /** 人类可读描述（日志/弹窗展示） */
    val description: String,
    /** 已知类全限定名列表（跨版本积累），按优先级排序 */
    val knownNames: List<String> = emptyList(),
    /** DexKit 特征查询（knownNames 全部失败时使用），null 表示不支持特征定位 */
    val query: DexQuery? = null,
)

/**
 * DexKit 查询特征描述。各字段均可选，由 [DexKitResolver] 组装为 FindClass。
 */
data class DexQuery(
    /** 类名匹配（混淆类经常保留部分可读前缀，如 recyler. 保持不变） */
    val className: String? = null,
    /** 父类（支持通配，如 androidx.recyclerview.widget.RecyclerView$Adapter） */
    val superClass: String? = null,
    /** 实现接口（支持通配） */
    val interfaces: List<String> = emptyList(),
    /** 类内方法特征（方法名/签名/字符串），用于二次确认 */
    val methodHints: List<MethodHint> = emptyList(),
    /** 类内字段特征 */
    val fieldHints: List<FieldHint> = emptyList(),
    /** 类中使用的字符串（usingStrings），用于批量字符串定位 */
    val usingStrings: List<String> = emptyList(),
    /** Covode 类索引（static 块中的常量），特征极强 */
    val covodeIndex: Int? = null,
    /** 搜索限定包前缀（提升性能，如 com.dragon.read.） */
    val searchPackage: String? = null,
    /** 类访问标志位（如 abstract），Java 反射 Modifier 值 */
    val classModifiers: Int? = null,
)

data class MethodHint(
    val name: String? = null,
    val returnType: String? = null,
    val paramTypes: List<String> = emptyList(),
    val usingStrings: List<String> = emptyList(),
)

data class FieldHint(
    val name: String? = null,
    val type: String? = null,
)

/**
 * 定位结果：解析出的真实类名 + 缓存元数据。
 */
data class LocateResult(
    val targetId: String,
    /** 解析出的真实类全限定名；null 表示定位失败 */
    val className: String?,
    /** 解析耗时（ms） */
    val elapsedMs: Long,
    /** 命中方式：KNOWN_NAME / DEXKIT_QUERY / CACHE */
    val source: String,
)
