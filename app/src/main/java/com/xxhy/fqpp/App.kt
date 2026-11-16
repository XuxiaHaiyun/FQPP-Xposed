package com.xxhy.fqpp

import android.app.Application
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.concurrent.Volatile

/**
 * 模块 App 入口。
 *
 * <p>通过 libxposed service 库与框架通信：框架会将服务 Binder 推送到
 * 模块 App 的导出 Provider（{@code com.xxhy.fqpp.XposedService}），
 * 模块 App 自身进程无需被注入即可获取 [XposedService]——
 * 用于判断激活状态、读取作用域、请求作用域、读写远程偏好等。
 */
class App : Application(), XposedServiceHelper.OnServiceListener {

    companion object {
        @Volatile
        var service: XposedService? = null
            private set

        private val serviceStateListeners =
            CopyOnWriteArraySet<ServiceStateListener>()

        private fun dispatchServiceState(
            listener: ServiceStateListener,
            service: XposedService?,
        ) {
            if (serviceStateListeners.contains(listener)) {
                listener.onServiceStateChanged(service)
            }
        }

        fun addServiceStateListener(
            listener: ServiceStateListener,
            notifyImmediately: Boolean,
        ) {
            serviceStateListeners.add(listener)
            if (notifyImmediately) {
                dispatchServiceState(listener, service)
            }
        }

        fun removeServiceStateListener(listener: ServiceStateListener) {
            serviceStateListeners.remove(listener)
        }
    }

    private fun notifyServiceStateChanged(service: XposedService?) {
        for (listener in serviceStateListeners) {
            dispatchServiceState(listener, service)
        }
    }

    override fun onCreate() {
        super.onCreate()
        XposedServiceHelper.registerListener(this)
    }

    override fun onServiceBind(service: XposedService) {
        App.service = service
        notifyServiceStateChanged(service)
    }

    override fun onServiceDied(service: XposedService) {
        App.service = null
        notifyServiceStateChanged(null)
    }

    interface ServiceStateListener {
        fun onServiceStateChanged(service: XposedService?)
    }
}
