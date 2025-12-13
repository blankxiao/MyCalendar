package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 业务语义化颜色系统（Design Tokens - Semantic）
 * 
 * 设计理念：简约 · 精致 · 细节
 * - 低饱和度的蓝灰色调为主
 * - 柔和的强调色
 * - 丰富的灰度层次
 */

@Immutable
data class CustomColors(
    
    // ==================== 通用基础颜色 ====================
    
    // 主色调 - 柔和的蓝灰色
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    
    // 表面颜色
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    
    // 背景颜色
    val background: Color,
    val onBackground: Color,
    
    // 边框和分隔线
    val outline: Color,
    val outlineVariant: Color,
    
    // 错误/危险状态
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    
    // 成功状态
    val success: Color,
    val onSuccess: Color,
    
    // 警告状态
    val warning: Color,
    val onWarning: Color,
    
    // 遮罩层
    val scrim: Color,
    
    // 禁用状态
    val disabled: Color,
    val onDisabled: Color,
    
    // 表单输入框
    val inputText: Color,
    val inputLabel: Color,
    val inputIcon: Color,
    
    // 按钮
    val buttonPrimaryBackground: Color,
    val buttonPrimaryText: Color,
    val buttonSecondaryBackground: Color,
    val buttonSecondaryText: Color,
    
    // ==================== 日历相关颜色 ====================
    
    // 选中状态 - 使用点缀蓝
    val calendarSelectedBackground: Color,
    val calendarSelectedText: Color,
    
    // 今天标记 - 柔和的强调
    val calendarTodayBackground: Color,
    val calendarTodayBorder: Color,
    val calendarTodayText: Color,
    
    // 普通日期
    val calendarNormalText: Color,
    val calendarOtherMonthText: Color,
    val calendarWeekendText: Color,
    
    // 日历标题和导航
    val calendarHeaderText: Color,
    val calendarWeekLabelText: Color,
    val calendarNavigationIcon: Color,
    
    // 日历背景和分割线
    val calendarBackground: Color,
    val calendarDivider: Color,
    
    // 日历待办指示点
    val calendarScheduleDot: Color,
    
    // 日历拖动手柄
    val calendarDragHandle: Color,
    
    // ==================== 日程相关颜色 ====================
    
    // 日程项状态
    val scheduleUncompletedText: Color,
    val scheduleCompletedText: Color,
    val scheduleDateText: Color,
    
    // 日程复选框 - 使用点缀蓝
    val scheduleCheckboxChecked: Color,
    val scheduleCheckboxUnchecked: Color,
    val scheduleCheckboxCheckmark: Color,

    // 日程卡片
    val scheduleCardBackground: Color,
    
    // 日程优先级
    val scheduleUrgent: Color,
    val scheduleImportant: Color,
    val scheduleNormal: Color,

    // 日程列表
    val scheduleListBackground: Color,
    val scheduleListTitleText: Color,
    val scheduleListEmptyText: Color,
    
    // 滑动删除
    val scheduleSwipeDeleteBackground: Color,
    val scheduleSwipeDeleteIcon: Color,
    
    // ==================== 文字层次 ====================
    
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
)

// ==================== 浅色主题 - 简约精致风格 ====================

