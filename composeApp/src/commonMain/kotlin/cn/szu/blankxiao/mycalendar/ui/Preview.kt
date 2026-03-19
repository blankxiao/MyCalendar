package cn.szu.blankxiao.mycalendar.ui

/**
 * 跨平台 Preview 注解
 * Android: 使用 Compose UI Tooling 的 @Preview
 * iOS/JVM: 空实现，编译通过但不渲染预览
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
expect annotation class Preview(val showBackground: Boolean = false)
