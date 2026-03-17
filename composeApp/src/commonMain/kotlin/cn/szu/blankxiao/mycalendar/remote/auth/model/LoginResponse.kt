package cn.szu.blankxiao.mycalendar.remote.auth.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginResponse (
    @SerialName(value = "userId")
    val userId: kotlin.Long? = null,
    @SerialName(value = "username")
    val username: kotlin.String? = null,
    @SerialName(value = "email")
    val email: kotlin.String? = null,
    @SerialName(value = "token")
    val token: kotlin.String? = null
)
