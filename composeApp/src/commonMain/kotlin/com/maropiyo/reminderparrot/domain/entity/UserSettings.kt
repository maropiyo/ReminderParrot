package com.maropiyo.reminderparrot.domain.entity

/**
 * ユーザー設定のエンティティ
 *
 * @property isRemindNetSharingEnabled リマインネットへの投稿を有効にするかどうか
 * @property isDebugFastMemoryEnabled すぐわすれるモード（5秒で忘却）を有効にするかどうか
 */
data class UserSettings(
    val isRemindNetSharingEnabled: Boolean = false,
    val isDebugFastMemoryEnabled: Boolean = false
)
