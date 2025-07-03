package com.maropiyo.reminderparrot.util

import com.maropiyo.reminderparrot.BuildConfig as AndroidBuildConfig

/**
 * Android用のビルド設定実装
 */
actual object BuildConfig {
    actual val isDebug: Boolean = AndroidBuildConfig.DEBUG
}
