package com.yilmaz.bimutfak.domain.usecase.pantry

import com.yilmaz.bimutfak.domain.model.PantryItem
import com.yilmaz.bimutfak.domain.model.PantrySection
import com.yilmaz.bimutfak.domain.repository.PantryRepositoryContract
import javax.inject.Inject

class GetPantryItemsUseCase @Inject constructor(
    private val repository: PantryRepositoryContract
) {

    suspend operator fun invoke(): List<PantryItem> {
        return repository.getItems()
    }
}

class AddPantryItemUseCase @Inject constructor(
    private val repository: PantryRepositoryContract
) {

    suspend operator fun invoke(
        name: String,
        quantity: Double,
        unit: String,
        section: PantrySection
    ): PantryItem {
        return repository.addItem(
            name = name,
            quantity = quantity,
            unit = unit,
            section = section
        )
    }
}

class DeletePantryItemUseCase @Inject constructor(
    private val repository: PantryRepositoryContract
) {

    suspend operator fun invoke(
        itemId: String
    ) {
        repository.deleteItem(itemId)
    }
}
