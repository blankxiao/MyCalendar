package cn.szu.blankxiao.mycalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cn.szu.blankxiao.mycalendar.model.settings.ThemeMode
import cn.szu.blankxiao.mycalendar.model.settings.ThemeSettingsManager
import cn.szu.blankxiao.mycalendar.navigation.AppNavHost
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val themeSettingsManager = remember { ThemeSettingsManager(this) }
			val themeMode by themeSettingsManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
			
			MyCalendarTheme(themeMode = themeMode) {
				val navController = rememberNavController()
				AppNavHost(
					navController = navController,
					modifier = Modifier.fillMaxSize(),
					themeStorage = themeSettingsManager
				)
			}
		}
	}
}
