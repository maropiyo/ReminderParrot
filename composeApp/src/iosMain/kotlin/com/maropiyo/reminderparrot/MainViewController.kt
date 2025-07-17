package com.maropiyo.reminderparrot

import androidx.compose.ui.window.ComposeUIViewController
import com.maropiyo.reminderparrot.config.getSupabaseConfig
import com.maropiyo.reminderparrot.di.databaseModule
import com.maropiyo.reminderparrot.di.initKoin
import com.maropiyo.reminderparrot.di.platformModule
import com.maropiyo.reminderparrot.ui.components.common.ad.AdFactory
import com.maropiyo.reminderparrot.ui.components.common.ad.BannerAdViewFactory
import com.maropiyo.reminderparrot.ui.components.common.ad.IOSAdFactory
import org.koin.dsl.module
import platform.UIKit.UIViewController

// iOSアプリのエントリポイント
fun MainViewController(bannerAdViewFactory: BannerAdViewFactory): UIViewController {
    // Koinの初期化
    initKoin(
        supabaseConfig = getSupabaseConfig(),
        additionalModules =
        listOf(
            databaseModule,
            platformModule,
            // AdMob
            module {
                single<AdFactory> { IOSAdFactory(bannerAdViewFactory) }
            }
        )
    )

    // ComposeUIの作成
    return ComposeUIViewController {
        App()
    }
}
