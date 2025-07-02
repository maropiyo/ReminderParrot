package com.maropiyo.reminderparrot.data.service

import com.maropiyo.reminderparrot.domain.entity.UserSettings
import com.maropiyo.reminderparrot.domain.service.UserSettingsService
import platform.Foundation.NSUserDefaults

/**
 * iOS用のユーザー設定サービス
 */
class IOSUserSettingsService : UserSettingsService {

    companion object {
        private const val KEY_REMIND_NET_SHARING_ENABLED = "remind_net_sharing_enabled"
    }

    override suspend fun getUserSettings(): UserSettings {
        val userDefaults = NSUserDefaults.standardUserDefaults
        return UserSettings(
            isRemindNetSharingEnabled = userDefaults.boolForKey(KEY_REMIND_NET_SHARING_ENABLED)
        )
    }

    override suspend fun saveUserSettings(settings: UserSettings) {
        val userDefaults = NSUserDefaults.standardUserDefaults
        userDefaults.setBool(settings.isRemindNetSharingEnabled, KEY_REMIND_NET_SHARING_ENABLED)
    }
}
