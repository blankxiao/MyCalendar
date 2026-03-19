package cn.szu.blankxiao.mycalendar.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import cn.szu.blankxiao.mycalendar.data.local.dao.ScheduleDao
import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity

/**
 * @author BlankXiao
 * @description 应用数据库配置（KMP 共享）
 * @date 2025-12-11
 *
 * Room 数据库主配置类
 * - 定义数据库版本
 * - 注册所有实体类
 * - 提供 DAO 访问接口
 * - 使用 @ConstructedBy 支持 KMP 多平台
 */
@Database(
	entities = [ScheduleEntity::class],
	version = 2,  // 版本升级：添加提醒功能
	exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

	abstract fun scheduleDao(): ScheduleDao
}

/**
 * Room 编译器会为各平台（含 Android / JVM / iOS）生成 actual 实现。
 * 使用 @Suppress 消除「无 actual」的 IDE 警告，切勿在 iosMain 等下手写 actual。
 */
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
	override fun initialize(): AppDatabase
}
