package com.xxhy.fqpp.xposed.xcallback

import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.utils.DragonUtils
import io.github.libxposed.api.XposedInterface
import org.joor.Reflect

/**
 * VipInfoModel 钩子（VIP 信息模型构造方法）。
 *
 * <p>Hook 目标：{@code com.dragon.read.user.model.VipInfoModel} 的构造方法
 * （类名稳定；构造参数：String expireTime, String isVip, String leftTime,
 *  boolean isAutoCharge, boolean isUnionVip, int unionSource, boolean isAdVip, VipCommonSubType subType）
 *
 * <p>作用：在 VIP 信息模型构造前篡改参数，伪造会员身份。
 *
 * <p>注：HookManager.hookVipInfoModel() 当前被注释未启用（理论可由 PrivilegeManager 覆盖），
 * 保留作为保险。启用时需核查构造参数顺序与字段类型是否变化。
 *
 * <p>枚举依赖：{@code com.dragon.read.rpc.model.VipCommonSubType.Default}
 * （未混淆枚举值，另含 ShortStory/Publish/AdFree）
 */
class VipInfoModelHooker : XposedInterface.Hooker {

    override fun intercept(chain: XposedInterface.Chain): Any? {
        val sp = ConfigManager.prefs()
        if (sp.getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)) {
            if (!DragonUtils.isLogin()) return chain.proceed()

            // 构造方法的参数数组，准备篡改VIP初始化数据
            val constructorArgs = chain.getArgs().toTypedArray()

            // 篡改参数：设置VIP过期时间（未来时间戳）
            constructorArgs[0] = "218342534400"
            // 篡改参数：标记为VIP（1表示是VIP）
            constructorArgs[1] = "1"
            // 篡改参数：设置剩余VIP时间
            constructorArgs[2] = "218342534400"
            // 篡改参数：开启自动续费
            constructorArgs[3] = true
            // 篡改参数：标记为联合VIP
            constructorArgs[4] = true
            // 篡改参数：设置联合会员来源（1表示特定来源）
            constructorArgs[5] = 1
            // 篡改参数：标记为广告VIP（免广告权限）
            constructorArgs[6] = true
            // 篡改参数：设置VIP子类型为默认类型
            constructorArgs[7] = Reflect.onClass(
                "com.dragon.read.rpc.model.VipCommonSubType", // VIP子类型枚举类
                DragonGlobals.getDragonClassLoader()
            ).field("Default") // 获取"Default"类型枚举值
                .get<Any>()

            return chain.proceed(constructorArgs)
        }
        return chain.proceed()
    }
}
