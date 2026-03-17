package cn.szu.blankxiao.mycalendar.model.calendar

import kotlinx.datetime.LocalDate

/**
 * 年月（kotlinx.datetime 无此类型，自行封装）
 */
data class YearMonth(val year: Int, val month: Int) {

    val lengthOfMonth: Int
        get() = when (month) {
            2 -> if (isLeapYear) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }

    private val isLeapYear: Boolean
        get() = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

    fun atDay(day: Int): LocalDate = LocalDate(year, month, day.coerceIn(1, lengthOfMonth))

    fun atEndOfMonth(): LocalDate = LocalDate(year, month, lengthOfMonth)

    fun plusMonths(months: Long): YearMonth {
        var m = month + months.toInt()
        var y = year
        while (m > 12) {
            m -= 12
            y++
        }
        while (m < 1) {
            m += 12
            y--
        }
        return YearMonth(y, m)
    }

    fun minusMonths(months: Long): YearMonth = plusMonths(-months)

    companion object {
        fun from(date: LocalDate): YearMonth = YearMonth(date.year, date.monthNumber)
    }
}
