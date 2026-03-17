package cn.szu.blankxiao.mycalendar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

/**
 * @author BlankXiao
 * @description Schedule数据访问对象
 * @date 2025-12-11
 *
 * 使用Flow实现响应式数据流，当数据库数据变化时自动通知UI更新
 * 使用suspend函数支持协程，避免阻塞主线程
 */
@Dao
interface ScheduleDao {

	/**
	 * 查询所有日程
	 * 使用Flow实现响应式数据流
	 * 排序规则：按日期降序，同一天内未完成的在前，同状态内按更新时间降序
	 */
	@Query("SELECT * FROM schedules ORDER BY date DESC, isChecked ASC, updatedAt DESC")
	fun getAllSchedules(): Flow<List<ScheduleEntity>>

	/**
	 * 查询所有日程
	 */
	@Query("SELECT * FROM schedules ORDER BY date DESC, isChecked ASC, updatedAt DESC")
	suspend fun getAllSchedulesOnce(): List<ScheduleEntity>

	/**
	 * 根据日期查询日程
	 * @param startDate 开始日期时间戳
	 * @param endDate 结束日期时间戳
	 * 排序规则：未完成的在前，同状态内按更新时间降序
	 */
	@Query("SELECT * FROM schedules WHERE date >= :startDate AND date < :endDate ORDER BY isChecked ASC, updatedAt DESC")
	fun getSchedulesByDateRange(startDate: Long, endDate: Long): Flow<List<ScheduleEntity>>

	/**
	 * 根据ID查询单个日程
	 */
	@Query("SELECT * FROM schedules WHERE id = :id")
	suspend fun getScheduleById(id: Long): ScheduleEntity?

	/**
	 * 查询未完成的日程
	 * 排序规则：按日期升序（最早的在前），同一天内按更新时间降序
	 */
	@Query("SELECT * FROM schedules WHERE isChecked = 0 ORDER BY date ASC, updatedAt DESC")
	fun getUncompletedSchedules(): Flow<List<ScheduleEntity>>

	/**
	 * 查询已完成的日程
	 * 排序规则：按更新时间降序（最近完成的在前）
	 */
	@Query("SELECT * FROM schedules WHERE isChecked = 1 ORDER BY updatedAt DESC")
	fun getCompletedSchedules(): Flow<List<ScheduleEntity>>

	/**
	 * 插入日程
	 * 如果冲突则替换
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(schedule: ScheduleEntity): Long

	/**
	 * 批量插入日程
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(schedules: List<ScheduleEntity>)

	/**
	 * 更新日程
	 */
	@Update
	suspend fun update(schedule: ScheduleEntity)

	/**
	 * 删除日程
	 */
	@Delete
	suspend fun delete(schedule: ScheduleEntity)

	/**
	 * 根据ID删除日程
	 */
	@Query("DELETE FROM schedules WHERE id = :id")
	suspend fun deleteById(id: Long)

	/**
	 * 删除所有日程
	 */
	@Query("DELETE FROM schedules")
	suspend fun deleteAll()

	/**
	 * 更新日程完成状态
	 */
	@Query("UPDATE schedules SET isChecked = :isChecked, updatedAt = :updatedAt WHERE id = :id")
	suspend fun updateCheckStatus(id: Long, isChecked: Boolean, updatedAt: Long = Clock.System.now().toEpochMilliseconds())
}
