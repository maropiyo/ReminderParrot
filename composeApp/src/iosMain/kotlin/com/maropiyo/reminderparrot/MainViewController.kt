package com.maropiyo.reminderparrot

import androidx.compose.ui.window.ComposeUIViewController
import com.maropiyo.reminderparrot.di.initKoin
import platform.Foundation.NSBundle
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // Info.plistに設定したSUPABASE_URLとSUPABASE_KEYを取得
    val supabaseUrl = NSBundle.mainBundle.objectForInfoDictionaryKey("SUPABASE_URL") as? String ?: ""
    val supabaseKey = NSBundle.mainBundle.objectForInfoDictionaryKey("SUPABASE_KEY") as? String ?: ""

    // Koinの初期化
    initKoin(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    )

    // ComposeUIの作成
    return ComposeUIViewController {
        App()
    }
}
