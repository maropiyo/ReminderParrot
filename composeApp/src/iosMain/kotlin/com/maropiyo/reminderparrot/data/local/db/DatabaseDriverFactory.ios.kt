package com.maropiyo.reminderparrot.data.local.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.maropiyo.reminderparrot.db.ReminderParrotDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

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
    actual fun createDriver(): SqlDriver {
        // ドキュメントディレクトリのパスを取得
        val documentPath = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).first() as String

        // データベースファイルのフルパスを作成
        val databasePath = "$documentPath/ReminderParrot.db"

        return NativeSqliteDriver(
            schema = ReminderParrotDatabase.Schema,
            name = databasePath
        )
    }
}
