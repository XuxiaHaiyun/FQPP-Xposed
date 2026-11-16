package com.xxhy.fqpp.xposed.xcallback

import com.xxhy.fqpp.xposed.utils.LogUtils

/**
 * 激励秒领（激励广告跳过播放 → 自动完成 → 真实发放奖励）Hook。
 *
 * <p>7.0.9.32 反编译实证（最终链路）：
 * <ul>
 *   <li>金币盒子任务（GoldCoinBox*TaskViewModel）→ qd1.b（LuckydogShowAdManager）→
 *       requestExcitingVideo → onSuccess → startExcitingVideo(config, C4079a) →
 *       SDK 创建 {@code ExcitingVideoFragment}；</li>
 *   <li>发奖核心 {@code ExcitingVideoFragment.executeReward(o)}：回调
 *       {@code mRewardCompleteListener.onRewardComplete(2, params)}（金币盒子 C4079a 判定成功，
 *       pd1.a.c()=true + f.a.a(token) 发奖）<b>并</b>调用 {@code i0.P0(videoAd,...)}
 *       （SDK 向服务端上报"播放完成+发奖"）——<b>服务端校验的依据</b>；</li>
 *   <li>之前方案跳过 SDK 直接模拟 listener/eVar，i0.P0 从未上报 → 服务端无"完成"记录 →
 *       后续 MainService.d 领取请求被拒 → 提示"活动繁忙"。</li>
 * </ul>
 * 因此改为<b>放行 SDK 正常流程</b>：hook {@code ExcitingVideoFragment.onCreateView}，
 * 创建完成后立即 {@code closeFragment(true)}（等价"看完自动关闭"）→ SDK 自己走
 * executeReward（onRewardComplete(2) 发奖 + i0.P0 服务端上报）→ 广告一闪即过、
 * 服务端认可、奖励真实到账。同时隐藏"加载中……"弹窗（za1.m.o）。
 */
object InstantRewardHooker {

    /** 日志场景 TAG：激励秒领。 */
    private const val TAG = "InstantReward"

    private fun hookInstantReward() {
        // 核心：广告 Fragment 创建完成后立即模拟"看完关闭" → SDK executeReward
        HookSupport.safeHook(
            TAG,
            "com.ss.android.excitingvideo.sdk.ExcitingVideoFragment",
            "onCreateView",
            arrayOf("android.view.LayoutInflater", "android.view.ViewGroup", "android.os.Bundle"),
            extraLog = "激励秒领(立即发奖)",
        ) { chain ->
            val r = chain.proceed()
            runCatching {
                val frag = chain.getThisObject()
                frag.javaClass.getMethod("closeFragment", Boolean::class.javaPrimitiveType)
                    .invoke(frag, true)
                LogUtils.log(TAG, "激励秒领：已触发 closeFragment(true) 立即发奖")
            }
            r
        }
        // 隐藏"加载中……"弹窗（za1.m.o = showLoadingView）
        HookSupport.safeHook(
            TAG,
            "za1.m",
            "o",
            arrayOf("bc.g"),
            extraLog = "激励秒领(隐藏加载弹窗)",
        ) { null }
    }

    /** 安装激励秒领 hook。 */
    @JvmStatic
    fun installInstantReward() {
        hookInstantReward()
        LogUtils.log(TAG, "激励秒领 hook 已安装")
    }
}
