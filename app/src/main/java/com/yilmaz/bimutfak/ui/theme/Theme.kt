package com.yilmaz.bimutfak.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BiMutfakColorScheme = lightColorScheme(

    // Ana marka renkleri
    primary = Plum,
    onPrimary = White,

    secondary = OliveGreen,
    onSecondary = White,

    tertiary = ButterYellow,
    onTertiary = TextPrimary,

    // Ekran ve kart yüzeyleri
    background = BackgroundSoftBlue,
    onBackground = TextPrimary,

    surface = BackgroundSoftBlue,
    onSurface = TextPrimary,

    surfaceVariant = SurfaceInput,
    onSurfaceVariant = TextSecondary,

    // Çerçeveler ve hata durumları
    outline = OutlineSoft,

    error = ErrorRed,
    onError = White
)

@Composable
fun BiMutfakTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BiMutfakColorScheme,
        typography = Typography,
        shapes = BiMutfakShapes,
        content = content
    )
}
