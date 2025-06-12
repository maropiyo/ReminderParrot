package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.local.ReminderLocalDataSource
import com.maropiyo.reminderparrot.data.remote.ReminderRemoteDataSource
import com.maropiyo.reminderparrot.domain.entity.Reminder
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * ReminderRepositoryImplのテストクラス
 *
 * リマインダーリポジトリ実装の動作を検証します
 */
class ReminderRepositoryImplTest {

    private val mockLocalDataSource = mockk<ReminderLocalDataSource>()
    private val mockRemoteDataSource = mockk<ReminderRemoteDataSource>()
    private val repository = ReminderRepositoryImpl(mockLocalDataSource, mockRemoteDataSource)

    /**
     * リマインダー作成が成功する場合のテスト
     */
    @Test
    fun `createReminder - 正常な場合はリマインダーが作成される`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "テストリマインダー")
        coEvery { mockLocalDataSource.createReminder(reminder) } returns reminder

        // When
        val result = repository.createReminder(reminder)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(reminder, result.getOrNull())
    }

    /**
     * リマインダー作成でローカルデータソースがエラーを投げる場合のテスト
     */
    @Test
    fun `createReminder - ローカルデータソースエラーの場合はFailureが返される`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "エラーテスト")
        val exception = RuntimeException("ローカル保存エラー")
        coEvery { mockLocalDataSource.createReminder(reminder) } throws exception

        // When
        val result = repository.createReminder(reminder)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    /**
     * リマインダー取得が成功する場合のテスト
     */
    @Test
    fun `getReminders - 正常な場合はリマインダーリストが返される`() = runTest {
        // Given
        val reminders = listOf(
            Reminder(id = "1", text = "テスト1"),
            Reminder(id = "2", text = "テスト2", isCompleted = true)
        )
        coEvery { mockLocalDataSource.getReminders() } returns reminders

        // When
        val result = repository.getReminders()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(reminders, result.getOrNull())
    }

    /**
     * リマインダー取得でローカルデータソースがエラーを投げる場合のテスト
     */
    @Test
    fun `getReminders - ローカルデータソースエラーの場合はFailureが返される`() = runTest {
        // Given
        val exception = RuntimeException("データ取得エラー")
        coEvery { mockLocalDataSource.getReminders() } throws exception

        // When
        val result = repository.getReminders()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    /**
     * リマインダー更新が成功する場合のテスト
     */
    @Test
    fun `updateReminder - 正常な場合はUnitが返される`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "更新テスト", isCompleted = true)
        coJustRun { mockLocalDataSource.updateReminder(reminder) }

        // When
        val result = repository.updateReminder(reminder)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    /**
     * リマインダー更新でローカルデータソースがエラーを投げる場合のテスト
     */
    @Test
    fun `updateReminder - ローカルデータソースエラーの場合はFailureが返される`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "エラーテスト")
        val exception = RuntimeException("更新エラー")
        coEvery { mockLocalDataSource.updateReminder(reminder) } throws exception

        // When
        val result = repository.updateReminder(reminder)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    /**
     * 空のリマインダーリストが返される場合のテスト
     */
    @Test
    fun `getReminders - 空のリストが返される場合`() = runTest {
        // Given
        val emptyList = emptyList<Reminder>()
        coEvery { mockLocalDataSource.getReminders() } returns emptyList

        // When
        val result = repository.getReminders()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList, result.getOrNull())
    }
}
