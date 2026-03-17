package cn.szu.blankxiao.mycalendar.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

private const val DATABASE_NAME = "my_calendar_database"

/**
 * 创建 AppDatabase 实例（JVM/Desktop 平台）
 * 数据库文件存储在用户目录 .mycalendar 下
 */
fun createAppDatabase(): AppDatabase {
    val dbDir = File(System.getProperty("user.home"), ".mycalendar")
    dbDir.mkdirs()
    val dbPath = File(dbDir, "$DATABASE_NAME.db").absolutePath
    return Room.databaseBuilder<AppDatabase>(name = dbPath)
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}
