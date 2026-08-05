package com.yilmaz.bimutfak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yilmaz.bimutfak.core.navigation.AppNavGraph
import com.yilmaz.bimutfak.ui.theme.BiMutfakTheme
import dagger.hilt.android.AndroidEntryPoint

// MainActivity'nin Hilt tarafından sağlanan bağımlılıkları kullanmasını sağlar.
// Android her Activity açıldığında bir Activity nesnesine ihtiyaç duyar. Bu nedenle MainActivity, oluşturulabilir bir sınıftır.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BiMutfakTheme {
                AppNavGraph()
            }
        }
    }
}