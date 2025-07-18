package com.maropiyo.reminderparrot.ui.components.common.ad

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import com.maropiyo.reminderparrot.domain.usecase.GetUserSettingsUseCase

class IOSAdFactory(
    private val bannerAdViewFactory: BannerAdViewFactory,
    private val nativeAdViewFactory: NativeAdViewFactory,
    private val getUserSettingsUseCase: GetUserSettingsUseCase
) : AdFactory {
    @Composable
    override fun BannerAd(modifier: Modifier) {
        var userSettings by remember { mutableStateOf<com.maropiyo.reminderparrot.domain.entity.UserSettings?>(null) }

        LaunchedEffect(Unit) {
            userSettings = getUserSettingsUseCase()
        }

        // 広告が無効な場合は何も表示しない
        if (userSettings?.isAdsEnabled == false) {
            return
        }

        UIKitView(
            factory = {
                bannerAdViewFactory.createBannerAdView()
            },
            modifier = modifier.fillMaxWidth().height(50.dp)
        )
    }

    @Composable
    override fun NativeAd(modifier: Modifier, adPosition: Int) {
        var userSettings by remember { mutableStateOf<com.maropiyo.reminderparrot.domain.entity.UserSettings?>(null) }

        LaunchedEffect(Unit) {
            userSettings = getUserSettingsUseCase()
        }

        // 広告が無効な場合は何も表示しない
        if (userSettings?.isAdsEnabled == false) {
            return
        }

        UIKitView(
            factory = {
                nativeAdViewFactory.createNativeAdView(adPosition)
            },
            modifier = modifier.fillMaxWidth().height(54.dp)
        )
    }
}
