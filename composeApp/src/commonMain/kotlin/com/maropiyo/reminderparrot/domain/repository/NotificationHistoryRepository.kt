package com.maropiyo.reminderparrot.domain.repository

/**
 * 通知履歴リポジトリ
 */
interface NotificationHistoryRepository {
    /**
     * 通知送信履歴を確認する
     *
     * @param postId 投稿ID
     * @param senderUserId 送信者ユーザーID
     * @return 送信済みの場合true
     */
    fun hasAlreadySent(postId: String, senderUserId: String): Boolean

    /**
     * 通知送信履歴を記録する
     *
     * @param postId 投稿ID
     * @param senderUserId 送信者ユーザーID
     */
    fun recordSendHistory(postId: String, senderUserId: String)
}
