package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cn.szu.blankxiao.mycalendar.data.settings.ThemeMode

/**
 * 主题配置文件
 * 统一管理Material3颜色和自定义业务颜色
 */

// ==================== Material3 浅色主题 ====================
private val LightColorScheme = lightColorScheme(
    // 主色调
    primary = AccentBlue,
    onPrimary = White,
    primaryContainer = AccentBlueLight,
    onPrimaryContainer = Blue600,
    
    // 次要色
    secondary = Blue300,
    onSecondary = White,
    secondaryContainer = Blue50,
    onSecondaryContainer = Blue600,
    
    // 第三色
    tertiary = Green300,
    onTertiary = White,
    tertiaryContainer = Green50,
    onTertiaryContainer = Green600,
    
    // 错误色
    error = Rose400,
    onError = White,
    errorContainer = Rose50,
    onErrorContainer = Rose600,
    
    // 背景和表面
    background = Slate50,
    onBackground = Slate700,
    surface = White,
    onSurface = Slate700,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    
    // 边框
    outline = Slate200,
    outlineVariant = Slate150,
    
    // 反向表面（用于Snackbar等）
    inverseSurface = Slate800,
    inverseOnSurface = Slate50,
    inversePrimary = Blue200,
    
    // 遮罩
    scrim = ScrimLight,
)

// ==================== Material3 深色主题 ====================
private val DarkColorScheme = darkColorScheme(
    // 主色调
    primary = AccentBlue,
    onPrimary = White,
    primaryContainer = Blue600,
    onPrimaryContainer = Blue100,
    
    // 次要色
    secondary = Blue200,
    onSecondary = Blue700,
    secondaryContainer = Blue600,
    onSecondaryContainer = Blue100,
    
    // 第三色
    tertiary = Green300,
    onTertiary = Green700,
    tertiaryContainer = Green600,
    onTertiaryContainer = Green100,
    
    // 错误色
    error = Rose300,
    onError = Rose700,
    errorContainer = Rose700,
    onErrorContainer = Rose100,
    
    // 背景和表面
    background = Dark100,
    onBackground = Dark800,
    surface = Dark200,
    onSurface = Dark800,
    surfaceVariant = Dark300,
    onSurfaceVariant = Dark700,
    
    // 边框
    outline = Dark500,
    outlineVariant = Dark400,
    
    // 反向表面
    inverseSurface = Dark900,
    inverseOnSurface = Dark200,
    inversePrimary = Blue300,
    
    // 遮罩
    scrim = ScrimDark,
)

// ==================== Material3 圣诞主题 ====================
private val ChristmasColorScheme = lightColorScheme(
    // 主色调 - 圣诞绿
    primary = ChristmasGreen400,
    onPrimary = White,
    primaryContainer = ChristmasGreen100,
    onPrimaryContainer = ChristmasGreen600,
    
    // 次要色 - 圣诞红
    secondary = ChristmasRed400,
    onSecondary = White,
    secondaryContainer = ChristmasRed100,
    onSecondaryContainer = ChristmasRed600,
    
    // 第三色 - 圣诞金
    tertiary = ChristmasGold400,
    onTertiary = ChristmasGreen700,
    tertiaryContainer = ChristmasGold100,
    onTertiaryContainer = ChristmasGold600,
    
    // 错误色
    error = ChristmasRed500,
    onError = White,
    errorContainer = ChristmasRed50,
    onErrorContainer = ChristmasRed600,
    
    // 背景和表面 - 雪白
    background = ChristmasSnow,
    onBackground = ChristmasGreen700,
    surface = White,
    onSurface = ChristmasGreen700,
    surfaceVariant = ChristmasGreen50,
    onSurfaceVariant = ChristmasGreen500,
    
    // 边框
    outline = ChristmasGreen200,
    outlineVariant = ChristmasGreen100,
    
    // 反向表面
    inverseSurface = ChristmasGreen700,
    inverseOnSurface = ChristmasSnow,
    inversePrimary = ChristmasGreen200,
    
    // 遮罩
    scrim = ScrimLight,
)

/**
 * 简化的主题函数（保持向后兼容）
 */
@Composable
fun MyCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 选择对应的配色方案
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    // 提供自定义颜色到组合树
    CompositionLocalProvider(LocalCustomColors provides customColors) {
        // 应用主题（包含Material3的colorScheme）
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * 带主题模式的主题函数
 */
@Composable
fun MyCalendarTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    
    // 根据主题模式选择配色方案
    val (colorScheme, customColors) = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme to LightCustomColors
        ThemeMode.DARK -> DarkColorScheme to DarkCustomColors
        ThemeMode.CHRISTMAS -> ChristmasColorScheme to ChristmasCustomColors
        ThemeMode.SYSTEM -> {
            if (isSystemDark) DarkColorScheme to DarkCustomColors
            else LightColorScheme to LightCustomColors
        }
    }

    // 提供自定义颜色到组合树
    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
