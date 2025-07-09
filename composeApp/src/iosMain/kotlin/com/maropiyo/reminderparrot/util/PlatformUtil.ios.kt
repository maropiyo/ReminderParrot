package com.maropiyo.reminderparrot.util

/**
 * iOS用プラットフォーム判定ユーティリティ
 */
actual class PlatformUtil {
    actual fun isAndroid(): Boolean = false
    actual fun isIOS(): Boolean = true
}
