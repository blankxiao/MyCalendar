package cn.szu.blankxiao.mycalendar.model.schedule

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

val eatExample: ScheduleItemData =
	ScheduleItemData(title = "吃饭", date = LocalDate(2025, 11, 22), description = "按时吃饭有利于健康")
val runExample: ScheduleItemData =
	ScheduleItemData(title = "跑步", date = LocalDate(2025, 11, 22), description = "及时运动")
val gameExample: ScheduleItemData =
	ScheduleItemData(title = "游戏", date = LocalDate(2025, 11, 22), description = "爽玩")

val exampleScheduleItemList = listOf(eatExample, runExample, gameExample)

/**
 * @author BlankXiao
 * @description ScheduleItemData
 * @date 2025-11-03 21:36
 */
data class ScheduleItemData(
	val title: String,
	val date: LocalDate,
	val description: String,
	var isChecked: Boolean = false,
	val id: Long = 0,
	val reminderEnabled: Boolean = false,
	val reminderTime: LocalDateTime? = null,
	val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
	val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)
