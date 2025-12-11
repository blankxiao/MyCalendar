package cn.szu.blankxiao.mycalendar.data.schedule

import java.time.LocalDate

val eatExample: ScheduleItemData =
	ScheduleItemData(title = "吃饭", date = LocalDate.of(2025, 11, 22), desc = "按时吃饭有利于健康")
val runExample: ScheduleItemData =
	ScheduleItemData(title = "跑步", date = LocalDate.of(2025, 11, 22), desc = "及时运动")
val gameExample: ScheduleItemData =
	ScheduleItemData(title = "游戏", date = LocalDate.of(2025, 11, 22), desc = "爽玩")

val exampleScheduleItemList = listOf(eatExample, runExample, gameExample)


/**
 * @author BlankXiao
 * @description ScheduleItemData
 * @date 2025-11-03 21:36
 */
data class ScheduleItemData(
	val title: String,
	val date: LocalDate,
	val desc: String,
    var isChecked: Boolean = false,
    val id: Long = 0,  // 数据库ID，0表示新建
)