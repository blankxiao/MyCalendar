package cn.szu.blankxiao.mycalendar.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
actual fun currentWindowWidthClass(): WindowWidthClass {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp.dp
    return windowWidthClassFrom(widthDp)
}
