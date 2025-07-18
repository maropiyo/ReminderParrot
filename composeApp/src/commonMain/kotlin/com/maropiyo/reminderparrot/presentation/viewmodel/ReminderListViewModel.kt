package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.service.AuthService
import com.maropiyo.reminderparrot.domain.usecase.AddParrotExperienceUseCase
import com.maropiyo.reminderparrot.domain.usecase.CancelForgetNotificationUseCase
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.DeleteExpiredRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.DeleteReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetUserSettingsUseCase
import com.maropiyo.reminderparrot.domain.usecase.ScheduleForgetNotificationUseCase
import com.maropiyo.reminderparrot.domain.usecase.UpdateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.remindnet.CreateRemindNetPostUseCase
import com.maropiyo.reminderparrot.domain.usecase.remindnet.DeleteRemindNetPostUseCase
import com.maropiyo.reminderparrot.presentation.state.ReminderListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * リマインダー一覧のビューモデル
 *
 * @property getRemindersUseCase リマインダー取得ユースケース
 * @property createReminderUseCase リマインダー作成ユースケース
 * @property updateReminderUseCase リマインダー更新ユースケース
 * @property deleteReminderUseCase リマインダー削除ユースケース
 * @property deleteExpiredRemindersUseCase 期限切れリマインダー削除ユースケース
 * @property addParrotExperienceUseCase インコの経験値追加ユースケース
 * @property cancelForgetNotificationUseCase 忘却通知キャンセルユースケース
 * @property scheduleForgetNotificationUseCase 忘却通知スケジューリングユースケース
 */
class ReminderListViewModel(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val deleteExpiredRemindersUseCase: DeleteExpiredRemindersUseCase,
    private val addParrotExperienceUseCase: AddParrotExperienceUseCase,
    private val cancelForgetNotificationUseCase: CancelForgetNotificationUseCase,
    private val scheduleForgetNotificationUseCase: ScheduleForgetNotificationUseCase,
    private val createRemindNetPostUseCase: CreateRemindNetPostUseCase,
    private val deleteRemindNetPostUseCase: DeleteRemindNetPostUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val authService: AuthService
) : ViewModel() {
    private val _state = MutableStateFlow(ReminderListState())
    val state: StateFlow<ReminderListState> = _state.asStateFlow()

    // 定期更新用のJob
    private var periodicUpdateJob: Job? = null

    init {
        // 初期化時にリマインダーを取得
        loadReminders()
        // 定期的な更新を開始
        startPeriodicUpdate()
    }

    /**
     * リマインダーを作成する
     *
     * @param text リマインダーのテキスト
     * @param onExperienceAdded 経験値加算完了時のコールバック
     */
    fun createReminder(text: String, onExperienceAdded: (() -> Unit)? = null) {
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
                        .onSuccess {
                            // 経験値加算成功時のコールバック実行
                            onExperienceAdded?.invoke()
                        }
                        .onFailure { exception ->
                            // 経験値加算失敗をログに記録（UI表示はしない）
                            println("経験値加算に失敗しました: ${exception.message}")
                            // 失敗してもコールバックは実行する（リマインダー作成自体は成功）
                            onExperienceAdded?.invoke()
                        }

                    // 設定を確認してリマインネットに投稿するかどうかを決める
                    val settings = getUserSettingsUseCase()
                    if (settings.isRemindNetSharingEnabled) {
                        val displayName = try {
                            authService.getDisplayName()
                        } catch (e: Exception) {
                            null
                        }
                        val userName = displayName?.takeIf { it.isNotBlank() } ?: "ひよっこインコ"
                        createRemindNetPostUseCase(
                            reminderId = reminder.id,
                            reminderText = reminder.text,
                            forgetAt = reminder.forgetAt,
                            userName = userName
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

                // 通知をキャンセル
                cancelForgetNotificationUseCase(reminderId)

                deleteReminderUseCase(reminderId)
                    .onSuccess {
                        // 対応するリマインネット投稿も削除
                        deleteRemindNetPostIfExists(reminderId)

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

                    // 通知のテキストを更新するために再スケジューリング
                    try {
                        // 既存の通知をキャンセルしてから新しいテキストで再スケジューリング
                        cancelForgetNotificationUseCase(reminderId)
                        scheduleForgetNotificationUseCase(updatedReminder)
                    } catch (e: Exception) {
                        // 通知の更新に失敗してもリマインダー更新は成功とする
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
            // 通知をキャンセル
            cancelForgetNotificationUseCase(reminderId)

            deleteReminderUseCase(reminderId)
                .onSuccess {
                    // 対応するリマインネット投稿も削除
                    deleteRemindNetPostIfExists(reminderId)

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
     * リマインネット投稿が存在する場合に削除する
     * エラーが発生してもリマインダー削除処理は継続する
     *
     * @param reminderId リマインダーのID（RemindNet投稿のIDと同じ）
     */
    private suspend fun deleteRemindNetPostIfExists(reminderId: String) {
        try {
            val currentUserId = authService.getCurrentUserId()
            if (currentUserId != null) {
                deleteRemindNetPostUseCase(reminderId, currentUserId)
                    .onFailure { // エラーが発生してもログのみ出力し、処理は継続
                        println("RemindNet投稿削除に失敗: ${it.message}")
                    }
            }
        } catch (e: Exception) {
            // 例外が発生してもリマインダー削除処理は継続
            println("RemindNet投稿削除でエラー: ${e.message}")
        }
    }

    /**
     * リマインダーを手動で再読み込みする（公開メソッド）
     */
    fun refresh() {
        loadReminders()
    }

    /**
     * リマインダーを読み込む
     *
     * リマインダーの取得に成功した場合は、リマインダーリストを更新する
     */
    private fun loadReminders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // 期限切れリマインダーを削除
            deleteExpiredRemindersUseCase.execute()

            // 期限切れリマインダーの通知は個別にキャンセルされるため、
            // 全ての通知をキャンセルする必要はない

            getRemindersUseCase()
                .onSuccess { reminders ->
                    _state.update { it.copy(reminders = reminders, isLoading = false) }
                }.onFailure { exception ->
                    _state.update { it.copy(error = exception.message, isLoading = false) }
                }
        }
    }

    /**
     * 定期的な更新を開始する
     * 1分ごとにUIの更新を行う
     */
    private fun startPeriodicUpdate() {
        periodicUpdateJob = viewModelScope.launch {
            while (true) {
                delay(60_000) // 1分待機

                // 時間表示の更新のためにStateを更新
                _state.update { it.copy(lastUpdated = Clock.System.now().toEpochMilliseconds()) }
            }
        }
    }

    /**
     * ViewModel破棄時のクリーンアップ
     */
    override fun onCleared() {
        super.onCleared()
        periodicUpdateJob?.cancel()
    }
}
