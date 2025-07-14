package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.datasource.local.NotificationHistoryLocalDataSource
import com.maropiyo.reminderparrot.domain.repository.NotificationHistoryRepository

/**
 * 通知履歴リポジトリの実装
 */
class NotificationHistoryRepositoryImpl(
    private val localDataSource: NotificationHistoryLocalDataSource
) : NotificationHistoryRepository {

    override fun hasAlreadySent(postId: String, senderUserId: String): Boolean {
        return localDataSource.hasAlreadySent(postId, senderUserId)
    }

    override fun recordSendHistory(postId: String, senderUserId: String) {
        localDataSource.recordNotificationHistory(postId, senderUserId)
    }
}
