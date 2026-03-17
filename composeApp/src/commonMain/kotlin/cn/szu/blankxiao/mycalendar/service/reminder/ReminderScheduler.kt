package cn.szu.blankxiao.mycalendar.service.reminder

import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData

/**
 * 提醒调度器接口（KMP expect/actual）
 *
 * Android：AlarmManager + ReminderReceiver
 * iOS/Desktop：空实现或系统通知
 */
interface ReminderScheduler {
    /**
     * 设置提醒
     */
    fun setReminder(schedule: ScheduleItemData)

    /**
     * 取消提醒
     */
    fun cancelReminder(scheduleId: Long)

    /**
     * 测试提醒（立即触发通知）
     */
    fun testReminder(schedule: ScheduleItemData)
}
