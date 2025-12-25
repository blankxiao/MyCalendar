package cn.szu.blankxiao.mycalendar.ui.screen.dayview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.component.dialog.AddScheduleDialog
import cn.szu.blankxiao.mycalendar.ui.component.dialog.EditScheduleDialog
import cn.szu.blankxiao.mycalendar.ui.screen.main.ScheduleViewModel
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import com.nlf.calendar.Lunar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * @author BlankXiao
 * @description 日视图页面 - 显示当天所有日程和农历信息
 * @date 2025-12-13
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayViewScreen(
    date: LocalDate,
    viewModel: ScheduleViewModel,
    onNavigateBack: () -> Unit
) {
    val customColors = MaterialTheme.customColors
    val allSchedules by viewModel.allSchedules.collectAsState()
    val schedules = remember(date, allSchedules) {
        allSchedules.filter { it.date == date }
    }
    
    // 农历信息
    val lunar = remember(date) {
        Lunar.fromDate(
            Date.from(
            date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        ))
    }
    
    // 日期格式化
    val dateFormatter = remember { 
        DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.CHINA) 
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<ScheduleItemData?>(null) }
    
    Scaffold(
        containerColor = customColors.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "日视图",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = customColors.buttonPrimaryBackground,
                contentColor = customColors.buttonPrimaryText
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加日程"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimensions.Padding.large),
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.medium)
        ) {
            // 日期头部
            item {
                DateHeader(
                    date = date,
                    dateFormatter = dateFormatter,
                    lunar = lunar
                )
            }
            
            // 农历详情卡片
            item {
                LunarInfoCard(lunar = lunar)
            }
            
            // 分隔
            item {
                Spacer(modifier = Modifier.height(Dimensions.Spacing.small))
            }
            
            // 日程标题
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "日程安排",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = customColors.textPrimary
                    )
                    Text(
                        text = "${schedules.size} 项",
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColors.textSecondary
                    )
                }
            }
            
            // 日程列表
            if (schedules.isEmpty()) {
                item {
                    EmptyScheduleCard()
                }
            } else {
                items(
                    items = schedules,
                    key = { schedule -> schedule.id }
                ) { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        onClick = {
                            editingSchedule = schedule
                            showEditDialog = true
                        }
                    )
                }
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(Dimensions.Spacing.huge))
            }
        }
    }
    
    // 添加日程对话框
    if (showAddDialog) {
        AddScheduleDialog(
            selectedDate = date,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, scheduleDate, description, _, _ ->
                viewModel.addSchedule(
                    title = title,
                    date = scheduleDate,
                    description = description
                )
            }
        )
    }
    
    // 编辑日程对话框
    if (showEditDialog && editingSchedule != null) {
        EditScheduleDialog(
            scheduleData = editingSchedule!!,
            onDismiss = { 
                showEditDialog = false
                editingSchedule = null
            },
            onConfirm = { _, title, scheduleDate, description, reminderEnabled, reminderDateTime ->
                val updatedSchedule = editingSchedule!!.copy(
                    title = title,
                    date = scheduleDate,
                    description = description,
                    reminderEnabled = reminderEnabled,
                    reminderTime = reminderDateTime
                )
                viewModel.updateSchedule(updatedSchedule)
            },
            onDelete = {
                editingSchedule?.let { viewModel.deleteSchedule(it) }
            }
        )
    }
}

/**
 * 日期头部
 */
@Composable
private fun DateHeader(
    date: LocalDate,
    dateFormatter: DateTimeFormatter,
    lunar: Lunar
) {
    val customColors = MaterialTheme.customColors
    val isToday = date == LocalDate.now()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.Padding.medium)
    ) {
        // 公历日期
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = if (isToday) customColors.buttonPrimaryBackground else customColors.textPrimary
            )
            
            Column {
                Text(
                    text = date.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    color = customColors.textPrimary
                )
                if (isToday) {
                    Text(
                        text = "今天",
                        style = MaterialTheme.typography.labelLarge,
                        color = customColors.buttonPrimaryBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Dimensions.Spacing.small))
        
        // 农历日期
        Text(
            text = "农历 ${lunar.monthInChinese}月${lunar.dayInChinese}",
            style = MaterialTheme.typography.titleSmall,
            color = customColors.textSecondary
        )
    }
}

/**
 * 农历信息卡片
 */
