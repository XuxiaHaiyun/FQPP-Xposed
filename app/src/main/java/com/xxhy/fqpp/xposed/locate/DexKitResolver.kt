package com.xxhy.fqpp.xposed.locate

import com.xxhy.fqpp.xposed.utils.LogUtils
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.enums.StringMatchType
import org.luckypray.dexkit.query.matchers.ClassMatcher
import org.luckypray.dexkit.query.matchers.FieldMatcher
import org.luckypray.dexkit.query.matchers.MethodMatcher
import java.lang.reflect.Modifier

/**
 * DexKit 混淆定位器。
 *
 * 流程：
 * 1. 命中缓存（同 versionCode）→ 直接返回，零开销；
 * 2. 已知类名快路径 → ClassLoader.loadClass 逐个尝试；
 * 3. 全失败 → DexKit 按特征查询（进度通过 [onProgress] 回传给 ImGuiOverlay 日志弹窗）；
 * 4. 定位成功 → 写入缓存；失败 → 记录失败清单供降级。
 */
class DexKitResolver(
    private val classLoader: ClassLoader,
    val versionCode: Int,
    private val cache: LocationCache,
) {

    interface ProgressListener {
        /** 单个目标定位进度（开始/成功/失败） */
        fun onTargetProgress(spec: TargetSpec, status: TargetStatus)
    }

    enum class TargetStatus(val label: String) {
        CACHE_HIT("缓存命中"),
        KNOWN_NAME("已知名命中"),
        DEXKIT_OK("DexKit定位成功"),
        FAILED("定位失败"),
    }

    data class ResolvedTarget(
        val spec: TargetSpec,
        val className: String?,
        val status: TargetStatus,
        val elapsedMs: Long,
    )

    @Volatile
    private var bridge: DexKitBridge? = null

    private val progressListeners = mutableListOf<ProgressListener>()

    fun addProgressListener(listener: ProgressListener) {
        progressListeners.add(listener)
    }

    fun removeListener(listener: ProgressListener) {
        progressListeners.remove(listener)
    }

    /**
     * 定位单个目标。返回解析出的类名（可能为 null）。
     */
    fun resolve(spec: TargetSpec): ResolvedTarget {
        val start = System.currentTimeMillis()

        // 1. 缓存命中
        cache.ensureVersion(versionCode)
        cache.getClassName(spec.id)?.let { className ->
            notify(spec, TargetStatus.CACHE_HIT)
            return ResolvedTarget(spec, className, TargetStatus.CACHE_HIT, System.currentTimeMillis() - start)
        }

        // 2. 已知类名快路径
        for (known in spec.knownNames) {
            try {
                classLoader.loadClass(known)
                cache.putClassName(spec.id, known)
                notify(spec, TargetStatus.KNOWN_NAME)
                return ResolvedTarget(spec, known, TargetStatus.KNOWN_NAME, System.currentTimeMillis() - start)
            } catch (_: Throwable) {
                // 继续尝试下一个
            }
        }

        // 3. DexKit 特征查询
        val query = spec.query
        if (query != null) {
            try {
                val dexClass = queryClass(query)
                if (dexClass != null) {
                    cache.putClassName(spec.id, dexClass)
                    notify(spec, TargetStatus.DEXKIT_OK)
                    return ResolvedTarget(spec, dexClass, TargetStatus.DEXKIT_OK, System.currentTimeMillis() - start)
                }
            } catch (t: Throwable) {
                LogUtils.logWarn("DexKit", "查询异常 ${spec.id}: ${t.message}")
            }
        }

        // 4. 失败（不写缓存，下次重试）
        notify(spec, TargetStatus.FAILED)
        return ResolvedTarget(spec, null, TargetStatus.FAILED, System.currentTimeMillis() - start)
    }

    /**
     * 批量定位。逐个执行并回调进度。
     *
     * @return 定位成功（className != null）的目标列表
     */
    fun resolveAll(specs: List<TargetSpec>): List<ResolvedTarget> {
        cache.ensureVersion(versionCode)
        return specs.map { resolve(it) }
    }

    /**
     * 批量按字符串特征定位类（BatchFindClassUsingStrings 一次扫全 dex，性能更好）。
     * 仅当多个目标都以 usingStrings 为主特征时使用。
     */
    fun resolveBatchUsingStrings(specs: List<TargetSpec>): Map<String, String?> {
        cache.ensureVersion(versionCode)
        val result = mutableMapOf<String, String?>()

        // 缓存/已知名优先
        specs.forEach { spec ->
            val hit = resolveFastPathOnly(spec)
            if (hit != null) {
                result[spec.id] = hit
            }
        }

        // 剩余目标用 batch 字符串查询
        val pending = specs.filter { result[it.id] == null && !it.query?.usingStrings.isNullOrEmpty() }
        if (pending.isNotEmpty()) {
            try {
                val bridge = getBridge()
                val query = org.luckypray.dexkit.query.BatchFindClassUsingStrings.create()
                val searchPackage = pending.firstNotNullOfOrNull { it.query?.searchPackage }
                if (searchPackage != null) query.searchPackages(searchPackage)
                pending.forEach { spec ->
                    query.addSearchGroup(spec.id, spec.query!!.usingStrings, StringMatchType.Contains, false)
                }
                val map = bridge.batchFindClassUsingStrings(query)
                pending.forEach { spec ->
                    val dexClasses = map[spec.id]
                    if (dexClasses != null && dexClasses.size == 1) {
                        val name = dexClasses[0].name
                        cache.putClassName(spec.id, name)
                        result[spec.id] = name
                    }
                }
            } catch (t: Throwable) {
                LogUtils.logWarn("DexKit", "批量字符串查询异常: ${t.message}")
            }
        }
        return result
    }

    /**
     * 在已定位的类中定位混淆方法名。
     *
     * @param ownerClassName 目标类全限定名（须已加载成功）
     * @param hints 方法特征（名字提示/签名/字符串），全部命中才返回
     * @return 匹配的方法名；无法唯一确定返回 null
     */
    fun resolveMethod(ownerClassName: String, hints: List<MethodHint>): String? {
        return resolveMethods(ownerClassName, hints).singleOrNull()
    }

    /**
     * 在已定位的类中定位混淆方法名（多候选版本）。
     *
     * @param ownerClassName 目标类全限定名（须已加载成功）
     * @param hints 方法特征，全部命中才返回；首个 hint 有命中即返回
     * @return 匹配的全部方法名（可能多个同签名候选，如 b2/c2）
     */
    fun resolveMethods(ownerClassName: String, hints: List<MethodHint>): List<String> {
        val bridge = getBridge()
        for (hint in hints) {
            try {
                val mm = MethodMatcher.create()
                mm.declaredClass(ownerClassName, StringMatchType.Equals, false)
                hint.name?.let { mm.name(it, StringMatchType.Equals, false) }
                hint.returnType?.let { mm.returnType(it, StringMatchType.Equals, false) }
                if (hint.paramTypes.isNotEmpty()) {
                    mm.paramTypes(hint.paramTypes)
                }
                hint.usingStrings.forEach { mm.addUsingString(it, StringMatchType.Contains, false) }
                val query = org.luckypray.dexkit.query.FindMethod.create()
                    .searchPackages(ownerClassName.substringBeforeLast('.', "com.dragon.read"))
                    .matcher(mm)
                val found = bridge.findMethod(query)
                if (!found.isNullOrEmpty()) {
                    return found.map { it.methodName }.distinct()
                }
            } catch (t: Throwable) {
                LogUtils.logWarn("DexKit", "方法定位异常 ${hint.name}: ${t.message}")
            }
        }
        return emptyList()
    }

    /**
     * 运行时按父类定位直接子类（用于运行时动态查找实现类）。
     *
     * @param superClassName 父类全限定名（Equals 精确匹配）
     * @param searchPackage 限定搜索包前缀
     * @return 唯一命中的子类全限定名；多个候选返回 null
     */
    fun findSubclassOf(superClassName: String, searchPackage: String): String? {
        return runCatching {
            val matcher = ClassMatcher.create()
                .superClass(superClassName, StringMatchType.Equals, false)
            val found = getBridge().findClass(
                FindClass.create().searchPackages(searchPackage).matcher(matcher)
            )
            if (found != null && found.size == 1) found[0].name else null
        }.getOrNull()
    }

    /**
     * 释放 DexKit 资源（进程退出/长时间不用时调用）。
     */
    fun close() {
        bridge?.close()
        bridge = null
    }

    private fun resolveFastPathOnly(spec: TargetSpec): String? {
        cache.getClassName(spec.id)?.let { return it }
        for (known in spec.knownNames) {
            try {
                classLoader.loadClass(known)
                cache.putClassName(spec.id, known)
                return known
            } catch (_: Throwable) {
            }
        }
        return null
    }

    private fun queryClass(query: DexQuery): String? {
        val bridge = getBridge()
        val findClass = buildFindClass(query)
        val classes = bridge.findClass(findClass)
        if (classes.isNullOrEmpty()) return null
        // 若有 covodeIndex 或方法提示，过滤确认
        val confirmed = classes.firstOrNull { confirmQuery(it.name, query) } ?: classes.first()
        return confirmed.name
    }

    private fun getBridge(): DexKitBridge {
        bridge?.let { return it }
        synchronized(this) {
            bridge?.let { return it }
            // DexKit 加载 native 库失败时抛异常，由调用方捕获降级
            val b = DexKitBridge.create(classLoader, true)
            b.setThreadNum(Runtime.getRuntime().availableProcessors())
            b.setMaxConcurrentQueries(4)
            bridge = b
            return b
        }
    }

    private fun buildFindClass(query: DexQuery): FindClass {
        val matcher = ClassMatcher.create()
        query.className?.let { matcher.className(it, StringMatchType.Contains, false) }
        query.superClass?.let { matcher.superClass(it, StringMatchType.Contains, false) }
        query.interfaces.forEach { matcher.addInterface(it, StringMatchType.Contains, false) }
        query.classModifiers?.let { matcher.modifiers(it, org.luckypray.dexkit.query.enums.MatchType.Contains) }
        query.covodeIndex?.let { idx ->
            val methodsMatcher = org.luckypray.dexkit.query.matchers.MethodsMatcher.create()
            val clinitMatcher = MethodMatcher.create()
                .name("<clinit>", StringMatchType.Equals, false)
                .usingNumbers(idx.toLong())
            methodsMatcher.add(clinitMatcher)
            matcher.methods(methodsMatcher)
        }
        return FindClass.create()
            .searchPackages(query.searchPackage ?: "com.dragon.read")
            .matcher(matcher)
    }

    private fun confirmQuery(className: String, query: DexQuery): Boolean {
        // 已通过 Covode 索引或唯一性确认的无需二次校验
        if (query.covodeIndex != null) return true
        if (query.methodHints.isEmpty() && query.fieldHints.isEmpty()) return true

        val bridge = getBridge()
        // 方法提示二次确认
        for (hint in query.methodHints) {
            try {
                val methodQuery = org.luckypray.dexkit.query.FindMethod.create()
                    .searchPackages(query.searchPackage ?: "com.dragon.read")
                val mm = MethodMatcher.create()
                mm.declaredClass(className, StringMatchType.Equals, false)
                hint.name?.let { mm.name(it, StringMatchType.Equals, false) }
                hint.returnType?.let { mm.returnType(it, StringMatchType.Equals, false) }
                if (hint.paramTypes.isNotEmpty()) {
                    mm.paramTypes(hint.paramTypes)
                }
                hint.usingStrings.forEach { mm.addUsingString(it, StringMatchType.Contains, false) }
                val found = bridge.findMethod(methodQuery.matcher(mm))
                if (found.isNullOrEmpty()) return false
            } catch (_: Throwable) {
                return false
            }
        }
        // 字段提示二次确认
        for (hint in query.fieldHints) {
            try {
                val fieldQuery = org.luckypray.dexkit.query.FindField.create()
                    .searchPackages(query.searchPackage ?: "com.dragon.read")
                val fm = FieldMatcher.create()
                fm.declaredClass(className, StringMatchType.Equals, false)
                hint.name?.let { fm.name(it, StringMatchType.Equals, false) }
                hint.type?.let { fm.type(it, StringMatchType.Equals, false) }
                val found = bridge.findField(fieldQuery.matcher(fm))
                if (found.isNullOrEmpty()) return false
            } catch (_: Throwable) {
                return false
            }
        }
        return true
    }

    private fun notify(spec: TargetSpec, status: TargetStatus) {
        progressListeners.forEach { it.onTargetProgress(spec, status) }
    }
}
