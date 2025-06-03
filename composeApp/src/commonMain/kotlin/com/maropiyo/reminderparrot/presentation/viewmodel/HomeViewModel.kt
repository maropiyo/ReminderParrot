package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.UpdateReminderUseCase
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
 * @property updateReminderUseCase リマインダー更新ユースケース
 */
class HomeViewModel(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase
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
            createReminderUseCase(text)
                .onSuccess { reminder ->
                    // リマインダーの作成に成功した場合、リマインダーを更新する
                    _state.update {
                        val updatedReminders = it.reminders + reminder
                        // 未完了→完了の順にソート
                        val sortedReminders = updatedReminders.sortedBy { r -> r.isCompleted }
                        it.copy(
                            reminders = sortedReminders,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { exception ->
                    // リマインダーの作成に失敗した場合、エラーメッセージを表示する
                    _state.update { it.copy(error = exception.message) }
                }
        }
    }

    /**
     * リマインダーの完了状態を切り替える
     *
     * @param reminderId リマインダーのID
     */
    fun toggleReminderCompletion(reminderId: String) {
        viewModelScope.launch {
            val reminder = _state.value.reminders.find { it.id == reminderId } ?: return@launch
            val updatedReminder = reminder.copy(isCompleted = !reminder.isCompleted)

            updateReminderUseCase(updatedReminder)
                .onSuccess {
                    _state.update { currentState ->
                        val updatedReminders = currentState.reminders.map {
                            if (it.id == reminderId) updatedReminder else it
                        }
                        // 未完了→完了の順にソート
                        val sortedReminders = updatedReminders.sortedBy { it.isCompleted }
                        currentState.copy(reminders = sortedReminders)
                    }
                }
                .onFailure { exception ->
                    _state.update { it.copy(error = exception.message) }
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
                    // 未完了→完了の順にソート
                    val sortedReminders = reminders.sortedBy { it.isCompleted }
                    _state.update { it.copy(reminders = sortedReminders, isLoading = false) }
                }.onFailure { exception ->
                    _state.update { it.copy(error = exception.message, isLoading = false) }
                }
        }
    }
}
