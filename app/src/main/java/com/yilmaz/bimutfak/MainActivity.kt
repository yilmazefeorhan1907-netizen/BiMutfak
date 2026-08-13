package com.yilmaz.bimutfak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.yilmaz.bimutfak.core.navigation.AppNavGraph
import com.yilmaz.bimutfak.ui.splash.SplashScreen
import com.yilmaz.bimutfak.ui.theme.BiMutfakTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {
            BiMutfakTheme {
                var isSplashVisible by rememberSaveable {
                    mutableStateOf(true)
                }

                if (isSplashVisible) {
                    SplashScreen(
                        onFinished = {
                            isSplashVisible = false
                        }
                    )
                } else {
                    AppNavGraph()
                }
            }
        }
    }
}