package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.datasource.local.ImportHistoryLocalDataSource
import com.maropiyo.reminderparrot.domain.entity.ImportHistory
import com.maropiyo.reminderparrot.domain.repository.ImportHistoryRepository

/**
 * インポート履歴リポジトリの実装
 */
class ImportHistoryRepositoryImpl(
    private val localDataSource: ImportHistoryLocalDataSource
) : ImportHistoryRepository {

    override fun recordImportHistory(postId: String, importerUserId: String): ImportHistory {
        return localDataSource.recordImportHistory(postId, importerUserId)
    }

    override fun hasAlreadyImported(postId: String, importerUserId: String): Boolean {
        return localDataSource.hasAlreadyImported(postId, importerUserId)
    }

    override fun deleteHistoryForPost(postId: String) {
        localDataSource.deleteHistoryForPost(postId)
    }
}
