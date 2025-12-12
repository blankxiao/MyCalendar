package cn.szu.blankxiao.mycalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cn.szu.blankxiao.mycalendar.navigation.AppNavHost
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// enableEdgeToEdge()
		setContent {
			MyCalendarTheme {
				val navController = rememberNavController()
				AppNavHost(
					navController = navController,
					modifier = Modifier.fillMaxSize()
				)
			}
		}
	}
}
