package cn.szu.blankxiao.mycalendar.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import cn.szu.blankxiao.mycalendar.data.local.datastore.ThemeStorage
import cn.szu.blankxiao.mycalendar.ui.screen.settings.DataManagementScreen
import cn.szu.blankxiao.mycalendar.ui.screen.dayview.DayViewScreen
import cn.szu.blankxiao.mycalendar.ui.screen.main.MainScreen
import cn.szu.blankxiao.mycalendar.ui.screen.settings.SettingsScreen
import cn.szu.blankxiao.mycalendar.ui.screen.auth.LoginScreen
import cn.szu.blankxiao.mycalendar.ui.screen.auth.RegisterScreen
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel

/**
 * @author BlankXiao
 * @description 应用导航图
 * @date 2025-12-11
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = koinViewModel(),
    themeStorage: ThemeStorage
) {
    val context = LocalContext.current
    val onShowMessage: (String) -> Unit = { msg ->
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = modifier
    ) {
        // 主页面
        composable(Screen.Main.route) {
            MainScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToDayView = { date ->
                    navController.navigate(Screen.DayView.createRoute(date.toString()))
                }
            )
        }
        
        // 设置页面
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                themeStorage = themeStorage,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDataManagement = {
                    navController.navigate(Screen.DataManagement.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onShowMessage = onShowMessage
            )
        }
        
        // 登录页面
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    // 登录成功后返回设置页面
                    navController.popBackStack()
                }
            )
        }
        
        // 注册页面
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // 注册成功后返回设置页面
                    navController.popBackStack(Screen.Settings.route, inclusive = false)
                }
            )
        }
        
        // 日程导入导出页面
        composable(Screen.DataManagement.route) {
            DataManagementScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // 日视图页面
        composable(
            route = Screen.DayView.route,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: ""
            val date = try {
                LocalDate.parse(dateString)
            } catch (e: Exception) {
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            }
            
            DayViewScreen(
                date = date,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
