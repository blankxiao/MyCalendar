package cn.szu.blankxiao.mycalendar.service.reminder

import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData

/**
 * ReminderScheduler 空实现
 * Desktop / iOS 平台暂不支持提醒功能
 */
class NoOpReminderScheduler : ReminderScheduler {
    override fun setReminder(schedule: ScheduleItemData) {}
    override fun cancelReminder(scheduleId: Long) {}
    override fun testReminder(schedule: ScheduleItemData) {}
}
