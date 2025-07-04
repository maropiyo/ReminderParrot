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

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    private val _accountCreationError = MutableStateFlow<String?>(null)
    val accountCreationError: StateFlow<String?> = _accountCreationError.asStateFlow()

    init {
        loadSettings()
        loadUserId()
        loadDisplayName()
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
     * 表示名を読み込む
     */
    private fun loadDisplayName() {
        viewModelScope.launch {
            try {
                val name = authService.getDisplayName()
                _displayName.value = name
            } catch (e: Exception) {
                println("SettingsViewModel: 表示名の読み込みエラー - $e")
            }
        }
    }

    /**
     * リマインコの名前を更新
     */
    fun updateParrotName(name: String) {
        viewModelScope.launch {
            try {
                authService.updateDisplayName(name)
                _displayName.value = name
            } catch (e: Exception) {
                println("SettingsViewModel: 表示名の更新エラー - $e")
            }
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
        loadDisplayName()
    }

    /**
     * 全ての設定情報を再読み込みする
     * タブ切り替え時などに呼び出される
     */
    fun refreshAll() {
        loadSettings()
        loadUserId()
        loadDisplayName()
    }

    /**
     * アカウントを作成する
     */
    fun createAccount() {
        viewModelScope.launch {
            try {
                // エラーをクリア
                _accountCreationError.value = null

                // 匿名認証でアカウントを作成
                val userId = authService.getUserId()
                println("SettingsViewModel: アカウント作成成功 - UserId: $userId")

                // 初期名を設定
                authService.updateDisplayName("ひよっこインコ")
                println("SettingsViewModel: 初期名を設定 - ひよっこインコ")

                // ユーザーIDを再読み込み
                loadUserId()
                // 表示名を再読み込み
                loadDisplayName()
            } catch (e: Exception) {
                println("SettingsViewModel: アカウント作成エラー: ${e.message}")
                val errorMessage = when {
                    e.message?.contains("anonymous_provider_disabled") == true ->
                        "アカウントのせっていがひつようです。\nかんりしゃにれんらくしてください。"
                    else ->
                        "アカウントのさくせいにしっぱいしました。\nもういちどためしてください。"
                }
                _accountCreationError.value = errorMessage
            }
        }
    }

    /**
     * アカウント作成エラーをクリア
     */
    fun clearAccountCreationError() {
        _accountCreationError.value = null
    }

    /**
     * ログアウトする
     */
    fun logout() {
        viewModelScope.launch {
            try {
                authService.logout()
                println("SettingsViewModel: ログアウト成功")

                // 状態をクリア
                _userId.value = null
                _displayName.value = null
                _accountCreationError.value = null
            } catch (e: Exception) {
                println("SettingsViewModel: ログアウトエラー: ${e.message}")
            }
        }
    }
}
