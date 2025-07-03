package com.maropiyo.reminderparrot.domain.service

import com.maropiyo.reminderparrot.domain.entity.UserSettings

/**
 * ユーザー設定を管理するサービス
 */
interface UserSettingsService {
    /**
     * ユーザー設定を取得する
     */
    suspend fun getUserSettings(): UserSettings

    /**
     * ユーザー設定を保存する
     */
    suspend fun saveUserSettings(settings: UserSettings)
}
