package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 颜色定义库 - 简约精致风格（Design Tokens - Primitive）
 * 
 * 设计理念：
 * - 简约：低饱和度，柔和舒适
 * - 精致：精心挑选的色值，和谐统一
 * - 细节：丰富的灰度层次，微妙的色彩变化
 */

// ========================================
// 主色系列（Primary Blue）- 柔和的蓝灰色调
// ========================================

val Blue50 = Color(0xFFF0F4F8)               // 极浅蓝灰 - 悬浮背景
val Blue100 = Color(0xFFD9E2EC)              // 淡蓝灰 - 选中背景
val Blue200 = Color(0xFFBCCCDC)              // 浅蓝灰
val Blue300 = Color(0xFF627D98)              // 蓝灰色 - 主色调（低饱和）
val Blue400 = Color(0xFF486581)              // 深蓝灰
val Blue500 = Color(0xFF334E68)              // 更深蓝灰
val Blue600 = Color(0xFF243B53)              // 深蓝
val Blue700 = Color(0xFF102A43)              // 最深蓝

// 点缀蓝 - 用于强调元素
val AccentBlue = Color(0xFF5C7CFA)           // 明亮但不刺眼的蓝
val AccentBlueLight = Color(0xFFDBE4FF)      // 浅点缀蓝

// ========================================
// 成功色系列（Green）- 清新的薄荷色调
// ========================================

val Green50 = Color(0xFFE6F7F1)              // 极浅绿
val Green100 = Color(0xFFC1EBD9)             // 淡绿
val Green200 = Color(0xFF8DD4B8)             // 浅绿
val Green300 = Color(0xFF4DB890)             // 薄荷绿 - 成功色
val Green400 = Color(0xFF38A077)             // 深绿
val Green500 = Color(0xFF2D8563)             // 更深绿
val Green600 = Color(0xFF216A4F)             // 墨绿
val Green700 = Color(0xFF155239)             // 最深绿

// ========================================
// 警告色系列（Amber）- 温暖的琥珀色调
// ========================================

val Amber50 = Color(0xFFFFF8E6)              // 极浅琥珀
val Amber100 = Color(0xFFFFECC7)             // 淡琥珀
val Amber200 = Color(0xFFFFD97A)             // 浅琥珀
val Amber300 = Color(0xFFF5B942)             // 琥珀色 - 警告色
val Amber400 = Color(0xFFE09D18)             // 深琥珀
val Amber500 = Color(0xFFBB7F0A)             // 更深琥珀
val Amber600 = Color(0xFF946300)             // 棕琥珀
val Amber700 = Color(0xFF6B4800)             // 最深琥珀

// ========================================
// 错误色系列（Rose）- 柔和的玫瑰色调
// ========================================

val Rose50 = Color(0xFFFEF2F2)               // 极浅玫瑰
val Rose100 = Color(0xFFFEE2E2)              // 淡玫瑰
val Rose200 = Color(0xFFFECACA)              // 浅玫瑰
val Rose300 = Color(0xFFF87171)              // 玫瑰红 - 错误色
val Rose400 = Color(0xFFEF4444)              // 深玫瑰
val Rose500 = Color(0xFFDC2626)              // 更深玫瑰
val Rose600 = Color(0xFFB91C1C)              // 暗红
val Rose700 = Color(0xFF991B1B)              // 最深红

// ========================================
// 灰色系列（Slate）- 精致的蓝灰色调
// 浅色主题用（从浅到深）
// ========================================

val Slate50 = Color(0xFFF8FAFC)              // 几乎白 - 页面背景
val Slate100 = Color(0xFFF1F5F9)             // 极浅灰 - 卡片悬浮
val Slate150 = Color(0xFFE2E8F0)             // 浅灰 - 分隔线
val Slate200 = Color(0xFFCBD5E1)             // 边框灰
val Slate300 = Color(0xFF94A3B8)             // 占位符/禁用
val Slate400 = Color(0xFF64748B)             // 次要文字
val Slate500 = Color(0xFF475569)             // 中等文字
val Slate600 = Color(0xFF334155)             // 主要文字
val Slate700 = Color(0xFF1E293B)             // 标题文字
val Slate800 = Color(0xFF0F172A)             // 最深 - 强调

// ========================================
// 深色主题灰色系列
// ========================================

