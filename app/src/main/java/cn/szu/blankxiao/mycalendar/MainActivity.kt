package cn.szu.blankxiao.mycalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cn.szu.blankxiao.mycalendar.data.settings.ThemeMode
import cn.szu.blankxiao.mycalendar.data.settings.ThemeSettingsManager
import cn.szu.blankxiao.mycalendar.navigation.AppNavHost
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val themeSettingsManager = remember { ThemeSettingsManager(this) }
			val themeMode by themeSettingsManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
			
			// 根据主题模式决定是否使用深色主题
			val isDarkTheme = when (themeMode) {
				ThemeMode.LIGHT -> false
				ThemeMode.DARK -> true
				ThemeMode.SYSTEM -> isSystemInDarkTheme()
			}
			
			MyCalendarTheme(darkTheme = isDarkTheme) {
				val navController = rememberNavController()
				AppNavHost(
					navController = navController,
					modifier = Modifier.fillMaxSize(),
					themeSettingsManager = themeSettingsManager
				)
			}
		}
	}
}
