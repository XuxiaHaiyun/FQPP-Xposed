package com.xxhy.fqpp.xposed.xcallback

import com.xxhy.fqpp.xposed.config.ConfigManager
import com.xxhy.fqpp.xposed.constant.SPConstants
import com.xxhy.fqpp.xposed.global.DragonGlobals
import com.xxhy.fqpp.xposed.utils.DragonUtils
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Method
import org.joor.Reflect

/**
 * PrivilegeManager 钩子（权限管理器多个方法）。
 *
 * <p>Hook 目标：{@code com.dragon.read.component.biz.impl.privilege.PrivilegeManager} 的多个方法
 * （类名稳定，方法名多数未混淆：getInstance / updateVipInfo / isVip / hasNoAd* / canShowVipRelational 等）
 *
 * <p>作用：本地会员模拟——强制权限判断方法返回 true，并注入伪造的 VipInfoModel。
 *
 * <p>混淆字段依赖（getInstance 分支中反射访问，需跨版本核查）：
 * <ul>
 *   <li>"vipInfoModel" —— private VipInfoModel，当前 VIP 信息（【未混淆】跨版本稳定）。
 *   <li>"j" —— private List&lt;VipInfoModel&gt;，VIP 信息列表（【混淆】）。
 *       特征：PrivilegeManager 中唯一的 List&lt;VipInfoModel&gt; 类型字段。
 *   <li>"l" —— private static volatile PrivilegeManager，单例实例（【混淆】）。
 *       特征：类中唯一的 private static volatile 本类型字段。
 * </ul>
 *
 * <p>VipInfoModel 构造参数顺序（create 调用）：
 * (expireTime:String, isVip:String, leftTime:String, isAutoCharge:boolean, isUnionVip:boolean,
 *  unionSource:int, isAdVip:boolean, subType:VipCommonSubType)
 */
class PrivilegeManagerHooker : XposedInterface.Hooker {

    companion object {
        /** 伪造的 VIP 过期时间（未来时间戳，字符串形式） */
        private const val FAKE_VIP_EXPIRE_TIME = "218342534400"
        /** VIP 子类型枚举值名称数组，用于构建多类型 VIP 信息列表 */
        private val VIP_SUB_TYPES = arrayOf("ShortStory", "Publish", "AdFree", "Default")

        /** VIP 子类型枚举类全限定名 */
        private const val CLASS_VIP_COMMON_SUB_TYPE = "com.dragon.read.rpc.model.VipCommonSubType"
        /** VIP 信息模型类全限定名 */
        private const val CLASS_VIP_INFO_MODEL = "com.dragon.read.user.model.VipInfoModel"

        /**
         * 创建伪造的 VipInfoModel 实例，模拟 VIP 会员身份。
         *
         * <p>构造参数：expireTime, isVip, leftTime, isAutoCharge, isUnionVip, unionSource, isAdVip, subType
         *
         * <p>7.0.9.32 实证：VIP 卡片按 {@code SdkVersion.VERSION.equals(vipInfoModel.isVip)}
         * 判定会员身份（FanqieMineFragmentV2 卡片构建），故 isVip 必须填<b>当前版本号</b>
         * （而非 "1"），否则显示"开通会员"而非"续费"。
         *
         * @return 伪造的 VipInfoModel 对象
         */
        private fun createFakeVipInfoModel(): Any? {
            val cl = DragonGlobals.getDragonClassLoader()
            val defaultSubType = Reflect.onClass(CLASS_VIP_COMMON_SUB_TYPE, cl)
                .field("Default").get<Any>()
            // isVip = 当前版本号（SdkVersion.VERSION），如 "10.8.0"
            val version = runCatching {
                Reflect.onClass("com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants\$SdkVersion", cl)
                    .field("VERSION").get<String>()
            }.getOrNull() ?: "1"
            return Reflect.onClass(CLASS_VIP_INFO_MODEL, cl)
                .create(FAKE_VIP_EXPIRE_TIME, version, FAKE_VIP_EXPIRE_TIME, true, true, 1, true, defaultSubType)
                .get<Any>()
        }

        /**
         * 向 PrivilegeManager 单例注入伪造的 VIP 信息。
         * 覆盖 vipInfoModel 字段、VIP 信息列表（List&lt;VipInfoModel&gt; 类型字段）及单例引用。
         *
         * @param privilegeManagerInstance PrivilegeManager 单例对象
         */
        private fun injectFakeVipInfo(privilegeManagerInstance: Any?) {
            val vipInfoModel = createFakeVipInfoModel()
            if (vipInfoModel == null || privilegeManagerInstance == null) return

            // 设置当前 VIP 信息（字段名未混淆，跨版本稳定）
            Reflect.on(privilegeManagerInstance).set("vipInfoModel", vipInfoModel)

            // 构建多类型 VIP 信息列表（覆盖多种会员子类型）
            val vipInfoList = ArrayList<Any?>()
            for (subTypeStr in VIP_SUB_TYPES) {
                Reflect.on(vipInfoModel)
                    .set(
                        "subType",
                        Reflect.onClass(CLASS_VIP_COMMON_SUB_TYPE, DragonGlobals.getDragonClassLoader())
                            .field(subTypeStr).get<Any>()
                    )
                vipInfoList.add(vipInfoModel)
            }

            // 【混淆】VIP 列表字段（7.0.9.32 为 f145862j，真实名 j）：按类型特征定位
            // （List<VipInfoModel> 类型字段），规避字段名跨版本差异。
            val listField = privilegeManagerInstance.javaClass.declaredFields.firstOrNull {
                it.type == java.util.List::class.java &&
                    it.genericType.typeName.contains("VipInfoModel")
            }
            if (listField != null) {
                listField.isAccessible = true
                listField.set(privilegeManagerInstance, vipInfoList)
            }
            // 单例字段（f145852l）无需修改：getInstance 已返回实例本身
        }
    }

    override fun intercept(chain: XposedInterface.Chain): Any? {
        val sp = ConfigManager.prefs()
        if (sp.getBoolean(SPConstants.LOCAL_VIP, SPConstants.DEFAULT_LOCAL_VIP)) {
            if (!DragonUtils.isLogin()) return chain.proceed()
            val method = chain.getExecutable() as Method
            val methodName = method.name

            // updateVipInfo：篡改第一个参数为伪造的 VipInfoModel
            if ("updateVipInfo" == methodName) {
                val args = chain.getArgs().toTypedArray()
                args[0] = createFakeVipInfoModel()
                return chain.proceed(args)
            }

            val result = chain.proceed()

            // 1. 特殊处理 getInstance 方法（权限管理类的单例获取方法）
            if ("getInstance" == methodName) {
                val privilegeManagerInstance = result
                injectFakeVipInfo(privilegeManagerInstance)
                return privilegeManagerInstance
            } else if ("updateVipInfo" == methodName) {
                return result
            } else if ("showPayVipEntranceInChapterEnd" == methodName) {
                return false // 不显示章末"成为会员无广告畅读"
            } else if ("isBookAdFree" == methodName) {
                return 1
            }

            // 2. 其他权限相关方法：统一返回 true（表示拥有该权限）
            return true
        }
        return chain.proceed()
    }
}
