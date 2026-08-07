package com.yilmaz.bimutfak.ui.pantry

import androidx.annotation.StringRes
import com.yilmaz.bimutfak.domain.model.PantryItem
import com.yilmaz.bimutfak.domain.model.PantrySection

// Dolabım ekranında gösterilen güncel arayüz durumunu tutar.
data class PantryUiState(

    val items: List<PantryItem> = emptyList(),
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val selectedSection: PantrySection = PantrySection.DRY_FOOD,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isAddItemDialogVisible: Boolean = false,
    val deletingItemId: String? = null,

    @StringRes val errorMessageResId: Int? = null
)