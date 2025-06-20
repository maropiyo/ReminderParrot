package com.maropiyo.reminderparrot

import androidx.compose.ui.window.ComposeUIViewController
import com.maropiyo.reminderparrot.config.getSupabaseConfig
import com.maropiyo.reminderparrot.di.databaseModule
import com.maropiyo.reminderparrot.di.initKoin
import com.maropiyo.reminderparrot.di.platformModule
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // Koinの初期化
    initKoin(
        supabaseConfig = getSupabaseConfig(),
        additionalModules =
        listOf(
            databaseModule,
            platformModule
        )
    )

    // ComposeUIの作成
    return ComposeUIViewController {
        App()
    }
}
