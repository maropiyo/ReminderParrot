package com.maropiyo.reminderparrot.util

/**
 * ビルド設定を管理するオブジェクト
 */
expect object BuildConfig {
    /**
     * デバッグビルドかどうかを判定する
     */
    val isDebug: Boolean
}
