package com.yilmaz.bimutfak.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.ui.theme.BackgroundSoftBlue
import com.yilmaz.bimutfak.ui.theme.Plum
import com.yilmaz.bimutfak.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
                painter = painterResource(
                    R.mipmap.ic_launcher_foreground
                ),
            contentDescription = stringResource(
                R.string.app_name
            ),
            modifier = Modifier.size(180.dp)
        )

        Text(
            text = stringResource(
                R.string.splash_tagline
            ),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = Plum
        )

        Text(
            text = stringResource(
                R.string.splash_byline
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

private const val SPLASH_DURATION_MILLIS =
    1500L