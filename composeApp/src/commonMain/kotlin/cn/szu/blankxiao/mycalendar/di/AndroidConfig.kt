package cn.szu.blankxiao.mycalendar.di

/**
 * Android 构建配置，由 androidApp 模块通过 Koin 注入
 */
data class AndroidConfig(
    val baseUrl: String,
    val enableLogging: Boolean
)
