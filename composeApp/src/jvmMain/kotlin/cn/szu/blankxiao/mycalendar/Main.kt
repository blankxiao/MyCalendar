package cn.szu.blankxiao.mycalendar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import cn.szu.blankxiao.mycalendar.di.desktopModules
import cn.szu.blankxiao.mycalendar.ui.DesktopAppContent
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin

@OptIn(ExperimentalMaterial3Api::class)
fun main() {
    startKoin {
        modules(desktopModules)
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MyCalendar",
        ) {
            MaterialTheme {
                Surface {
                    DesktopAppContent()
                }
            }
        }
    }
}
