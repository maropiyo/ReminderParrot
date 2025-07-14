package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.repository.NotificationHistoryRepository

/**
 * 通知履歴確認ユースケース
 * 特定の投稿に既に通知を送信済みかどうかを確認する
 */
class CheckNotificationHistoryUseCase(
    private val notificationHistoryRepository: NotificationHistoryRepository
) {
    /**
     * 通知履歴を確認する
     *
     * @param postId 投稿ID
     * @param senderUserId 送信者ユーザーID
     * @return 送信済みの場合true
     */
    operator fun invoke(postId: String, senderUserId: String): Boolean {
        return notificationHistoryRepository.hasAlreadySent(postId, senderUserId)
    }
}
