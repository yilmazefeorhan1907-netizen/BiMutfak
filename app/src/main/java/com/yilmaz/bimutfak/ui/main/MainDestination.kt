package com.yilmaz.bimutfak.ui.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.ui.graphics.vector.ImageVector
import com.yilmaz.bimutfak.R
import androidx.compose.material.icons.outlined.Person

// Ana ekranın alt menüsünde gösterilecek uygulama bölümlerini tanımlar.
enum class MainDestination(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector
) {

    Pantry(
        route = "pantry",
        labelResId = R.string.main_tab_pantry,
        icon = Icons.Outlined.Kitchen
    ),

    Household(
        route = "household",
        labelResId = R.string.main_tab_household,
        icon = Icons.Outlined.Groups
    ),

    Profile(
        route = "profile",
        labelResId = R.string.main_tab_profile,
        icon = Icons.Outlined.Person
    ),

    Basket(
        route = "basket",
        labelResId = R.string.main_tab_basket,
        icon = Icons.Outlined.ShoppingBasket
    ),

    Recipes(
        route = "recipes",
        labelResId = R.string.main_tab_recipes,
        icon = Icons.Outlined.RestaurantMenu
    )
}