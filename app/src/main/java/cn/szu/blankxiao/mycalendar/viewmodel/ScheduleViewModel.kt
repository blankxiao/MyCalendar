package cn.szu.blankxiao.mycalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.szu.blankxiao.mycalendar.dao.repository.ScheduleRepository
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description Schedule视图模型
 * @date 2025-12-11
 * 
 * ViewModel负责：
 * - 管理UI状态
 * - 处理业务逻辑
 * - 与Repository交互
 * - 提供响应式数据流给UI
 */
class ScheduleViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {
    
    // ==================== 状态管理 ====================
    
    /**
     * 所有日程列表（响应式）
     * 注意：日期选择由 CalendarState 管理，这里只负责提供数据
     */
    val allSchedules: StateFlow<List<ScheduleItemData>> = repository.getAllSchedules()
        .catch { e ->
            // 错误处理
            _errorMessage.value = "加载日程失败: ${e.message}"
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * 未完成的日程列表
     */
    val uncompletedSchedules: StateFlow<List<ScheduleItemData>> = 
        repository.getUncompletedSchedules()
            .catch { e ->
                _errorMessage.value = "加载未完成日程失败: ${e.message}"
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    /**
     * 错误消息
     */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * 加载状态
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // ==================== 公共方法 ====================
    
    /**
     * 获取指定日期的日程（响应式）
     * 注意：调用者需要在 Composable 中使用 collectAsState() 收集
     * 
     * @param date 指定的日期
     * @return 该日期的日程列表流
     */
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
    
    /**
     * 添加日程
     */
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
                    desc = description,
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
    
    /**
     * 添加日程并返回ID
     */
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
    
    /**
     * 更新日程
     */
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
    
    /**
     * 删除日程
     */
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
    
    /**
     * 切换日程完成状态
     */
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
    
    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    /**
     * 删除所有日程（慎用）
     */
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
}

