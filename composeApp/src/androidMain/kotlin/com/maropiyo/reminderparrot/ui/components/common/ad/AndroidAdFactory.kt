package com.maropiyo.reminderparrot.ui.components.common.ad

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class AndroidAdFactory : AdFactory {
    @Composable
    override fun AdBanner(modifier: Modifier) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                AdView(context).apply {
                    adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    setAdSize(
                        AdSize(
                            AdSize.FULL_WIDTH,
                            50
                        )
                    )
                    loadAd(AdRequest.Builder().build())
                }
            },
        )
    }
}
