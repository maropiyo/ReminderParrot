package com.maropiyo.reminderparrot.presentation.viewmodel

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.UpdateReminderUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

    private val mockGetRemindersUseCase = mockk<GetRemindersUseCase>()
    private val mockCreateReminderUseCase = mockk<CreateReminderUseCase>()
    private val mockUpdateReminderUseCase = mockk<UpdateReminderUseCase>()

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
        coEvery { mockGetRemindersUseCase() } returns Result.success(reminders)

        // When
        val viewModel = ReminderListViewModel(
            mockGetRemindersUseCase,
            mockCreateReminderUseCase,
            mockUpdateReminderUseCase
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.state.first()
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

        coEvery { mockGetRemindersUseCase() } returns Result.success(initialReminders)
        coEvery { mockCreateReminderUseCase("新規") } returns Result.success(newReminder)

        val viewModel = ReminderListViewModel(
            mockGetRemindersUseCase,
            mockCreateReminderUseCase,
            mockUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.createReminder("新規")
        advanceUntilIdle()

        // Then
        val state = viewModel.state.first()
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

        coEvery { mockGetRemindersUseCase() } returns Result.success(initialReminders)
        coEvery { mockCreateReminderUseCase("新規") } returns Result.failure(exception)

        val viewModel = ReminderListViewModel(
            mockGetRemindersUseCase,
            mockCreateReminderUseCase,
            mockUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.createReminder("新規")
        advanceUntilIdle()

        // Then
        val state = viewModel.state.first()
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
        val updatedReminder = reminder.copy(isCompleted = true)

        coEvery { mockGetRemindersUseCase() } returns Result.success(listOf(reminder))
        coEvery { mockUpdateReminderUseCase(updatedReminder) } returns Result.success(Unit)

        val viewModel = ReminderListViewModel(
            mockGetRemindersUseCase,
            mockCreateReminderUseCase,
            mockUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.toggleReminderCompletion("1")
        advanceUntilIdle()

        // Then
        val state = viewModel.state.first()
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
        val updatedReminder = reminder.copy(isCompleted = true)
        val exception = RuntimeException("更新エラー")

        coEvery { mockGetRemindersUseCase() } returns Result.success(listOf(reminder))
        coEvery { mockUpdateReminderUseCase(updatedReminder) } returns Result.failure(exception)

        val viewModel = ReminderListViewModel(
            mockGetRemindersUseCase,
            mockCreateReminderUseCase,
            mockUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.toggleReminderCompletion("1")
        advanceUntilIdle()

        // Then
        val state = viewModel.state.first()
        assertEquals("更新エラー", state.error)
    }

    /**
     * 存在しないリマインダーのIDで完了状態を切り替えようとした場合のテスト
     */
    @Test
    fun `toggleReminderCompletion - 存在しないIDの場合は何も起こらない`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "テスト")
        coEvery { mockGetRemindersUseCase() } returns Result.success(listOf(reminder))

        val viewModel = ReminderListViewModel(
            mockGetRemindersUseCase,
            mockCreateReminderUseCase,
            mockUpdateReminderUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.toggleReminderCompletion("999") // 存在しないID
        advanceUntilIdle()

        // Then
        val state = viewModel.state.first()
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
        coEvery { mockGetRemindersUseCase() } returns Result.failure(exception)

        // When
        val viewModel = ReminderListViewModel(
            mockGetRemindersUseCase,
            mockCreateReminderUseCase,
            mockUpdateReminderUseCase
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.state.first()
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
        coEvery { mockGetRemindersUseCase() } returns Result.success(reminders)

        // When
        val viewModel = ReminderListViewModel(
            mockGetRemindersUseCase,
            mockCreateReminderUseCase,
            mockUpdateReminderUseCase
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.state.first()
        assertEquals(4, state.reminders.size)

        // 最初の2つは未完了
        assertFalse(state.reminders[0].isCompleted)
        assertFalse(state.reminders[1].isCompleted)

        // 最後の2つは完了済み
        assertTrue(state.reminders[2].isCompleted)
        assertTrue(state.reminders[3].isCompleted)
    }
}
