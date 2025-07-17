package com.maropiyo.reminderparrot.ui.components.common.ad

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView

class IOSAdFactory(
    private val bannerAdViewFactory: BannerAdViewFactory
) : AdFactory {
    @Composable
    override fun BannerAd(modifier: Modifier) {
        UIKitView(
            factory = {
                bannerAdViewFactory.createBannerAdView()
            },
            modifier = modifier.fillMaxWidth().height(50.dp)
        )
    }

    @Composable
    override fun NativeAd(modifier: Modifier) {
        // TODO: iOS用のNativeAd実装
        // GADNativeAdを使用してiOS向けのネイティブ広告を実装する予定
        Box(
            modifier = modifier.fillMaxWidth().height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("iOS NativeAd - 実装予定")
        }
    }
}
