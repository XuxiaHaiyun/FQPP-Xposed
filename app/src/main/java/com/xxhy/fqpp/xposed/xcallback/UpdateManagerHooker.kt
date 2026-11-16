package com.xxhy.fqpp.xposed.xcallback

import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.utils.LogUtils
import io.github.libxposed.api.XposedInterface

/**
 * UpdateManager 钩子（检查更新拦截）。
 */
class UpdateManagerHooker : XposedInterface.Hooker {

    override fun intercept(chain: XposedInterface.Chain): Any? {
        val sp = ConfigManager.prefs()
        if (sp.getBoolean(SPConstants.BLOCK_UPDATES, SPConstants.DEFAULT_BLOCK_UPDATES)) {
            LogUtils.log("Update", "拦截检查更新")
            // 拦截原方法执行：直接返回 null 并跳过原方法调用
            return null
        }
        return chain.proceed()
    }
}
