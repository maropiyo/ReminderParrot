package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.data.datasource.local.NotificationHistoryLocalDataSource
import com.maropiyo.reminderparrot.domain.entity.RemindNetNotification
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository
import com.maropiyo.reminderparrot.domain.service.AuthService

/**
 * リマインド通知送信UseCase
 *
 * リマインネットの投稿者にプッシュ通知を送信する
 * 1投稿につき1ユーザー1回まで送信可能な制限を適用
 */
class SendRemindNotificationUseCase(
    private val remindNetRepository: RemindNetRepository,
    private val authService: AuthService,
    private val notificationHistoryLocalDataSource: NotificationHistoryLocalDataSource
) {
    suspend operator fun invoke(post: RemindNetPost): Result<Unit> {
        // 現在のユーザー情報を取得
        val currentUserId = authService.getCurrentUserId()
            ?: return Result.failure(Exception("ユーザーがログインしていません"))

        val currentUserName = authService.getDisplayName()
            ?: "ひよっこインコ"

        // 自分の投稿には通知を送らない
        if (post.userId == currentUserId) {
            return Result.failure(Exception("自分の投稿には通知を送れません"))
        }

        // 送信制限チェック（1投稿につき1ユーザー1回まで）
        if (notificationHistoryLocalDataSource.hasAlreadySent(post.id, currentUserId)) {
            return Result.failure(Exception("この投稿には既に通知を送信済みです"))
        }

        // 通知エンティティを作成
        val notification = RemindNetNotification(
            postId = post.id,
            postUserId = post.userId ?: return Result.failure(Exception("投稿者のユーザーIDが不明です")),
            senderUserId = currentUserId,
            senderUserName = currentUserName,
            reminderText = post.reminderText
        )

        // 通知を送信
        println("SendRemindNotificationUseCase: 通知送信開始")
        println("  送信先ユーザーID: ${notification.postUserId}")
        println("  送信元: ${notification.senderUserName}")
        println("  リマインダー: ${notification.reminderText}")

        val result = remindNetRepository.sendRemindNotification(notification)

        result.onSuccess {
            // 送信成功時に履歴を記録
            notificationHistoryLocalDataSource.recordNotificationHistory(post.id, currentUserId)
            println("SendRemindNotificationUseCase: 通知送信成功")
        }.onFailure { error ->
            println("SendRemindNotificationUseCase: 通知送信失敗 - $error")
        }

        return result
    }
}
