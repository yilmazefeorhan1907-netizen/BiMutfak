package com.yilmaz.bimutfak.data.repository


import com.yilmaz.bimutfak.data.firestore.FirestorePantryDataSource
import com.yilmaz.bimutfak.domain.model.PantryItem
import com.yilmaz.bimutfak.domain.model.PantrySection
import javax.inject.Inject
import javax.inject.Singleton
import com.yilmaz.bimutfak.domain.repository.PantryRepositoryContract
import com.yilmaz.bimutfak.domain.repository.HouseholdRepositoryContract

// Dolap verileriyle ilgili uygulama işlemlerini yönetir.
@Singleton
class PantryRepository @Inject constructor(
    private val householdRepository: HouseholdRepositoryContract,
    private val pantryDataSource: FirestorePantryDataSource
) : PantryRepositoryContract {
    override suspend fun getItems(): List<PantryItem> {
        return pantryDataSource.getItems(
            userId = householdRepository.getDataOwnerId()
        )
    }

    override suspend fun addItem(
        name: String,
        quantity: Double,
        unit: String,
        section: PantrySection
    ): PantryItem {
        val item = PantryItem(
            name = name.trim(),
            quantity = quantity,
            unit = unit.trim(),
            section = section,
            createdAt = System.currentTimeMillis()
        )

        return pantryDataSource.saveItem(
            userId = householdRepository.getDataOwnerId(),
            item = item
        )
    }

    override suspend fun deleteItem(
        itemId: String
    ) {
        pantryDataSource.deleteItem(
            userId = householdRepository.getDataOwnerId(),
            itemId = itemId
        )
    }
}