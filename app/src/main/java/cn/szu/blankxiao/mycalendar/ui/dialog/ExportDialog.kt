package cn.szu.blankxiao.mycalendar.ui.dialog

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 导出位置类型
 */
sealed class ExportLocation {
    data class DefaultPath(val file: File) : ExportLocation()
    data class CustomUri(val uri: Uri) : ExportLocation()
}

/**
 * @author BlankXiao
 * @description 导出对话框（支持多种格式）
 * @date 2025-12-11
 */
@Composable
fun ExportDialog(
    fileExtension: String = "json",
    mimeType: String = "application/json",
    onDismiss: () -> Unit,
    onConfirm: (ExportLocation) -> Unit
) {
    val customColors = MaterialTheme.customColors
    
    // 默认下载文件夹
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    
    // 生成默认文件名
    val defaultFileName = remember(fileExtension) {
        "MyCalendar_Export_${
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                .format(LocalDateTime.now())
        }.$fileExtension"
    }
    
    // 格式显示名称
    val formatDisplayName = when (fileExtension) {
        "ics" -> "iCalendar (.ics)"
        "json" -> "JSON"
        else -> fileExtension.uppercase()
    }
    
    var useCustomPath by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    
    // 系统文件选择器
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(mimeType)
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            useCustomPath = true
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(Dimensions.CornerRadius.large),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.medium),
            colors = CardDefaults.cardColors(
                containerColor = customColors.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.large),
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.medium)
            ) {
                // 标题
                Text(
                    text = "导出日程 ($formatDisplayName)",
                    style = MaterialTheme.typography.titleLarge,
                    color = customColors.calendarNormalText
                )
                
                Text(
                    text = "选择文件保存位置",
                    style = MaterialTheme.typography.bodyMedium,
                    color = customColors.scheduleDateText
                )
                
                // 下载文件夹选项
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            useCustomPath = false
                            selectedUri = null
                        }
                        .padding(vertical = Dimensions.Padding.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !useCustomPath,
                        onClick = {
                            useCustomPath = false
                            selectedUri = null
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = customColors.buttonPrimaryBackground,
                            unselectedColor = customColors.outline
                        )
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = Dimensions.Padding.small)
                    ) {
                        Text(
                            text = "下载文件夹",
                            style = MaterialTheme.typography.bodyLarge,
                            color = customColors.calendarNormalText
                        )
                        Text(
                            text = downloadsDir.absolutePath,
                            style = MaterialTheme.typography.bodySmall,
                            color = customColors.scheduleDateText
                        )
                    }
                }
                
                // 自定义位置选项
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            createDocumentLauncher.launch(defaultFileName)
                        }
                        .padding(vertical = Dimensions.Padding.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = useCustomPath && selectedUri != null,
                        onClick = {
                            createDocumentLauncher.launch(defaultFileName)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = customColors.buttonPrimaryBackground,
                            unselectedColor = customColors.outline
                        )
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = Dimensions.Padding.small)
                    ) {
                        Text(
                            text = "选择其他位置",
                            style = MaterialTheme.typography.bodyLarge,
                            color = customColors.calendarNormalText
                        )
                        if (selectedUri != null) {
                            Text(
                                text = selectedUri.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = customColors.scheduleDateText,
                                maxLines = 2
                            )
                        } else {
                            Text(
                                text = "点击选择保存位置",
                                style = MaterialTheme.typography.bodySmall,
                                color = customColors.scheduleDateText
                            )
                        }
                    }
                }
                
                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = customColors.textSecondary
                        )
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = {
                            val location = if (useCustomPath && selectedUri != null) {
                                ExportLocation.CustomUri(selectedUri!!)
                            } else {
                                ExportLocation.DefaultPath(downloadsDir)
                            }
                            onConfirm(location)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = customColors.buttonPrimaryBackground,
                            contentColor = customColors.buttonPrimaryText
                        )
                    ) {
                        Text("导出")
                    }
                }
            }
        }
    }
}
