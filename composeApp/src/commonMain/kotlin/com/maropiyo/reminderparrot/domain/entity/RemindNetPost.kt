package com.maropiyo.reminderparrot.domain.entity

import kotlinx.datetime.Instant

/**
 * リマインネット投稿エンティティ
 * SNS機能で共有されるリマインダー投稿を表す
 */
data class RemindNetPost(
    val id: String,
    val reminderText: String,
    val userId: String?,
    val userName: String = "Anonymous",
    val createdAt: Instant,
    val forgetAt: Instant,
    val likesCount: Int = 0,
    val isDeleted: Boolean = false
)
