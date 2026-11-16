package com.xxhy.fqpp.xposed.utils

/**
 * 字段名命名风格转换工具类
 * 提供驼峰命名与下划线命名之间的相互转换
 */
object FieldNameUtils {

    /**
     * 驼峰转下划线的正则表达式
     * 匹配非首字符的大写字母或数字（使用正向预查确保前面有字符，避免首字符被匹配）
     */
    private val humpPattern = Regex("(?<=.)[A-Z0-9]")

    /**
     * 将驼峰命名风格的字符串转换为下划线命名风格
     * 例如：userName → user_name；User2Name → user_2_name
     *
     * @param param 待转换的驼峰风格字符串（null或空字符串返回空）
     * @return 转换后的下划线风格字符串
     */
    @JvmStatic
    fun humpToUnderline(param: String?): String {
        if (param.isNullOrEmpty()) {
            return ""
        }
        // 匹配非首字符的大写字母/数字，替换为"_+小写"
        val sb = StringBuilder()
        var lastIndex = 0
        for (match in humpPattern.findAll(param)) {
            sb.append(param, lastIndex, match.range.first)
            // 将匹配到的字符转为小写并在前面加下划线
            sb.append('_').append(match.value.lowercase())
            lastIndex = match.range.last + 1
        }
        sb.append(param, lastIndex, param.length)
        return sb.toString()
    }

    /**
     * 将下划线命名风格的字符串转换为驼峰命名风格
     * 例如：user_name → userName；user__name → userName；_user_name → UserName
     *
     * @param param 待转换的下划线风格字符串（null、空字符串或纯空白返回空）
     * @return 转换后的驼峰风格字符串
     */
    @JvmStatic
    fun underlineToCamel(param: String?): String {
        // 处理空值或纯空白字符串
        if (param.isNullOrBlank()) {
            return ""
        }
        val length = param.length
        val sb = StringBuilder(length)
        var i = 0
        while (i < length) {
            var currentChar = param[i]
            // 统一转为小写处理（兼容大写字母的下划线命名）
            currentChar = currentChar.lowercaseChar()

            if (currentChar == UNDERLINE) {
                // 遇到下划线，跳过当前下划线，将下一个字符转为大写（若存在）
                if (++i < length) {
                    sb.append(param[i].uppercaseChar())
                }
            } else {
                // 非下划线字符直接拼接
                sb.append(currentChar)
            }
            i++
        }
        return sb.toString()
    }

    /** 下划线字符常量 */
    private const val UNDERLINE = '_'
}
