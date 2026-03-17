package cn.szu.blankxiao.mycalendar.data.local.datastore

/**
 * 用户信息（本地存储）
 */
data class UserInfo(
    val userId: Long,
    val username: String,
    val email: String,
    val token: String
)
