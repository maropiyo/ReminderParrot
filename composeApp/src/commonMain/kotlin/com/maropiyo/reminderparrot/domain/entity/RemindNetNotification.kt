package com.maropiyo.reminderparrot.domain.entity

/**
 * リマインネット通知エンティティ
 *
 * リマインネットの投稿者に送るプッシュ通知を表すエンティティ
 */
data class RemindNetNotification(
    val postId: String,
    val postUserId: String,
    val senderUserId: String,
    val senderUserName: String,
    val reminderText: String,
    val notificationType: NotificationType = NotificationType.REMIND
)

/**
 * 通知タイプ
 */
enum class NotificationType {
    REMIND // リマインド通知
}
