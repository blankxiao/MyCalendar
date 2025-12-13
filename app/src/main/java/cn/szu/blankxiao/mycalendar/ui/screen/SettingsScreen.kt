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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import cn.szu.blankxiao.mycalendar.data.settings.ThemeMode
import cn.szu.blankxiao.mycalendar.data.settings.ThemeSettingsManager
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch

/**
 * @author BlankXiao
 * @description 设置页面
 * @date 2025-12-11
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ScheduleViewModel,
    themeSettingsManager: ThemeSettingsManager,
    onNavigateBack: () -> Unit,
    onNavigateToDataManagement: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    val currentThemeMode by themeSettingsManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    
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
            // 外观设置组
            SettingsGroup(title = "外观") {
                SettingsItem(
                    title = "主题",
                    subtitle = when (currentThemeMode) {
                        ThemeMode.LIGHT -> "浅色模式"
                        ThemeMode.DARK -> "深色模式"
                        ThemeMode.SYSTEM -> "跟随系统"
                    },
                    showArrow = true,
                    onClick = { showThemeDialog = true }
                )
            }
            
            // 分隔线
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Dimensions.Padding.large),
                color = customColors.outline
            )
            
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
    
    // 主题选择对话框
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = currentThemeMode,
            onDismiss = { showThemeDialog = false },
            onSelect = { mode ->
                scope.launch {
                    themeSettingsManager.setThemeMode(mode)
                }
                showThemeDialog = false
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
 * 主题选择对话框
 */
@Composable
private fun ThemeSelectionDialog(
    currentMode: ThemeMode,
    onDismiss: () -> Unit,
    onSelect: (ThemeMode) -> Unit
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
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
            ) {
                Text(
                    text = "选择主题",
                    style = MaterialTheme.typography.titleLarge,
                    color = customColors.textPrimary
                )
                
                // 主题选项
                ThemeOption(
                    title = "浅色模式",
                    subtitle = "始终使用浅色主题",
                    isSelected = currentMode == ThemeMode.LIGHT,
                    onClick = { onSelect(ThemeMode.LIGHT) }
                )
                
                ThemeOption(
                    title = "深色模式",
                    subtitle = "始终使用深色主题",
                    isSelected = currentMode == ThemeMode.DARK,
                    onClick = { onSelect(ThemeMode.DARK) }
                )
                
                ThemeOption(
                    title = "跟随系统",
                    subtitle = "根据系统设置自动切换",
                    isSelected = currentMode == ThemeMode.SYSTEM,
                    onClick = { onSelect(ThemeMode.SYSTEM) }
                )
            }
        }
    }
}

/**
 * 主题选项
 */
@Composable
private fun ThemeOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val customColors = MaterialTheme.customColors
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimensions.Padding.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
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
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = customColors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textSecondary
            )
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
