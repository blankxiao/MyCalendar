package cn.szu.blankxiao.mycalendar.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.model.calendar.YearMonth
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.component.DesktopMonthView
import cn.szu.blankxiao.mycalendar.ui.dialog.DesktopAddScheduleDialog
import cn.szu.blankxiao.mycalendar.ui.dialog.DesktopEditScheduleDialog
import cn.szu.blankxiao.mycalendar.ui.screen.settings.DesktopSettingsScreen
import cn.szu.blankxiao.mycalendar.util.formatForDisplay
import cn.szu.blankxiao.mycalendar.viewmodel.AuthViewModel
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

/**
 * PC 端主界面：侧边栏 + 顶栏 + 月历网格
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopMainScreen(
    modifier: Modifier = Modifier,
    scheduleViewModel: ScheduleViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    isLocalOnly: Boolean = false,
    onLogout: () -> Unit = {}
) {
    val allSchedules by scheduleViewModel.allSchedules.collectAsState()
    val isLoading by scheduleViewModel.isLoading.collectAsState()

    var currentYearMonth by remember {
        mutableStateOf(
            YearMonth.from(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            )
        )
    }
    var selectedDate by remember {
        mutableStateOf<kotlinx.datetime.LocalDate?>(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<ScheduleItemData?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val addDate = selectedDate ?: currentYearMonth.atDay(1)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = currentYearMonth.year.toString() + "年" + currentYearMonth.month + "月",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = {
                            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                            currentYearMonth = YearMonth.from(today)
                            selectedDate = today
                        }) {
                            Text("今天")
                        }
                        TextButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(-1) }) {
                            Text("◀")
                        }
                        TextButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                            Text("▶")
                        }
                    }
                },
                actions = {
                    if (isLocalOnly) {
                        Text(
                            text = "本地模式",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    TextButton(onClick = onLogout) {
                        Text(if (isLocalOnly) "返回登录" else "退出")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 侧边栏
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                NavigationRailItem(
                    selected = !showSettings,
                    onClick = { showSettings = false },
                    icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "日历") }
                )
                NavigationRailItem(
                    selected = showSettings,
                    onClick = { showSettings = true },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "设置") }
                )
            }

            // 主内容区
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (showSettings) {
                    DesktopSettingsScreen(
                        onNavigateBack = { showSettings = false },
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    )
                } else {
                    DesktopMonthView(
                        yearMonth = currentYearMonth,
                        allSchedules = allSchedules,
                        selectedDate = selectedDate,
                        onDateClick = { selectedDate = it },
                        onScheduleClick = { editingSchedule = it; showEditDialog = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        DesktopAddScheduleDialog(
            selectedDate = addDate,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, date, description ->
                scheduleViewModel.addSchedule(title, date, description)
                showAddDialog = false
            }
        )
    }

    if (showEditDialog && editingSchedule != null) {
        DesktopEditScheduleDialog(
            schedule = editingSchedule!!,
            onDismiss = {
                showEditDialog = false
                editingSchedule = null
            },
            onConfirm = { updated ->
                scheduleViewModel.updateSchedule(updated)
                showEditDialog = false
                editingSchedule = null
            },
            onDelete = {
                scheduleViewModel.deleteSchedule(editingSchedule!!)
                showEditDialog = false
                editingSchedule = null
            }
        )
    }
}
