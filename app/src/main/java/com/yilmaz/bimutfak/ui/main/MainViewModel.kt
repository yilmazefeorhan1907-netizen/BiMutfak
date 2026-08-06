package com.yilmaz.bimutfak.ui.main

import androidx.lifecycle.ViewModel
import com.yilmaz.bimutfak.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Ana ekrandaki oturum işlemlerini yönetir.
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun logout() {
        authRepository.logout()
    }
}