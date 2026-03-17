package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthViewModel
import cn.szu.blankxiao.mycalendar.ui.screen.main.ScheduleViewModel
import cn.szu.blankxiao.mycalendar.ui.screen.schedule.ScheduleScreen
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

/**
 * PC 端主界面：使用共享 ScheduleScreen
 */
@Composable
fun DesktopMainScreen(
    scheduleViewModel: ScheduleViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    onLogout: () -> Unit = {}
) {
    var selectedDate by remember {
        mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<ScheduleItemData?>(null) }

    ScheduleScreen(
        scheduleViewModel = scheduleViewModel,
        selectedDate = selectedDate,
        onDateChange = { selectedDate = it },
        onAddClick = { showAddDialog = true },
        onEditClick = { editingSchedule = it; showEditDialog = true },
        topBarActions = {
            TextButton(onClick = onLogout) {
                Text("退出")
            }
        }
    )

    if (showAddDialog) {
        DesktopAddScheduleDialog(
            selectedDate = selectedDate,
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