val Dark50 = Color(0xFF0A0A0B)               // 最深 - 背景
val Dark100 = Color(0xFF111113)              // 深背景
val Dark200 = Color(0xFF18181B)              // 卡片背景
val Dark300 = Color(0xFF27272A)              // 悬浮背景
val Dark400 = Color(0xFF3F3F46)              // 边框
val Dark500 = Color(0xFF52525B)              // 分隔线
val Dark600 = Color(0xFF71717A)              // 禁用/占位
val Dark700 = Color(0xFFA1A1AA)              // 次要文字
val Dark800 = Color(0xFFD4D4D8)              // 主要文字
val Dark900 = Color(0xFFFAFAFA)              // 最亮 - 强调

// ========================================
// 特殊颜色
// ========================================

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Transparent = Color(0x00000000)

// 遮罩层 - 更柔和
val ScrimLight = Color(0x40000000)           // 25% 黑
val ScrimDark = Color(0x60000000)            // 38% 黑

// ========================================
// 兼容旧代码（已废弃）
// ========================================

@Deprecated("Use Blue300 instead", ReplaceWith("Blue300"))
val SkyBlue = Blue300

@Deprecated("Use Blue50 instead", ReplaceWith("Blue50"))
val LightSkyBlue = Blue50

@Deprecated("Use Blue400 instead", ReplaceWith("Blue400"))
val DeepSkyBlue = Blue400

@Deprecated("Use Blue200 instead", ReplaceWith("Blue200"))
val BrightBlue = Blue200

@Deprecated("Use Blue600 instead", ReplaceWith("Blue600"))
val DarkBlue = Blue600

@Deprecated("Use Green300 instead", ReplaceWith("Green300"))
val MintGreen = Green300

@Deprecated("Use Green50 instead", ReplaceWith("Green50"))
val PaleMintGreen = Green50

@Deprecated("Use Amber300 instead", ReplaceWith("Amber300"))
val WarmOrange = Amber300

@Deprecated("Use Rose400 instead", ReplaceWith("Rose400"))
val SoftRed = Rose400

@Deprecated("Use Slate50 instead", ReplaceWith("Slate50"))
val VeryLightGray = Slate50

@Deprecated("Use Slate50 instead", ReplaceWith("Slate50"))
val SoftBackground = Slate50

@Deprecated("Use White instead", ReplaceWith("White"))
val PureWhite = White

@Deprecated("Use Slate100 instead", ReplaceWith("Slate100"))
val WhisperGray = Slate100

@Deprecated("Use Slate700 instead", ReplaceWith("Slate700"))
val CharcoalGray = Slate700

@Deprecated("Use Slate400 instead", ReplaceWith("Slate400"))
val MediumGray = Slate400

@Deprecated("Use Slate300 instead", ReplaceWith("Slate300"))
val SilverGray = Slate300

@Deprecated("Use Slate200 instead", ReplaceWith("Slate200"))
val LightBorderGray = Slate200

@Deprecated("Use Slate150 instead", ReplaceWith("Slate150"))
val PaleBorderGray = Slate150

@Deprecated("Use Dark100 instead", ReplaceWith("Dark100"))
val DeepCharcoal = Dark100

@Deprecated("Use Dark200 instead", ReplaceWith("Dark200"))
val DarkCharcoal = Dark200

@Deprecated("Use Dark300 instead", ReplaceWith("Dark300"))
val SlateGray = Dark300

@Deprecated("Use Dark800 instead", ReplaceWith("Dark800"))
val LightGray = Dark800

@Deprecated("Use Dark700 instead", ReplaceWith("Dark700"))
val NeutralGray = Dark700

@Deprecated("Use Dark600 instead", ReplaceWith("Dark600"))
val DimGray = Dark600

@Deprecated("Use Dark500 instead", ReplaceWith("Dark500"))
val DarkBorderGray = Dark500

@Deprecated("Use Slate700 instead", ReplaceWith("Slate700"))
val DeepBorderGray = Slate700

@Deprecated("Use Rose50 instead", ReplaceWith("Rose50"))
val PaleRed = Rose50

@Deprecated("Use Rose600 instead", ReplaceWith("Rose600"))
val DeepRed = Rose600

@Deprecated("Use Rose100 instead", ReplaceWith("Rose100"))
val LightRed = Rose100

