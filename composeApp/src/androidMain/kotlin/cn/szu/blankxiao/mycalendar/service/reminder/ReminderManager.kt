package cn.szu.blankxiao.mycalendar.service.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * @author BlankXiao
 * @description 提醒管理器（ReminderScheduler 的 Android 实现）
 * @date 2025-12-11
 *
 * 负责设置和取消日程提醒，使用 AlarmManager + ReminderReceiver
 */
class ReminderManager(
    private val context: Context,
    private val reminderReceiverClass: Class<*>
) : ReminderScheduler {
    companion object {
        private const val TAG = "ReminderManager"
    }

    override fun setReminder(schedule: ScheduleItemData) {
        if (!schedule.reminderEnabled || schedule.reminderTime == null) {
            Log.w(TAG, "提醒未启用或提醒时间为空")
            return
        }

        val reminderTimeMillis = schedule.reminderTime!!
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // 检查权限（Android 12+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "没有精确闹钟权限")
                return
            }
        }
        
        val intent = Intent(this.context, reminderReceiverClass).apply {
            putExtra("scheduleId", schedule.id)
            putExtra("scheduleTitle", schedule.title)
            putExtra("scheduleDesc", schedule.description)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            this.context,
            schedule.id.toInt(), // 使用scheduleId作为requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            // 使用精确闹钟，即使在Doze模式也能触发
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            }
            
            Log.d(TAG, "提醒设置成功: ${schedule.title} at ${schedule.reminderTime}")
        } catch (e: Exception) {
            Log.e(TAG, "设置提醒失败", e)
        }
    }
    
    override fun testReminder(schedule: ScheduleItemData) {
        Log.d(TAG, "测试提醒: ${schedule.title}")
        NotificationHelper.showReminderNotification(
            context = this.context,
            scheduleId = schedule.id,
            title = schedule.title,
            description = schedule.description
        )
    }

    override fun cancelReminder(scheduleId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this.context, reminderReceiverClass)
        val pendingIntent = PendingIntent.getBroadcast(
            this.context,
            scheduleId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        
        Log.d(TAG, "提醒已取消: scheduleId=$scheduleId")
    }
}

