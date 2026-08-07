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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yilmaz.bimutfak.R

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
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
            stringResource(R.string.profile_greeting_generic)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.size(16.dp))

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        ProfileSectionCard(
            title = stringResource(R.string.profile_daily_menu_title),
            icon = Icons.Outlined.RestaurantMenu
        ) {
            if (uiState.dailyMenu.isEmpty()) {
                ProfileEmptyText(
                    text = stringResource(
                        R.string.profile_daily_menu_empty
                    )
                )
            } else {
                uiState.dailyMenu.forEach { menuItem ->
                    ProfileListItem(text = menuItem)
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ProfileSectionCard(
            title = stringResource(R.string.profile_favorites_title),
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
                    ProfileListItem(text = recipe)
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
                text = stringResource(R.string.profile_logout)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))
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
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.25f
                )
            )

            Spacer(modifier = Modifier.size(12.dp))

            content()
        }
    }
}

@Composable
private fun ProfileListItem(
    text: String
) {
    Row(
        modifier = Modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ProfileEmptyText(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}