package cn.szu.blankxiao.mycalendar.remote.auth.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RegisterRequest (
    @SerialName(value = "email")
    val email: kotlin.String,
    @SerialName(value = "code")
    val code: kotlin.String,
    @SerialName(value = "username")
    val username: kotlin.String,
    @SerialName(value = "password")
    val password: kotlin.String
)
