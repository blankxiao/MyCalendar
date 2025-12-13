package cn.szu.blankxiao.mycalendar.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cn.szu.blankxiao.mycalendar.ui.screen.DataManagementScreen
import cn.szu.blankxiao.mycalendar.ui.screen.DayViewScreen
import cn.szu.blankxiao.mycalendar.ui.screen.MainScreen
import cn.szu.blankxiao.mycalendar.ui.screen.SettingsScreen
import java.time.LocalDate
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
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
    viewModel: ScheduleViewModel = koinViewModel()
) {
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
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDataManagement = {
                    navController.navigate(Screen.DataManagement.route)
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
                LocalDate.now()
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
