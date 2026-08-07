package com.yilmaz.bimutfak.ui.pantry

import com.yilmaz.bimutfak.domain.model.PantrySection

// Kullanıcının Dolabım ekranında gerçekleştirebileceği işlemleri tanımlar.
sealed interface PantryEvent {

    data class NameChanged(
        val name: String
    ) : PantryEvent

    data class QuantityChanged(
        val quantity: String
    ) : PantryEvent

    data class UnitChanged(
        val unit: String
    ) : PantryEvent

    data class SectionChanged(
        val section: PantrySection
    ) : PantryEvent

    data class DeleteItemClicked(
        val itemId: String
    ) : PantryEvent

    data object AddItemRequested : PantryEvent

    data object AddItemDismissed : PantryEvent

    data object SaveItemClicked : PantryEvent

    data object RetryClicked : PantryEvent

    data object ClearError : PantryEvent
}