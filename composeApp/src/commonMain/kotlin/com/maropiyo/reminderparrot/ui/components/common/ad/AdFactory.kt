package com.maropiyo.reminderparrot.ui.components.common.ad

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface AdFactory {
    @Composable
    fun BannerAd(modifier: Modifier)

    @Composable
    fun NativeAd(modifier: Modifier)
}
