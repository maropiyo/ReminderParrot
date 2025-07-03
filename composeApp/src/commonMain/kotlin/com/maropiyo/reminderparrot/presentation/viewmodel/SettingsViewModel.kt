package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.entity.UserSettings
import com.maropiyo.reminderparrot.domain.usecase.GetUserSettingsUseCase
import com.maropiyo.reminderparrot.domain.usecase.SaveUserSettingsUseCase
import com.maropiyo.reminderparrot.util.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 設定画面のViewModel
 */
class SettingsViewModel(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val saveUserSettingsUseCase: SaveUserSettingsUseCase
) : ViewModel() {

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * 設定を読み込む
     */
    private fun loadSettings() {
        viewModelScope.launch {
            // デバッグ用ログ
            println("SettingsViewModel: loadSettings called")
            val settings = getUserSettingsUseCase()
            println("  loaded settings: isDebugFastMemoryEnabled = ${settings.isDebugFastMemoryEnabled}")
            _settings.value = settings
        }
    }

    /**
     * リマインネット投稿設定を更新する
     */
    fun updateRemindNetSharingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val newSettings = _settings.value.copy(isRemindNetSharingEnabled = enabled)
            saveUserSettingsUseCase(newSettings)
            _settings.value = newSettings
        }
    }

    /**
     * デバッグ用高速記憶設定を更新する
     */
    fun updateDebugFastMemoryEnabled(enabled: Boolean) {
        // リリースビルドでは無効
        if (!BuildConfig.isDebug) return

        viewModelScope.launch {
            // デバッグ用ログ
            println("SettingsViewModel: updateDebugFastMemoryEnabled called")
            println("  current value: ${_settings.value.isDebugFastMemoryEnabled}")
            println("  new value: $enabled")

            val newSettings = _settings.value.copy(isDebugFastMemoryEnabled = enabled)
            saveUserSettingsUseCase(newSettings)
            _settings.value = newSettings

            println("  saved and updated state")
        }
    }

    /**
     * デバッグ用忘却時間を更新する
     */
    fun updateDebugForgetTimeSeconds(seconds: Int) {
        // リリースビルドでは無効
        if (!BuildConfig.isDebug) return

        viewModelScope.launch {
            val newSettings = _settings.value.copy(debugForgetTimeSeconds = seconds)
            saveUserSettingsUseCase(newSettings)
            _settings.value = newSettings
        }
    }
}
