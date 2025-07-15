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
import kotlinx.datetime.Clock

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

    private val _isUpdatingParrotName = MutableStateFlow(false)
    val isUpdatingParrotName: StateFlow<Boolean> = _isUpdatingParrotName.asStateFlow()

    private val _nameUpdateError = MutableStateFlow<String?>(null)
    val nameUpdateError: StateFlow<String?> = _nameUpdateError.asStateFlow()

    private val _isNameUpdateCooldown = MutableStateFlow(false)
    val isNameUpdateCooldown: StateFlow<Boolean> = _isNameUpdateCooldown.asStateFlow()

    private var lastNameUpdateTime = 0L
    private val NAME_UPDATE_COOLDOWN_MS = 2000L // 2秒のクールダウン

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
            val settings = getUserSettingsUseCase()
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
                // 既存のアカウントがある場合のみ表示名を取得
                val currentUserId = authService.getCurrentUserId()
                if (currentUserId != null) {
                    val name = authService.getDisplayName()
                    _displayName.value = name
                } else {
                    _displayName.value = null
                }
            } catch (e: Exception) {
                // エラーは無視してnullのまま
                _displayName.value = null
            }
        }
    }

    /**
     * インコの名前を更新
     */
    fun updateParrotName(name: String) {
        // 既に更新中の場合は処理しない
        if (_isUpdatingParrotName.value) return

        // クールダウン期間中は処理しない
        val currentTime = Clock.System.now().toEpochMilliseconds()
        if (currentTime - lastNameUpdateTime < NAME_UPDATE_COOLDOWN_MS) {
            val remainingSeconds = ((NAME_UPDATE_COOLDOWN_MS - (currentTime - lastNameUpdateTime)) / 1000L).toInt() + 1
            _nameUpdateError.value = "${remainingSeconds}びょうまってからもういちどおためしください"
            return
        }

        viewModelScope.launch {
            try {
                _isUpdatingParrotName.value = true
                println("SettingsViewModel: 名前更新開始 - '$name'")
                authService.updateDisplayName(name)
                _displayName.value = name
                lastNameUpdateTime = currentTime

                // クールダウン期間開始
                _isNameUpdateCooldown.value = true
                startCooldownTimer()

                println("SettingsViewModel: 名前更新完了 - '$name'")
            } catch (e: Exception) {
                println("SettingsViewModel: 名前更新エラー - ${e.message}")
                println("SettingsViewModel: エラー詳細 - $e")

                // エラーメッセージを設定
                val errorMessage = when {
                    e.message?.contains("over_request_rate_limit") == true ->
                        "なまえのへんこうがおおすぎます\nしばらくまってからもういちどおためしください"
                    e.message?.contains("network") == true ->
                        "インターネットにせつぞくできません\nもういちどおためしください"
                    else ->
                        "なまえのへんこうにしっぱいしました\nもういちどおためしください"
                }
                _nameUpdateError.value = errorMessage

                // エラーが発生した場合は表示名を元に戻す
                loadDisplayName()
            } finally {
                _isUpdatingParrotName.value = false
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
            val newSettings = _settings.value.copy(isDebugFastMemoryEnabled = enabled)
            saveUserSettingsUseCase(newSettings)
            _settings.value = newSettings
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
                // 既存のアカウントがある場合のみユーザーIDを取得（自動作成はしない）
                val userId = authService.getCurrentUserId()
                _userId.value = userId
            } catch (e: Exception) {
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

                // 匿名認証でアカウントを作成（初期名は自動設定される）
                authService.getUserId()

                // ユーザーIDを再読み込み
                loadUserId()
                // 表示名を再読み込み
                loadDisplayName()
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("anonymous_provider_disabled") == true ->
                        "アカウントのせっていがひつようです\nかんりしゃにれんらくしてください"
                    else ->
                        "アカウントのさくせいにしっぱいしました\nもういちどためしてください"
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
     * 名前更新エラーをクリア
     */
    fun clearNameUpdateError() {
        _nameUpdateError.value = null
    }

    /**
     * クールダウンタイマーを開始
     */
    private fun startCooldownTimer() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(NAME_UPDATE_COOLDOWN_MS)
            _isNameUpdateCooldown.value = false
        }
    }

    /**
     * ログアウトする
     */
    fun logout() {
        viewModelScope.launch {
            try {
                authService.logout()

                // 状態をクリア
                _userId.value = null
                _displayName.value = null
                _accountCreationError.value = null
            } catch (e: Exception) {
                // ログアウトエラーは無視
            }
        }
    }
}
