package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.UpdateReminderUseCase
import com.maropiyo.reminderparrot.presentation.state.ReminderListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * リマインダー一覧画面のビューモデル
 *
 * @property getRemindersUseCase リマインダー取得ユースケース
 * @property createReminderUseCase リマインダー作成ユースケース
 * @property updateReminderUseCase リマインダー更新ユースケース
 */
class ReminderListViewModel(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase
) : ViewModel() {
    // リマインダーの状態を保持するStateFlow
    private val _state = MutableStateFlow(ReminderListState())

    // 外部からはStateFlowとして公開する
    val state: StateFlow<ReminderListState> = _state.asStateFlow()

    /**
     * リマインダーリストをソートする（未完了→完了の順）
     *
     * @param reminders ソート対象のリマインダーリスト
     * @return ソート済みリマインダーリスト
     */
    private fun sortReminders(reminders: List<Reminder>): List<Reminder> {
        return reminders.sortedBy { it.isCompleted }
    }

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
                        it.copy(
                            reminders = sortReminders(updatedReminders),
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
                        currentState.copy(reminders = sortReminders(updatedReminders))
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
                    _state.update { it.copy(reminders = sortReminders(reminders), isLoading = false) }
                }.onFailure { exception ->
                    _state.update { it.copy(error = exception.message, isLoading = false) }
                }
        }
    }
}
