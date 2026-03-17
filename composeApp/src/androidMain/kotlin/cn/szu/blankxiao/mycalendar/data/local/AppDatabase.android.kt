package cn.szu.blankxiao.mycalendar.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

private const val DATABASE_NAME = "my_calendar_database"

/**
 * 创建 AppDatabase 实例（Android 平台）
 * 使用 BundledSQLiteDriver 以支持 KMP
 */
fun createAppDatabase(context: Context): AppDatabase {
	val appContext = context.applicationContext
	val dbFile = appContext.getDatabasePath(DATABASE_NAME)
	return Room.databaseBuilder<AppDatabase>(
		context = appContext,
		name = dbFile.absolutePath
	)
		.setDriver(BundledSQLiteDriver())
		.fallbackToDestructiveMigration()
		.build()
}
