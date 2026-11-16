package com.xxhy.fqpp.xposed.xcallback

import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import io.github.libxposed.api.XposedInterface
import org.joor.Reflect
import com.xxhy.fqpp.xposed.global.DragonGlobals

/**
 * AcctManager 钩子（用户信息拦截 → 模拟 VIP/免广告身份，底层字段级修改）。
 *
 * <p>7.0.9.32 反编译实证：
 * <ul>
 *   <li>{@code AcctManager.P()}（public static）返回单例实例，字段 {@code userModel}
 *       （AcctUserModel，{@code getUserInfo()} 即 {@code return this.userModel}）；</li>
 *   <li>AcctUserModel 字段（未混淆，实证）：userName/description(包私有)/
 *       adVipAvailable/freeAd/freeAdDay/freeAdExpire/freeAdLeft/isOfficialCert/
 *       canSetUsernamePrivilege/needSetUserinfoPrivilege/hasPublishTopicPrivilege/
 *       vipInfo/vipInfoList/vipLastExpiredTime/vipProfileShow；</li>
 *   <li>VipInfo 字段：expireTime/isVip/isAdVip/isUnionVip/leftTime/autoRenew/renewType/
 *       continueMonth/continueMonthBuy/subType/unionSource；
 *       枚举 VipRenewType.VipRenewYear、VipCommonSubType.{Default,ShortStory,Publish,AdFree}。</li>
 * </ul>
 * 策略（更底层）：hook {@code P()} 直接改实例的 {@code userModel} 字段，
 * 所有读该字段的方法（getUserInfo/getUserName/getVipInfo/isFreeAd 等）自动生效；
 * 同时保留 getUserInfo() 拦截（兜底）。
 */
class AcctManagerHooker : XposedInterface.Hooker {

    override fun intercept(chain: XposedInterface.Chain): Any? {
        val result = chain.proceed()
        val sp = ConfigManager.prefs()
        if (!sp.getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)) {
            return result
        }
        // getUserInfo() 返回 AcctUserModel（userModel 本身）
        val userModel = result ?: return result
        return try {
            val isLogin: Boolean = Reflect.on(chain.getThisObject()).call("islogin").get()
            if (!isLogin) return result
            modifyUserModel(userModel)
            userModel
        } catch (t: Throwable) {
            result
        }
    }

    companion object {

        /**
         * 底层字段级拦截：反射读取 AcctManager 实例的 userModel 字段并修改。
         * 由 P()（单例方法）hook 调用；修改成功后所有读该字段的 getter 自动生效。
         *
         * @return 是否成功修改
         */
        @JvmStatic
        fun applyUserModel(acct: Any?): Boolean {
            if (acct == null) return false
            return try {
                val userModel = Reflect.on(acct).field("userModel").get<Any>() ?: return false
                val isLogin = Reflect.on(acct).call("islogin").get<Boolean>()
                if (!isLogin) return false
                modifyUserModel(userModel)
                true
            } catch (t: Throwable) {
                false
            }
        }

        /** 修改 AcctUserModel 字段（分步容错：单个字段/构建失败不阻断其余修改）。 */
        private fun modifyUserModel(userModel: Any) {
            // 基本信息（核心，失败不影响免广告/VIP）
            runCatching {
                Reflect.on(userModel)
                    .set("userName", "XuxiaHaiyun")
                    .set("description", "用户信息拦截成功")
            }
            // 免广告 + 特权（boolean/long 字段，字段名经 7.0.9.32 实证）
            runCatching {
                Reflect.on(userModel)
                    .set("adVipAvailable", true)
                    .set("freeAd", true)
                    .set("freeAdDay", 100000f)
                    .set("freeAdExpire", 218342534400L)
                    .set("freeAdLeft", 218342534400L)
                    .set("isOfficialCert", true)
                    .set("canSetUsernamePrivilege", true)
                    .set("needSetUserinfoPrivilege", false)
                    .set("hasPublishTopicPrivilege", true)
            }
            // VIP 信息（构建 + 多子类型列表）
            runCatching {
                val vipInfo = buildVipInfo()
                if (vipInfo != null) {
                    Reflect.on(userModel).set("vipInfo", vipInfo)
                    val cl = DragonGlobals.getDragonClassLoader()
                    val vipInfoList = ArrayList<Any?>()
                    for (subTypeStr in arrayOf("ShortStory", "Publish", "AdFree", "Default")) {
                        Reflect.on(vipInfo)
                            .set(
                                "subType",
                                Reflect.onClass("com.dragon.read.rpc.model.VipCommonSubType", cl)
                                    .field(subTypeStr).get<Any>()
                            )
                        vipInfoList.add(vipInfo)
                    }
                    Reflect.on(userModel).set("vipInfoList", vipInfoList)
                }
            }
            runCatching {
                Reflect.on(userModel).set("vipLastExpiredTime", "218342534400")
            }
            // VIP 资料展示
            runCatching {
                val vps = buildVipProfileShow()
                if (vps != null) {
                    Reflect.on(userModel).set("vipProfileShow", vps)
                }
            }
        }

        /** 构建模拟 VipInfo（字段类型漂移时返回 null，调用方容错）。 */
        @JvmStatic
        fun buildVipInfo(): Any? = runCatching {
            val cl = DragonGlobals.getDragonClassLoader()
            Reflect.onClass("com.dragon.read.rpc.model.VipInfo", cl).create().get<Any>().also { vip ->
                Reflect.on(vip)
                    .set("expireTime", "218342534400")
                    .set("isVip", "1")
                    .set("isAdVip", true)
                    .set("isUnionVip", true)
                    .set("leftTime", "218342534400")
                    .set("autoRenew", true)
                    .set(
                        "renewType",
                        Reflect.onClass("com.dragon.read.rpc.model.VipRenewType", cl)
                            .field("VipRenewYear").get<Any>()
                    )
                    .set("continueMonth", true)
                    .set("continueMonthBuy", true)
                    .set(
                        "subType",
                        Reflect.onClass("com.dragon.read.rpc.model.VipCommonSubType", cl)
                            .field("Default").get<Any>()
                    )
                    .set("unionSource", 1)
            }
        }.getOrNull()

        /** 构建模拟 VipProfileShow。 */
        @JvmStatic
        fun buildVipProfileShow(): Any? = runCatching {
            val cl = DragonGlobals.getDragonClassLoader()
            Reflect.onClass("com.dragon.read.rpc.model.VipProfileShow", cl).create().get<Any>().also { v ->
                Reflect.on(v)
                    .set("couponStrategy", "show")
                    .set("useLynx", true)
            }
        }.getOrNull()
    }
}
