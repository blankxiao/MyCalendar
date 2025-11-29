package cn.szu.blankxiao.mycalendar.data.calendar

/**
 * @author BlankXiao
 * @description OutDateStyle 月份外日期的生成样式
 * @date 2025-11-29 18:47
 */
enum class OutDateStyle {
	/** 只填充到行尾（每月可能有不同的周数）*/
	EndOfRow,

	/** 填充到固定 6 周（所有月份都是 6 周）*/
	EndOfGrid
}
