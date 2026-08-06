package com.yilmaz.bimutfak.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yilmaz.bimutfak.ui.components.BiMutfakButton

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

// Giriş işleminden sonra gösterilecek geçici ana ekranı oluşturur.
@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bi’Mutfak ana ekranı",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        BiMutfakButton(
            text = "Çıkış yap",
            onClick = onLogout
        )
    }
}