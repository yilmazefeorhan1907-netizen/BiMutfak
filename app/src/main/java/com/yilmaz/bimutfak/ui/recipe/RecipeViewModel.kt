package com.yilmaz.bimutfak.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.data.repository.RecipeRepository
import com.yilmaz.bimutfak.data.repository.RecipeSelectionRepository
import com.yilmaz.bimutfak.data.repository.RecipeSelectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Tarif listesini ve kullanıcının tarif seçimlerini yönetir.
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val selectionRepository:
    RecipeSelectionRepository
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

            is RecipeEvent.FavoriteClicked -> {
                toggleFavorite(event.recipeId)
            }

            is RecipeEvent.DailyMenuClicked -> {
                toggleDailyMenu(event.recipeId)
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

            RecipeEvent.ClearMessage -> {
                _uiState.update {
                    it.copy(
                        userMessageResId = null
                    )
                }
            }
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null,
                    userMessageResId = null
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

                loadRecipeSelections()
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

    private suspend fun loadRecipeSelections() {
        try {
            val favorites =
                selectionRepository.getFavoriteRecipes()

            val dailyMenu =
                selectionRepository.getDailyMenu()

            _uiState.update {
                it.copy(
                    favoriteRecipeIds = favorites
                        .map { recipe -> recipe.id }
                        .toSet(),
                    dailyMenuRecipeIds = dailyMenu
                        .map { recipe -> recipe.id }
                        .toSet()
                )
            }
        } catch (_: Exception) {
            _uiState.update {
                it.copy(
                    userMessageResId =
                        R.string.recipe_selection_error
                )
            }
        }
    }

    private fun toggleFavorite(
        recipeId: String
    ) {
        val state = _uiState.value

        if (state.processingRecipeId != null) return

        val recipe = state.recipes.firstOrNull {
            it.id == recipeId
        } ?: return

        val wasFavorite =
            recipeId in state.favoriteRecipeIds

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processingRecipeId = recipeId,
                    userMessageResId = null
                )
            }

            try {
                when (
                    selectionRepository.toggleFavorite(
                        recipe
                    )
                ) {
                    RecipeSelectionResult.ADDED -> {
                        _uiState.update {
                            it.copy(
                                favoriteRecipeIds =
                                    it.favoriteRecipeIds +
                                            recipeId
                            )
                        }
                    }

                    RecipeSelectionResult.REMOVED -> {
                        _uiState.update {
                            it.copy(
                                favoriteRecipeIds =
                                    it.favoriteRecipeIds -
                                            recipeId
                            )
                        }
                    }

                    RecipeSelectionResult
                        .FAVORITE_LIMIT_REACHED -> {
                        _uiState.update {
                            it.copy(
                                userMessageResId =
                                    R.string
                                        .recipe_favorite_limit_reached
                            )
                        }
                    }

                    RecipeSelectionResult
                        .DAILY_MENU_LIMIT_REACHED -> Unit
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        favoriteRecipeIds =
                            if (wasFavorite) {
                                it.favoriteRecipeIds + recipeId
                            } else {
                                it.favoriteRecipeIds - recipeId
                            },
                        userMessageResId =
                            R.string.recipe_selection_error
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(
                        processingRecipeId = null
                    )
                }
            }
        }
    }

    private fun toggleDailyMenu(
        recipeId: String
    ) {
        val state = _uiState.value

        if (state.processingRecipeId != null) return

        val recipe = state.recipes.firstOrNull {
            it.id == recipeId
        } ?: return

        val wasInDailyMenu =
            recipeId in state.dailyMenuRecipeIds

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processingRecipeId = recipeId,
                    userMessageResId = null
                )
            }

            try {
                when (
                    selectionRepository.toggleDailyMenu(
                        recipe
                    )
                ) {
                    RecipeSelectionResult.ADDED -> {
                        _uiState.update {
                            it.copy(
                                dailyMenuRecipeIds =
                                    it.dailyMenuRecipeIds +
                                            recipeId
                            )
                        }
                    }

                    RecipeSelectionResult.REMOVED -> {
                        _uiState.update {
                            it.copy(
                                dailyMenuRecipeIds =
                                    it.dailyMenuRecipeIds -
                                            recipeId
                            )
                        }
                    }

                    RecipeSelectionResult
                        .DAILY_MENU_LIMIT_REACHED -> {
                        _uiState.update {
                            it.copy(
                                userMessageResId =
                                    R.string
                                        .recipe_daily_menu_limit_reached
                            )
                        }
                    }

                    RecipeSelectionResult
                        .FAVORITE_LIMIT_REACHED -> Unit
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        dailyMenuRecipeIds =
                            if (wasInDailyMenu) {
                                it.dailyMenuRecipeIds + recipeId
                            } else {
                                it.dailyMenuRecipeIds - recipeId
                            },
                        userMessageResId =
                            R.string.recipe_selection_error
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(
                        processingRecipeId = null
                    )
                }
            }
        }
    }
}