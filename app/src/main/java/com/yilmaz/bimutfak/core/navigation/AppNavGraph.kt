package com.yilmaz.bimutfak.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Bu fonksiyonu Compose arayüz sisteminin kullanabileceği hâle getirir.
@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Login.route
    ) {

        composable(AppRoute.Login.route) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Giriş ekranı")

                Button(
                    onClick = {
                        navController.navigate(AppRoute.Register.route)
                    }
                ) {
                    Text(text = "Kayıt ekranına git")
                }

                Button(
                    onClick = {
                        navController.navigate(AppRoute.Main.route)
                    }
                ) {
                    Text(text = "Ana ekrana git")
                }
            }
        }

        composable(AppRoute.Register.route) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Kayıt ekranı")

                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(text = "Giriş ekranına dön")
                }
            }
        }

        composable(AppRoute.Main.route) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Bi’Mutfak ana ekranı")

                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(text = "Geri dön")
                }
            }
        }
    }
}