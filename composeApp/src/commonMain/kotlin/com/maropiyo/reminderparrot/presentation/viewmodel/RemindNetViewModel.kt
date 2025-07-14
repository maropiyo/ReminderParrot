package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.usecase.CheckImportHistoryUseCase
import com.maropiyo.reminderparrot.domain.usecase.CheckNotificationHistoryUseCase
import com.maropiyo.reminderparrot.domain.service.AuthService
import com.maropiyo.reminderparrot.domain.usecase.AddParrotExperienceUseCase
import com.maropiyo.reminderparrot.domain.usecase.ImportRemindNetPostUseCase
import com.maropiyo.reminderparrot.domain.usecase.SendRemindNotificationUseCase
import com.maropiyo.reminderparrot.domain.usecase.remindnet.DeleteRemindNetPostUseCase
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
    private val checkNotificationHistoryUseCase: CheckNotificationHistoryUseCase,
    private val deleteRemindNetPostUseCase: DeleteRemindNetPostUseCase,
    private val addParrotExperienceUseCase: AddParrotExperienceUseCase,
    private val parrotViewModel: ParrotViewModel,
    private val importRemindNetPostUseCase: ImportRemindNetPostUseCase,
    private val checkImportHistoryUseCase: CheckImportHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RemindNetState())
    val state: StateFlow<RemindNetState> = _state.asStateFlow()

    private val _needsAccountCreation = MutableStateFlow(false)
    val needsAccountCreation: StateFlow<Boolean> = _needsAccountCreation.asStateFlow()

    private val _accountCreationError = MutableStateFlow<String?>(null)
    val accountCreationError: StateFlow<String?> = _accountCreationError.asStateFlow()

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    private val _isLoadingDisplayName = MutableStateFlow(false)
    val isLoadingDisplayName: StateFlow<Boolean> = _isLoadingDisplayName.asStateFlow()

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
                            checkNotificationHistoryUseCase(post.id, currentUserId)
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

                    val importedPostIds = if (currentUserId != null) {
                        posts.filter { post ->
                            checkImportHistoryUseCase(post.id, currentUserId)
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
                            myPostIds = myPostIds,
                            importedPostIds = importedPostIds
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
                // アカウントがない場合は表示名もクリア
                _displayName.value = null
            } else {
                _needsAccountCreation.value = false
                loadPosts()
                // アカウントが確認できた後に表示名を読み込み
                loadDisplayName()
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

                // アカウント作成成功後、投稿を読み込み
                _needsAccountCreation.value = false
                loadPosts()
            } catch (e: Exception) {
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

                    // リマインド送信成功時に経験値+1
                    addParrotExperienceUseCase(1)
                        .onSuccess { updatedParrot ->
                            // インコの状態表示をリアルタイムで更新
                            parrotViewModel.loadParrot()
                        }
                        .onFailure { exception ->
                            // 経験値追加の失敗はユーザーにエラーを表示しない（リマインド送信は成功している）
                        }
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
        return checkNotificationHistoryUseCase(postId, currentUserId)
    }

    /**
     * 投稿が自分のものかどうかを確認
     */
    suspend fun isMyPost(post: RemindNetPost): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        return post.userId == currentUserId
    }

    /**
     * 投稿を削除する
     */
    fun deletePost(postId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val currentUserId = authService.getCurrentUserId()

            if (currentUserId == null) {
                _state.update {
                    it.copy(error = "ユーザーにんしょうがひつようです")
                }
                return@launch
            }

            deleteRemindNetPostUseCase(postId, currentUserId)
                .onSuccess {
                    // 楽観的UI更新：削除された投稿をリストから即座に除去
                    _state.update { currentState ->
                        currentState.copy(
                            posts = currentState.posts.filter { it.id != postId },
                            myPostIds = currentState.myPostIds - postId
                        )
                    }
                    onSuccess()
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            error = "とうこうのさくじょにしっぱいしました"
                        )
                    }
                }
        }
    }

    /**
     * 表示名を読み込む
     */
    private fun loadDisplayName() {
        viewModelScope.launch {
            _isLoadingDisplayName.value = true
            try {
                val currentUserId = authService.getCurrentUserId()
                if (currentUserId != null) {
                    val name = authService.getDisplayName()
                    _displayName.value = name
                } else {
                    _displayName.value = null
                }
            } catch (e: Exception) {
                _displayName.value = null
            } finally {
                _isLoadingDisplayName.value = false
            }
        }
    }

    /**
     * リマインネット投稿をリマインダーとしてインポートする
     * 他のインコの投稿を自分のインコに覚えさせる機能
     */
    fun importPost(post: RemindNetPost, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            // 自分の投稿はインポートできない
            if (isMyPost(post)) {
                _state.update {
                    it.copy(error = "じぶんのとうこうはおぼえられません")
                }
                return@launch
            }

            // 既にインポート済みの投稿はインポートできない（データベースから確認）
            val currentUserId = authService.getCurrentUserId()
            if (currentUserId != null && checkImportHistoryUseCase(post.id, currentUserId)) {
                _state.update {
                    it.copy(error = "すでにおぼえているよ")
                }
                return@launch
            }

            importRemindNetPostUseCase(post)
                .onSuccess { importedReminder ->

                    // インポート済みのpostIdをセットに追加（状態を即座に更新）
                    _state.update { currentState ->
                        currentState.copy(
                            importedPostIds = currentState.importedPostIds + post.id
                        )
                    }

                    // インコの状態表示をリアルタイムで更新（経験値+1が追加されている）
                    parrotViewModel.loadParrot()

                    // リマインダーリスト更新のためのコールバック実行
                    onSuccess()

                    _state.update {
                        it.copy(error = null)
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            error = "ことばをおぼえるのにしっぱいしました"
                        )
                    }
                }
        }
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
    val myPostIds: Set<String> = emptySet(),
    val importedPostIds: Set<String> = emptySet()
)
