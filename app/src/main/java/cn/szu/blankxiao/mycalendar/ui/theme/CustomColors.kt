package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 业务语义化颜色系统
 */

@Immutable
data class CustomColors(
    // ==================== 日历相关颜色 ====================
    
    // 选中状态
    val calendarSelectedBackground: Color,      // 选中日期的背景色
    val calendarSelectedText: Color,            // 选中日期的文字色
    
    // 今天标记
    val calendarTodayBackground: Color,         // 今天日期的背景色
    val calendarTodayBorder: Color,             // 今天日期的边框色
    val calendarTodayText: Color,               // 今天日期的文字色
    
    // 普通日期
    val calendarNormalText: Color,              // 普通日期的文字色
    val calendarOtherMonthText: Color,          // 非当前月日期的文字色
    val calendarWeekendText: Color,             // 周末日期的文字色
    
    // 日历标题和导航
    val calendarHeaderText: Color,              // 月份标题文字
    val calendarWeekLabelText: Color,           // 星期标题文字
    val calendarNavigationIcon: Color,          // 导航箭头颜色
    
    // 日历背景和分割线
    val calendarBackground: Color,              // 日历整体背景
    val calendarDivider: Color,                 // 分割线颜色
    
    // ==================== TodoRelate 相关颜色 ====================
    
    // TodoRelate 项状态
    val todoUncompletedText: Color,             // 未完成任务的文字色
    val todoCompletedText: Color,               // 已完成任务的文字色
    val todoDateText: Color,                    // 任务日期文字色
    
    // TodoRelate 复选框
    val todoCheckboxChecked: Color,             // 复选框选中颜色
    val todoCheckboxUnchecked: Color,           // 复选框未选中颜色
    val todoCheckboxCheckmark: Color,           // 复选框未选中颜色

    // TodoRelate 卡片
    val todoCardBackground: Color,              // TodoRelate 卡片背景
    val todoCardElevation: Color,               // TodoRelate 卡片阴影（可选）
    
    // TodoRelate 优先级
    val todoUrgent: Color,                      // 紧急任务标记
    val todoImportant: Color,                   // 重要任务标记
    val todoNormal: Color,                      // 普通任务标记

    // TodoList 整体
    val todoListBackground: Color,              // TodoList 整体背景
    val todoListTitleText: Color,               // TodoList 标题文字
    val todoListEmptyText: Color,               // 空状态提示文字

    // ==================== 通用 UI 颜色 ====================
    
    // 按钮状态
    val buttonPrimaryBackground: Color,         // 主要按钮背景
    val buttonPrimaryText: Color,               // 主要按钮文字
    val buttonSecondaryBackground: Color,       // 次要按钮背景
    val buttonSecondaryText: Color,             // 次要按钮文字
    
    // 底部导航/抽屉
    val bottomSheetBackground: Color,           // 底部抽屉背景
    val bottomSheetHandle: Color,               // 抽屉拖拽条颜色
    
    // 通用状态
    val successColor: Color,                    // 成功状态
    val warningColor: Color,                    // 警告状态
    val errorColor: Color,                      // 错误状态
)

// ==================== 浅色主题自定义颜色 ====================
val LightCustomColors = CustomColors(
    // 日历 - 选中状态（最醒目：深色背景 + 白色文字）
    calendarSelectedBackground = SkyBlue,           // 天蓝色背景
    calendarSelectedText = FixedWhite,              // 白色文字（高对比度）
    
    // 日历 - 今天标记
    // TODO 显示问题
    calendarTodayBackground = LightSkyBlue,         // 极浅蓝背景
    calendarTodayBorder = SkyBlue,                  // 天蓝色边框（突出）
    calendarTodayText = DeepSkyBlue,                // 深蓝色文字（高对比度）
    
    // 日历 - 普通日期（标准：透明背景 + 深色文字）
    calendarNormalText = CharcoalGray,              // 炭灰色（清晰可读）
    calendarOtherMonthText = SilverGray,            // 银灰色（明显弱化）
    calendarWeekendText = MediumGray,               // 中灰色（周末）
    
    // 日历 - 标题和导航
    calendarHeaderText = CharcoalGray,              // 炭灰色（标题）
    calendarWeekLabelText = MediumGray,             // 中灰色（星期标签）
    calendarNavigationIcon = SkyBlue,               // 天蓝色（导航箭头）
    
    // 日历 - 背景和分割线
    calendarBackground = PureWhite,                 // 纯白背景
    calendarDivider = PaleBorderGray,               // 极浅灰分割线
    
    // TodoRelate - 任务状态
    todoUncompletedText = CharcoalGray,             // 炭灰色（醒目）
    todoCompletedText = MediumGray,                 // 中灰色（弱化）
    todoDateText = MediumGray,                      // 中灰色（日期）
    
    // TodoRelate - 复选框
    todoCheckboxChecked = SkyBlue,                  // 天蓝色（选中）
    todoCheckboxUnchecked = LightBorderGray,        // 浅边框灰（未选中）
    todoCheckboxCheckmark = FixedWhite,           // 白色

    // TodoRelate - 卡片
    todoCardBackground = PureWhite,                 // 纯白背景
    todoCardElevation = SilverGray,                 // 银灰色（轻阴影）
    
    // TodoRelate - 优先级
    todoUrgent = SoftRed,                           // 柔和红（紧急）
    todoImportant = WarmOrange,                     // 暖橙色（重要）
    todoNormal = MintGreen,                         // 薄荷绿（普通）
    
    // TodoList - 整体
    todoListBackground = SoftBackground,            // 柔和浅灰背景（区别于卡片的纯白）
    todoListTitleText = CharcoalGray,               // 炭灰色（标题）
    todoListEmptyText = MediumGray,                 // 中灰色（空状态提示）
    
    // 通用 - 按钮
    buttonPrimaryBackground = SkyBlue,              // 天蓝色（主按钮）
    buttonPrimaryText = FixedWhite,                 // 白色文字
    buttonSecondaryBackground = WhisperGray,        // 轻灰色（次按钮）
    buttonSecondaryText = MediumGray,               // 中灰色文字
    
    // 通用 - 底部抽屉
    bottomSheetBackground = PureWhite,              // 纯白背景
    bottomSheetHandle = SilverGray,                 // 银灰色（拖拽条）
    
    // 通用 - 状态颜色
    successColor = MintGreen,                       // 薄荷绿（成功）
    warningColor = WarmOrange,                      // 暖橙色（警告）
    errorColor = SoftRed,                           // 柔和红（错误）
)

