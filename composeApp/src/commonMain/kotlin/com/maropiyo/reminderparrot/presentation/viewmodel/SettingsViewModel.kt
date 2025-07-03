package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.entity.UserSettings
import com.maropiyo.reminderparrot.domain.service.AuthService
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
    private val saveUserSettingsUseCase: SaveUserSettingsUseCase,
    private val authService: AuthService
) : ViewModel() {

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        loadSettings()
        loadUserId()
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

    /**
     * ユーザーIDを読み込む
     */
    private fun loadUserId() {
        viewModelScope.launch {
            try {
                val currentUserId = authService.getCurrentUserId()
                _userId.value = currentUserId
            } catch (e: Exception) {
                println("SettingsViewModel: ユーザーID取得エラー: ${e.message}")
                _userId.value = null
            }
        }
    }

    /**
     * ユーザーIDを再読み込みする
     */
    fun refreshUserId() {
        loadUserId()
    }

    /**
     * アカウントを作成する
     */
    fun createAccount() {
        viewModelScope.launch {
            try {
                // 匿名認証でアカウントを作成
                val userId = authService.getUserId()
                println("SettingsViewModel: アカウント作成成功 - UserId: $userId")

                // ユーザーIDを再読み込み
                loadUserId()
            } catch (e: Exception) {
                println("SettingsViewModel: アカウント作成エラー: ${e.message}")
                // エラーハンドリングは必要に応じて追加
            }
        }
    }
}
