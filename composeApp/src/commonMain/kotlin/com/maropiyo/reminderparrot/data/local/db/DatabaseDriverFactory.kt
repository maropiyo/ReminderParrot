package com.maropiyo.reminderparrot.data.local.db

import app.cash.sqldelight.db.SqlDriver

/**
 * データベースドライバファクトリ
 *
 * SQLDelightのドライバを提供するインターフェース
 */
expect class DatabaseDriverFactory {
    /**
     * データベースドライバを作成する
     *
     * @return SQLDelightのドライバ
     */
    fun createDriver(): SqlDriver
}
