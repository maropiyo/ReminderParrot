package com.maropiyo.reminderparrot.util

import platform.Foundation.NSBundle

/**
 * iOS用のビルド設定実装
 */
actual object BuildConfig {
    actual val isDebug: Boolean = run {
        // iOS のデバッグビルドかどうかをBundle情報から判定
        val mainBundle = NSBundle.mainBundle
        val debugMode = mainBundle.objectForInfoDictionaryKey("IS_DEBUG") as? String
        debugMode == "YES"
    }
}
