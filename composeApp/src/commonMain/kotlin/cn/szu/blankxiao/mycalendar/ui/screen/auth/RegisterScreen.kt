package cn.szu.blankxiao.mycalendar.ui.screen.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.data.repository.CodeType
import cn.szu.blankxiao.mycalendar.ui.component.auth.RegisterFormContent
import cn.szu.blankxiao.mycalendar.viewmodel.AuthViewModel
import cn.szu.blankxiao.mycalendar.viewmodel.AuthUiState
import org.koin.compose.viewmodel.koinViewModel

/**
 * 注册页面（复用 RegisterFormContent）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val codeCountdown by viewModel.codeCountdown.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                onRegisterSuccess()
                viewModel.resetState()
            }
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
                viewModel.resetState()
            }
            is AuthUiState.CodeSent -> {
                snackbarHostState.showSnackbar("验证码已发送")
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("注册") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
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
            isLoading = uiState is AuthUiState.Loading,
            onRegisterClick = { viewModel.register(email, code, username, password) },
            onBackToLoginClick = onNavigateToLogin,
            onSendCodeClick = { viewModel.sendCode(email, CodeType.REGISTER) },
            codeCountdown = codeCountdown,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        )
    }
}
