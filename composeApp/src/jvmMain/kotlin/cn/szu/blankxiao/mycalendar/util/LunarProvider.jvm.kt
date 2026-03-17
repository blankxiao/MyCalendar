package cn.szu.blankxiao.mycalendar.util

import kotlinx.datetime.LocalDate

/**
 * Desktop 平台农历空实现
 * PC 端暂不显示农历，可后续接入 cn.6tail:lunar
 */
internal actual object LunarProvider {
    actual fun getLunarDayText(date: LocalDate): String = ""
    actual fun clearCache() {}
}
