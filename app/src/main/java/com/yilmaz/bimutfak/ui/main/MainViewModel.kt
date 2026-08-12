package com.yilmaz.bimutfak.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.yilmaz.bimutfak.domain.usecase.auth.LogoutUseCase

// Ana ekrandaki oturum işlemlerini yönetir.
@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    fun logout() {
        logoutUseCase()
    }
}