package cn.szu.blankxiao.mycalendar.data.calendar

import java.time.LocalDate

/**
 * @author BlankXiao
 * @description CustomCalendarController
 * @date 2025-12-06 15:34
 */
interface CustomCalendarController {

	fun toWeekMode()

	fun toMonthMode()

	fun getDataForPage(pageIndex: Int): CustomCalendarData

	suspend fun scrollToDate(date: LocalDate, animate: Boolean = false)

	suspend fun finishDragAndSnap(velocity: Float = 0f)

	suspend fun switchToMode(mode: CalendarMode, animate: Boolean = true)

}