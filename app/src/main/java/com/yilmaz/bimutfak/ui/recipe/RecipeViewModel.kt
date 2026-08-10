package com.yilmaz.bimutfak.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Tarif listesinin yüklenmesini ve kullanıcı işlemlerini yönetir.
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RecipeUiState()
    )

    val uiState: StateFlow<RecipeUiState> =
        _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    fun onEvent(event: RecipeEvent) {
        when (event) {
            is RecipeEvent.RecipeClicked -> {
                val selectedRecipe =
                    _uiState.value.recipes.firstOrNull {
                            recipe ->
                        recipe.id == event.recipeId
                    }

                _uiState.update {
                    it.copy(
                        selectedRecipe = selectedRecipe
                    )
                }
            }

            RecipeEvent.RecipeDetailDismissed -> {
                _uiState.update {
                    it.copy(
                        selectedRecipe = null
                    )
                }
            }

            RecipeEvent.RetryClicked -> {
                loadRecipes()
            }

            RecipeEvent.ClearError -> {
                _uiState.update {
                    it.copy(
                        errorMessageResId = null
                    )
                }
            }
        }
    }

    private fun loadRecipes() {
        if (_uiState.value.isLoading &&
            _uiState.value.recipes.isNotEmpty()
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null
                )
            }

            try {
                val recipes =
                    recipeRepository.getRecipes()

                _uiState.update {
                    it.copy(
                        recipes = recipes,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId =
                            R.string.recipe_error_load
                    )
                }
            }
        }
    }
}