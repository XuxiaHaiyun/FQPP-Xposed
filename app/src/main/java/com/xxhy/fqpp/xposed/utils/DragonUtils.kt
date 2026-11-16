package com.xxhy.fqpp.xposed.utils

import com.xxhy.fqpp.xposed.global.DragonGlobals
import org.joor.Reflect

/**
 * 目标应用（番茄小说）反射工具类。
 *
 * <p>依赖 AcctManager 的【混淆】单例方法 P() 与【稳定】方法 islogin()/getUserId()。
 *
 * <p>混淆方法：
 * <ul>
 *   <li>{@code AcctManager.P()} —— public static AcctManager P()，单例获取器。
 *       特征：类中唯一的 public static 且返回自身类型的无参方法。
 *   <li>7.2.7: P()→I()，签名不变(public static AcctManager)，仅方法名变。
 *   <li>跨版本若 P()/I() 改名，可改用反射 callStaticMethod 或 DexKit 定位。
 * </ul>
 */
object DragonUtils {

    /**
     * 判断当前用户是否登录。
     * 调用链：AcctManager.P() -> islogin()。
     * islogin() 为【稳定】public boolean islogin()。
     */
    @JvmStatic
    fun isLogin(): Boolean {
        return Reflect.onClass(
            "com.dragon.read.user.AcctManager",
            DragonGlobals.getDragonClassLoader()
        )
            .call("P")  // 【混淆】单例获取器（7.2.7: →I）
            .call("islogin")  // 【稳定】
            .get()
    }

    /**
     * 获取当前用户 ID。
     * 调用链：AcctManager.P() -> getUserId()。
     * getUserId() 为【稳定】public String getUserId()。
     * 未登录返回 "0"。
     */
    @JvmStatic
    fun getUserId(): String {
        if (!isLogin()) return "0"
        return Reflect.onClass(
            "com.dragon.read.user.AcctManager",
            DragonGlobals.getDragonClassLoader()
        )
            .call("P")  // 【混淆】单例获取器（7.2.7: →I）
            .call("getUserId")  // 【稳定】
            .get()
    }
}
