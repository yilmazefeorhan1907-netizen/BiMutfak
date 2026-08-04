package com.yilmaz.bimutfak.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val BiMutfakShapes = Shapes(

    // Küçük rozetler ve kompakt alanlar
    extraSmall = RoundedCornerShape(8.dp),

    // Metin alanları ve küçük kartlar
    small = RoundedCornerShape(12.dp),

    // Standart butonlar
    medium = RoundedCornerShape(14.dp),

    // Normal içerik kartları
    large = RoundedCornerShape(16.dp),

    // Dialog, büyük kart ve alt sayfalar
    extraLarge = RoundedCornerShape(24.dp)
)