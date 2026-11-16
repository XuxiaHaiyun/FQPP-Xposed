package com.xxhy.fqpp.xposed.global

import com.xxhy.fqpp.xposed.XposedEntry
import io.github.libxposed.api.XposedModuleInterface

/**
 * Xposed全局变量管理类
 * 用于存储和提供Xposed模块运行过程中的核心全局对象，如Xposed入口实例等
 * 采用工具类设计模式，确保全局访问的唯一性
 */
object XposedGlobals {

    // Xposed模块入口实例（单例）
    @Volatile
    private var xposedEntryInstance: XposedEntry? = null
    // 应用包加载参数（包含目标应用的包信息、类加载器等）
    @Volatile
    private var packageLoadedParam: XposedModuleInterface.PackageLoadedParam? = null

    /**
     * 设置Xposed入口实例
     * @param instance XposedEntry实例（通常在XposedEntry构造时调用）
     */
    @JvmStatic
    fun setXposedEntryInstance(instance: XposedEntry) {
        xposedEntryInstance = instance
    }

    /**
     * 获取Xposed入口实例
     * @return 已初始化的XposedEntry实例
     * @throws NullPointerException 若实例未初始化则抛出
     */
    @JvmStatic
    fun getXposedEntryInstance(): XposedEntry {
        return xposedEntryInstance
            ?: throw NullPointerException("XposedEntryInstance has not been initialized")
    }

    /**
     * 尝试获取Xposed入口实例
     * @return 已初始化的XposedEntry实例，未初始化（未激活/未加载）时返回 null
     */
    @JvmStatic
    fun tryGetXposedEntryInstance(): XposedEntry? {
        return xposedEntryInstance
    }

    /**
     * 设置应用包加载参数
     * @param param 应用包加载参数（通常在目标应用加载完成时调用）
     */
    @JvmStatic
    fun setPackageLoadedParam(param: XposedModuleInterface.PackageLoadedParam) {
        packageLoadedParam = param
    }

    /**
     * 获取应用包加载参数
     * @return 已初始化的应用包加载参数
     * @throws NullPointerException 若参数未初始化则抛出
     */
    @JvmStatic
    fun getPackageLoadedParam(): XposedModuleInterface.PackageLoadedParam {
        return packageLoadedParam
            ?: throw NullPointerException("PackageLoadedParam has not been initialized")
    }
}
