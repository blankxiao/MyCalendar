package cn.szu.blankxiao.mycalendar.remote

import io.ktor.client.plugins.api.createClientPlugin

/**
 * Ktor 插件：为每个请求自动添加 Authorization header
 * 使用 suspend 获取 token，支持 DataStore 等异步存储
 */
val AuthTokenPlugin = createClientPlugin("AuthToken", ::AuthTokenPluginConfig) {
    val tokenProvider = pluginConfig.tokenProvider
    onRequest { request, _ ->
        val token = tokenProvider()
        if (!token.isNullOrEmpty()) {
            request.headers.append("Authorization", token)
        }
    }
}

class AuthTokenPluginConfig {
    var tokenProvider: suspend () -> String? = { null }
}
