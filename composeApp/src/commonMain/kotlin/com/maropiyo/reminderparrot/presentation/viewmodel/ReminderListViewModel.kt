package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.usecase.AddParrotExperienceUseCase
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.DeleteReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.UpdateReminderUseCase
import com.maropiyo.reminderparrot.presentation.state.ReminderListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * リマインダー一覧のビューモデル
 *
 * @property getRemindersUseCase リマインダー取得ユースケース
 * @property createReminderUseCase リマインダー作成ユースケース
 * @property updateReminderUseCase リマインダー更新ユースケース
 * @property deleteReminderUseCase リマインダー削除ユースケース
 * @property addParrotExperienceUseCase インコの経験値追加ユースケース
 */
class ReminderListViewModel(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val addParrotExperienceUseCase: AddParrotExperienceUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ReminderListState())
    val state: StateFlow<ReminderListState> = _state.asStateFlow()

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
                            reminders = updatedReminders,
                            isLoading = false,
                            error = null
                        )
                    }
                    // インコの経験値を追加（+1）
                    addParrotExperienceUseCase()
                }.onFailure { exception ->
                    // リマインダーの作成に失敗した場合、エラーメッセージを表示する
                    _state.update { it.copy(error = exception.message) }
                }
        }
    }

    /**
     * リマインダーの完了状態を切り替える
     * 完了にした場合は、アニメーション後に自動削除される
     *
     * @param reminderId リマインダーのID
     */
    fun toggleReminderCompletion(reminderId: String) {
        viewModelScope.launch {
            val reminder = _state.value.reminders.find { it.id == reminderId } ?: return@launch

            if (!reminder.isCompleted) {
                // 未完了→完了の場合は経験値追加後に削除
                // まず完了状態に更新
                val updatedReminder = reminder.copy(isCompleted = true)
                _state.update { currentState ->
                    val updatedReminders = currentState.reminders.map {
                        if (it.id == reminderId) updatedReminder else it
                    }
                    currentState.copy(reminders = updatedReminders)
                }

                // 経験値を追加
                addParrotExperienceUseCase()

                // アニメーションの完了を待ってから削除
                delay(400) // アニメーション時間（100ms + 300ms）
                deleteReminderUseCase(reminderId)
                    .onSuccess {
                        _state.update { currentState ->
                            val filteredReminders = currentState.reminders.filter { it.id != reminderId }
                            currentState.copy(reminders = filteredReminders)
                        }
                    }
            } else {
                // 完了→未完了の場合は通常の更新
                val updatedReminder = reminder.copy(isCompleted = false)
                updateReminderUseCase(updatedReminder)
                    .onSuccess {
                        _state.update { currentState ->
                            val updatedReminders = currentState.reminders.map {
                                if (it.id == reminderId) updatedReminder else it
                            }
                            currentState.copy(reminders = updatedReminders)
                        }
                    }.onFailure { exception ->
                        _state.update { it.copy(error = exception.message) }
                    }
            }
        }
    }

    /**
     * リマインダーのテキストを更新する
     *
     * @param reminderId リマインダーのID
     * @param newText 新しいテキスト
     */
    fun updateReminderText(reminderId: String, newText: String) {
        viewModelScope.launch {
            val reminder = _state.value.reminders.find { it.id == reminderId } ?: return@launch
            val updatedReminder = reminder.copy(text = newText)

            updateReminderUseCase(updatedReminder)
                .onSuccess {
                    _state.update { currentState ->
                        val updatedReminders =
                            currentState.reminders.map {
                                if (it.id == reminderId) updatedReminder else it
                            }
                        currentState.copy(reminders = updatedReminders)
                    }
                }.onFailure { exception ->
                    _state.update { it.copy(error = exception.message) }
                }
        }
    }

    /**
     * リマインダーを削除する
     *
     * @param reminderId リマインダーのID
     */
    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            deleteReminderUseCase(reminderId)
                .onSuccess {
                    _state.update { currentState ->
                        val updatedReminders = currentState.reminders.filter { it.id != reminderId }
                        currentState.copy(reminders = updatedReminders)
                    }
                }.onFailure { exception ->
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
                    _state.update { it.copy(reminders = reminders, isLoading = false) }
                }.onFailure { exception ->
                    _state.update { it.copy(error = exception.message, isLoading = false) }
                }
        }
    }
}
