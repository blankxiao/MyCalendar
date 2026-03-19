package cn.szu.blankxiao.mycalendar.ui

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
actual annotation class Preview(actual val showBackground: Boolean = false)
