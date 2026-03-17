package cn.szu.blankxiao.mycalendar.util

import kotlinx.datetime.LocalDate

/**
 * 农历提供者 expect/actual
 *
 * Android：使用 com.nlf.calendar 实现
 * iOS/Desktop：空实现（返回空字符串）
 */
internal expect object LunarProvider {
    fun getLunarDayText(date: LocalDate): String
    fun clearCache()
}

/**
 * 获取农历日期显示文本（带缓存）
 */
fun getLunarDayText(date: LocalDate): String = LunarProvider.getLunarDayText(date)

/**
 * 清空农历缓存
 */
fun clearLunarCache() = LunarProvider.clearCache()
