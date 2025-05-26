package com.maropiyo.reminderparrot.data.local.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.maropiyo.reminderparrot.db.ReminderParrotDatabase

/**
 * データベースドライバファクトリ
 *
 * SQLDelightのドライバを提供するインターフェース
 */
actual class DatabaseDriverFactory {
    /**
     * データベースドライバを作成する
     *
     * @return SQLDelightのドライバ
     */
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        schema = ReminderParrotDatabase.Schema,
        name = "ReminderParrot.db"
    )
}
