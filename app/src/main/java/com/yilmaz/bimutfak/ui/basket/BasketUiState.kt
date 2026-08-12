package com.yilmaz.bimutfak.ui.basket

import androidx.annotation.StringRes
import com.yilmaz.bimutfak.domain.model.BasketItem

// Bi’Sepet ekranının güncel arayüz durumunu tutar.
data class BasketUiState(
    val items: List<BasketItem> = emptyList(),
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isAddItemDialogVisible: Boolean = false,
    val processingItemId: String? = null,

    @StringRes val errorMessageResId: Int? = null
)