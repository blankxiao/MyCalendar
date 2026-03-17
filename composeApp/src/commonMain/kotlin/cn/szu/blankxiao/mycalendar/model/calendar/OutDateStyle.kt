package cn.szu.blankxiao.mycalendar.model.calendar

/**
 * 月份外日期的生成样式
 */
enum class OutDateStyle {
    /** 只填充到行尾（每月可能有不同的周数）*/
    EndOfRow,

    /** 填充到固定 6 周（所有月份都是 6 周）*/
    EndOfGrid
}
