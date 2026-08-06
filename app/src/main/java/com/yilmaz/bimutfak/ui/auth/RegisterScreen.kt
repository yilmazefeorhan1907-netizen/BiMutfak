package com.yilmaz.bimutfak.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.ui.components.BiMutfakButton
import com.yilmaz.bimutfak.ui.components.BiMutfakPasswordField
import com.yilmaz.bimutfak.ui.components.BiMutfakTextField

// AuthViewModel ile kayıt ekranı arasındaki bağlantıyı kurar.
@Composable
fun RegisterRoute(
    onNavigateToLogin: () -> Unit,
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Kayıt başarılı olduğunda ana ekrana geçiş yapılmasını bildirir.
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthenticated()
        }
    }

    RegisterScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToLogin = onNavigateToLogin
    )
}

// Kayıt ekranının yalnızca kullanıcı arayüzünü oluşturur.
@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.auth_register_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.auth_register_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))
            BiMutfakTextField(
                value = uiState.firstName,
                onValueChange = {
                    onEvent(AuthEvent.FirstNameChanged(it))
                },
                label = stringResource(R.string.auth_first_name_label),
                placeholder = stringResource(
                    R.string.auth_first_name_placeholder
                ),
                keyboardType = KeyboardType.Text,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            BiMutfakTextField(
                value = uiState.lastName,
                onValueChange = {
                    onEvent(AuthEvent.LastNameChanged(it))
                },
                label = stringResource(R.string.auth_last_name_label),
                placeholder = stringResource(
                    R.string.auth_last_name_placeholder
                ),
                keyboardType = KeyboardType.Text,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
            BiMutfakTextField(
                value = uiState.email,
                onValueChange = {
                    onEvent(AuthEvent.EmailChanged(it))
                },
                label = stringResource(R.string.auth_email_label),
                placeholder = stringResource(R.string.auth_email_placeholder),
                keyboardType = KeyboardType.Email,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            BiMutfakPasswordField(
                value = uiState.password,
                onValueChange = {
                    onEvent(AuthEvent.PasswordChanged(it))
                },
                label = stringResource(R.string.auth_password_label),
                placeholder = stringResource(R.string.auth_password_placeholder),
                isPasswordVisible = uiState.isPasswordVisible,
                onVisibilityChange = {
                    onEvent(AuthEvent.TogglePasswordVisibility)
                },
                showPasswordText = stringResource(R.string.auth_show_password),
                hidePasswordText = stringResource(R.string.auth_hide_password),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            BiMutfakPasswordField(
                value = uiState.confirmPassword,
                onValueChange = {
                    onEvent(AuthEvent.ConfirmPasswordChanged(it))
                },
                label = stringResource(R.string.auth_password_confirm_label),
                placeholder = stringResource(
                    R.string.auth_password_confirm_placeholder
                ),
                isPasswordVisible = uiState.isConfirmPasswordVisible,
                onVisibilityChange = {
                    onEvent(AuthEvent.ToggleConfirmPasswordVisibility)
                },
                showPasswordText = stringResource(R.string.auth_show_password),
                hidePasswordText = stringResource(R.string.auth_hide_password),
                enabled = !uiState.isLoading
            )

            uiState.errorMessageResId?.let { messageResId ->
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(messageResId),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BiMutfakButton(
                text = stringResource(R.string.auth_register_button),
                onClick = {
                    onEvent(AuthEvent.RegisterClicked)
                },
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.auth_has_account),
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(
                    onClick = onNavigateToLogin,
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = stringResource(R.string.auth_go_to_login)
                    )
                }
            }
        }
    }
}