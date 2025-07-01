package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
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
    private val getRemindNetPostsUseCase: GetRemindNetPostsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RemindNetState())
    val state: StateFlow<RemindNetState> = _state.asStateFlow()

    init {
        loadPosts()
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
     * エラーをクリア
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
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
