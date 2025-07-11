package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.data.datasource.local.NotificationHistoryLocalDataSource
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.service.AuthService
import com.maropiyo.reminderparrot.domain.usecase.SendRemindNotificationUseCase
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
    private val sendRemindNotificationUseCase: SendRemindNotificationUseCase,
    private val authService: AuthService,
    private val notificationHistoryLocalDataSource: NotificationHistoryLocalDataSource
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
                    // 送信済み状態と自分の投稿も併せて取得
                    val currentUserId = authService.getCurrentUserId()
                    val sentPostIds = if (currentUserId != null) {
                        posts.filter { post ->
                            notificationHistoryLocalDataSource.hasAlreadySent(post.id, currentUserId)
                        }.map { it.id }.toSet()
                    } else {
                        emptySet()
                    }

                    val myPostIds = if (currentUserId != null) {
                        posts.filter { post ->
                            post.userId == currentUserId
                        }.map { it.id }.toSet()
                    } else {
                        emptySet()
                    }

                    _state.update {
                        it.copy(
                            posts = posts,
                            isLoading = false,
                            error = null,
                            sentPostIds = sentPostIds,
                            myPostIds = myPostIds
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

    /**
     * リマインド通知を送信する
     */
    fun sendRemindNotification(post: RemindNetPost) {
        viewModelScope.launch {
            sendRemindNotificationUseCase(post)
                .onSuccess {
                    // 送信成功時は送信済み状態を更新
                    _state.update { currentState ->
                        currentState.copy(
                            sentPostIds = currentState.sentPostIds + post.id
                        )
                    }
                    println("リマインド通知を送信しました: ${post.userName}へ")
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            error = when {
                                exception.message?.contains("自分の投稿") == true ->
                                    "じぶんのとうこうにはつうちできません"
                                exception.message?.contains("既に通知を送信済み") == true ->
                                    "このとうこうにはもうつうちをおくったよ"
                                else ->
                                    "つうちのそうしんにしっぱいしました"
                            }
                        )
                    }
                }
        }
    }

    /**
     * 特定の投稿に送信済みかどうかを確認
     */
    suspend fun hasAlreadySent(postId: String): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        return notificationHistoryLocalDataSource.hasAlreadySent(postId, currentUserId)
    }

    /**
     * 投稿が自分のものかどうかを確認
     */
    suspend fun isMyPost(post: RemindNetPost): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        return post.userId == currentUserId
    }
}

/**
 * リマインネット画面の状態
 */
data class RemindNetState(
    val posts: List<RemindNetPost> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sentPostIds: Set<String> = emptySet(),
    val myPostIds: Set<String> = emptySet()
)
