package com.xxhy.fqpp.xposed.locate

import com.xxhy.fqpp.xposed.utils.LogUtils

/**
 * 混淆定位服务：一次性定位全部目标，进度/结果通过 ImGuiOverlay 日志弹窗展示。
 *
 * 使用方式：
 * ```
 * val service = LocatorService.create(context, classLoader, versionCode)
 * val results = service.resolveAll()
 * service.resolveMethod(...) // 方法名定位
 * service.close()
 * ```
 */
class LocatorService private constructor(
    private val resolver: DexKitResolver,
) {

    data class ResolutionSummary(
        val total: Int,
        val ok: Int,
        val failed: List<TargetSpec>,
        val classNameMap: Map<String, String>,
    )

    @Volatile
    private var summary: ResolutionSummary? = null

    private val lock = Object()

    /**
     * 后台线程预启动定位（onPackageLoaded 时调用）。
     */
    fun resolveAsync() {
        Thread({
            runCatching { resolveAll() }
        }, "FQPP-DexKit-Locator").start()
    }

    /**
     * 同步等待定位完成（超时 15s，避免阻塞 hook 流程太久）。
     * 未开始定位时（如定位在更早进程阶段失败），直接尝试一次同步定位。
     */
    fun awaitResolved(timeoutMs: Long = 15_000): ResolutionSummary? {
        summary?.let { return it }
        synchronized(lock) {
            summary?.let { return it }
            try {
                (lock as java.lang.Object).wait(timeoutMs)
            } catch (_: InterruptedException) {
            }
            return summary
        }
    }

    /**
     * 解析全部类目标（缓存命中优先），并输出进度日志。
     */
    fun resolveAll(
        specs: List<TargetSpec> = DragonTargets.ALL,
    ): ResolutionSummary {
        val builder = StringBuilder()
        builder.append("===== 开始定位混淆目标（versionCode=")
            .append(resolverVersionCode)
            .append("）=====")
        LogUtils.log("DexKit", builder.toString())

        val failed = mutableListOf<TargetSpec>()
        val classNameMap = mutableMapOf<String, String>()

        val listener = object : DexKitResolver.ProgressListener {
            override fun onTargetProgress(spec: TargetSpec, status: DexKitResolver.TargetStatus) {
                val text = when (status) {
                    DexKitResolver.TargetStatus.CACHE_HIT ->
                        "[缓存] ${spec.description} → 命中"
                    DexKitResolver.TargetStatus.KNOWN_NAME ->
                        "[已知名] ${spec.description} → ${spec.knownNames.firstOrNull()}"
                    DexKitResolver.TargetStatus.DEXKIT_OK ->
                        "[DexKit] ${spec.description} → ${spec.query?.let { describeQuery(it) } ?: "特征"}"
                    DexKitResolver.TargetStatus.FAILED ->
                        "[失败] ${spec.description} ← 未找到，功能将降级"
                }
                LogUtils.log("DexKit", text)
            }
        }
        resolver.addProgressListener(listener)

        val results = resolver.resolveAll(specs)
        resolver.removeListener(listener)

        results.forEach { r ->
            if (r.className != null) {
                classNameMap[r.spec.id] = r.className
            } else {
                failed.add(r.spec)
            }
        }

        // 汇总弹窗日志
        LogUtils.log("DexKit", "===== 定位完成：成功 ${classNameMap.size}/${specs.size} =====")
        if (failed.isNotEmpty()) {
            LogUtils.logWarn("DexKit", "以下目标定位失败，相关功能将停用：")
            failed.forEach {
                LogUtils.logWarn("DexKit", "  x ${it.id}（${it.description}）")
            }
        }
        val result = ResolutionSummary(specs.size, classNameMap.size, failed, classNameMap)
        synchronized(lock) {
            summary = result
            (lock as java.lang.Object).notifyAll()
        }
        return result
    }

    /**
     * 在已定位类中定位混淆方法名。
     */
    fun resolveMethod(ownerClassName: String, hints: List<MethodHint>): String? =
        resolver.resolveMethod(ownerClassName, hints)

    /**
     * 在已定位类中定位混淆方法名（返回全部候选，用于同签名多方法场景）。
     */
    fun resolveMethods(ownerClassName: String, hints: List<MethodHint>): List<String> =
        resolver.resolveMethods(ownerClassName, hints)

    /**
     * 运行时按父类定位子类（如 settings.item 包内 extends itemModel 的实现类）。
     */
    fun findSubclassOf(superClassName: String, searchPackage: String): String? =
        resolver.findSubclassOf(superClassName, searchPackage)

    fun close() = resolver.close()

    private val resolverVersionCode: Int get() = resolver.versionCode

    private fun describeQuery(q: DexQuery): String {
        return listOfNotNull(
            q.className?.let { "类名≈$it" },
            q.superClass?.let { "父类≈$it" },
            q.covodeIndex?.let { "Covode#$it" },
            q.usingStrings.take(2).joinToString("/")?.let { "字符串[$it]" },
        ).joinToString(", ")
    }

    companion object {
        @Volatile
        private var instance: LocatorService? = null

        /**
         * 全局定位服务（由入口在 onPackageLoaded 时创建）。
         */
        fun getInstance(): LocatorService? = instance

        /**
         * 创建定位服务并注册为全局单例。
         *
         * @param classLoader 目标应用类加载器
         * @param versionCode 目标应用版本号（缓存分桶键）
         */
        fun create(classLoader: ClassLoader, versionCode: Int): LocatorService {
            val context = com.xxhy.fqpp.xposed.global.DragonGlobals.getDragonApplication()
                ?: com.xxhy.fqpp.xposed.utils.AppUtils.createAppContext()
                ?: error("无法获取目标应用 Context")
            val cache = LocationCache.getInstance(context)
            val resolver = DexKitResolver(classLoader, versionCode, cache)
            return LocatorService(resolver).also { instance = it }
        }

        /**
         * 便捷方法：等待并返回定位结果（未创建时返回 null）。
         */
        fun await(): ResolutionSummary? = instance?.awaitResolved()
    }
}
