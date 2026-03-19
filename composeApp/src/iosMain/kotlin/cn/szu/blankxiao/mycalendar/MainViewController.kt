package cn.szu.blankxiao.mycalendar

import androidx.compose.ui.window.ComposeUIViewController
import cn.szu.blankxiao.mycalendar.ui.IosAppContent

/**
 * iOS 入口 ViewController
 * 在 Swift 中通过 Main_iosKt.MainViewController() 调用
 */
fun MainViewController() = ComposeUIViewController { IosAppContent() }
