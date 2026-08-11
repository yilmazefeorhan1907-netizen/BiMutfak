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
import com.yilmaz.bimutfak.data.repository.RecipeSelectionRepository

// Profil ekranında gösterilecek kullanıcı verilerini ve işlemleri yönetir.
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val selectionRepository:
    RecipeSelectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(isLoading = true)
    )

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    init {
        refreshProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.RecipeClicked -> {
                val selectedRecipe = (
                        _uiState.value.dailyMenu +
                                _uiState.value.favoriteRecipes
                        )
                    .distinctBy { recipe ->
                        recipe.id
                    }
                    .firstOrNull { recipe ->
                        recipe.id == event.recipeId
                    }

                _uiState.update {
                    it.copy(
                        selectedRecipe = selectedRecipe
                    )
                }
            }

            ProfileEvent.RecipeDetailDismissed -> {
                _uiState.update {
                    it.copy(
                        selectedRecipe = null
                    )
                }
            }

            is ProfileEvent.RemoveDailyMenuRecipeClicked -> {
                removeDailyMenuRecipe(event.recipeId)
            }

            is ProfileEvent.RemoveFavoriteRecipeClicked -> {
                removeFavoriteRecipe(event.recipeId)
            }

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

    fun refreshProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null
                )
            }

            try {
                val user = authRepository.getCurrentUser()

                val dailyMenu =
                    selectionRepository.getDailyMenu()

                val favorites =
                    selectionRepository.getFavoriteRecipes()

                _uiState.update {
                    it.copy(
                        firstName = user?.firstName.orEmpty(),
                        lastName = user?.lastName.orEmpty(),
                        email = user?.email.orEmpty(),
                        dailyMenu = dailyMenu,
                        favoriteRecipes = favorites,
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
    private fun removeDailyMenuRecipe(
        recipeId: String
    ) {
        val recipe = _uiState.value.dailyMenu
            .firstOrNull { item ->
                item.id == recipeId
            } ?: return

        viewModelScope.launch {
            try {
                selectionRepository.toggleDailyMenu(recipe)

                _uiState.update {
                    it.copy(
                        dailyMenu = it.dailyMenu.filterNot {
                                item ->
                            item.id == recipeId
                        }
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessageResId =
                            R.string.recipe_selection_error
                    )
                }
            }
        }
    }

    private fun removeFavoriteRecipe(
        recipeId: String
    ) {
        val recipe = _uiState.value.favoriteRecipes
            .firstOrNull { item ->
                item.id == recipeId
            } ?: return

        viewModelScope.launch {
            try {
                selectionRepository.toggleFavorite(recipe)

                _uiState.update {
                    it.copy(
                        favoriteRecipes =
                            it.favoriteRecipes.filterNot {
                                    item ->
                                item.id == recipeId
                            }
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessageResId =
                            R.string.recipe_selection_error
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