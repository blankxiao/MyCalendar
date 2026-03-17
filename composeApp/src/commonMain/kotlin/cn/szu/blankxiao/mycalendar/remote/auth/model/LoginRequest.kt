package cn.szu.blankxiao.mycalendar.remote.auth.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginRequest (
    @SerialName(value = "email")
    val email: kotlin.String,
    @SerialName(value = "loginType")
    val loginType: kotlin.String,
    @SerialName(value = "code")
    val code: kotlin.String? = null,
    @SerialName(value = "password")
    val password: kotlin.String? = null
)
