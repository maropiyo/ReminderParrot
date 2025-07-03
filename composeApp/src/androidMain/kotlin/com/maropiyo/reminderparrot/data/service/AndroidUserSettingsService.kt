package com.maropiyo.reminderparrot.data.service

import android.content.Context
import com.maropiyo.reminderparrot.domain.entity.UserSettings
import com.maropiyo.reminderparrot.domain.service.UserSettingsService

/**
 * Android用のユーザー設定サービス
 */
class AndroidUserSettingsService(
    private val context: Context
) : UserSettingsService {

    companion object {
        private const val PREFS_NAME = "user_settings"
        private const val KEY_PARROT_NAME = "parrot_name"
        private const val KEY_REMIND_NET_SHARING_ENABLED = "remind_net_sharing_enabled"
        private const val KEY_DEBUG_FAST_MEMORY_ENABLED = "debug_fast_memory_enabled"
        private const val KEY_DEBUG_FORGET_TIME_SECONDS = "debug_forget_time_seconds"
    }

    override suspend fun getUserSettings(): UserSettings {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val parrotName = prefs.getString(KEY_PARROT_NAME, "むめいのインコ") ?: "むめいのインコ"
        val isRemindNetSharingEnabled = prefs.getBoolean(KEY_REMIND_NET_SHARING_ENABLED, false)
        val isDebugFastMemoryEnabled = prefs.getBoolean(KEY_DEBUG_FAST_MEMORY_ENABLED, false)
        val debugForgetTimeSeconds = prefs.getInt(KEY_DEBUG_FORGET_TIME_SECONDS, 10)

        // デバッグ用ログ
        println("AndroidUserSettingsService: getUserSettings called")
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
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_PARROT_NAME, settings.parrotName)
            .putBoolean(KEY_REMIND_NET_SHARING_ENABLED, settings.isRemindNetSharingEnabled)
            .putBoolean(KEY_DEBUG_FAST_MEMORY_ENABLED, settings.isDebugFastMemoryEnabled)
            .putInt(KEY_DEBUG_FORGET_TIME_SECONDS, settings.debugForgetTimeSeconds)
            .apply()

        // デバッグ用ログ
        println("AndroidUserSettingsService: saveUserSettings called")
        println("  parrotName: ${settings.parrotName}")
        println("  isRemindNetSharingEnabled: ${settings.isRemindNetSharingEnabled}")
        println("  isDebugFastMemoryEnabled: ${settings.isDebugFastMemoryEnabled}")
        println("  debugForgetTimeSeconds: ${settings.debugForgetTimeSeconds}")
    }
}