// ==================== 深色主题配色方案 ====================
val DarkCustomColors = CustomColors(
    // 日历 - 选中状态（最醒目：亮色背景 + 深色文字）
    calendarSelectedBackground = BrightBlue,        // 明亮蓝背景
    calendarSelectedText = DeepCharcoal,            // 深炭色文字（高对比度）
    
    // 日历 - 今天标记（次醒目：深色背景 + 亮色文字 + 边框）
    calendarTodayBackground = DarkBlue,             // 深蓝背景
    calendarTodayBorder = BrightBlue,               // 明亮蓝边框（突出）
    calendarTodayText = LightGray,                  // 浅灰文字（高对比度）
    
    // 日历 - 普通日期（标准：透明背景 + 浅色文字）
    calendarNormalText = LightGray,                 // 浅灰色（清晰可读）
    calendarOtherMonthText = DimGray,               // 暗灰色（明显弱化）
    calendarWeekendText = NeutralGray,              // 中性灰（周末）
    
    // 日历 - 标题和导航
    calendarHeaderText = LightGray,                 // 浅灰色（标题）
    calendarWeekLabelText = NeutralGray,            // 中性灰（星期标签）
    calendarNavigationIcon = BrightBlue,            // 明亮蓝（导航箭头）
    
    // 日历 - 背景和分割线
    calendarBackground = DarkCharcoal,              // 深灰背景
    calendarDivider = DeepBorderGray,               // 深边框灰分割线
    
    // TodoRelate - 任务状态
    todoUncompletedText = LightGray,                // 浅灰色（醒目）
    todoCompletedText = NeutralGray,                // 中性灰（弱化）
    todoDateText = NeutralGray,                     // 中性灰（日期）
    
    // TodoRelate - 复选框
    todoCheckboxChecked = BrightBlue,               // 明亮蓝（选中）
    todoCheckboxUnchecked = DarkBorderGray,         // 深边框灰（未选中）
    todoCheckboxCheckmark = FixedBlack,           // 白色

    // TodoRelate - 卡片
    todoCardBackground = DarkCharcoal,              // 深灰背景
    todoCardElevation = DeepCharcoal,               // 深炭色（中等阴影）
    
    // TodoRelate - 优先级
    todoUrgent = SoftRed,                           // 柔和红（紧急）
    todoImportant = BrightOrange,                   // 明亮橙（重要）
    todoNormal = LimeGreen,                         // 柠檬绿（普通）
    
    // TodoList - 整体
    todoListBackground = SlateGray,                 // 石板灰背景（区别于卡片的深灰）
    todoListTitleText = LightGray,                  // 浅灰色（标题）
    todoListEmptyText = NeutralGray,                // 中性灰（空状态提示）
    
    // 通用 - 按钮
    buttonPrimaryBackground = BrightBlue,           // 明亮蓝（主按钮）
    buttonPrimaryText = DeepCharcoal,               // 深炭色文字
    buttonSecondaryBackground = SlateGray,          // 石板灰（次按钮）
    buttonSecondaryText = NeutralGray,              // 中性灰文字
    
    // 通用 - 底部抽屉
    bottomSheetBackground = DarkCharcoal,           // 深灰背景
    bottomSheetHandle = DimGray,                    // 暗灰色（拖拽条）
    
    // 通用 - 状态颜色
    successColor = LimeGreen,                       // 柠檬绿（成功）
    warningColor = BrightOrange,                    // 明亮橙（警告）
    errorColor = SoftRed,                           // 柔和红（错误）
)

// ==================== CompositionLocal ====================

/**
 * 提供自定义颜色的 CompositionLocal
 */
val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }

/**
 * MaterialTheme 扩展属性，便捷访问自定义颜色
 */
val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current
