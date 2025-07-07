package com.maropiyo.reminderparrot.domain.entity

import kotlinx.datetime.Instant

/**
 * 通知送信履歴エンティティ
 * 1投稿につき1ユーザー1回まで送信可能な制限を管理
 */
data class NotificationHistory(
    val id: String,
    val postId: String,
    val senderUserId: String,
    val sentAt: Instant
)
