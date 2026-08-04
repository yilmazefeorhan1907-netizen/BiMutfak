package com.yilmaz.bimutfak.core.navigation

sealed class AppRoute(val route: String) {

    data object Login : AppRoute("login")

    data object Register : AppRoute("register")

    data object Main : AppRoute("main")
}