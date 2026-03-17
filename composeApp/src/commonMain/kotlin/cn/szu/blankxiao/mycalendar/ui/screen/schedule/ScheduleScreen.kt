package cn.szu.blankxiao.mycalendar.ui.screen.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.Dp
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.component.schedule.ScheduleDetailContent
import cn.szu.blankxiao.mycalendar.ui.component.schedule.ScheduleItemCard
import cn.szu.blankxiao.mycalendar.ui.screen.main.ScheduleViewModel
import cn.szu.blankxiao.mycalendar.ui.util.AdaptiveLayout
import cn.szu.blankxiao.mycalendar.util.formatForDisplay
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/**
 * 共享日程主屏（Android + 桌面）
 * 根据窗口宽度自适应：Compact 为紧凑布局，Expanded 为 Master-Detail
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    scheduleViewModel: ScheduleViewModel,
    selectedDate: kotlinx.datetime.LocalDate,
    onDateChange: (kotlinx.datetime.LocalDate) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (ScheduleItemData) -> Unit,
    modifier: Modifier = Modifier,
    topBarActions: @Composable () -> Unit = {}
) {
    val allSchedules by scheduleViewModel.allSchedules.collectAsState()
    val isLoading by scheduleViewModel.isLoading.collectAsState()

    val selectedDateSchedules = remember(selectedDate, allSchedules) {
        allSchedules.filter { it.date == selectedDate }
    }

    var selectedId by remember { mutableStateOf<Long?>(null) }
    val selectedSchedule = remember(selectedId, selectedDateSchedules) {
        selectedId?.let { id -> selectedDateSchedules.find { it.id == id } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            Dp(16f)
                        )
                    ) {
                        androidx.compose.material3.TextButton(onClick = {
                            onDateChange(selectedDate.minus(1, DateTimeUnit.DAY))
                        }) {
                            Text("◀")
                        }
                        Text(
                            text = selectedDate.formatForDisplay("yyyy年M月d日 EEEE"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        androidx.compose.material3.TextButton(onClick = {
                            onDateChange(selectedDate.plus(1, DateTimeUnit.DAY))
                        }) {
                            Text("▶")
                        }
                    }
                },
                actions = { topBarActions() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                AdaptiveLayout(
                    modifier = Modifier.fillMaxSize(),
                    compactContent = {
                        CompactScheduleLayout(
                            schedules = selectedDateSchedules,
                            selectedSchedule = selectedSchedule,
                            onScheduleClick = { selectedId = it.id },
                            onScheduleToggle = { scheduleViewModel.toggleScheduleStatus(it) },
                            onEditClick = onEditClick,
                            onSelectSchedule = { s -> selectedId = s?.id }
                        )
                    },
                    expandedContent = {
                        MasterDetailScheduleLayout(
                            schedules = selectedDateSchedules,
                            selectedSchedule = selectedSchedule,
                            onScheduleClick = { selectedId = it.id },
                            onScheduleToggle = { scheduleViewModel.toggleScheduleStatus(it) },
                            onEditClick = onEditClick
                        )
                    }
                )
            }
        }
    }
}
