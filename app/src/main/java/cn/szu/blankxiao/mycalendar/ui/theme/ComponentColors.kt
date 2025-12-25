package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

/**
 * 组件颜色配置
 * 提供统一的组件颜色，避免重复定义
 */

/**
 * 获取统一的 OutlinedTextField 颜色配置
 */
@Composable
fun outlinedTextFieldColors(): TextFieldColors {
    val customColors = MaterialTheme.customColors
    return TextFieldDefaults.colors(
        // 正常状态
        unfocusedContainerColor = customColors.surface,
        focusedContainerColor = customColors.surface,
        unfocusedTextColor = customColors.inputText,
        focusedTextColor = customColors.inputText,
        unfocusedLabelColor = customColors.inputLabel,
        focusedLabelColor = customColors.buttonPrimaryBackground,
        unfocusedIndicatorColor = customColors.outline,
        focusedIndicatorColor = customColors.buttonPrimaryBackground,
        cursorColor = customColors.buttonPrimaryBackground,
        unfocusedTrailingIconColor = customColors.inputIcon,
        focusedTrailingIconColor = customColors.buttonPrimaryBackground,
        unfocusedLeadingIconColor = customColors.inputIcon,
        focusedLeadingIconColor = customColors.buttonPrimaryBackground,
        unfocusedPlaceholderColor = customColors.textTertiary,
        focusedPlaceholderColor = customColors.textTertiary,
        // 禁用状态
        disabledContainerColor = customColors.surfaceVariant,
        disabledTextColor = customColors.inputText,
        disabledLabelColor = customColors.inputLabel,
        disabledIndicatorColor = customColors.outline,
        disabledTrailingIconColor = customColors.inputIcon,
        disabledLeadingIconColor = customColors.inputIcon,
        disabledPlaceholderColor = customColors.textDisabled,
        // 错误状态
        errorContainerColor = customColors.surface,
        errorTextColor = customColors.inputText,
        errorLabelColor = customColors.error,
        errorIndicatorColor = customColors.error,
        errorTrailingIconColor = customColors.error,
        errorLeadingIconColor = customColors.error,
        errorCursorColor = customColors.error,
        errorPlaceholderColor = customColors.textTertiary
    )
}

