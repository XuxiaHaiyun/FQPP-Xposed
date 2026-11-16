package com.xxhy.fqpp.xposed.global

import android.app.Application
import com.xxhy.fqpp.xposed.utils.AppUtils
import org.joor.Reflect

/**
 * 目标应用全局变量管理类
 * 用于存储和提供目标应用（com.dragon.read）的核心全局对象，如应用实例、类加载器等
 * 采用工具类设计模式，确保全局访问的唯一性
 */
object DragonGlobals {

    // 目标应用的类加载器（用于加载目标应用内部类）
    @Volatile
    private var classLoader: ClassLoader? = null
    // 目标应用的Application实例（全局上下文）
    @Volatile
    private var application: Application? = null

    /**
     * 获取目标应用的Application实例
     * @return 目标应用的Application对象（全局上下文），若获取失败可能返回null
     * 说明：优先通过AppUtils获取，若失败则通过反射调用目标应用的AppUtils获取
     */
    @JvmStatic
    fun getDragonApplication(): Application {
        if (application == null) {
            try {
                // 尝试通过通用工具类获取应用实例
                application = AppUtils.getApplication()
                // 若获取失败，反射调用目标应用内部的AppUtils获取上下文
                if (application == null) {
                    application = Reflect.onClass(
                        "com.dragon.read.base.util.AppUtils", // 目标应用的AppUtils类
                        getDragonClassLoader() // 目标应用的类加载器
                    )
                        .call("context") // 调用该类的context方法获取应用实例
                        .get()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return application ?: throw NullPointerException("Application has not been initialized")
    }

    /**
     * 设置目标应用的Application实例
     * @param app 目标应用的Application对象
     * 说明：通常在应用初始化完成后（如MainApplication的onCreate后）调用
     */
    @JvmStatic
    fun setDragonApplication(app: Application) {
        application = app
    }

    /**
     * 初始化目标应用的类加载器
     * @param classLoader 目标应用的ClassLoader实例
     * 说明：在目标应用加载完成时调用，用于后续反射加载目标应用内部类
     */
    @JvmStatic
    fun initDragonClassLoader(classLoader: ClassLoader) {
        DragonGlobals.classLoader = classLoader
    }

    /**
     * 检查目标应用类加载器是否已初始化
     * @return 若已初始化则返回true，否则返回false
     */
    @JvmStatic
    fun hasInit(): Boolean {
        return classLoader != null
    }

    /**
     * 获取目标应用的类加载器
     * @return 已初始化的ClassLoader实例
     * @throws NullPointerException 若类加载器未初始化则抛出
     */
    @JvmStatic
    fun getDragonClassLoader(): ClassLoader {
        return classLoader ?: throw NullPointerException("ClassLoader has not been initialized")
    }
}
