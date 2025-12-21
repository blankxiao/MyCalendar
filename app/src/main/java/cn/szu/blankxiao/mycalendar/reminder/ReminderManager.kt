package cn.szu.blankxiao.mycalendar.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import java.time.ZoneId

private const val TAG: String = "ReminderManager"

/**
 * @author BlankXiao
 * @description 提醒管理器
 * @date 2025-12-11
 * 
 * 负责设置和取消日程提醒
 */
object ReminderManager {
    private const val TAG = "ReminderManager"
    
    /**
     * 设置提醒
     * @param context 上下文
     * @param schedule 日程数据
     */
    fun setReminder(context: Context, schedule: ScheduleItemData) {
        if (!schedule.reminderEnabled || schedule.reminderTime == null) {
            Log.w(TAG, "提醒未启用或提醒时间为空")
            return
        }
        
        // 转换LocalDateTime为时间戳
        val reminderTimeMillis = schedule.reminderTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // 检查权限（Android 12+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "没有精确闹钟权限")
                return
            }
        }
        
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("scheduleId", schedule.id)
            putExtra("scheduleTitle", schedule.title)
            putExtra("scheduleDesc", schedule.description)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
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
    
    /**
     * 测试提醒（立即触发）
     * @param context 上下文
     * @param schedule 日程数据
     */
    fun testReminder(context: Context, schedule: ScheduleItemData) {
        Log.d(TAG, "测试提醒: ${schedule.title}")
        
        // 直接发送通知
        NotificationHelper.showReminderNotification(
            context = context,
            scheduleId = schedule.id,
            title = schedule.title,
            description = schedule.description
        )
    }

    /**
     * 取消提醒
     * @param context 上下文
     * @param scheduleId 日程ID
     */
    fun cancelReminder(context: Context, scheduleId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        
        Log.d(TAG, "提醒已取消: scheduleId=$scheduleId")
    }
}

