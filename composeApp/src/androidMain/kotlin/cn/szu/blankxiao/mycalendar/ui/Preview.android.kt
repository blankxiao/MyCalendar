package cn.szu.blankxiao.mycalendar.ui

/**
 * Android 平台 Preview 的 actual 实现，与 expect 保持单参数签名一致。
 * 若需在 Android Studio 中显示布局预览，可在仅 Android 的预览函数上额外使用
 * androidx.compose.ui.tooling.preview.Preview。
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
actual annotation class Preview(actual val showBackground: Boolean = false)
