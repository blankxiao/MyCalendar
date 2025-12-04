package cn.szu.blankxiao.mycalendar.utils

import android.util.LruCache
import com.nlf.calendar.Solar
import java.time.LocalDate

object LunarUtil {
    
    // LRU 缓存：最多缓存 500 个日期的农历数据（约 16 个月）
    private val lunarCache = LruCache<LocalDate, String>(500)
    
    /**
     * 获取农历日期显示文本（带缓存）
     * @param date 公历日期
     * @return 农历日期文本，如 "初一"、"十五"、"春节"
     */
    fun getLunarDayText(date: LocalDate): String {
        // 先查缓存
        lunarCache.get(date)?.let { return it }
        
        // 缓存未命中，计算农历
        val result = calculateLunarText(date)
        
        // 存入缓存
        lunarCache.put(date, result)
        
        return result
    }
    
    /**
     * 计算农历文本（耗时操作）
     */
    private fun calculateLunarText(date: LocalDate): String {
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
    
    /**
     * 清空缓存
     */
    fun clearCache() {
        lunarCache.evictAll()
    }
}

