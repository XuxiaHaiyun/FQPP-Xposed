package com.xxhy.fqpp.xposed.xcallback

import android.app.Application
import com.xxhy.fqpp.xposed.HookManager
import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.utils.LogUtils
import com.xxhy.fqpp.xposed.utils.NetworkUtils
import com.xxhy.fqpp.xposed.utils.ToastUtils
import com.xxhy.fqpp.xposed.web.HttpServer
import io.github.libxposed.api.XposedInterface
import java.io.IOException

/**
 * MainApplication 钩子（onCreateAlways 方法）。
 *
 * <p>Hook 目标：{@code com.dragon.read.app.MainApplication.onCreateAlways()}（类名/方法名均稳定）
 *
 * <p>作用：
 * <ul>
 *   <li>原方法执行前：注入全局 Application/ClassLoader 并安装全部钩子（双重检查锁）；
 *   <li>原方法执行后：按需启动 HTTP 服务（随应用启动开关，双重检查锁）。
 * </ul>
 */
class MainApplicationHooker : XposedInterface.Hooker {

    companion object {
        /** 标记 hookAll() 是否已执行（拦截器中，双重检查锁） */
        @Volatile
        var hooksInstalled = false

        /** 标记 HTTP 服务启动逻辑是否已执行（拦截器中，双重检查锁） */
        @Volatile
        var serverStartChecked = false

        private val LOCK = Any()
    }

    override fun intercept(chain: XposedInterface.Chain): Any? {
        // ===== 原方法执行前：安装全部钩子 =====
        if (!hooksInstalled) {
            synchronized(LOCK) {
                if (!hooksInstalled) {
                    // 获取当前应用实例（MainApplication对象），并设置为全局可用
                    val app = chain.getThisObject() as Application
                    val cl = app.classLoader
                    DragonGlobals.setDragonApplication(app)
                    DragonGlobals.initDragonClassLoader(cl)

                    HookManager.hookAll()

                    hooksInstalled = true
                }
            }
        }

        val result = chain.proceed()

        // ===== 原方法执行后：按需启动 HTTP 服务 =====
        if (!serverStartChecked) {
            synchronized(LOCK) {
                if (!serverStartChecked) {
                    // 读取SP配置，判断是否需要随应用启动HTTP服务
                    val sp = ConfigManager.prefs()
                    if (sp.getBoolean(SPConstants.START_WITH_APP, SPConstants.DEFAULT_START_WITH_APP)) {
                        try {
                            val httpServer = HttpServer.getInstance()
                            httpServer.start()

                            val ipAddress = NetworkUtils.getIPAddress(true)
                            val port = sp.getString(SPConstants.PORT, SPConstants.DEFAULT_PORT)
                            val message = "HTTP Server已启动\n$ipAddress:$port"
                            LogUtils.log("MainApp", message)
                            ToastUtils.show(message)
                        } catch (e: IOException) {
                            LogUtils.logError("MainApp", "Failed to start HTTP server:", e)
                            ToastUtils.show(e.toString())
                        }
                    }
                    serverStartChecked = true
                }
            }
        }
        return result
    }
}
