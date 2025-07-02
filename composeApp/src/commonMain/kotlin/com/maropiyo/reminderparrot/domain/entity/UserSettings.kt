package com.maropiyo.reminderparrot.domain.entity

/**
 * ユーザー設定のエンティティ
 *
 * @property isRemindNetSharingEnabled リマインネットへの投稿を有効にするかどうか
 */
data class UserSettings(
    val isRemindNetSharingEnabled: Boolean = false
)
