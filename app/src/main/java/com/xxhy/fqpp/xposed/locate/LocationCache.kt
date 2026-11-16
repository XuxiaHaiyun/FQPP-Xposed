package com.xxhy.fqpp.xposed.locate

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * 定位结果缓存：按 目标应用 versionCode 分桶持久化。
 *
 * 设计目标：
 * - 定位结果以 JSON 形式保存在独立 SP 文件（dexkit_locations）中；
 * - key = "locate_" + targetId，value = {"className": "..."}；
 * - 目标应用升级（versionCode 变化）后整体失效，强制重新定位；
 * - 定位失败（className 为空）不写入缓存，下次仍会重试。
 */
class LocationCache private constructor(context: Context) {

    companion object {
        private const val SP_NAME = "dexkit_locations"
        private const val KEY_PREFIX = "locate_"
        private const val VERSION_KEY = "target_version_code"

        @Volatile
        private var instance: LocationCache? = null

        fun getInstance(context: Context): LocationCache {
            return instance ?: synchronized(this) {
                instance ?: LocationCache(context.applicationContext).also { instance = it }
            }
        }
    }

    private val sp: SharedPreferences =
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    /**
     * 目标应用版本号；null 表示尚未记录。
     */
    var targetVersionCode: Int?
        get() {
            val v = sp.getInt(VERSION_KEY, -1)
            return if (v == -1) null else v
        }
        private set(value) {
            sp.edit().putInt(VERSION_KEY, value ?: -1).apply()
        }

    /**
     * 检查缓存是否为指定版本。版本变化时自动清空旧数据。
     */
    fun ensureVersion(versionCode: Int) {
        val current = targetVersionCode
        if (current != versionCode) {
            sp.edit().clear().putInt(VERSION_KEY, versionCode).apply()
        }
    }

    /**
     * 读取单个目标定位结果。
     *
     * @return 定位到的类全限定名；未命中/未定位到返回 null
     */
    fun getClassName(targetId: String): String? {
        val raw = sp.getString(KEY_PREFIX + targetId, null) ?: return null
        return runCatching {
            val json = JSONObject(raw)
            json.optString("className", "").ifEmpty { null }
        }.getOrNull()
    }

    /**
     * 写入单个目标定位结果（成功才写）。
     */
    fun putClassName(targetId: String, className: String) {
        if (className.isBlank()) return
        val json = JSONObject().put("className", className)
        sp.edit().putString(KEY_PREFIX + targetId, json.toString()).apply()
    }

    /**
     * 获取全部已缓存的目标定位结果（用于日志汇总）。
     */
    fun getAllCached(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        sp.all.forEach { (key, value) ->
            if (key.startsWith(KEY_PREFIX) && value is String) {
                val id = key.removePrefix(KEY_PREFIX)
                val className = runCatching {
                    JSONObject(value).optString("className", "").ifEmpty { null }
                }.getOrNull()
                if (className != null) result[id] = className
            }
        }
        return result
    }

    /**
     * 清空全部缓存（调试用）。
     */
    fun clearAll() {
        sp.edit().clear().apply()
    }
}
