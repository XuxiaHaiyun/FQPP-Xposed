package com.xxhy.fqpp.xposed.xcallback

import io.github.libxposed.api.XposedInterface

/**
 * BookUtils 钩子。
 *
 * <p>libxposed 链式拦截模型：拦截 isOverallOffShelf / isUnsafeBook，
 * 无论原方法结果如何，一律返回 false（视为安全书籍）。
 *
 * <p>注意：高频调用，不打印日志到界面（会卡）。
 */
class BookUtilsHooker : XposedInterface.Hooker {

    override fun intercept(chain: XposedInterface.Chain): Any? {
        chain.proceed()
        // 高频调用，打印日志到界面会卡
        return false
    }
}
