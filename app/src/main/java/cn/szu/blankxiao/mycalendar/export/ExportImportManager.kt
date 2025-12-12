package cn.szu.blankxiao.mycalendar.export

import android.content.Context
import cn.szu.blankxiao.mycalendar.dao.local.entity.ScheduleEntity
import java.io.File

/**
 * @author BlankXiao
 * @description 导入导出管理器接口
 * @date 2025-12-11
 */
interface ExportImportManager {
    
    /**
     * 导出数据到文件
     * @param context 上下文
     * @param schedules 要导出的日程列表
     * @param outputFile 输出文件
     * @return 是否成功
     */
    suspend fun exportData(
        context: Context,
        schedules: List<ScheduleEntity>,
        outputFile: File
    ): Result<File>
    
    /**
     * 从文件导入数据
     * @param context 上下文
     * @param inputFile 输入文件
     * @return 导入的日程列表
     */
    suspend fun importData(
        context: Context,
        inputFile: File
    ): Result<List<ScheduleEntity>>
    
    /**
     * 获取支持的文件扩展名
     */
    fun getFileExtension(): String
    
    /**
     * 获取MIME类型
     */
    fun getMimeType(): String
    
    /**
     * 导出数据为字符串
     * @param schedules 要导出的日程列表
     * @return 导出的字符串
     */
    fun exportToString(schedules: List<ScheduleEntity>): String
    
    /**
     * 从字符串导入数据
     * @param jsonString JSON字符串
     * @return 导入的日程列表
     */
    fun importFromString(jsonString: String): Result<List<ScheduleEntity>>
}

