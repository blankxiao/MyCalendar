package cn.szu.blankxiao.mycalendar.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.szu.blankxiao.mycalendar.data.local.datastore.UserInfo
import cn.szu.blankxiao.mycalendar.data.repository.AuthRepository
import cn.szu.blankxiao.mycalendar.data.repository.CodeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 认证ViewModel
 * 处理登录、注册、注销等UI状态
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 登录状态
    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 用户信息
    val userInfo: StateFlow<UserInfo?> = authRepository.userInfo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI状态
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // 验证码倒计时
    private val _codeCountdown = MutableStateFlow(0)
    val codeCountdown: StateFlow<Int> = _codeCountdown.asStateFlow()

    /**
     * 邮箱密码登录
     */
    fun loginWithPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("请填写完整信息")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.loginWithPassword(email, password)
                .onSuccess {
                    _uiState.value = AuthUiState.Success("登录成功")
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "登录失败")
                }
        }
    }

    /**
     * 邮箱验证码登录
     */
    fun loginWithCode(email: String, code: String) {
        if (email.isBlank() || code.isBlank()) {
            _uiState.value = AuthUiState.Error("请填写完整信息")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.loginWithCode(email, code)
                .onSuccess {
                    _uiState.value = AuthUiState.Success("登录成功")
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "登录失败")
                }
        }
    }

    /**
     * 注册
     */
    fun register(email: String, code: String, username: String, password: String) {
        if (email.isBlank() || code.isBlank() || username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("请填写完整信息")
            return
        }

        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("密码长度至少6位")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(email, code, username, password)
                .onSuccess {
                    _uiState.value = AuthUiState.Success("注册成功")
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "注册失败")
                }
        }
    }

    /**
     * 发送验证码
     */
    fun sendCode(email: String, type: CodeType) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error("请填写邮箱")
            return
        }

        if (_codeCountdown.value > 0) {
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.sendCode(email, type)
                .onSuccess {
                    _uiState.value = AuthUiState.CodeSent
                    startCountdown()
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "发送验证码失败")
                }
        }
    }

    /**
     * 开始倒计时
     */
    private fun startCountdown() {
        viewModelScope.launch {
            _codeCountdown.value = 60
            while (_codeCountdown.value > 0) {
                delay(1000)
                _codeCountdown.value -= 1
            }
        }
    }

    /**
     * 注销
     */
    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.logout()
                .onSuccess {
                    _uiState.value = AuthUiState.LoggedOut
                }
                .onFailure {
                    _uiState.value = AuthUiState.LoggedOut
                }
        }
    }

    /**
     * 重置UI状态
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

/**
 * 认证UI状态
 */
sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object CodeSent : AuthUiState()
    data object LoggedOut : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

