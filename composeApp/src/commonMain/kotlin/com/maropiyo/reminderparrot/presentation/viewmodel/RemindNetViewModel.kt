package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.service.AuthService
import com.maropiyo.reminderparrot.domain.usecase.remindnet.GetRemindNetPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * リマインネット画面のビューモデル
 */
class RemindNetViewModel(
    private val getRemindNetPostsUseCase: GetRemindNetPostsUseCase,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(RemindNetState())
    val state: StateFlow<RemindNetState> = _state.asStateFlow()

    private val _needsAccountCreation = MutableStateFlow(false)
    val needsAccountCreation: StateFlow<Boolean> = _needsAccountCreation.asStateFlow()

    private val _accountCreationError = MutableStateFlow<String?>(null)
    val accountCreationError: StateFlow<String?> = _accountCreationError.asStateFlow()

    init {
        checkAccountAndLoadPosts()
    }

    /**
     * 投稿を読み込む
     */
    private fun loadPosts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getRemindNetPostsUseCase()
                .catch { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "うまくいかなかったよ"
                        )
                    }
                }
                .collect { posts ->
                    _state.update {
                        it.copy(
                            posts = posts,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * 手動リフレッシュ
     */
    fun refresh() {
        loadPosts()
    }

    /**
     * アカウント確認と投稿読み込み
     */
    private fun checkAccountAndLoadPosts() {
        viewModelScope.launch {
            val currentUserId = authService.getCurrentUserId()
            if (currentUserId == null) {
                _needsAccountCreation.value = true
            } else {
                _needsAccountCreation.value = false
                loadPosts()
            }
        }
    }

    /**
     * 画面に遷移した時に呼ばれる
     */
    fun onScreenEntered() {
        checkAccountAndLoadPosts()
    }

    /**
     * エラーをクリア
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
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
                println("RemindNetViewModel: アカウント作成成功 - UserId: $userId")

                // アカウント作成成功後、投稿を読み込み
                _needsAccountCreation.value = false
                loadPosts()
            } catch (e: Exception) {
                println("RemindNetViewModel: アカウント作成エラー: ${e.message}")
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
}

/**
 * リマインネット画面の状態
 */
data class RemindNetState(
    val posts: List<RemindNetPost> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
