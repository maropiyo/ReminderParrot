package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.presentation.state.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ホーム画面のビューモデル
 *
 * @property getRemindersUseCase リマインダー取得ユースケース
 * @property createReminderUseCase リマインダー作成ユースケース
 */
class HomeViewModel(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
) : ViewModel() {
    // リマインダーの状態を保持するStateFlow
    private val _state = MutableStateFlow(HomeState())

    // 外部からはStateFlowとして公開する
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        // 初期化時にリマインダーを取得
        loadReminders()
    }

    /**
     * リマインダーを作成する
     *
     * @param text リマインダーのテキスト
     */
    fun createReminder(text: String) {
        viewModelScope.launch {
            val reminder = createReminderUseCase(text)
            if (reminder != null) {
                _state.update { it.copy(reminders = it.reminders) }
                loadReminders()
            } else {
                _state.update { it.copy(error = "リマインダーの作成に失敗しました") }
            }
        }
    }

    /**
     * リマインダーを読み込む
     *
     * リマインダーの取得に成功した場合は、リマインダーリストを更新する
     */
    private fun loadReminders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getRemindersUseCase()
                .onSuccess { reminders ->
                    _state.update { it.copy(reminders = reminders, isLoading = false) }
                }.onFailure { exception ->
                    _state.update { it.copy(error = exception.message, isLoading = false) }
                }
        }
    }
}
