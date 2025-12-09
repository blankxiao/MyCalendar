package cn.szu.blankxiao.mycalendar.data.schedule

import java.time.LocalDate

val eatExample: ScheduleItemData = ScheduleItemData("吃饭", LocalDate.of(2025, 11, 22), "按时吃饭有利于健康", false)
val runExample: ScheduleItemData = ScheduleItemData("跑步", LocalDate.of(2025, 11, 22), "及时运动", false)
val gameExample: ScheduleItemData = ScheduleItemData("游戏", LocalDate.of(2025, 11, 22), "爽玩", false)

val exampleScheduleItemList = listOf(eatExample, runExample, gameExample)


/**
 * @author BlankXiao
 * @description ScheduleItemData
 * @date 2025-11-03 21:36
 */
data class ScheduleItemData(val title: String, val date: LocalDate, val desc: String, var isChecked: Boolean = false)