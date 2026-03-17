/**
 * 用户信息响应（KMP 共享，createTime 使用 String 兼容）
 */

package cn.szu.blankxiao.mycalendar.remote.auth.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * 用户信息响应
 *
 * @param userId 用户ID
 * @param username 用户名
 * @param email 邮箱
 * @param avatar 头像
 * @param status 状态: 0-禁用, 1-正常
 * @param createTime 创建时间（ISO 8601 字符串）
 */
@Serializable
data class UserInfoResponse (
    @SerialName(value = "userId")
    val userId: kotlin.Long? = null,
    @SerialName(value = "username")
    val username: kotlin.String? = null,
    @SerialName(value = "email")
    val email: kotlin.String? = null,
    @SerialName(value = "avatar")
    val avatar: kotlin.String? = null,
    @SerialName(value = "status")
    val status: kotlin.Int? = null,
    @SerialName(value = "createTime")
    val createTime: kotlin.String? = null
)
