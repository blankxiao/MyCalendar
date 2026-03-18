package cn.szu.blankxiao.mycalendar.ui.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.model.settings.ThemeMode
import cn.szu.blankxiao.mycalendar.ui.theme.customColors

/**
 * 主题选择内容（共享组件）
 * 用于设置页的主题切换，可被 Dialog 或内联布局复用
 */
@Composable
fun ThemeSelectionContent(
    currentMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    useCompactLabels: Boolean = false
) {
    val customColors = MaterialTheme.customColors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ThemeMode.entries.forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(mode) }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentMode == mode,
                    onClick = { onSelect(mode) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = customColors.buttonPrimaryBackground,
                        unselectedColor = customColors.outline
                    )
                )
                Text(
                    text = themeModeDisplayName(mode, useCompactLabels),
                    style = MaterialTheme.typography.bodyLarge,
                    color = customColors.textPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

private fun themeModeDisplayName(mode: ThemeMode, compact: Boolean): String =
    if (compact) {
        when (mode) {
            ThemeMode.LIGHT -> "浅色"
            ThemeMode.DARK -> "深色"
            ThemeMode.SYSTEM -> "跟随系统"
            ThemeMode.CHRISTMAS -> "圣诞"
        }
    } else {
        when (mode) {
            ThemeMode.LIGHT -> "浅色模式"
            ThemeMode.DARK -> "深色模式"
            ThemeMode.SYSTEM -> "跟随系统"
            ThemeMode.CHRISTMAS -> "圣诞主题"
        }
    }
