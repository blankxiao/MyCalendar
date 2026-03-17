package cn.szu.blankxiao.mycalendar.util

import com.nlf.calendar.Solar
import kotlinx.datetime.LocalDate

internal actual object LunarProvider {
    private val lunarCache = object : LinkedHashMap<LocalDate, String>(500, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<LocalDate, String>?) = size > 500
    }

    actual fun getLunarDayText(date: LocalDate): String {
        lunarCache[date]?.let { return it }
        val result = calculateLunarText(date)
        lunarCache[date] = result
        return result
    }

    actual fun clearCache() {
        lunarCache.clear()
    }

    private fun calculateLunarText(date: LocalDate): String {
        val solar = Solar.fromYmd(date.year, date.monthNumber, date.dayOfMonth)
        val lunar = solar.lunar

        val festivals = lunar.festivals
        if (festivals.isNotEmpty()) return festivals[0]

        val jieQi = lunar.jieQi
        if (jieQi.isNotEmpty()) return jieQi

        return when (lunar.day) {
            1 -> lunar.monthInChinese
            else -> lunar.dayInChinese
        }
    }
}
