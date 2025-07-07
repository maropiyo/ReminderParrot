package com.maropiyo.reminderparrot.data.local

import com.maropiyo.reminderparrot.db.ReminderParrotDatabase
import com.maropiyo.reminderparrot.domain.entity.NotificationHistory
import kotlinx.datetime.Clock

/**
 * 通知送信履歴のローカルデータソース
 * 1投稿につき1ユーザー1回まで送信可能な制限を管理
 */
class NotificationHistoryLocalDataSource(
    private val database: ReminderParrotDatabase
) {
    /**
     * 通知送信履歴を記録する
     *
     * @param postId 投稿ID
     * @param senderUserId 送信者ユーザーID
     * @return 記録した通知履歴
     */
    fun recordNotificationHistory(postId: String, senderUserId: String): NotificationHistory {
        val history = NotificationHistory(
            id = generateId(),
            postId = postId,
            senderUserId = senderUserId,
            sentAt = Clock.System.now()
        )

        database.reminderParrotDatabaseQueries.insertNotificationHistory(
            id = history.id,
            post_id = history.postId,
            sender_user_id = history.senderUserId,
            sent_at = history.sentAt.toEpochMilliseconds()
        )

        return history
    }

    /**
     * 送信履歴を確認する（送信済みかチェック）
     *
     * @param postId 投稿ID
     * @param senderUserId 送信者ユーザーID
     * @return 送信済みの場合true
     */
    fun hasAlreadySent(postId: String, senderUserId: String): Boolean {
        return database.reminderParrotDatabaseQueries.checkNotificationHistory(
            postId,
            senderUserId
        ).executeAsOne() > 0
    }

    /**
     * 特定投稿の送信履歴を削除する
     *
     * @param postId 投稿ID
     */
    fun deleteHistoryForPost(postId: String) {
        database.reminderParrotDatabaseQueries.deleteNotificationHistoryForPost(postId)
    }

    /**
     * 一意のIDを生成する
     */
    private fun generateId(): String {
        return "notification_history_${Clock.System.now().toEpochMilliseconds()}_${(0..9999).random()}"
    }
}
