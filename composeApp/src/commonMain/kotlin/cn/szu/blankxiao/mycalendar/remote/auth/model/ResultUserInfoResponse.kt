package cn.szu.blankxiao.mycalendar.remote.auth.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ResultUserInfoResponse (
    @SerialName(value = "success")
    val success: kotlin.Boolean? = null,
    @SerialName(value = "code")
    val code: kotlin.Int? = null,
    @SerialName(value = "info")
    val info: kotlin.String? = null,
    @SerialName(value = "data")
    val `data`: UserInfoResponse? = null
)
