package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import cn.szu.blankxiao.mycalendar.ui.component.auth.RegisterFormContent
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthUiState
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * PC 端登录/注册界面（使用共享 LoginFormContent、RegisterFormContent）
 * 标题栏由 DesktopAppTitleBar 统一提供
 */
@Composable
fun DesktopAuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit = {},
    onSkipLogin: () -> Unit = {}
) {
    var showRegister by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var useCodeLogin by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val codeCountdown by viewModel.codeCountdown.collectAsState()

    val isLoading = uiState is AuthUiState.Loading

    // 注册成功/验证码已发送后重置 UI 状态
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success, is AuthUiState.CodeSent -> viewModel.resetState()
            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 360.dp),
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
                is AuthUiState.CodeSent -> {
                    Text(
                        text = "验证码已发送",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {}
            }

            if (showRegister) {
                RegisterFormContent(
                    email = email,
                    onEmailChange = { email = it },
                    code = code,
                    onCodeChange = { code = it },
                    username = username,
                    onUsernameChange = { username = it },
                    password = password,
                    onPasswordChange = { password = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    passwordVisible = passwordVisible,
                    onTogglePasswordVisible = { passwordVisible = !passwordVisible },
                    isLoading = isLoading,
                    onRegisterClick = { viewModel.register(email, code, username, password) },
                    onBackToLoginClick = { showRegister = false },
                    onSendCodeClick = { viewModel.sendCode(email, CodeType.REGISTER) },
                    codeCountdown = codeCountdown
                )
            } else {
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
                    onRegisterClick = { showRegister = true },
                    onSendCodeClick = { viewModel.sendCode(email, CodeType.LOGIN) },
                    codeCountdown = codeCountdown,
                    onSkipLogin = onSkipLogin
                )
            }
        }
    }
}
