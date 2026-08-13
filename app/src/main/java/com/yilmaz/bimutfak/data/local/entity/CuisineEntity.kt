package com.yilmaz.bimutfak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cuisines")
data class CuisineEntity(
    @PrimaryKey
    val name: String,
    val country: String,
    val cachedAt: Long
)