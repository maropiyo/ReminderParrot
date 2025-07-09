package com.maropiyo.reminderparrot.domain.entity

import kotlinx.datetime.Instant

/**
 * プッシュ通知トークンエンティティ
 *
 * ユーザーのデバイストークンを管理するエンティティ
 */
data class PushNotificationToken(
    val id: String,
    val userId: String,
    val token: String,
    val platform: Platform,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isActive: Boolean = true
)

/**
 * プラットフォーム
 */
enum class Platform {
    ANDROID,
    IOS
}
