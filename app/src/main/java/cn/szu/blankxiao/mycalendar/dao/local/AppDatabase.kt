package cn.szu.blankxiao.mycalendar.dao.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.szu.blankxiao.mycalendar.dao.local.dao.ScheduleDao
import cn.szu.blankxiao.mycalendar.dao.local.entity.ScheduleEntity

/**
 * @author BlankXiao
 * @description 应用数据库配置
 * @date 2025-12-11
 * 
 * Room数据库主配置类
 * - 定义数据库版本
 * - 注册所有实体类
 * - 提供DAO访问接口
 * - 使用单例模式确保全局唯一实例
 */
@Database(
    entities = [ScheduleEntity::class],
    version = 2,  // 版本升级：添加提醒功能
    exportSchema = false  // 不导出schema（生产环境建议设为true）
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * 获取ScheduleDao实例
     */
    abstract fun scheduleDao(): ScheduleDao
    
    companion object {
        // 数据库名称
        private const val DATABASE_NAME = "my_calendar_database"
        
        // 单例实例
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * 获取数据库实例（单例模式）
         * @param context 应用上下文
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()  // 简化迁移（开发阶段）
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * 销毁数据库实例（用于测试）
         */
        fun destroyInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

