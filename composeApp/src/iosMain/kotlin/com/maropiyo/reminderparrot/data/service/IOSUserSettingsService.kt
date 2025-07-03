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
        private const val KEY_DEBUG_FAST_MEMORY_ENABLED = "debug_fast_memory_enabled"
    }

    override suspend fun getUserSettings(): UserSettings {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val isRemindNetSharingEnabled = userDefaults.boolForKey(KEY_REMIND_NET_SHARING_ENABLED)
        val isDebugFastMemoryEnabled = userDefaults.boolForKey(KEY_DEBUG_FAST_MEMORY_ENABLED)

        // デバッグ用ログ
        println("IOSUserSettingsService: getUserSettings called")
        println("  isRemindNetSharingEnabled: $isRemindNetSharingEnabled")
        println("  isDebugFastMemoryEnabled: $isDebugFastMemoryEnabled")

        return UserSettings(
            isRemindNetSharingEnabled = isRemindNetSharingEnabled,
            isDebugFastMemoryEnabled = isDebugFastMemoryEnabled
        )
    }

    override suspend fun saveUserSettings(settings: UserSettings) {
        val userDefaults = NSUserDefaults.standardUserDefaults
        userDefaults.setBool(settings.isRemindNetSharingEnabled, KEY_REMIND_NET_SHARING_ENABLED)
        userDefaults.setBool(settings.isDebugFastMemoryEnabled, KEY_DEBUG_FAST_MEMORY_ENABLED)

        // デバッグ用ログ
        println("IOSUserSettingsService: saveUserSettings called")
        println("  isRemindNetSharingEnabled: ${settings.isRemindNetSharingEnabled}")
        println("  isDebugFastMemoryEnabled: ${settings.isDebugFastMemoryEnabled}")
    }
}
