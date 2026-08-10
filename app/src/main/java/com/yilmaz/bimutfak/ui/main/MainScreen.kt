package com.yilmaz.bimutfak.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yilmaz.bimutfak.ui.pantry.PantryRoute
import com.yilmaz.bimutfak.ui.basket.BasketRoute
import com.yilmaz.bimutfak.ui.household.HouseholdRoute
import com.yilmaz.bimutfak.ui.recipe.RecipeRoute

// MainViewModel ile ana ekran arasındaki bağlantıyı kurar.
@Composable
fun MainRoute(
    onLoggedOut: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    MainScreen(
        onLogout = {
            viewModel.logout()
            onLoggedOut()
        }
    )
}

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    // Ana bölümlerin kendi navigation geçmişini yönetir.
    val mainNavController = rememberNavController()

    // Alt menüde hangi bölümün seçili olduğunu takip eder.
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route
        ?: MainDestination.Profile.route

    // Bütün alt menü geçişlerinin aynı kuralları kullanmasını sağlar.
    val navigateToDestination: (MainDestination) -> Unit = { destination ->
        mainNavController.navigate(destination.route) {
            popUpTo(
                mainNavController.graph
                    .findStartDestination()
                    .id
            ) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BiMutfakBottomBar(
                currentRoute = currentRoute,
                onDestinationSelected = navigateToDestination
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = mainNavController,
            startDestination = MainDestination.Profile.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MainDestination.entries.forEach { destination ->
                composable(destination.route) {
                    when (destination) {

                        MainDestination.Profile -> {
                            ProfileRoute(
                                onLogout = onLogout
                            )
                        }

                        MainDestination.Pantry -> {
                            PantryRoute()
                        }

                        MainDestination.Basket -> {
                            BasketRoute()
                        }

                        MainDestination.Recipes -> {
                            RecipeRoute()
                        }
                        MainDestination.Household -> {
                            HouseholdRoute()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BiMutfakBottomBar(
    currentRoute: String,
    onDestinationSelected: (MainDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        MainDestination.entries.forEach { destination ->

            val label = stringResource(destination.labelResId)
            val isSelected = currentRoute == destination.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    onDestinationSelected(destination)
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =
                        MaterialTheme.colorScheme.primary,
                    selectedTextColor =
                        MaterialTheme.colorScheme.primary,
                    indicatorColor =
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.12f
                        ),
                    unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

