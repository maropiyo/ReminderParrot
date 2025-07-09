package com.maropiyo.reminderparrot.util

/**
 * Android用プラットフォーム判定ユーティリティ
 */
actual class PlatformUtil {
    actual fun isAndroid(): Boolean = true
    actual fun isIOS(): Boolean = false
}
