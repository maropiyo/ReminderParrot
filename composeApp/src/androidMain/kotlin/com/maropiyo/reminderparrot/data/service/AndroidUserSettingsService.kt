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
        private const val KEY_REMIND_NET_SHARING_ENABLED = "remind_net_sharing_enabled"
    }

    override suspend fun getUserSettings(): UserSettings {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return UserSettings(
            isRemindNetSharingEnabled = prefs.getBoolean(KEY_REMIND_NET_SHARING_ENABLED, false)
        )
    }

    override suspend fun saveUserSettings(settings: UserSettings) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_REMIND_NET_SHARING_ENABLED, settings.isRemindNetSharingEnabled)
            .apply()
    }
}
