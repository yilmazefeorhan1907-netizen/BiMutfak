package com.yilmaz.bimutfak.data.local.mapper

import com.yilmaz.bimutfak.data.local.entity.CuisineEntity
import com.yilmaz.bimutfak.domain.model.Cuisine

fun Cuisine.toCuisineEntity(
    cachedAt: Long
): CuisineEntity {
    return CuisineEntity(
        name = name,
        country = country,
        cachedAt = cachedAt
    )
}

fun CuisineEntity.toCuisine(): Cuisine {
    return Cuisine(
        name = name,
        country = country
    )
}