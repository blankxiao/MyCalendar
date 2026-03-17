package cn.szu.blankxiao.mycalendar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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
	val createdAt: Long = Clock.System.now().toEpochMilliseconds(),

	/** 更新时间戳 */
	val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),

	/** 是否启用提醒 */
	val reminderEnabled: Boolean = false,

	/** 提醒时间戳（具体的提醒时间） */
	val reminderTime: Long? = null
) {
	/**
	 * 转换为UI数据模型
	 */
	fun toScheduleItemData(): ScheduleItemData {
		val timeZone = TimeZone.currentSystemDefault()
		val localDate = Instant.fromEpochMilliseconds(date)
			.toLocalDateTime(timeZone)
			.date

		val reminderDateTime = reminderTime?.let {
			Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone)
		}

		return ScheduleItemData(
			id = id,
			title = title,
			date = localDate,
			description = description,
			isChecked = isChecked,
			reminderEnabled = reminderEnabled,
			reminderTime = reminderDateTime,
			createdAt = createdAt,
			updatedAt = updatedAt
		)
	}

	companion object {
		/**
		 * 从UI数据模型创建Entity
		 */
		fun fromScheduleItemData(data: ScheduleItemData): ScheduleEntity {
			val timeZone = TimeZone.currentSystemDefault()
			val timestamp = data.date.atStartOfDayIn(timeZone).toEpochMilliseconds()

			val reminderTimestamp = data.reminderTime?.let {
				it.toInstant(timeZone).toEpochMilliseconds()
			}

			return ScheduleEntity(
				id = data.id,
				title = data.title,
				date = timestamp,
				description = data.description,
				isChecked = data.isChecked,
				reminderEnabled = data.reminderEnabled,
				reminderTime = reminderTimestamp,
				createdAt = data.createdAt,
				updatedAt = data.updatedAt
			)
		}
	}
}
