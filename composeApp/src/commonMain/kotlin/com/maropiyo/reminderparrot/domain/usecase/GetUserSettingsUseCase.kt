package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.UserSettings
import com.maropiyo.reminderparrot.domain.service.UserSettingsService

/**
 * ユーザー設定取得ユースケース
 */
class GetUserSettingsUseCase(
    private val userSettingsService: UserSettingsService
) {
    suspend operator fun invoke(): UserSettings {
        return userSettingsService.getUserSettings()
    }
}
