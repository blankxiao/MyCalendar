package cn.szu.blankxiao.mycalendar.service.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import cn.szu.blankxiao.mycalendar.MainActivity
import cn.szu.blankxiao.mycalendar.R

/**
 * @author BlankXiao
 * @description 通知帮助类
 * @date 2025-12-11
 * 
 * 负责创建通知渠道和发送通知
 */
object NotificationHelper {
    
    private const val CHANNEL_ID = "schedule_reminder"
    private const val CHANNEL_NAME = "日程提醒"
    
    fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = "日程的提前提醒通知"

            // 启用震动
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500)

            // 启用指示灯
            enableLights(true)

            // 设置提醒音
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            setSound(soundUri, audioAttributes)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    /**
     * 显示提醒通知
     */
    fun showReminderNotification(
        context: Context,
        scheduleId: Long,
        title: String,
        description: String
    ) {
        // 确保通知渠道已创建
        createNotificationChannel(context)
        
        // 点击通知打开应用的Intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            scheduleId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 构建通知
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_monochrome)
            .setContentTitle(title)
            .setContentText(description.ifBlank { "日程即将开始" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)  // 点击后自动消失
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)  // 使用所有默认设置（声音、震动、灯光）
        
        // 显示通知
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(scheduleId.toInt(), builder.build())
    }
}

