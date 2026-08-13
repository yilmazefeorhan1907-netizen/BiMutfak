package com.yilmaz.bimutfak.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.domain.usecase.recipe.SearchRecipesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.yilmaz.bimutfak.domain.model.RecipeSelectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.yilmaz.bimutfak.domain.usecase.recipe.GetCuisinesUseCase
import com.yilmaz.bimutfak.domain.usecase.recipe.GetDailyMenuUseCase
import com.yilmaz.bimutfak.domain.usecase.recipe.GetFavoriteRecipesUseCase
import com.yilmaz.bimutfak.domain.usecase.recipe.GetRecipeByIdUseCase
import com.yilmaz.bimutfak.domain.usecase.recipe.GetRecipesByCuisineUseCase
import com.yilmaz.bimutfak.domain.usecase.recipe.ToggleDailyMenuUseCase
import com.yilmaz.bimutfak.domain.usecase.recipe.ToggleFavoriteUseCase

// Tarif listesini ve kullanıcının tarif seçimlerini yönetir.
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getCuisinesUseCase:
    GetCuisinesUseCase,
    private val getRecipesByCuisineUseCase:
    GetRecipesByCuisineUseCase,
    private val getRecipeByIdUseCase:
    GetRecipeByIdUseCase,
    private val searchRecipesUseCase:
    SearchRecipesUseCase,
    private val getFavoriteRecipesUseCase:
    GetFavoriteRecipesUseCase,
    private val getDailyMenuUseCase:
    GetDailyMenuUseCase,
    private val toggleFavoriteUseCase:
    ToggleFavoriteUseCase,
    private val toggleDailyMenuUseCase:
    ToggleDailyMenuUseCase
) : ViewModel() {

    private var searchJob: Job? = null
    private val _uiState = MutableStateFlow(
        RecipeUiState()
    )

    val uiState: StateFlow<RecipeUiState> =
        _uiState.asStateFlow()

    init {
        loadInitialContent()
    }

    fun onEvent(event: RecipeEvent) {
        when (event) {
            is RecipeEvent.CuisineSelected -> {
                selectCuisine(event.cuisine)
            }

            is RecipeEvent.SearchQueryChanged -> {
                updateSearchQuery(event.query)
            }

            is RecipeEvent.RecipeClicked -> {
                loadRecipeDetail(event.recipeId)
            }

            is RecipeEvent.FavoriteClicked -> {
                toggleFavorite(event.recipeId)
            }

            is RecipeEvent.DailyMenuClicked -> {
                toggleDailyMenu(event.recipeId)
            }

            RecipeEvent.PreviousPageClicked -> {
                showPreviousPage()
            }

            RecipeEvent.NextPageClicked -> {
                showNextPage()
            }

            RecipeEvent.RecipeDetailDismissed -> {
                _uiState.update {
                    it.copy(
                        selectedRecipe = null
                    )
                }
            }

            RecipeEvent.RetryClicked -> {
                val state = _uiState.value
                val query = state.searchQuery.trim()

                if (query.length >= MIN_SEARCH_LENGTH) {
                    updateSearchQuery(state.searchQuery)
                } else if (
                    state.selectedCuisine.isNotBlank()
                ) {
                    loadRecipesByCuisine(
                        state.selectedCuisine
                    )
                } else {
                    loadInitialContent()
                }
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

    private fun loadInitialContent() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null,
                    userMessageResId = null
                )
            }

            try {
                val cuisines = getCuisinesUseCase()

                val selectedCuisine = cuisines
                    .firstOrNull { cuisine ->
                        cuisine.name.equals(
                            other = PRIMARY_CUISINE,
                            ignoreCase = true
                        )
                    }
                    ?.name
                    ?: cuisines.firstOrNull { cuisine ->
                        cuisine.name.equals(
                            other = FALLBACK_CUISINE,
                            ignoreCase = true
                        )
                    }
                        ?.name
                    ?: cuisines.firstOrNull()
                        ?.name
                        .orEmpty()

                val recipes = if (
                    selectedCuisine.isBlank()
                ) {
                    emptyList()
                } else {
                    getRecipesByCuisineUseCase(
                        selectedCuisine
                    )
                }

                _uiState.update {
                    it.copy(
                        cuisines = cuisines,
                        selectedCuisine = selectedCuisine,
                        recipes = recipes,
                        currentPage = 0,
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

    private fun loadRecipesByCuisine(
        cuisine: String
    ) {
        val normalizedCuisine = cuisine.trim()

        if (normalizedCuisine.isBlank()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedCuisine = normalizedCuisine,
                    recipes = emptyList(),
                    currentPage = 0,
                    isLoading = true,
                    isSearching = false,
                    errorMessageResId = null,
                    userMessageResId = null
                )
            }

            try {
                val recipes =
                    getRecipesByCuisineUseCase(
                        normalizedCuisine
                    )

                _uiState.update {
                    it.copy(
                        recipes = recipes,
                        currentPage = 0,
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

    private fun selectCuisine(
        cuisine: String
    ) {
        searchJob?.cancel()

        _uiState.update {
            it.copy(
                searchQuery = "",
                isSearching = false
            )
        }

        loadRecipesByCuisine(cuisine)
    }

    private fun updateSearchQuery(
        query: String
    ) {
        val previousQuery =
            _uiState.value.searchQuery.trim()

        val normalizedQuery = query.trim()

        searchJob?.cancel()

        _uiState.update {
            it.copy(
                searchQuery = query,
                currentPage = 0,
                errorMessageResId = null
            )
        }

        if (
            normalizedQuery.length <
            MIN_SEARCH_LENGTH
        ) {
            _uiState.update {
                it.copy(
                    isSearching = false
                )
            }

            if (
                previousQuery.length >=
                MIN_SEARCH_LENGTH
            ) {
                val selectedCuisine =
                    _uiState.value.selectedCuisine

                if (selectedCuisine.isNotBlank()) {
                    loadRecipesByCuisine(
                        selectedCuisine
                    )
                }
            }

            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)

            if (
                _uiState.value.searchQuery.trim() !=
                normalizedQuery
            ) {
                return@launch
            }

            _uiState.update {
                it.copy(
                    recipes = emptyList(),
                    currentPage = 0,
                    isSearching = true,
                    selectedRecipe = null,
                    errorMessageResId = null
                )
            }

            try {
                val recipes =
                    searchRecipesUseCase(
                        normalizedQuery
                    )

                if (
                    _uiState.value.searchQuery.trim() ==
                    normalizedQuery
                ) {
                    _uiState.update {
                        it.copy(
                            recipes = recipes,
                            currentPage = 0,
                            isSearching = false
                        )
                    }
                }
            } catch (_: Exception) {
                if (
                    _uiState.value.searchQuery.trim() ==
                    normalizedQuery
                ) {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            errorMessageResId =
                                R.string.recipe_error_load
                        )
                    }
                }
            }
        }
    }

    private fun loadRecipeDetail(
        recipeId: String
    ) {
        val summaryRecipe =
            _uiState.value.recipes.firstOrNull {
                    recipe -> recipe.id == recipeId
            } ?: return

        if (_uiState.value.isLoadingRecipeDetail) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingRecipeDetail = true,
                    userMessageResId = null
                )
            }

            try {
                val detailedRecipe =
                    getRecipeByIdUseCase(recipeId)
                        ?: summaryRecipe

                _uiState.update {
                    it.copy(
                        recipes = it.recipes.map { recipe ->
                            if (recipe.id == recipeId) {
                                detailedRecipe
                            } else {
                                recipe
                            }
                        },
                        selectedRecipe = detailedRecipe,
                        isLoadingRecipeDetail = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingRecipeDetail = false,
                        userMessageResId =
                            R.string.recipe_error_load
                    )
                }
            }
        }
    }

    private fun showPreviousPage() {
        _uiState.update {
            it.copy(
                currentPage =
                    (it.currentPage - 1)
                        .coerceAtLeast(0)
            )
        }
    }

    private fun showNextPage() {
        _uiState.update {
            val lastPageIndex =
                (it.pageCount - 1)
                    .coerceAtLeast(0)

            it.copy(
                currentPage =
                    (it.currentPage + 1)
                        .coerceAtMost(lastPageIndex)
            )
        }
    }

    private suspend fun loadRecipeSelections() {
        try {
            val favorites =
                getFavoriteRecipesUseCase()

            val dailyMenu =
                getDailyMenuUseCase()

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

        val summaryRecipe = state.recipes.firstOrNull {
                recipe -> recipe.id == recipeId
        } ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processingRecipeId = recipeId,
                    userMessageResId = null
                )
            }

            try {
                val recipe =
                    getRecipeByIdUseCase(recipeId)
                        ?: summaryRecipe

                _uiState.update {
                    it.copy(
                        recipes = it.recipes.map {
                                currentRecipe ->
                            if (
                                currentRecipe.id == recipeId
                            ) {
                                recipe
                            } else {
                                currentRecipe
                            }
                        }
                    )
                }

                when (
                    toggleFavoriteUseCase(recipe)
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

        val summaryRecipe = state.recipes.firstOrNull {
                recipe -> recipe.id == recipeId
        } ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processingRecipeId = recipeId,
                    userMessageResId = null
                )
            }

            try {
                val recipe =
                    getRecipeByIdUseCase(recipeId)
                        ?: summaryRecipe

                _uiState.update {
                    it.copy(
                        recipes = it.recipes.map {
                                currentRecipe ->
                            if (
                                currentRecipe.id == recipeId
                            ) {
                                recipe
                            } else {
                                currentRecipe
                            }
                        }
                    )
                }

                when (
                    toggleDailyMenuUseCase(recipe)
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
    companion object {
        private const val PRIMARY_CUISINE =
            "Turkish"

        private const val FALLBACK_CUISINE =
            "Mediterranean"

        private const val MIN_SEARCH_LENGTH = 3

        private const val SEARCH_DEBOUNCE_MILLIS =
            400L
    }
}