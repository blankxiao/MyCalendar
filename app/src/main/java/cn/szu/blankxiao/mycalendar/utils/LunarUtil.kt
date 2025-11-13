package cn.szu.blankxiao.mycalendar.utils

import com.nlf.calendar.Solar
import java.time.LocalDate

object LunarUtil {
    
    /**
     * 获取农历日期显示文本
     * @param date 公历日期
     * @return 农历日期文本，如 "初一"、"十五"、"春节"
     */
    fun getLunarDayText(date: LocalDate): String {
        val solar = Solar.fromYmd(date.year, date.monthValue, date.dayOfMonth)
        val lunar = solar.lunar
        
        // 优先显示节日
        val festivals = lunar.festivals
        if (festivals.isNotEmpty()) {
            return festivals[0]
        }
        
        // 显示节气
        val jieQi = lunar.jieQi
        if (jieQi.isNotEmpty()) {
            return jieQi
        }
        
        // 显示农历日期
        return when (lunar.day) {
            1 -> lunar.monthInChinese  // 初一显示月份，如 "正月"、"二月"
            else -> lunar.dayInChinese  // 其他显示日期，如 "初二"、"十五"
        }
    }

}