@Deprecated("Use White instead", ReplaceWith("White"))
val FixedWhite = White

@Deprecated("Use Black instead", ReplaceWith("Black"))
val FixedBlack = Black

@Deprecated("Use Green400 instead", ReplaceWith("Green400"))
val LimeGreen = Green400

@Deprecated("Use Amber200 instead", ReplaceWith("Amber200"))
val BrightOrange = Amber200

@Deprecated("Use Blue100 instead", ReplaceWith("Blue100"))
val PaleBlue = Blue100

@Deprecated("Use Green600 instead", ReplaceWith("Green600"))
val DarkMintGreen = Green600

@Deprecated("Use Green200 instead", ReplaceWith("Green200"))
val LightLimeGreen = Green200

@Deprecated("Use Green500 instead", ReplaceWith("Green500"))
val ForestGreen = Green500

@Deprecated("Use Amber50 instead", ReplaceWith("Amber50"))
val PaleOrange = Amber50

@Deprecated("Use Amber600 instead", ReplaceWith("Amber600"))
val DeepOrange = Amber600

@Deprecated("Use Amber400 instead", ReplaceWith("Amber400"))
val VividOrange = Amber400

@Deprecated("Use Amber100 instead", ReplaceWith("Amber100"))
val LightOrange = Amber100

// 旧的Gray系列兼容
@Deprecated("Use Slate50", ReplaceWith("Slate50"))
val Gray50 = Slate50
@Deprecated("Use Slate100", ReplaceWith("Slate100"))
val Gray100 = Slate100
@Deprecated("Use Slate150", ReplaceWith("Slate150"))
val Gray150 = Slate150
@Deprecated("Use Slate200", ReplaceWith("Slate200"))
val Gray200 = Slate200
@Deprecated("Use Slate300", ReplaceWith("Slate300"))
val Gray300 = Slate300
@Deprecated("Use Slate400", ReplaceWith("Slate400"))
val Gray400 = Slate400
@Deprecated("Use Slate500", ReplaceWith("Slate500"))
val Gray500 = Slate500
@Deprecated("Use Slate600", ReplaceWith("Slate600"))
val Gray600 = Slate600
@Deprecated("Use Slate700", ReplaceWith("Slate700"))
val Gray700 = Slate700
@Deprecated("Use Slate800", ReplaceWith("Slate800"))
val Gray900 = Slate800

@Deprecated("Use Dark100", ReplaceWith("Dark100"))
val GrayDark100 = Dark100
@Deprecated("Use Dark200", ReplaceWith("Dark200"))
val GrayDark200 = Dark200
@Deprecated("Use Dark300", ReplaceWith("Dark300"))
val GrayDark300 = Dark300
@Deprecated("Use Dark400", ReplaceWith("Dark400"))
val GrayDark400 = Dark400
@Deprecated("Use Dark500", ReplaceWith("Dark500"))
val GrayDark500 = Dark500
@Deprecated("Use Dark600", ReplaceWith("Dark600"))
val GrayDark600 = Dark600
@Deprecated("Use Dark700", ReplaceWith("Dark700"))
val GrayDark700 = Dark700
@Deprecated("Use Dark800", ReplaceWith("Dark800"))
val GrayDark800 = Dark800
@Deprecated("Use Dark900", ReplaceWith("Dark900"))
val GrayDark900 = Dark900

// Red系列兼容
@Deprecated("Use Rose50", ReplaceWith("Rose50"))
val Red50 = Rose50
@Deprecated("Use Rose100", ReplaceWith("Rose100"))
val Red100 = Rose100
@Deprecated("Use Rose300", ReplaceWith("Rose300"))
val Red300 = Rose300
@Deprecated("Use Rose400", ReplaceWith("Rose400"))
val Red400 = Rose400
@Deprecated("Use Rose500", ReplaceWith("Rose500"))
val Red500 = Rose500
@Deprecated("Use Rose600", ReplaceWith("Rose600"))
val Red600 = Rose600
@Deprecated("Use Rose700", ReplaceWith("Rose700"))
val Red700 = Rose700

// Orange系列兼容
@Deprecated("Use Amber300", ReplaceWith("Amber300"))
val Orange300 = Amber300
@Deprecated("Use Amber500", ReplaceWith("Amber500"))
val Orange500 = Amber500
