@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package cn.szu.blankxiao.mycalendar.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

private const val DATABASE_NAME = "my_calendar_database"

/**
 * 创建 AppDatabase 实例（iOS 平台）
 * 数据库文件存储在 Documents 目录
 * 使用 BundledSQLiteDriver 保持与 Android/JVM 一致
 */
fun createAppDatabase(): AppDatabase {
    val dbFilePath = documentDirectory() + "/$DATABASE_NAME.db"
    return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(documentDirectory?.path)
}
