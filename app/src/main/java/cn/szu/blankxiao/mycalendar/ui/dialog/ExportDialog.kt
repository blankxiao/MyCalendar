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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
 * @description 导出对话框
 * @date 2025-12-11
 */
@Composable
fun ExportDialog(
    onDismiss: () -> Unit,
    onConfirm: (ExportLocation) -> Unit
) {
    val customColors = MaterialTheme.customColors
    
    // 默认下载文件夹
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    
    // 生成默认文件名
    val defaultFileName = remember {
        "MyCalendar_Export_${
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                .format(LocalDateTime.now())
        }.json"
    }
    
    var useCustomPath by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    
    // 系统文件选择器
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
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
                .padding(Dimensions.Padding.medium)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.large),
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.medium)
            ) {
                // 标题
                Text(
                    text = "导出日程",
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
                        }
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
                        }
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
                    TextButton(onClick = onDismiss) {
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
                        }
                    ) {
                        Text("导出")
                    }
                }
            }
        }
    }
}
