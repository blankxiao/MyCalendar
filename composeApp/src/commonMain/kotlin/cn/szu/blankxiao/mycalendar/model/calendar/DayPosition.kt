package cn.szu.blankxiao.mycalendar.model.calendar

/**
 * 日期在月份中的位置
 */
enum class DayPosition {
    /** 月份前的填充日期（上个月的日期）*/
    InDate,

    /** 当前月份的日期 */
    MonthDate,

    /** 月份后的填充日期（下个月的日期）*/
    OutDate
}
