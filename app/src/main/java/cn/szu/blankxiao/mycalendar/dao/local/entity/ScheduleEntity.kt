package cn.szu.blankxiao.mycalendar.dao.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * @author BlankXiao
 * @description Schedule数据库实体
 * @date 2025-12-11
 * 
 * Room数据库表定义
 * - 使用Long类型存储日期（时间戳），便于查询和排序
 * - 支持自动生成主键ID
 * - 记录创建和更新时间
 */
@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** 日程标题 */
    val title: String,
    
    /** 日程日期（使用时间戳存储，精确到天） */
    val date: Long,
    
    /** 日程描述 */
    val description: String,
    
    /** 是否已完成 */
    val isChecked: Boolean = false,
    
    /** 创建时间戳 */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** 更新时间戳 */
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * 转换为UI数据模型
     */
    fun toScheduleItemData(): ScheduleItemData {
        val localDate = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        
        return ScheduleItemData(
            id = id,
            title = title,
            date = localDate,
            desc = description,
            isChecked = isChecked
        )
    }
    
    companion object {
        /**
         * 从UI数据模型创建Entity
         */
        fun fromScheduleItemData(data: ScheduleItemData): ScheduleEntity {
            val timestamp = data.date
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            
            return ScheduleEntity(
                id = data.id,
                title = data.title,
                date = timestamp,
                description = data.desc,
                isChecked = data.isChecked
            )
        }
    }
}

