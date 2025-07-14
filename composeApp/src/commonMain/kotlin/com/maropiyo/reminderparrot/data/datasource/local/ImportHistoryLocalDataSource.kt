package com.maropiyo.reminderparrot.data.datasource.local

import com.maropiyo.reminderparrot.db.ReminderParrotDatabase
import com.maropiyo.reminderparrot.domain.entity.ImportHistory
import kotlinx.datetime.Clock

/**
 * インポート履歴のローカルデータソース
 * 1投稿につき1ユーザー1回までインポート可能な制限を管理
 */
class ImportHistoryLocalDataSource(
    private val database: ReminderParrotDatabase
) {
    /**
     * インポート履歴を記録する
     *
     * @param postId 投稿ID
     * @param importerUserId インポートしたユーザーID
     * @return 記録したインポート履歴
     */
    fun recordImportHistory(postId: String, importerUserId: String): ImportHistory {
        val history = ImportHistory(
            id = generateId(),
            postId = postId,
            importerUserId = importerUserId,
            importedAt = Clock.System.now()
        )

        database.reminderParrotDatabaseQueries.insertImportHistory(
            id = history.id,
            post_id = history.postId,
            importer_user_id = history.importerUserId,
            imported_at = history.importedAt.toEpochMilliseconds()
        )

        return history
    }

    /**
     * インポート履歴を確認する（インポート済みかチェック）
     *
     * @param postId 投稿ID
     * @param importerUserId インポートしたユーザーID
     * @return インポート済みの場合true
     */
    fun hasAlreadyImported(postId: String, importerUserId: String): Boolean {
        return database.reminderParrotDatabaseQueries.checkImportHistory(
            postId,
            importerUserId
        ).executeAsOne() > 0
    }

    /**
     * 特定投稿のインポート履歴を削除する
     *
     * @param postId 投稿ID
     */
    fun deleteHistoryForPost(postId: String) {
        database.reminderParrotDatabaseQueries.deleteImportHistoryForPost(postId)
    }

    /**
     * 一意のIDを生成する
     */
    private fun generateId(): String {
        return "import_history_${Clock.System.now().toEpochMilliseconds()}_${(0..9999).random()}"
    }
}
