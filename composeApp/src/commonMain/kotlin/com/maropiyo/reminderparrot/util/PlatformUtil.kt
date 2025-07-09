package com.maropiyo.reminderparrot.util

/**
 * プラットフォーム判定ユーティリティ
 */
expect class PlatformUtil {
    fun isAndroid(): Boolean
    fun isIOS(): Boolean
}
