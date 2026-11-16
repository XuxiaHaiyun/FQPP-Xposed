package com.xxhy.fqpp.xposed.xcallback

import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.global.XposedGlobals
import com.xxhy.fqpp.xposed.utils.LogUtils
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Method
import java.util.Collections

/**
 * Hook 公共支撑：方法/构造函数安全叠加 + 统一分级日志（场景 TAG 由调用方传入）。
 *
 * <p>所有 hook 均幂等（同一 Executable 只挂一次）、单异常隔离（不影响其余 hook）、
 * 成功/失败通过 [LogUtils] 输出到调试浮窗与框架日志，供排障与混淆重定位。
 *
 * <p>日志遵循模块规范："场景 TAG + 级别"，安全 hook 仅记录安装/失败等低频事件，
 * 绝不在高频回调路径（如 onAttachedToWindow、阅读计时上报）内反复输出。
 */
internal object HookSupport {

    /** 已挂载的方法/构造器集合（幂等防重复注册，全局唯一） */
    val hookedMethods = Collections.synchronizedSet(mutableSetOf<Any>())

    /** 目标应用类加载器。 */
    fun dragonLoader(): ClassLoader = DragonGlobals.getDragonClassLoader()

    /** 参数类型字符串 → Class（内置原语映射，其余按应用类加载器解析）。 */
    private fun mapParam(classLoader: ClassLoader?, type: String): Class<*>? = when (type) {
        "int" -> Int::class.javaPrimitiveType
        "boolean" -> Boolean::class.javaPrimitiveType
        "long" -> Long::class.javaPrimitiveType
        "float" -> Float::class.javaPrimitiveType
        "double" -> Double::class.javaPrimitiveType
        else -> classLoader!!.loadClass(type)
    }

    /**
     * 幂等 hook 实例方法/静态方法。
     *
     * @param scene 日志场景 TAG（如 "UiClean"、"InstantReward"）
     * @param clazz 目标类全限定名（混淆类的最终名称按 7.0.9.32 验证嵌入）
     * @param methodName 目标方法名
     * @param paramTypes 混淆方法参数类型（null/空 表示无参）
     * @param extraLog 附加描述（用于日志区分多个 hook）
     * @param interceptor 拦截逻辑
     */
    fun safeHook(
        scene: String,
        clazz: String,
        methodName: String,
        paramTypes: Array<String>? = null,
        extraLog: String = "",
        interceptor: (XposedInterface.Chain) -> Any?,
    ) {
        val target = "$clazz.$methodName"
        try {
            val cls = dragonLoader().loadClass(clazz)
            val method: Method = if (paramTypes.isNullOrEmpty()) {
                cls.getDeclaredMethod(methodName)
            } else {
                val types = paramTypes.map { mapParam(cls.classLoader, it)!! }.toTypedArray()
                cls.getDeclaredMethod(methodName, *types)
            }
            if (!hookedMethods.add(method)) return
            XposedGlobals.getXposedEntryInstance().hook(method).intercept { chain ->
                interceptor(chain)
            }
            val desc = extraLog.trimEnd()
            LogUtils.log(scene, "已挂钩 ${if (desc.isNotEmpty()) "$desc " else ""}$target")
        } catch (t: Throwable) {
            LogUtils.logError(scene, "$target hook 失败: ${t.message}", null)
        }
    }

    /**
     * 幂等 hook 构造函数（构造参数可变，如把 showRedDot 强制为 false）。
     */
    fun safeHookCtor(
        scene: String,
        clazz: String,
        paramTypes: Array<String>,
        extraLog: String = "",
        interceptor: (XposedInterface.Chain) -> Any?,
    ) {
        try {
            val cls = dragonLoader().loadClass(clazz)
            val types = paramTypes.map { mapParam(cls.classLoader, it)!! }.toTypedArray()
            val ctor = cls.getDeclaredConstructor(*types)
            if (!hookedMethods.add(ctor)) return
            XposedGlobals.getXposedEntryInstance().hook(ctor).intercept { chain ->
                interceptor(chain as XposedInterface.Chain)
            }
            val desc = extraLog.trimEnd()
            LogUtils.log(scene, "已挂构造器 ${if (desc.isEmpty()) "" else "$desc "}$clazz")
        } catch (t: Throwable) {
            LogUtils.logError(scene, "$clazz.<init> hook 失败: ${t.message}", null)
        }
    }
}