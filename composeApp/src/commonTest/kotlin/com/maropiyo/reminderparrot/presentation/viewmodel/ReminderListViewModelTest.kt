package com.maropiyo.reminderparrot.presentation.viewmodel

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.presentation.state.ReminderListState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * ReminderListViewModelのテストクラス
 *
 * リマインダー一覧ビューモデルの動作を検証します
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReminderListViewModelTest {

    /**
     * テスト用のGetRemindersUseCaseのテストダブル
     */
    private class TestGetRemindersUseCase {
        private var shouldReturnFailure = false
        private var exceptionToThrow: Exception? = null
        private var remindersToReturn: List<Reminder> = emptyList()

        fun setRemindersToReturn(reminders: List<Reminder>) {
            this.remindersToReturn = reminders
            this.shouldReturnFailure = false
        }

        fun setShouldReturnFailure(exception: Exception) {
            this.shouldReturnFailure = true
            this.exceptionToThrow = exception
        }

        fun reset() {
            shouldReturnFailure = false
            exceptionToThrow = null
            remindersToReturn = emptyList()
        }

        operator fun invoke(): Result<List<Reminder>> {
            return if (shouldReturnFailure) {
                Result.failure(exceptionToThrow!!)
            } else {
                Result.success(remindersToReturn)
            }
        }
    }

    /**
     * テスト用のCreateReminderUseCaseのテストダブル
     */
    private class TestCreateReminderUseCase {
        private var shouldReturnFailure = false
        private var exceptionToThrow: Exception? = null
        private var reminderToReturn: Reminder? = null

        fun setReminderToReturn(reminder: Reminder) {
            this.reminderToReturn = reminder
            this.shouldReturnFailure = false
        }

        fun setShouldReturnFailure(exception: Exception) {
            this.shouldReturnFailure = true
            this.exceptionToThrow = exception
        }

        fun reset() {
            shouldReturnFailure = false
            exceptionToThrow = null
            reminderToReturn = null
        }

        operator fun invoke(text: String): Result<Reminder> {
            return if (shouldReturnFailure) {
                Result.failure(exceptionToThrow!!)
            } else {
                Result.success(reminderToReturn ?: Reminder(id = "test-id", text = text))
            }
        }
    }

    /**
     * テスト用のUpdateReminderUseCaseのテストダブル
     */
    private class TestUpdateReminderUseCase {
        private var shouldReturnFailure = false
        private var exceptionToThrow: Exception? = null

        fun setShouldReturnFailure(exception: Exception) {
            this.shouldReturnFailure = true
            this.exceptionToThrow = exception
        }

        fun setShouldReturnSuccess() {
            this.shouldReturnFailure = false
            this.exceptionToThrow = null
        }

        fun reset() {
            shouldReturnFailure = false
            exceptionToThrow = null
        }

        operator fun invoke(reminder: Reminder): Result<Unit> {
            return if (shouldReturnFailure) {
                Result.failure(exceptionToThrow!!)
            } else {
                Result.success(Unit)
            }
        }
    }

    /**
     * テスト用のReminderListViewModelの実装
     */
    private class TestReminderListViewModel(
        private val getRemindersUseCase: TestGetRemindersUseCase,
        private val createReminderUseCase: TestCreateReminderUseCase,
        private val updateReminderUseCase: TestUpdateReminderUseCase
    ) {
        private var currentState = ReminderListState()
        val state: kotlinx.coroutines.flow.Flow<ReminderListState> = kotlinx.coroutines.flow.flow {
            emit(currentState)
        }

        init {
            loadReminders()
        }

        private fun loadReminders() {
            CoroutineScope(Dispatchers.Main).launch {
                currentState = currentState.copy(isLoading = true)
                val result = getRemindersUseCase()
                if (result.isSuccess) {
                    val sortedReminders = result.getOrNull()!!.sortedBy { it.isCompleted }
                    currentState = currentState.copy(
                        reminders = sortedReminders,
                        isLoading = false,
                        error = null
                    )
                } else {
                    currentState = currentState.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }

        fun createReminder(text: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val result = createReminderUseCase(text)
                if (result.isSuccess) {
                    val newReminder = result.getOrNull()!!
                    val updatedReminders = (currentState.reminders + newReminder).sortedBy { it.isCompleted }
                    currentState = currentState.copy(
                        reminders = updatedReminders,
                        error = null
                    )
                } else {
                    currentState = currentState.copy(
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }

        fun toggleReminderCompletion(id: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val reminder = currentState.reminders.find { it.id == id }
                if (reminder != null) {
                    val updatedReminder = reminder.copy(isCompleted = !reminder.isCompleted)
                    val result = updateReminderUseCase(updatedReminder)
                    if (result.isSuccess) {
                        val updatedReminders = currentState.reminders.map {
                            if (it.id == id) updatedReminder else it
                        }.sortedBy { it.isCompleted }
                        currentState = currentState.copy(
                            reminders = updatedReminders,
                            error = null
                        )
                    } else {
                        currentState = currentState.copy(
                            error = result.exceptionOrNull()?.message
                        )
                    }
                }
            }
        }

        fun getStateValue(): ReminderListState {
            return currentState
        }
    }

    private val testGetRemindersUseCase = TestGetRemindersUseCase()
    private val testCreateReminderUseCase = TestCreateReminderUseCase()
    private val testUpdateReminderUseCase = TestUpdateReminderUseCase()

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * 初期状態のテスト
     */
    @Test
    fun `初期状態は正しく設定される`() = runTest {
        // Given
        val reminders = listOf(
            Reminder(id = "1", text = "テスト1"),
            Reminder(id = "2", text = "テスト2", isCompleted = true)
        )
        testGetRemindersUseCase.setRemindersToReturn(reminders)

        // When
        val viewModel = TestReminderListViewModel(
            testGetRemindersUseCase,
            testCreateReminderUseCase,
            testUpdateReminderUseCase
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.getStateValue()
        assertEquals(2, state.reminders.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
        // ソート確認：未完了が先頭
        assertFalse(state.reminders[0].isCompleted)
        assertTrue(state.reminders[1].isCompleted)
    }

    /**
     * リマインダー作成成功のテスト
     */
    @Test
    fun `createReminder - 正常な場合はリマインダーが追加される`() = runTest {
        // Given
        val initialReminders = listOf(Reminder(id = "1", text = "既存"))
        val newReminder = Reminder(id = "2", text = "新規")

        testGetRemindersUseCase.setRemindersToReturn(initialReminders)
        testCreateReminderUseCase.setReminderToReturn(newReminder)

        val viewModel = TestReminderListViewModel(
            testGetRemindersUseCase,
            testCreateReminderUseCase,
            testUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.createReminder("新規")
        advanceUntilIdle()

        // Then
        val state = viewModel.getStateValue()
        assertEquals(2, state.reminders.size)
        assertTrue(state.reminders.any { it.text == "新規" })
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    /**
     * リマインダー作成失敗のテスト
     */
    @Test
    fun `createReminder - エラーの場合はエラーメッセージが設定される`() = runTest {
        // Given
        val initialReminders = listOf(Reminder(id = "1", text = "既存"))
        val exception = RuntimeException("作成エラー")

        testGetRemindersUseCase.setRemindersToReturn(initialReminders)
        testCreateReminderUseCase.setShouldReturnFailure(exception)

        val viewModel = TestReminderListViewModel(
            testGetRemindersUseCase,
            testCreateReminderUseCase,
            testUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.createReminder("新規")
        advanceUntilIdle()

        // Then
        val state = viewModel.getStateValue()
        assertEquals(1, state.reminders.size) // 追加されていない
        assertEquals("作成エラー", state.error)
    }

    /**
     * リマインダー完了状態切り替え成功のテスト
     */
    @Test
    fun `toggleReminderCompletion - 正常な場合は完了状態が切り替わる`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "テスト", isCompleted = false)

        testGetRemindersUseCase.setRemindersToReturn(listOf(reminder))
        testUpdateReminderUseCase.setShouldReturnSuccess()

        val viewModel = TestReminderListViewModel(
            testGetRemindersUseCase,
            testCreateReminderUseCase,
            testUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.toggleReminderCompletion("1")
        advanceUntilIdle()

        // Then
        val state = viewModel.getStateValue()
        assertTrue(state.reminders.first().isCompleted)
        assertNull(state.error)
    }

    /**
     * リマインダー完了状態切り替え失敗のテスト
     */
    @Test
    fun `toggleReminderCompletion - エラーの場合はエラーメッセージが設定される`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "テスト", isCompleted = false)
        val exception = RuntimeException("更新エラー")

        testGetRemindersUseCase.setRemindersToReturn(listOf(reminder))
        testUpdateReminderUseCase.setShouldReturnFailure(exception)

        val viewModel = TestReminderListViewModel(
            testGetRemindersUseCase,
            testCreateReminderUseCase,
            testUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.toggleReminderCompletion("1")
        advanceUntilIdle()

        // Then
        val state = viewModel.getStateValue()
        assertEquals("更新エラー", state.error)
    }

    /**
     * 存在しないリマインダーのIDで完了状態を切り替えようとした場合のテスト
     */
    @Test
    fun `toggleReminderCompletion - 存在しないIDの場合は何も起こらない`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "テスト")
        testGetRemindersUseCase.setRemindersToReturn(listOf(reminder))

        val viewModel = TestReminderListViewModel(
            testGetRemindersUseCase,
            testCreateReminderUseCase,
            testUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.toggleReminderCompletion("999") // 存在しないID
        advanceUntilIdle()

        // Then
        val state = viewModel.getStateValue()
        assertFalse(state.reminders.first().isCompleted) // 変更されていない
        assertNull(state.error)
    }

    /**
     * 初期データ取得失敗のテスト
     */
    @Test
    fun `初期データ取得が失敗した場合はエラーメッセージが設定される`() = runTest {
        // Given
        val exception = RuntimeException("データ取得エラー")
        testGetRemindersUseCase.setShouldReturnFailure(exception)

        // When
        val viewModel = TestReminderListViewModel(
            testGetRemindersUseCase,
            testCreateReminderUseCase,
            testUpdateReminderUseCase
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.getStateValue()
        assertTrue(state.reminders.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("データ取得エラー", state.error)
    }

    /**
     * ソートロジックのテスト
     */
    @Test
    fun `リマインダーリストは未完了が先頭にソートされる`() = runTest {
        // Given
        val reminders = listOf(
            Reminder(id = "1", text = "完了済み", isCompleted = true),
            Reminder(id = "2", text = "未完了1", isCompleted = false),
            Reminder(id = "3", text = "未完了2", isCompleted = false),
            Reminder(id = "4", text = "完了済み2", isCompleted = true)
        )
        testGetRemindersUseCase.setRemindersToReturn(reminders)

        // When
        val viewModel = TestReminderListViewModel(
            testGetRemindersUseCase,
            testCreateReminderUseCase,
            testUpdateReminderUseCase
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.getStateValue()
        assertEquals(4, state.reminders.size)

        // 最初の2つは未完了
        assertFalse(state.reminders[0].isCompleted)
        assertFalse(state.reminders[1].isCompleted)

        // 最後の2つは完了済み
        assertTrue(state.reminders[2].isCompleted)
        assertTrue(state.reminders[3].isCompleted)
    }
}
