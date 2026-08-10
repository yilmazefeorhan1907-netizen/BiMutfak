package com.yilmaz.bimutfak.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Profil ekranında gösterilecek kullanıcı verilerini ve işlemleri yönetir.
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(isLoading = true)
    )

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.FirstNameChanged -> {
                _uiState.update {
                    it.copy(
                        editableFirstName = event.firstName,
                        errorMessageResId = null
                    )
                }
            }

            is ProfileEvent.LastNameChanged -> {
                _uiState.update {
                    it.copy(
                        editableLastName = event.lastName,
                        errorMessageResId = null
                    )
                }
            }

            ProfileEvent.EditProfileRequested -> {
                _uiState.update {
                    it.copy(
                        editableFirstName = it.firstName,
                        editableLastName = it.lastName,
                        isEditProfileDialogVisible = true,
                        errorMessageResId = null
                    )
                }
            }

            ProfileEvent.EditProfileDismissed -> {
                dismissEditProfileDialog()
            }

            ProfileEvent.SaveProfileClicked -> {
                saveProfile()
            }

            ProfileEvent.ClearError -> {
                _uiState.update {
                    it.copy(
                        errorMessageResId = null
                    )
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null
                )
            }

            try {
                val user = authRepository.getCurrentUser()

                _uiState.update {
                    it.copy(
                        firstName = user?.firstName.orEmpty(),
                        lastName = user?.lastName.orEmpty(),
                        email = user?.email.orEmpty(),
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId =
                            R.string.profile_error_load
                    )
                }
            }
        }
    }

    private fun saveProfile() {
        val state = _uiState.value

        val firstName = state.editableFirstName.trim()
        val lastName = state.editableLastName.trim()

        val validationError = when {
            firstName.isBlank() ->
                R.string.profile_error_first_name_empty

            lastName.isBlank() ->
                R.string.profile_error_last_name_empty

            else -> null
        }

        if (validationError != null) {
            _uiState.update {
                it.copy(
                    errorMessageResId = validationError
                )
            }
            return
        }

        if (state.isSaving) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessageResId = null
                )
            }

            try {
                authRepository.updateCurrentUserName(
                    firstName = firstName,
                    lastName = lastName
                )

                _uiState.update {
                    it.copy(
                        firstName = firstName,
                        lastName = lastName,
                        editableFirstName = firstName,
                        editableLastName = lastName,
                        isSaving = false,
                        isEditProfileDialogVisible = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessageResId =
                            R.string.profile_error_update
                    )
                }
            }
        }
    }

    private fun dismissEditProfileDialog() {
        if (_uiState.value.isSaving) return

        _uiState.update {
            it.copy(
                editableFirstName = it.firstName,
                editableLastName = it.lastName,
                isEditProfileDialogVisible = false,
                errorMessageResId = null
            )
        }
    }
}