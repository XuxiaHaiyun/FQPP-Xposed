package com.xxhy.fqpp.xposed.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import org.joor.Reflect

/**
 * 应用相关工具类
 * 注意：此类通过反射调用Android系统内部API（如ActivityThread），这些API不属于公开稳定接口，
 * 可能因Android版本、厂商定制而存在兼容性问题，使用时需谨慎评估风险。
 */
object AppUtils {

    /**
     * 通过反射创建应用上下文（Application Context）
     * 原理：通过ActivityThread获取应用绑定信息，再通过ContextImpl创建上下文
     *
     * @return 应用级上下文，若反射调用失败可能返回null
     */
    @JvmStatic
    fun createAppContext(): Context? {
        return try {
            // 获取当前ActivityThread实例（Android系统内部管理Activity的核心类）
            val activityThread = Reflect.onClass("android.app.ActivityThread")
                .call("currentActivityThread")
                .get<Any>()

            // 获取应用绑定数据（存储应用相关信息）
            val appBindData = Reflect.on(activityThread)
                .field("mBoundApplication")
                .get<Any>()

            // 获取应用信息对象（包含PackageInfo等信息）
            val appInfo = Reflect.on(appBindData)
                .field("info")
                .get<Any>()

            // 通过ContextImpl创建应用上下文
            Reflect.onClass("android.app.ContextImpl")
                .call("createAppContext", activityThread, appInfo)
                .get()
        } catch (e: Exception) {
            // 捕获反射过程中可能出现的异常（类不存在、方法找不到等）
            e.printStackTrace()
            null
        }
    }

    /**
     * 通过反射获取Application实例
     * 原理：通过ActivityThread的getApplication()方法获取
     *
     * @return 应用的Application实例，若反射失败可能返回null
     */
    @JvmStatic
    fun getApplication(): Application? {
        return try {
            // 链式调用：获取ActivityThread -> 调用getApplication()
            Reflect.onClass("android.app.ActivityThread")
                .call("currentActivityThread")
                .call("getApplication")
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取当前处于前台的Activity（非暂停状态）
     * 原理：从ActivityThread的mActivities中筛选未暂停的Activity
     *
     * @return 当前活动的Activity，若未找到或反射失败返回null
     */
    @JvmStatic
    fun getCurrentActivity(): Activity? {
        return try {
            // 获取ActivityThread实例
            val activityThread = Reflect.onClass("android.app.ActivityThread")
                .call("currentActivityThread")
                .get<Any>()

            // 获取mActivities字段（存储ActivityRecord的Map，key为Token，value为ActivityRecord）
            val activityRecords = Reflect.on(activityThread)
                .field("mActivities")
                .get<Any>()

            // 转换为Map类型（ActivityRecord是系统内部类，存储Activity状态信息）
            val activityRecordMap = activityRecords as Map<*, *>

            // 遍历所有ActivityRecord，寻找未暂停的Activity
            for (activityRecord in activityRecordMap.values) {
                // 获取ActivityRecord中的paused字段（判断Activity是否处于暂停状态）
                val isPaused: Boolean = Reflect.on(activityRecord)
                    .field("paused")
                    .get()

                if (!isPaused) {
                    // 返回未暂停的Activity实例
                    return Reflect.on(activityRecord)
                        .field("activity")
                        .get()
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 根据类名获取目标Activity实例
     * 原理：遍历ActivityThread中管理的所有Activity，匹配类名
     *
     * @param targetClassName 目标Activity的完整类名（如"com.example.MainActivity"）
     * @return 匹配的Activity实例，若未找到或反射失败返回null
     */
    @JvmStatic
    fun getTargetActivity(targetClassName: String): Activity? {
        // 参数校验：目标类名为空直接返回
        if (targetClassName.isBlank()) {
            return null
        }

        return try {
            // 获取Application实例
            val application = getApplication() ?: return null

            // 从Application获取mLoadedApk（存储应用加载信息）
            val loadedApk = Reflect.on(application)
                .field("mLoadedApk")
                .get<Any>()

            // 从LoadedApk获取ActivityThread实例
            val activityThread = Reflect.on(loadedApk)
                .field("mActivityThread")
                .get<Any>()

            // 获取所有ActivityRecord（同getCurrentActivity逻辑）
            val activityRecords = Reflect.on(activityThread)
                .field("mActivities")
                .get<Any>()

            // 遍历ActivityRecordMap，匹配类名
            if (activityRecords is Map<*, *>) {
                for (entry in activityRecords.entries) {
                    val activityRecord = entry.value
                    // 获取ActivityRecord中的Activity实例
                    val activity = Reflect.on(activityRecord)
                        .field("activity")
                        .get() as? Activity

                    // 比较类名是否匹配
                    if (activity != null && targetClassName == activity.javaClass.name) {
                        return activity
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
