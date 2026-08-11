package com.yilmaz.bimutfak.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.ui.components.BiMutfakTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.yilmaz.bimutfak.domain.model.Recipe
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import com.yilmaz.bimutfak.ui.theme.ButterYellow
import com.yilmaz.bimutfak.ui.theme.OliveGreen

@Composable
fun ProfileRoute(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshProfile()
    }

    ProfileScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onLogout = onLogout
    )

}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        val greeting = if (uiState.firstName.isBlank()) {
            stringResource(
                R.string.profile_greeting_generic
            )
        } else {
            stringResource(
                R.string.profile_greeting_named,
                uiState.firstName
            )
        }

        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = stringResource(R.string.profile_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.size(16.dp))

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (
            uiState.errorMessageResId != null &&
            !uiState.isEditProfileDialogVisible
        ) {
            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(
                    uiState.errorMessageResId
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        ProfileSectionCard(
            title = stringResource(
                R.string.profile_personal_information
            ),
            icon = Icons.Outlined.Person
        ) {
            ProfileInformationItem(
                label = stringResource(
                    R.string.profile_first_name
                ),
                value = uiState.firstName
            )

            Spacer(modifier = Modifier.size(12.dp))

            ProfileInformationItem(
                label = stringResource(
                    R.string.profile_last_name
                ),
                value = uiState.lastName
            )

            Spacer(modifier = Modifier.size(12.dp))

            ProfileInformationItem(
                label = stringResource(
                    R.string.profile_email
                ),
                value = uiState.email
            )

            Spacer(modifier = Modifier.size(18.dp))

            OutlinedButton(
                onClick = {
                    onEvent(
                        ProfileEvent.EditProfileRequested
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(
                        R.string.profile_edit_information
                    )
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ProfileSectionCard(
            title = stringResource(
                R.string.profile_daily_menu_title
            ),
            icon = Icons.Outlined.RestaurantMenu
        ) {
            if (uiState.dailyMenu.isEmpty()) {
                ProfileEmptyText(
                    text = stringResource(
                        R.string.profile_daily_menu_empty
                    )
                )
            } else {
                uiState.dailyMenu.forEach { recipe ->
                    ProfileRecipeCard(
                        recipe = recipe,
                        removeColor = OliveGreen,
                        removeContentDescription =
                            stringResource(
                                R.string
                                    .profile_remove_daily_menu_recipe,
                                recipe.title
                            ),
                        onClick = {
                            onEvent(
                                ProfileEvent.RecipeClicked(
                                    recipeId = recipe.id
                                )
                            )
                        },
                        onRemoveClick = {
                            onEvent(
                                ProfileEvent
                                    .RemoveDailyMenuRecipeClicked(
                                        recipeId = recipe.id
                                    )
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ProfileSectionCard(
            title = stringResource(
                R.string.profile_favorites_title
            ),
            icon = Icons.Outlined.FavoriteBorder
        ) {
            if (uiState.favoriteRecipes.isEmpty()) {
                ProfileEmptyText(
                    text = stringResource(
                        R.string.profile_favorites_empty
                    )
                )
            } else {
                uiState.favoriteRecipes.forEach { recipe ->
                    ProfileRecipeCard(
                        recipe = recipe,
                        removeColor = ButterYellow,
                        removeContentDescription =
                            stringResource(
                                R.string.profile_remove_favorite_recipe,
                                recipe.title
                            ),
                        onClick = {
                            onEvent(
                                ProfileEvent.RecipeClicked(
                                    recipeId = recipe.id
                                )
                            )
                        },
                        onRemoveClick = {
                            onEvent(
                                ProfileEvent
                                    .RemoveFavoriteRecipeClicked(
                                        recipeId = recipe.id
                                    )
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(
                    R.string.profile_logout
                )
            )
        }

        Spacer(modifier = Modifier.size(16.dp))
    }

    if (uiState.isEditProfileDialogVisible) {
        EditProfileDialog(
            uiState = uiState,
            onEvent = onEvent
        )
    }

    uiState.selectedRecipe?.let { recipe ->
        ProfileRecipeDetailDialog(
            recipe = recipe,
            onDismiss = {
                onEvent(
                    ProfileEvent.RecipeDetailDismissed
                )
            }
        )
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            HorizontalDivider(
                color =
                    MaterialTheme.colorScheme.outline.copy(
                        alpha = 0.25f
                    )
            )

            Spacer(modifier = Modifier.size(12.dp))

            content()
        }
    }
}

@Composable
private fun ProfileRecipeCard(
    recipe: Recipe,
    removeColor: Color,
    removeContentDescription: String,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = stringResource(
                    R.string.recipe_image_description,
                    recipe.title
                ),
                modifier = Modifier.size(78.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = recipe.title,
                    style =
                        MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.size(5.dp))

                Text(
                    text = stringResource(
                        R.string.recipe_view_detail
                    ),
                    style =
                        MaterialTheme.typography.labelMedium,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onRemoveClick
            ) {
                Icon(
                    imageVector =
                        Icons.Outlined.RemoveCircleOutline,
                    contentDescription =
                        removeContentDescription,
                    tint = removeColor
                )
            }
        }
    }
}
@Composable
private fun ProfileRecipeDetailDialog(
    recipe: Recipe,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = recipe.title)
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
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.size(8.dp))

                if (recipe.ingredients.isEmpty()) {
                    Text(
                        text = stringResource(
                            R.string.recipe_ingredients_empty
                        ),
                        style =
                            MaterialTheme.typography.bodyMedium
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
                            text = "• $ingredientText",
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
private fun ProfileInformationItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.size(3.dp))

        Text(
            text = value.ifBlank { "-" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EditProfileDialog(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onEvent(ProfileEvent.EditProfileDismissed)
        },
        title = {
            Text(
                text = stringResource(
                    R.string.profile_edit_title
                )
            )
        },
        text = {
            Column {
                BiMutfakTextField(
                    value = uiState.editableFirstName,
                    onValueChange = {
                        onEvent(
                            ProfileEvent.FirstNameChanged(it)
                        )
                    },
                    label = stringResource(
                        R.string.profile_first_name
                    ),
                    placeholder = stringResource(
                        R.string.profile_first_name
                    ),
                    enabled = !uiState.isSaving
                )

                Spacer(modifier = Modifier.size(12.dp))

                BiMutfakTextField(
                    value = uiState.editableLastName,
                    onValueChange = {
                        onEvent(
                            ProfileEvent.LastNameChanged(it)
                        )
                    },
                    label = stringResource(
                        R.string.profile_last_name
                    ),
                    placeholder = stringResource(
                        R.string.profile_last_name
                    ),
                    enabled = !uiState.isSaving
                )

                uiState.errorMessageResId?.let {
                        messageResId ->

                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = stringResource(messageResId),
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(
                        ProfileEvent.SaveProfileClicked
                    )
                },
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(
                            R.string.profile_save
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(
                        ProfileEvent.EditProfileDismissed
                    )
                },
                enabled = !uiState.isSaving
            ) {
                Text(
                    text = stringResource(
                        R.string.common_cancel
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
private fun ProfileEmptyText(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color =
            MaterialTheme.colorScheme.onSurfaceVariant
    )
}