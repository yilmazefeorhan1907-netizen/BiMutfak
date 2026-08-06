package com.yilmaz.bimutfak.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val BiMutfakShapes = Shapes(

    // Küçük rozetler ve ürün miktarı etiketleri
    extraSmall = RoundedCornerShape(6.dp),

    // Küçük butonlar ve filtre alanları
    small = RoundedCornerShape(10.dp),

    // Metin alanları ve standart butonlar
    medium = RoundedCornerShape(14.dp),

    // Ana ekran ve tarif kartları
    large = RoundedCornerShape(18.dp),

    // Büyük içerik kartları ve açılır paneller
    extraLarge = RoundedCornerShape(24.dp)
)