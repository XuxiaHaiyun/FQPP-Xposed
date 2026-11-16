package com.xxhy.fqpp.xposed

import android.util.Log
import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.global.XposedGlobals
import com.xxhy.fqpp.xposed.locate.LocatorService
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * 模块入口（libxposed 现代 API 102）。
 *
 * <p>由框架通过 {@code META-INF/xposed/java_init.list} 发现并实例化，
 * 框架自动调用 {@link #attachFramework}，随后触发各生命周期回调。
 */
class XposedEntry : XposedModule() {

    companion object {
        private const val TAG = "FQPP"
        private const val TARGET_PACKAGE = "com.dragon.read"
    }

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        XposedGlobals.setXposedEntryInstance(this)
        log(
            Log.INFO, TAG,
            "onModuleLoaded, process: ${param.processName}, framework: $frameworkName " +
                "$frameworkVersion (code $frameworkVersionCode, api $apiVersion)"
        )
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        if (TARGET_PACKAGE != param.packageName) return
        val classLoader = param.defaultClassLoader
        DragonGlobals.initDragonClassLoader(classLoader)
        XposedGlobals.setXposedEntryInstance(this)
        XposedGlobals.setPackageLoadedParam(param)
        HookManager.hookMainApplication()

        // 后台启动 DexKit 混淆定位（缓存命中时为毫秒级，无缓存时异步扫描）
        val context = runCatching {
            com.xxhy.fqpp.xposed.utils.AppUtils.createAppContext()
        }.getOrNull()
        val versionCode: Int? = context?.let { ctx ->
            runCatching {
                ctx.packageManager.getPackageInfo(TARGET_PACKAGE, 0).versionCode.toInt()
            }.getOrNull()
        }
        if (versionCode != null) {
            runCatching {
                LocatorService.create(classLoader, versionCode).resolveAsync()
            }.onFailure {
                log(Log.WARN, TAG, "DexKit 定位服务初始化失败: ${it.message}")
            }
        } else {
            log(Log.WARN, TAG, "无法获取目标应用 versionCode，跳过 DexKit 定位")
        }
    }
}
