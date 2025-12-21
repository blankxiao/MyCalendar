package cn.szu.blankxiao.mycalendar.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.szu.blankxiao.mycalendar.dao.repository.ScheduleRepository
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.export.ExportImportManager
import cn.szu.blankxiao.mycalendar.export.IcsExportImportManager
import cn.szu.blankxiao.mycalendar.export.JsonExportImportManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    
    // 导出导入管理器
    private val jsonExportManager: ExportImportManager = JsonExportImportManager()
    private val icsExportManager: ExportImportManager = IcsExportImportManager()
    
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
    
    /**
     * 导出数据到文件
     * @param context 上下文
     * @param outputDirectory 输出目录
     * @return Result<File> 成功返回文件，失败返回异常
     */
    suspend fun exportSchedules(
        context: Context,
        outputDirectory: File
    ): Result<File> {
        return try {
            _isLoading.value = true
            
            // 获取所有日程Entity
            val entities = repository.getAllScheduleEntities()
            
            // 生成文件名（带时间戳）
            val fileName = "MyCalendar_Export_${
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                    .format(java.time.LocalDateTime.now())
            }.${jsonExportManager.getFileExtension()}"
            
            // 创建输出文件
            val outputFile = File(outputDirectory, fileName)
            
            // 执行导出
            val result = jsonExportManager.exportData(context, entities, outputFile)
            
            _errorMessage.value = null
            result
            
        } catch (e: Exception) {
            _errorMessage.value = "导出失败: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * 导出数据到Uri（使用SAF）
     * @param context 上下文
     * @param uri 输出Uri
     * @return Result<String> 成功返回文件名，失败返回异常
     */
    suspend fun exportSchedulesToUri(
        context: Context,
        uri: Uri
    ): Result<String> {
        return try {
            _isLoading.value = true
            
            // 获取所有日程Entity
            val entities = repository.getAllScheduleEntities()
            
            // 使用ContentResolver写入
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val jsonString = jsonExportManager.exportToString(entities)
                outputStream.write(jsonString.toByteArray())
            } ?: throw Exception("无法打开输出流")
            
            _errorMessage.value = null
            Result.success(uri.lastPathSegment ?: "export.json")
            
        } catch (e: Exception) {
            _errorMessage.value = "导出失败: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * 从文件导入数据
     * @param context 上下文
     * @param inputFile 输入文件
     * @return Result<Int> 成功返回导入数量，失败返回异常
     */
    suspend fun importSchedules(
        context: Context,
        inputFile: File
    ): Result<Int> {
        return try {
            _isLoading.value = true
            
            // 执行导入
            val result = jsonExportManager.importData(context, inputFile)
            
            if (result.isSuccess) {
                val entities = result.getOrThrow()
                
                // 批量插入数据库
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
     * 从Uri导入数据（使用SAF）
     * @param context 上下文
     * @param uri 输入Uri
     * @return Result<Int> 成功返回导入数量，失败返回异常
     */
    suspend fun importSchedulesFromUri(
        context: Context,
        uri: Uri
    ): Result<Int> {
        return try {
            _isLoading.value = true
            
            // 读取文件内容
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: return Result.failure(Exception("无法读取文件"))
            
            // 执行导入
            val result = jsonExportManager.importFromString(jsonString)
            
            if (result.isSuccess) {
                val entities = result.getOrThrow()
                
                // 批量插入数据库
                entities.forEach { entity ->
                    val scheduleData = entity.toScheduleItemData()
                    repository.addSchedule(scheduleData)
                }
                
                _errorMessage.value = null
                Result.success(entities.size)
            } else {
                val error = result.exceptionOrNull()
                _errorMessage.value = error?.message ?: "导入失败"
                Result.failure(error ?: Exception("Unknown error"))
            }
            
        } catch (e: Exception) {
            _errorMessage.value = "导入失败: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    // ==================== ICS 格式导出导入 ====================
    
    /**
     * 导出数据到ICS文件（目录）
     * @param context 上下文
     * @param outputDirectory 输出目录
     * @return Result<File> 成功返回文件，失败返回异常
     */
    suspend fun exportSchedulesAsIcs(
        context: Context,
        outputDirectory: File
    ): Result<File> {
        return try {
            _isLoading.value = true
            
            // 获取所有日程Entity
            val entities = repository.getAllScheduleEntities()
            
            // 生成文件名（带时间戳）
            val fileName = "MyCalendar_Export_${
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                    .format(java.time.LocalDateTime.now())
            }.${icsExportManager.getFileExtension()}"
            
            // 创建输出文件
            val outputFile = File(outputDirectory, fileName)
            
            // 执行导出
            val result = icsExportManager.exportData(context, entities, outputFile)
            
            _errorMessage.value = null
            result
            
        } catch (e: Exception) {
            _errorMessage.value = "ICS导出失败: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * 导出数据到ICS Uri（使用SAF）
     * @param context 上下文
     * @param uri 输出Uri
     * @return Result<String> 成功返回文件名，失败返回异常
     */
    suspend fun exportSchedulesToIcsUri(
        context: Context,
        uri: Uri
    ): Result<String> {
        return try {
            _isLoading.value = true
            
            // 获取所有日程Entity
            val entities = repository.getAllScheduleEntities()
            
            // 使用ContentResolver写入
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val icsString = icsExportManager.exportToString(entities)
                outputStream.write(icsString.toByteArray())
            } ?: throw Exception("无法打开输出流")
            
            _errorMessage.value = null
            Result.success(uri.lastPathSegment ?: "export.ics")
            
        } catch (e: Exception) {
            _errorMessage.value = "ICS导出失败: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * 从ICS Uri导入数据
     * @param context 上下文
     * @param uri 输入Uri
     * @return Result<Int> 成功返回导入数量，失败返回异常
     */
    suspend fun importSchedulesFromIcsUri(
        context: Context,
        uri: Uri
    ): Result<Int> {
        return try {
            _isLoading.value = true
            
            // 读取文件内容
            val icsString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: return Result.failure(Exception("无法读取文件"))
            
            // 执行导入
            val result = icsExportManager.importFromString(icsString)
            
            if (result.isSuccess) {
                val entities = result.getOrThrow()
                
                // 批量插入数据库
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
}

