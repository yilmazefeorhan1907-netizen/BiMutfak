package com.yilmaz.bimutfak.domain.usecase.basket

import com.yilmaz.bimutfak.domain.model.BasketItem
import com.yilmaz.bimutfak.domain.repository.BasketRepositoryContract
import javax.inject.Inject

class GetBasketItemsUseCase @Inject constructor(
    private val repository: BasketRepositoryContract
) {

    suspend operator fun invoke(): List<BasketItem> {
        return repository.getItems()
    }
}

class AddBasketItemUseCase @Inject constructor(
    private val repository: BasketRepositoryContract
) {

    suspend operator fun invoke(
        name: String,
        quantity: Double,
        unit: String
    ): BasketItem {
        return repository.addItem(
            name = name,
            quantity = quantity,
            unit = unit
        )
    }
}

class SetBasketItemCheckedUseCase @Inject constructor(
    private val repository: BasketRepositoryContract
) {

    suspend operator fun invoke(
        item: BasketItem,
        checked: Boolean
    ): BasketItem {
        return repository.setItemChecked(
            item = item,
            checked = checked
        )
    }
}

class DeleteBasketItemUseCase @Inject constructor(
    private val repository: BasketRepositoryContract
) {

    suspend operator fun invoke(
        itemId: String
    ) {
        repository.deleteItem(itemId)
    }
}