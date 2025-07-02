package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.UserSettings
import com.maropiyo.reminderparrot.domain.service.UserSettingsService

/**
 * ユーザー設定保存ユースケース
 */
class SaveUserSettingsUseCase(
    private val userSettingsService: UserSettingsService
) {
    suspend operator fun invoke(settings: UserSettings) {
        userSettingsService.saveUserSettings(settings)
    }
}
