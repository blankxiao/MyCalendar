package cn.szu.blankxiao.mycalendar.util

import kotlinx.datetime.LocalDate

/**
 * iOS 平台农历空实现
 * 暂不显示农历，可后续接入农历库
 */
internal actual object LunarProvider {
    actual fun getLunarDayText(date: LocalDate): String = ""
    actual fun clearCache() {}
}
