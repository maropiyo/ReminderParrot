package com.maropiyo.reminderparrot.ui.components.common.ad

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView

class IOSAdFactory(
    private val bannerAdViewFactory: BannerAdViewFactory,
    private val nativeAdViewFactory: NativeAdViewFactory
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
        UIKitView(
            factory = {
                nativeAdViewFactory.createNativeAdView()
            },
            modifier = modifier.fillMaxWidth().height(50.dp)
        )
    }
}