val LightCustomColors = CustomColors(
    // 通用基础颜色
    primary = Blue300,                          // 柔和蓝灰
    onPrimary = White,
    primaryContainer = Blue50,
    onPrimaryContainer = Blue600,
    
    surface = White,
    onSurface = Slate700,
    surfaceVariant = Slate50,
    onSurfaceVariant = Slate500,
    
    background = Slate50,
    onBackground = Slate700,
    
    outline = Slate200,
    outlineVariant = Slate150,
    
    error = Rose400,
    onError = White,
    errorContainer = Rose50,
    onErrorContainer = Rose600,
    
    success = Green300,
    onSuccess = White,
    
    warning = Amber300,
    onWarning = Slate800,
    
    scrim = ScrimLight,
    
    disabled = Slate200,
    onDisabled = Slate400,
    
    inputText = Slate700,
    inputLabel = Slate500,
    inputIcon = Slate400,
    
    buttonPrimaryBackground = AccentBlue,       // 点缀蓝按钮
    buttonPrimaryText = White,
    buttonSecondaryBackground = Slate100,
    buttonSecondaryText = Slate600,
    
    // 日历颜色
    calendarSelectedBackground = AccentBlue,    // 选中用点缀蓝
    calendarSelectedText = White,
    
    calendarTodayBackground = AccentBlueLight,  // 今天用浅点缀蓝
    calendarTodayBorder = AccentBlue,
    calendarTodayText = AccentBlue,
    
    calendarNormalText = Slate700,
    calendarOtherMonthText = Slate300,
    calendarWeekendText = Slate400,
    
    calendarHeaderText = Slate700,
    calendarWeekLabelText = Slate500,
    calendarNavigationIcon = Slate500,
    
    calendarBackground = White,
    calendarDivider = Slate150,
    
    calendarScheduleDot = AccentBlue,           // 待办点用点缀蓝
    
    calendarDragHandle = Slate300,
    
    // 日程颜色
    scheduleUncompletedText = Slate700,
    scheduleCompletedText = Slate400,
    scheduleDateText = Slate500,
    
    scheduleCheckboxChecked = AccentBlue,
    scheduleCheckboxUnchecked = Slate300,
    scheduleCheckboxCheckmark = White,

    scheduleCardBackground = White,
    
    scheduleUrgent = Rose400,
    scheduleImportant = Amber300,
    scheduleNormal = Green300,
    
    scheduleListBackground = Slate50,
    scheduleListTitleText = Slate700,
    scheduleListEmptyText = Slate400,
    
    scheduleSwipeDeleteBackground = Rose400,
    scheduleSwipeDeleteIcon = White,
    
    // 文字层次
    textPrimary = Slate700,
    textSecondary = Slate500,
    textTertiary = Slate400,
    textDisabled = Slate300,
)

// ==================== 深色主题 - 简约精致风格 ====================

val DarkCustomColors = CustomColors(
    // 通用基础颜色
    primary = Blue200,
    onPrimary = Blue700,
    primaryContainer = Blue600,
    onPrimaryContainer = Blue100,
    
    surface = Dark200,
    onSurface = Dark800,
    surfaceVariant = Dark300,
    onSurfaceVariant = Dark700,
    
    background = Dark100,
    onBackground = Dark800,
    
    outline = Dark500,
    outlineVariant = Dark400,
    
    error = Rose300,
    onError = Rose700,
    errorContainer = Rose700,
    onErrorContainer = Rose100,
    
    success = Green300,
    onSuccess = Green700,
    
    warning = Amber300,
    onWarning = Amber700,
    
    scrim = ScrimDark,
    
    disabled = Dark500,
    onDisabled = Dark600,
    
    inputText = Dark800,
    inputLabel = Dark700,
    inputIcon = Dark600,
    
    buttonPrimaryBackground = AccentBlue,
    buttonPrimaryText = White,
    buttonSecondaryBackground = Dark400,
    buttonSecondaryText = Dark800,
    
    // 日历颜色
    calendarSelectedBackground = AccentBlue,
    calendarSelectedText = White,
    
    calendarTodayBackground = Blue600,
    calendarTodayBorder = AccentBlue,
    calendarTodayText = AccentBlue,
    
    calendarNormalText = Dark800,
    calendarOtherMonthText = Dark600,
    calendarWeekendText = Dark700,
    
    calendarHeaderText = Dark800,
    calendarWeekLabelText = Dark700,
    calendarNavigationIcon = Dark700,
    
    calendarBackground = Dark200,
    calendarDivider = Dark400,
    
    calendarScheduleDot = AccentBlue,
    
    calendarDragHandle = Dark600,
    
    // 日程颜色
    scheduleUncompletedText = Dark800,
    scheduleCompletedText = Dark600,
    scheduleDateText = Dark700,
    
    scheduleCheckboxChecked = AccentBlue,
    scheduleCheckboxUnchecked = Dark500,
    scheduleCheckboxCheckmark = White,

    scheduleCardBackground = Dark300,
    
    scheduleUrgent = Rose300,
    scheduleImportant = Amber300,
    scheduleNormal = Green300,
    
    scheduleListBackground = Dark200,
    scheduleListTitleText = Dark800,
    scheduleListEmptyText = Dark600,
    
    scheduleSwipeDeleteBackground = Rose400,
    scheduleSwipeDeleteIcon = White,
    
    // 文字层次
    textPrimary = Dark800,
    textSecondary = Dark700,
    textTertiary = Dark600,
    textDisabled = Dark500,
)

// ==================== CompositionLocal ====================

val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }

val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current
