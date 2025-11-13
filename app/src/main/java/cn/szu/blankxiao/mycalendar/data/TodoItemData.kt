package cn.szu.blankxiao.mycalendar.data

import java.time.LocalDate

val eatExample: TodoItemData = TodoItemData("吃饭", LocalDate.of(2025, 11, 22), "按时吃饭有利于健康", false)
val runExample: TodoItemData = TodoItemData("跑步", LocalDate.of(2025, 11, 22), "及时运动", false)
val gameExample: TodoItemData = TodoItemData("游戏", LocalDate.of(2025, 11, 22), "爽玩", false)

val exampleTodoItemList = listOf(eatExample, runExample, gameExample)


/**
 * @author BlankXiao
 * @description TodoItem
 * @date 2025-11-03 21:36
 */
data class TodoItemData(val title: String, val date: LocalDate, val desc: String, var isChecked: Boolean = false)