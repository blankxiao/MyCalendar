package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.data.repository.CodeType
import cn.szu.blankxiao.mycalendar.ui.component.auth.LoginFormContent
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthUiState
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * PC 端登录界面（使用共享 LoginFormContent）
 */
@Composable
fun DesktopAuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit = {},
    onSkipLogin: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var useCodeLogin by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val codeCountdown by viewModel.codeCountdown.collectAsState()

    val isLoading = uiState is AuthUiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MyCalendar",
            style = MaterialTheme.typography.headlineLarge
        )

        Column(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (val state = uiState) {
                is AuthUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {}
            }

            LoginFormContent(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                code = code,
                onCodeChange = { code = it },
                useCodeLogin = useCodeLogin,
                onToggleLoginMode = { useCodeLogin = !useCodeLogin },
                passwordVisible = passwordVisible,
                onTogglePasswordVisible = { passwordVisible = !passwordVisible },
                isLoading = isLoading,
                onLoginClick = {
                    if (useCodeLogin) {
                        viewModel.loginWithCode(email, code)
                    } else {
                        viewModel.loginWithPassword(email, password)
                    }
                },
                onRegisterClick = { /* PC 端暂不支持注册，可后续扩展 */ },
                onSendCodeClick = { viewModel.sendCode(email, CodeType.LOGIN) },
                codeCountdown = codeCountdown,
                onSkipLogin = onSkipLogin
            )
        }
    }
}
