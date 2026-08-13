package com.yilmaz.bimutfak.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.domain.model.Recipe
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.IconButton
import com.yilmaz.bimutfak.ui.theme.ButterYellow
import com.yilmaz.bimutfak.ui.theme.OliveGreen
import com.yilmaz.bimutfak.ui.theme.ButterYellowSoft
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import com.yilmaz.bimutfak.ui.theme.OliveGreenSoft
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.OutlinedTextField

// RecipeViewModel ile Bi’Tarif ekranı arasındaki bağlantıyı kurar.
@Composable
fun RecipeRoute(
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RecipeScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun RecipeScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        Text(
            text = stringResource(R.string.recipe_title),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = stringResource(
                R.string.recipe_subtitle
            ),
            style = MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.size(16.dp))

        RecipeSearchField(
            query = uiState.searchQuery,
            isSearching = uiState.isSearching,
            onQueryChange = { query ->
                onEvent(
                    RecipeEvent.SearchQueryChanged(
                        query = query
                    )
                )
            }
        )

        Spacer(modifier = Modifier.size(12.dp))

        CuisineSelector(
            uiState = uiState,
            onCuisineSelected = { cuisine ->
                onEvent(
                    RecipeEvent.CuisineSelected(
                        cuisine = cuisine
                    )
                )
            }
        )

        Spacer(modifier = Modifier.size(16.dp))


        when {
            (uiState.isLoading || uiState.isSearching) && uiState.recipes.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessageResId != null &&
                    uiState.recipes.isEmpty() -> {

                RecipeErrorCard(
                    message = stringResource(
                        uiState.errorMessageResId
                    ),
                    onRetry = {
                        onEvent(RecipeEvent.RetryClicked)
                    }
                )
            }

            uiState.recipes.isEmpty() -> {
                RecipeEmptyCard()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = uiState.visibleRecipes,
                        key = { recipe -> recipe.id }
                    ) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            isFavorite =
                                recipe.id in
                                        uiState.favoriteRecipeIds,
                            isInDailyMenu =
                                recipe.id in
                                        uiState.dailyMenuRecipeIds,
                            selectionEnabled =
                                uiState.processingRecipeId == null,
                            onClick = {
                                onEvent(
                                    RecipeEvent.RecipeClicked(
                                        recipeId = recipe.id
                                    )
                                )
                            },
                            onFavoriteClick = {
                                onEvent(
                                    RecipeEvent.FavoriteClicked(
                                        recipeId = recipe.id
                                    )
                                )
                            },
                            onDailyMenuClick = {
                                onEvent(
                                    RecipeEvent.DailyMenuClicked(
                                        recipeId = recipe.id
                                    )
                                )
                            }
                        )
                    }

                    if (uiState.pageCount > 1) {
                        item {
                            RecipePaginationControls(
                                currentPage =
                                    uiState.currentPage,
                                pageCount =
                                    uiState.pageCount,
                                onPreviousClick = {
                                    onEvent(
                                        RecipeEvent
                                            .PreviousPageClicked
                                    )
                                },
                                onNextClick = {
                                    onEvent(
                                        RecipeEvent
                                            .NextPageClicked
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    uiState.selectedRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            onDismiss = {
                onEvent(
                    RecipeEvent.RecipeDetailDismissed
                )
            }
        )
    }

    uiState.userMessageResId?.let { messageResId ->
        AlertDialog(
            onDismissRequest = {
                onEvent(RecipeEvent.ClearMessage)
            },
            text = {
                Text(
                    text = stringResource(messageResId)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(RecipeEvent.ClearMessage)
                    }
                ) {
                    Text(
                        text = stringResource(
                            R.string.common_close
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun RecipeSearchField(
    query: String,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(
                    R.string.recipe_search_placeholder
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(
                    R.string.recipe_search_description
                )
            )
        },
        trailingIcon = {
            when {
                isSearching -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }

                query.isNotBlank() -> {
                    IconButton(
                        onClick = {
                            onQueryChange("")
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.Close,
                            contentDescription =
                                stringResource(
                                    R.string
                                        .recipe_clear_search
                                )
                        )
                    }
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large
    )
}

@Composable
private fun CuisineSelector(
    uiState: RecipeUiState,
    onCuisineSelected: (String) -> Unit
) {
    var isMenuExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(
                R.string.recipe_cuisine_label
            ),
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.size(6.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    isMenuExpanded = true
                },
                enabled =
                    uiState.cuisines.isNotEmpty() &&
                            !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiState.selectedCuisine
                        .ifBlank {
                            stringResource(
                                R.string.recipe_select_cuisine
                            )
                        },
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )

                Icon(
                    imageVector =
                        Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = {
                    isMenuExpanded = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                uiState.cuisines.forEach { cuisine ->
                    DropdownMenuItem(
                        text = {
                            Text(text = cuisine.name)
                        },
                        onClick = {
                            isMenuExpanded = false

                            onCuisineSelected(
                                cuisine.name
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipePaginationControls(
    currentPage: Int,
    pageCount: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement =
            Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onPreviousClick,
            enabled = currentPage > 0,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(
                    R.string.recipe_previous_page
                )
            )
        }

        Text(
            text = stringResource(
                R.string.recipe_page_indicator,
                currentPage + 1,
                pageCount
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )

        OutlinedButton(
            onClick = onNextClick,
            enabled = currentPage < pageCount - 1,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(
                    R.string.recipe_next_page
                )
            )
        }
    }
}
@Composable
private fun RecipeDetailDialog(
    recipe: Recipe,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = recipe.title
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 460.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = stringResource(
                        R.string.recipe_image_description,
                        recipe.title
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = stringResource(
                        R.string.recipe_ingredients_title
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.size(8.dp))

                if (recipe.ingredients.isEmpty()) {
                    Text(
                        text = stringResource(
                            R.string.recipe_ingredients_empty
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    recipe.ingredients.forEach { ingredient ->
                        val ingredientText = listOf(
                            ingredient.measure,
                            ingredient.name
                        )
                            .filter { value ->
                                value.isNotBlank()
                            }
                            .joinToString(" ")

                        Text(
                            text = stringResource(
                                R.string.common_ingredient_row,
                                ingredientText
                            ),
                            style =
                                MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(
                                vertical = 2.dp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                Text(
                    text = stringResource(
                        R.string.recipe_instructions_title
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = recipe.instructions
                        .joinToString("\n\n")
                        .ifBlank {
                            stringResource(
                                R.string.recipe_instructions_empty
                            )
                        },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(
                        R.string.recipe_detail_close
                    )
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor =
            MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    isFavorite: Boolean,
    isInDailyMenu: Boolean,
    selectionEnabled: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDailyMenuClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        AsyncImage(
            model = recipe.imageUrl,
            contentDescription = stringResource(
                R.string.recipe_image_description,
                recipe.title
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 14.dp,
                end = 8.dp,
                bottom = 8.dp
            )
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.recipe_view_detail
                    ),
                    modifier = Modifier.weight(1f),
                    style =
                        MaterialTheme.typography.labelLarge,
                    color =
                        MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = onDailyMenuClick,
                    enabled = selectionEnabled,
                    modifier = if (isInDailyMenu) {
                        Modifier.background(
                            color = OliveGreenSoft,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.RestaurantMenu,
                        contentDescription = stringResource(
                            if (isInDailyMenu) {
                                R.string
                                    .recipe_remove_daily_menu
                            } else {
                                R.string
                                    .recipe_add_daily_menu
                            }
                        ),
                        tint = OliveGreen
                    )
                }

                IconButton(
                    onClick = onFavoriteClick,
                    enabled = selectionEnabled,
                    modifier = if (isFavorite) {
                        Modifier.background(
                            color = ButterYellowSoft,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ) {
                    Icon(
                        imageVector =
                            if (isFavorite) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                        contentDescription = stringResource(
                            if (isFavorite) {
                                R.string
                                    .recipe_remove_favorite
                            } else {
                                R.string
                                    .recipe_add_favorite
                            }
                        ),
                        tint = ButterYellow
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.size(8.dp))

            TextButton(onClick = onRetry) {
                Text(
                    text = stringResource(
                        R.string.common_retry
                    )
                )
            }
        }
    }
}

@Composable
private fun RecipeEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector =
                    Icons.Outlined.RestaurantMenu,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = stringResource(
                    R.string.recipe_empty
                ),
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}