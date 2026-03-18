package cn.szu.blankxiao.mycalendar.ui.screen.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import cn.szu.blankxiao.mycalendar.ui.component.dialog.ExportDialog
import cn.szu.blankxiao.mycalendar.ui.component.dialog.ExportLocation
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import kotlinx.coroutines.launch

/**
 * @author BlankXiao
 * @description 日程导入导出页面
 * @date 2025-12-12
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagementScreen(
    viewModel: ScheduleViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val customColors = MaterialTheme.customColors
    
    var showJsonExportDialog by remember { mutableStateOf(false) }
    var showIcsExportDialog by remember { mutableStateOf(false) }
    
    // 文件选择器 - 导入JSON
    val jsonImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                val jsonString = context.contentResolver.openInputStream(uri)?.use {
                    it.bufferedReader().readText()
                }
                if (jsonString != null) {
                    val result = viewModel.importFromJsonString(jsonString)
                    if (result.isSuccess) {
                        val count = result.getOrNull() ?: 0
                        Toast.makeText(context, "导入成功：共 $count 条日程", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "未知错误"
                        Toast.makeText(context, "导入失败：$errorMsg", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "无法读取文件", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 文件选择器 - 导入ICS
    val icsImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                val icsString = context.contentResolver.openInputStream(uri)?.use {
                    it.bufferedReader().readText()
                }
                if (icsString != null) {
                    val result = viewModel.importFromIcsString(icsString)
                    if (result.isSuccess) {
                        val count = result.getOrNull() ?: 0
                        Toast.makeText(context, "导入成功：共 $count 条日程", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "未知错误"
                        Toast.makeText(context, "导入失败：$errorMsg", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "无法读取文件", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "日程导入导出",
                        color = customColors.textPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = customColors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = customColors.surface,
                    titleContentColor = customColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // JSON 格式
            DataFormatSection(title = "JSON 格式") {
                // 导出
                DataOperationItem(
                    title = "导出日程",
                    subtitle = "将所有日程导出为 JSON 文件（应用备份）",
                    onClick = { showJsonExportDialog = true }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.large),
                    color = customColors.outlineVariant
                )
                
                // 导入
                DataOperationItem(
                    title = "导入日程",
                    subtitle = "从 JSON 文件导入日程",
                    onClick = {
                        jsonImportLauncher.launch(arrayOf("application/json"))
                    }
                )
            }
            
            // iCalendar 格式
            DataFormatSection(title = "iCalendar 格式 (.ics)") {
                // 导出
                DataOperationItem(
                    title = "导出日程",
                    subtitle = "导出为通用日历格式，可导入 Google/Apple 日历",
                    onClick = { showIcsExportDialog = true }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.large),
                    color = customColors.outlineVariant
                )
                
                // 导入
                DataOperationItem(
                    title = "导入日程",
                    subtitle = "从 iCalendar (.ics) 文件导入日程",
                    onClick = {
                        icsImportLauncher.launch(arrayOf("text/calendar", "*/*"))
                    }
                )
            }
        }
    }
    
    // JSON 导出对话框
    if (showJsonExportDialog) {
        ExportDialog(
            fileExtension = "json",
            mimeType = "application/json",
            onDismiss = { showJsonExportDialog = false },
            onConfirm = { location ->
                showJsonExportDialog = false
                coroutineScope.launch {
                    val jsonString = viewModel.exportToJsonString()
                    when (location) {
                        is ExportLocation.DefaultPath -> {
                            try {
                                val outputFile = java.io.File(location.file, viewModel.getExportFileName("json"))
                                outputFile.parentFile?.mkdirs()
                                outputFile.writeText(jsonString)
                                Toast.makeText(context, "导出成功：${outputFile.name}", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "导出失败：${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        is ExportLocation.CustomUri -> {
                            try {
                                context.contentResolver.openOutputStream(location.uri)?.use {
                                    it.write(jsonString.toByteArray())
                                }
                                Toast.makeText(context, "导出成功", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "导出失败：${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        )
    }

    // ICS 导出对话框
    if (showIcsExportDialog) {
        ExportDialog(
            fileExtension = "ics",
            mimeType = "text/calendar",
            onDismiss = { showIcsExportDialog = false },
            onConfirm = { location ->
                showIcsExportDialog = false
                coroutineScope.launch {
                    val icsString = viewModel.exportToIcsString()
                    when (location) {
                        is ExportLocation.DefaultPath -> {
                            try {
                                val outputFile = java.io.File(location.file, viewModel.getExportFileName("ics"))
                                outputFile.parentFile?.mkdirs()
                                outputFile.writeText(icsString)
                                Toast.makeText(context, "导出成功：${outputFile.name}", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "导出失败：${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        is ExportLocation.CustomUri -> {
                            try {
                                context.contentResolver.openOutputStream(location.uri)?.use {
                                    it.write(icsString.toByteArray())
                                }
                                Toast.makeText(context, "导出成功", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "导出失败：${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        )
    }
}

/**
 * 数据格式区块
 */
@Composable
private fun DataFormatSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val customColors = MaterialTheme.customColors
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.Padding.medium)
    ) {
        // 格式标题
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Normal,
            color = customColors.scheduleDateText,
            modifier = Modifier.padding(
                horizontal = Dimensions.Padding.large,
                vertical = Dimensions.Padding.small
            )
        )
        
        // 操作项
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

/**
 * 数据操作项
 */
@Composable
private fun DataOperationItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val customColors = MaterialTheme.customColors
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = Dimensions.Padding.large,
                vertical = Dimensions.Padding.medium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = customColors.calendarNormalText
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = customColors.scheduleDateText
            )
        }
    }
}
