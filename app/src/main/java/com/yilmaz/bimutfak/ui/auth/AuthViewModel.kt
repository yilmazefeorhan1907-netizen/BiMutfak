package com.yilmaz.bimutfak.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.yilmaz.bimutfak.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.yilmaz.bimutfak.domain.usecase.auth.CheckUserLoggedInUseCase
import com.yilmaz.bimutfak.domain.usecase.auth.LoginUseCase
import com.yilmaz.bimutfak.domain.usecase.auth.RegisterUseCase

// Hilt’e bu ViewModel’i oluşturmasını ve Use Case bağımlılıklarını vermesini söyler.
// Authentication ekranlarının durumunu ve kullanıcı işlemlerini yönetir.

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkUserLoggedInUseCase:
    CheckUserLoggedInUseCase,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    // ViewModel içinde değiştirilebilen gerçek arayüz durumu.
    private val _uiState = MutableStateFlow(
        AuthUiState(
            isAuthenticated = checkUserLoggedInUseCase()
        )
    )

    // Ekranların okuyabildiği fakat doğrudan değiştiremediği arayüz durumu.
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Ekrandan gelen bütün kullanıcı olaylarını uygun işleme yönlendirir.
    /*
    _uiState ViewModel değiştirebilir
    uiState Ekran yalnızca okuyabilir
    */

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.FirstNameChanged -> {
                _uiState.update {
                    it.copy(
                        firstName = event.firstName,
                        errorMessageResId = null
                    )
                }
            }

            is AuthEvent.LastNameChanged -> {
                _uiState.update {
                    it.copy(
                        lastName = event.lastName,
                        errorMessageResId = null
                    )
                }
            }
            is AuthEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.email,
                        errorMessageResId = null
                    )
                }
            }

            is AuthEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.password,
                        errorMessageResId = null
                    )
                }
            }

            is AuthEvent.ConfirmPasswordChanged -> {
                _uiState.update {
                    it.copy(
                        confirmPassword = event.confirmPassword,
                        errorMessageResId = null
                    )
                }
            }

            AuthEvent.TogglePasswordVisibility -> {
                _uiState.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }

            AuthEvent.ToggleConfirmPasswordVisibility -> {
                _uiState.update {
                    it.copy(
                        isConfirmPasswordVisible = !it.isConfirmPasswordVisible
                    )
                }
            }

            AuthEvent.LoginClicked -> login()

            AuthEvent.RegisterClicked -> register()

            AuthEvent.ClearError -> {
                _uiState.update {
                    it.copy(errorMessageResId = null)
                }
            }
        }
    }

    // Formu doğruladıktan sonra Firebase giriş işlemini başlatır.
    private fun login() {
        val validationError = validateForm(
            checkConfirmPassword = false
        )

        if (validationError != null) {
            _uiState.update {
                it.copy(errorMessageResId = validationError)
            }
            return
        }

        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null
                )
            }

            try {
                loginUseCase(
                    email = _uiState.value.email.trim(),
                    password = _uiState.value.password
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = mapAuthError(exception)
                    )
                }
            }
        }
    }

    // Formu doğruladıktan sonra Firebase hesap oluşturma işlemini başlatır.
    private fun register() {
        val validationError = validateForm(
            checkConfirmPassword = true
        )

        if (validationError != null) {
            _uiState.update {
                it.copy(errorMessageResId = validationError)
            }
            return
        }

        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null
                )
            }

            try {
              registerUseCase(
                    firstName = _uiState.value.firstName.trim(),
                    lastName = _uiState.value.lastName.trim(),
                    email = _uiState.value.email.trim(),
                    password = _uiState.value.password
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = mapAuthError(exception)
                    )
                }
            }
        }
    }
    // Form alanlarını kontrol eder ve hata varsa ilgili metin kaynağını döndürür.
    private fun validateForm(
        checkConfirmPassword: Boolean
    ): Int? {
        val state = _uiState.value

        return when {

            checkConfirmPassword && state.firstName.isBlank() ->
                R.string.auth_error_first_name_empty

            checkConfirmPassword && state.lastName.isBlank() ->
                R.string.auth_error_last_name_empty

            state.email.isBlank() ->
                R.string.auth_error_email_empty

            !Patterns.EMAIL_ADDRESS
                .matcher(state.email.trim())
                .matches() ->
                R.string.auth_error_email_invalid

            state.password.isBlank() ->
                R.string.auth_error_password_empty

            state.password.length < 6 ->
                R.string.auth_error_password_short

            checkConfirmPassword &&
                    state.password != state.confirmPassword ->
                R.string.auth_error_passwords_not_match

            else -> null
        }
    }

    // Firebase'in teknik hatalarını kullanıcıya gösterilecek Türkçe metinlere çevirir.
    private fun mapAuthError(
        exception: Exception
    ): Int {
        return when (exception) {
            is FirebaseAuthUserCollisionException ->
                R.string.auth_error_email_in_use

            is FirebaseAuthInvalidUserException ->
                R.string.auth_error_user_not_found

            is FirebaseAuthInvalidCredentialsException ->
                R.string.auth_error_invalid_credentials

            is FirebaseTooManyRequestsException ->
                R.string.auth_error_too_many_requests

            is FirebaseNetworkException ->
                R.string.error_network

            else ->
                R.string.error_general
        }
    }
}
