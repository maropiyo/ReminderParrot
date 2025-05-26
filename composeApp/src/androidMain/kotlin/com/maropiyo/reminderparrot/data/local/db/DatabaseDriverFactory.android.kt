package com.maropiyo.reminderparrot.data.local.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.maropiyo.reminderparrot.db.ReminderParrotDatabase

/**
 * データベースドライバファクトリ
 *
 * SQLDelightのドライバを提供するインターフェース
 */
actual class DatabaseDriverFactory(
    private val context: Context
) {
    /**
     * データベースドライバを作成する
     */
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = ReminderParrotDatabase.Schema,
        context = context,
        name = "ReminderParrot.db"
    )
}
