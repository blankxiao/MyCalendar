package cn.szu.blankxiao.mycalendar.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel

/**
 * @author BlankXiao
 * @description 设置页面
 * @date 2025-12-11
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ScheduleViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDataManagement: () -> Unit
) {
    val context = LocalContext.current
    var showClearDataDialog by remember { mutableStateOf(false) }
    
    val customColors = MaterialTheme.customColors
    
    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "设置",
                        color = customColors.textPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            // 数据管理设置组
            SettingsGroup(title = "数据管理") {
                // 日程导入导出
                SettingsItem(
                    title = "日程导入导出",
                    subtitle = "导入或导出日程数据",
                    showArrow = true,
                    onClick = onNavigateToDataManagement
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.large),
                    color = customColors.outlineVariant
                )
                
                // 清空所有日程
                SettingsItem(
                    title = "清空所有日程",
                    subtitle = "删除所有日程数据，此操作不可恢复",
                    isDangerous = true,
                    onClick = { showClearDataDialog = true }
                )
            }
            
            // 分隔线
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Dimensions.Padding.large),
                color = customColors.outline
            )
            
            // 关于设置组
            SettingsGroup(title = "关于") {
                SettingsItem(
                    title = "版本",
                    subtitle = "v1.0.0",
                    onClick = {}
                )
            }
        }
    }
    
    // 清空数据确认对话框
    if (showClearDataDialog) {
        ClearDataConfirmDialog(
            onDismiss = { showClearDataDialog = false },
            onConfirm = {
                showClearDataDialog = false
                viewModel.deleteAllSchedules()
                Toast.makeText(context, "已清空所有日程数据", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * 清空数据确认对话框
 */
@Composable
private fun ClearDataConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val customColors = MaterialTheme.customColors
    
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
                Text(
                    text = "清空所有日程",
                    style = MaterialTheme.typography.titleLarge,
                    color = customColors.error
                )
                
                Text(
                    text = "确定要删除所有日程数据吗？\n\n此操作不可恢复，建议先导出备份。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = customColors.textPrimary
                )
                
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
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = customColors.error,
                            contentColor = customColors.onError
                        )
                    ) {
                        Text("确认清空")
                    }
                }
            }
        }
    }
}

/**
 * 设置组
 */
@Composable
private fun SettingsGroup(
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
        // 组标题
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
        
        // 设置项内容
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

/**
 * 设置项
 */
@Composable
private fun SettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    showArrow: Boolean = false,
    isDangerous: Boolean = false,
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
        // 文本
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDangerous) customColors.error else customColors.textPrimary
            )
            
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDangerous) customColors.error else customColors.textSecondary
                )
            }
        }
        
        // 箭头
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = customColors.scheduleDateText
            )
        }
    }
}
