package com.maropiyo.reminderparrot.data.service

import com.maropiyo.reminderparrot.domain.entity.UserSettings
import com.maropiyo.reminderparrot.domain.service.UserSettingsService
import platform.Foundation.NSUserDefaults

/**
 * iOS用のユーザー設定サービス
 */
class IOSUserSettingsService : UserSettingsService {

    companion object {
        private const val KEY_PARROT_NAME = "parrot_name"
        private const val KEY_REMIND_NET_SHARING_ENABLED = "remind_net_sharing_enabled"
        private const val KEY_DEBUG_FAST_MEMORY_ENABLED = "debug_fast_memory_enabled"
        private const val KEY_DEBUG_FORGET_TIME_SECONDS = "debug_forget_time_seconds"
    }

    override suspend fun getUserSettings(): UserSettings {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val parrotName = userDefaults.stringForKey(KEY_PARROT_NAME) ?: "むめいのインコ"
        val isRemindNetSharingEnabled = userDefaults.boolForKey(KEY_REMIND_NET_SHARING_ENABLED)
        val isDebugFastMemoryEnabled = userDefaults.boolForKey(KEY_DEBUG_FAST_MEMORY_ENABLED)
        val debugForgetTimeSeconds = userDefaults.integerForKey(KEY_DEBUG_FORGET_TIME_SECONDS).toInt().let {
            if (it == 0) 10 else it // デフォルト値として10を使用
        }

        // デバッグ用ログ
        println("IOSUserSettingsService: getUserSettings called")
        println("  parrotName: $parrotName")
        println("  isRemindNetSharingEnabled: $isRemindNetSharingEnabled")
        println("  isDebugFastMemoryEnabled: $isDebugFastMemoryEnabled")
        println("  debugForgetTimeSeconds: $debugForgetTimeSeconds")

        return UserSettings(
            parrotName = parrotName,
            isRemindNetSharingEnabled = isRemindNetSharingEnabled,
            isDebugFastMemoryEnabled = isDebugFastMemoryEnabled,
            debugForgetTimeSeconds = debugForgetTimeSeconds
        )
    }

    override suspend fun saveUserSettings(settings: UserSettings) {
        val userDefaults = NSUserDefaults.standardUserDefaults
        userDefaults.setObject(settings.parrotName, KEY_PARROT_NAME)
        userDefaults.setBool(settings.isRemindNetSharingEnabled, KEY_REMIND_NET_SHARING_ENABLED)
        userDefaults.setBool(settings.isDebugFastMemoryEnabled, KEY_DEBUG_FAST_MEMORY_ENABLED)
        userDefaults.setInteger(settings.debugForgetTimeSeconds.toLong(), KEY_DEBUG_FORGET_TIME_SECONDS)

        // デバッグ用ログ
        println("IOSUserSettingsService: saveUserSettings called")
        println("  parrotName: ${settings.parrotName}")
        println("  isRemindNetSharingEnabled: ${settings.isRemindNetSharingEnabled}")
        println("  isDebugFastMemoryEnabled: ${settings.isDebugFastMemoryEnabled}")
        println("  debugForgetTimeSeconds: ${settings.debugForgetTimeSeconds}")
    }
}
