package cn.szu.blankxiao.mycalendar.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * @author BlankXiao
 * @description 提醒广播接收器
 * @date 2025-12-11
 * 
 * 接收AlarmManager触发的提醒事件，显示通知
 */
class ReminderReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "ReminderReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "收到提醒触发")
        
        val scheduleId = intent.getLongExtra("scheduleId", -1)
        val scheduleTitle = intent.getStringExtra("scheduleTitle") ?: "日程提醒"
        val scheduleDesc = intent.getStringExtra("scheduleDesc") ?: ""
        
        // 显示通知
        NotificationHelper.showReminderNotification(
            context = context,
            scheduleId = scheduleId,
            title = scheduleTitle,
            description = scheduleDesc
        )
    }
}