@Composable
private fun LunarInfoCard(lunar: Lunar) {
    val customColors = MaterialTheme.customColors
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CornerRadius.medium),
        colors = CardDefaults.cardColors(
            containerColor = customColors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.medium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
        ) {
            // 干支年月日
            LunarInfoRow(
                label = "干支",
                value = "${lunar.yearInGanZhi}年 ${lunar.monthInGanZhi}月 ${lunar.dayInGanZhi}日"
            )
            
            HorizontalDivider(color = customColors.outlineVariant)
            
            // 生肖
            LunarInfoRow(
                label = "生肖",
                value = lunar.yearShengXiao
            )
            
            HorizontalDivider(color = customColors.outlineVariant)
            
            // 节气（如果有）
            val jieQi = lunar.jieQi
            if (!jieQi.isNullOrEmpty()) {
                LunarInfoRow(
                    label = "节气",
                    value = jieQi
                )
                HorizontalDivider(color = customColors.outlineVariant)
            }
            
            // 节日
            val festivals = mutableListOf<String>()
            lunar.festivals?.let { festivals.addAll(it) }
            lunar.otherFestivals?.let { festivals.addAll(it) }
            
            if (festivals.isNotEmpty()) {
                LunarInfoRow(
                    label = "节日",
                    value = festivals.joinToString("、")
                )
                HorizontalDivider(color = customColors.outlineVariant)
            }
            
            // 宜忌
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "宜",
                        style = MaterialTheme.typography.labelMedium,
                        color = customColors.success,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lunar.dayYi?.take(4)?.joinToString(" ") ?: "诸事皆宜",
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textSecondary
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "忌",
                        style = MaterialTheme.typography.labelMedium,
                        color = customColors.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lunar.dayJi?.take(4)?.joinToString(" ") ?: "诸事不宜",
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textSecondary
                    )
                }
            }
        }
    }
}

/**
 * 农历信息行
 */
@Composable
private fun LunarInfoRow(
    label: String,
    value: String
) {
    val customColors = MaterialTheme.customColors
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = customColors.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textPrimary
        )
    }
}

/**
 * 空日程卡片
 */
@Composable
private fun EmptyScheduleCard() {
    val customColors = MaterialTheme.customColors
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CornerRadius.medium),
        colors = CardDefaults.cardColors(
            containerColor = customColors.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.extraLarge),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.IconSize.extraLarge),
                    tint = customColors.textTertiary
                )
                Text(
                    text = "暂无日程安排",
                    style = MaterialTheme.typography.bodyMedium,
                    color = customColors.textSecondary
                )
                Text(
                    text = "点击右下角按钮添加日程",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textTertiary
                )
            }
        }
    }
}

/**
 * 日程卡片
 */
@Composable
private fun ScheduleCard(
    schedule: ScheduleItemData,
    onClick: () -> Unit
) {
    val customColors = MaterialTheme.customColors
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CornerRadius.medium),
        colors = CardDefaults.cardColors(
            containerColor = customColors.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.medium)
        ) {
            // 完成状态指示
            Box(
                modifier = Modifier
                    .size(Dimensions.Size.small)
                    .background(
                        color = if (schedule.isChecked) 
                            customColors.success 
                        else 
                            customColors.buttonPrimaryBackground,
                        shape = RoundedCornerShape(Dimensions.CornerRadius.small)
                    )
            )
            
            // 日程内容
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.tiny)
            ) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (schedule.isChecked) 
                        customColors.textTertiary 
                    else 
                        customColors.textPrimary,
                    textDecoration = if (schedule.isChecked) 
                        TextDecoration.LineThrough 
                    else 
                        TextDecoration.None
                )
                
                if (schedule.description.isNotBlank()) {
                    Text(
                        text = schedule.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textSecondary,
                        maxLines = 2
                    )
                }
                
                // 提醒时间
                if (schedule.reminderEnabled && schedule.reminderTime != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.tiny)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "提醒时间",
                            modifier = Modifier.size(Dimensions.IconSize.tiny),
                            tint = customColors.textSecondary
                        )
                        Text(
                            text = schedule.reminderTime.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = customColors.textSecondary
                        )
                    }
                }
            }
            
            // 完成状态文字
            Text(
                text = if (schedule.isChecked) "已完成" else "进行中",
                style = MaterialTheme.typography.labelSmall,
                color = if (schedule.isChecked) 
                    customColors.success 
                else 
                    customColors.buttonPrimaryBackground
            )
        }
    }
}

