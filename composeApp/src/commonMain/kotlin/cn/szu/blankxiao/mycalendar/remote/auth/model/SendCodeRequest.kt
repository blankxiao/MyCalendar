package cn.szu.blankxiao.mycalendar.remote.auth.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SendCodeRequest (
    @SerialName(value = "email")
    val email: kotlin.String,
    @SerialName(value = "type")
    val type: kotlin.String
)
