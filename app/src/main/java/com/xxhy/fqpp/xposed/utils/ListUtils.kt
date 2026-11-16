package com.xxhy.fqpp.xposed.utils

/**
 * 集合处理工具类
 */
object ListUtils {

    @JvmStatic
    fun getListString(collection: Collection<String>?, separator: String): String {
        // 判断集合是否为空
        if (isEmpty(collection)) {
            return ""
        }
        val sb = StringBuilder()
        var count = -1
        for (str in collection!!) {
            count++
            // 第一个元素直接追加，后续元素先加分隔符再加内容
            if (count == 0) {
                sb.append(str)
            } else {
                sb.append(separator).append(str)
            }
        }
        return sb.toString()
    }

    @JvmStatic
    fun getQueryList(collection: Collection<String>?): String {
        // 固定分隔符为英文逗号
        return getListString(collection, ",")
    }

    // 字节码中调用的 isEmpty 方法
    @JvmStatic
    fun isEmpty(collection: Collection<*>?): Boolean {
        return collection == null || collection.isEmpty()
    }
}
