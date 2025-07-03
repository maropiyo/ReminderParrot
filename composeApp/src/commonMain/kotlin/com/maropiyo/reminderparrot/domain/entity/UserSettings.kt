package com.maropiyo.reminderparrot.domain.entity

import com.maropiyo.reminderparrot.util.BuildConfig

/**
 * ユーザー設定のエンティティ
 *
 * @property isRemindNetSharingEnabled リマインネットへの投稿を有効にするかどうか
 * @property isDebugFastMemoryEnabled すぐわすれるモード（短時間で忘却）を有効にするかどうか
 * @property debugForgetTimeSeconds すぐわすれるモード時の忘却時間（秒）
 */
data class UserSettings(
    val isRemindNetSharingEnabled: Boolean = false,
    val isDebugFastMemoryEnabled: Boolean = false && BuildConfig.isDebug,
    val debugForgetTimeSeconds: Int = 10
)
