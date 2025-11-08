package cn.szu.blankxiao.mycalendar.data

import java.time.LocalDate

/**
 * @author BlankXiao
 * @description TodoItem
 * @date 2025-11-03 21:36
 */
data class TodoItemData(val title: String, val date: LocalDate, val desc: String, var isChecked: Boolean = false)