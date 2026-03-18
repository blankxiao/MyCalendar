package cn.szu.blankxiao.mycalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepositoryEx
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.service.export.ScheduleStringSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Schedule视图模型
 *
 * ViewModel负责：
 * - 管理UI状态
 * - 处理业务逻辑
 * - 与Repository交互
 * - 提供响应式数据流给UI
 *
 * 导出/导入：提供字符串接口，文件 I/O 由 UI 层负责（平台相关）
 */
class ScheduleViewModel(
	private val repository: ScheduleRepositoryEx,
	private val jsonSerializer: ScheduleStringSerializer,
	private val icsSerializer: ScheduleStringSerializer
) : ViewModel() {

    // ==================== 状态管理 ====================

    val allSchedules: StateFlow<List<ScheduleItemData>> = repository.getAllSchedules()
        .catch { e ->
            _errorMessage.value = "加载日程失败: ${e.message}"
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uncompletedSchedules: StateFlow<List<ScheduleItemData>> =
        repository.getUncompletedSchedules()
            .catch { e ->
                _errorMessage.value = "加载未完成日程失败: ${e.message}"
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ==================== 公共方法 ====================

    fun getSchedulesByDate(date: LocalDate): StateFlow<List<ScheduleItemData>> {
        return repository.getSchedulesByDate(date)
            .catch { e ->
                _errorMessage.value = "加载日程失败: ${e.message}"
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addSchedule(
		title: String,
		date: LocalDate,
		description: String = ""
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val scheduleData = ScheduleItemData(
					title = title,
					date = date,
					description = description,
					isChecked = false
				)
                repository.addSchedule(scheduleData)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "添加日程失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun addScheduleAndGetId(scheduleData: ScheduleItemData): Long {
        return try {
            _isLoading.value = true
            val id = repository.addSchedule(scheduleData)
            _errorMessage.value = null
            id
        } catch (e: Exception) {
            _errorMessage.value = "添加日程失败: ${e.message}"
            -1
        } finally {
            _isLoading.value = false
        }
    }

    fun updateSchedule(scheduleData: ScheduleItemData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateSchedule(scheduleData)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "更新日程失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSchedule(scheduleData: ScheduleItemData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteSchedule(scheduleData)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "删除日程失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleScheduleStatus(scheduleData: ScheduleItemData) {
        viewModelScope.launch {
            try {
                val updatedSchedule = scheduleData.copy(isChecked = !scheduleData.isChecked)
                repository.updateSchedule(updatedSchedule)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "更新状态失败: ${e.message}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun deleteAllSchedules() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteAllSchedules()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "删除所有日程失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== 导出/导入（字符串接口，平台无关） ====================

    /**
     * 导出为 JSON 字符串
     */
	suspend fun exportToJsonString(): String {
        val entities = repository.getAllScheduleEntities()
        return jsonSerializer.exportToString(entities)
    }

    /**
     * 从 JSON 字符串导入
     */
    suspend fun importFromJsonString(jsonString: String): Result<Int> {
        return try {
            _isLoading.value = true
            val result = jsonSerializer.importFromString(jsonString)
            if (result.isSuccess) {
                val entities = result.getOrThrow()
                entities.forEach { entity ->
                    val scheduleData = entity.toScheduleItemData()
                    repository.addSchedule(scheduleData)
                }
                _errorMessage.value = null
                Result.success(entities.size)
            } else {
                _errorMessage.value = "导入失败"
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            _errorMessage.value = "导入失败: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * 导出为 ICS 字符串
     */
	suspend fun exportToIcsString(): String {
        val entities = repository.getAllScheduleEntities()
        return icsSerializer.exportToString(entities)
    }

    /**
     * 从 ICS 字符串导入
     */
    suspend fun importFromIcsString(icsString: String): Result<Int> {
        return try {
            _isLoading.value = true
            val result = icsSerializer.importFromString(icsString)
            if (result.isSuccess) {
                val entities = result.getOrThrow()
                entities.forEach { entity ->
                    val scheduleData = entity.toScheduleItemData()
                    repository.addSchedule(scheduleData)
                }
                _errorMessage.value = null
                Result.success(entities.size)
            } else {
                val error = result.exceptionOrNull()
                _errorMessage.value = error?.message ?: "ICS导入失败"
                Result.failure(error ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            _errorMessage.value = "ICS导入失败: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * 生成导出文件名（带时间戳）
     */
    fun getExportFileName(extension: String): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault())
        val timestamp = "${now.year}${now.monthNumber.toString().padStart(2, '0')}${now.dayOfMonth.toString().padStart(2, '0')}_${now.hour.toString().padStart(2, '0')}${now.minute.toString().padStart(2, '0')}${now.second.toString().padStart(2, '0')}"
        return "MyCalendar_Export_$timestamp.$extension"
    }
}