package com.maropiyo.reminderparrot.util

import platform.Foundation.NSBundle

/**
 * iOS用のビルド設定実装
 */
actual object BuildConfig {
    actual val isDebug: Boolean = run {
        // iOS のデバッグビルドかどうかをBundle情報から判定
        val mainBundle = NSBundle.mainBundle
        val debugMarker = mainBundle.objectForInfoDictionaryKey("DEBUG") as? String
        debugMarker == "1" ||
            // または開発者証明書が使用されているかチェック
            mainBundle.objectForInfoDictionaryKey("CFBundleIdentifier")
            ?.toString()?.contains(".debug") == true ||
            // アプリケーション名に "Debug" が含まれているかチェック
            (mainBundle.objectForInfoDictionaryKey("CFBundleName") as? String)
            ?.contains("Debug", ignoreCase = true) == true
    }
}
