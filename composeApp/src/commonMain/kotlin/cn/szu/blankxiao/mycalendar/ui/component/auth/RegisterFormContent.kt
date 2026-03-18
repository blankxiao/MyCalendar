package cn.szu.blankxiao.mycalendar.ui.component.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 注册表单内容（共享组件）
 * 邮箱、验证码、用户名、密码、确认密码
 */
@Composable
fun RegisterFormContent(
    email: String,
    onEmailChange: (String) -> Unit,
    code: String,
    onCodeChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisible: () -> Unit,
    isLoading: Boolean,
    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    onSendCodeClick: () -> Unit,
    codeCountdown: Int,
    modifier: Modifier = Modifier
) {
    val canRegister = password == confirmPassword && password.length >= 6

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "创建账号",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "注册后即可同步日程到云端",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 邮箱
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("邮箱") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 验证码
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = onCodeChange,
                label = { Text("验证码") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onSendCodeClick,
                enabled = codeCountdown == 0 && email.isNotBlank(),
                modifier = Modifier.height(56.dp)
            ) {
                Text(if (codeCountdown > 0) "${codeCountdown}s" else "发送")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 用户名
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("用户名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 密码
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("密码 (至少6位)") },
            trailingIcon = {
                TextButton(onClick = onTogglePasswordVisible) {
                    Text(
                        text = if (passwordVisible) "隐藏" else "显示",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 确认密码
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("确认密码") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = confirmPassword.isNotEmpty() && password != confirmPassword,
            supportingText = {
                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Text("密码不一致", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 注册按钮
        Button(
            onClick = onRegisterClick,
            enabled = !isLoading && canRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("注册", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 返回登录
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("已有账号？", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onBackToLoginClick) {
                Text("立即登录")
            }
        }
    }
}
