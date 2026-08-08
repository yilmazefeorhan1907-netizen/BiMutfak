package com.yilmaz.bimutfak.ui.basket

// Kullanıcının Bi’Sepet ekranında gerçekleştirebileceği işlemleri tanımlar.
sealed interface BasketEvent {

    data class NameChanged(
        val name: String
    ) : BasketEvent

    data class QuantityChanged(
        val quantity: String
    ) : BasketEvent

    data class UnitChanged(
        val unit: String
    ) : BasketEvent

    data class ItemCheckedChanged(
        val itemId: String,
        val checked: Boolean
    ) : BasketEvent

    data class DeleteItemClicked(
        val itemId: String
    ) : BasketEvent

    data object AddItemRequested : BasketEvent

    data object AddItemDismissed : BasketEvent

    data object SaveItemClicked : BasketEvent

    data object RetryClicked : BasketEvent

    data object ClearError : BasketEvent
}