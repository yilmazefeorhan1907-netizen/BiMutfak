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

        Spacer(modifier = Modifier.size(20.dp))

        when {
            uiState.isLoading &&
                    uiState.recipes.isEmpty() -> {

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
                        items = uiState.recipes,
                        key = { recipe -> recipe.id }
                    ) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = {
                                onEvent(
                                    RecipeEvent.RecipeClicked(
                                        recipeId = recipe.id
                                    )
                                )
                            }
                        )
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
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = stringResource(
                    R.string.recipe_view_detail
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
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
                        R.string.recipe_instructions_title
                    ),
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = recipe.instructions
                        .joinToString("\n\n")
                        .ifBlank {
                            stringResource(
                                R.string
                                    .recipe_instructions_empty
                            )
                        },
                    style =
                        MaterialTheme.typography.bodyMedium
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