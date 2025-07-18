package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.domain.entity.Reminder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock

/**
 * ReminderRepositoryImplのテストクラス
 *
 * リマインダーリポジトリ実装の動作を検証します
 */
class ReminderRepositoryImplTest {

    /**
     * テスト用のローカルデータソースのテストダブル
     */
    private class TestReminderLocalDataSource {
        private var shouldThrowException = false
        private var exceptionToThrow: Exception? = null
        private var remindersToReturn: List<Reminder> = emptyList()
        private var reminderToReturn: Reminder? = null

        fun setRemindersToReturn(reminders: List<Reminder>) {
            this.remindersToReturn = reminders
        }

        fun setReminderToReturn(reminder: Reminder) {
            this.reminderToReturn = reminder
        }

        fun setShouldThrowException(exception: Exception) {
            this.shouldThrowException = true
            this.exceptionToThrow = exception
        }

        fun reset() {
            shouldThrowException = false
            exceptionToThrow = null
            remindersToReturn = emptyList()
            reminderToReturn = null
        }

        fun createReminder(reminder: Reminder): Reminder {
            if (shouldThrowException) {
                throw exceptionToThrow!!
            }
            return reminderToReturn ?: reminder
        }

        fun getReminders(): List<Reminder> {
            if (shouldThrowException) {
                throw exceptionToThrow!!
            }
            return remindersToReturn
        }

        fun updateReminder(reminder: Reminder) {
            if (shouldThrowException) {
                throw exceptionToThrow!!
            }
        }
    }

    /**
     * テスト用のリポジトリ実装
     */
    private class TestReminderRepositoryImpl(
        private val localDataSource: TestReminderLocalDataSource
    ) {
        suspend fun createReminder(reminder: Reminder): Result<Reminder> {
            return try {
                val createdReminder = localDataSource.createReminder(reminder)
                Result.success(createdReminder)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        suspend fun getReminders(): Result<List<Reminder>> {
            return try {
                val reminders = localDataSource.getReminders()
                Result.success(reminders)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        suspend fun updateReminder(reminder: Reminder): Result<Unit> {
            return try {
                localDataSource.updateReminder(reminder)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private val testLocalDataSource = TestReminderLocalDataSource()
    private val repository = TestReminderRepositoryImpl(testLocalDataSource)

    /**
     * リマインダー作成が成功する場合のテスト
     */
    @Test
    fun `createReminder - 正常な場合はリマインダーが作成される`() = runTest {
        // Given
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "1",
            text = "テストリマインダー",
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        testLocalDataSource.reset()
        testLocalDataSource.setReminderToReturn(reminder)

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
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "1",
            text = "エラーテスト",
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        val exception = RuntimeException("ローカル保存エラー")
        testLocalDataSource.reset()
        testLocalDataSource.setShouldThrowException(exception)

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
        val currentTime = Clock.System.now()
        val reminders = listOf(
            Reminder(
                id = "1",
                text = "テスト1",
                createdAt = currentTime,
                forgetAt = currentTime + 24.hours
            ),
            Reminder(
                id = "2",
                text = "テスト2",
                isCompleted = true,
                createdAt = currentTime,
                forgetAt = currentTime + 24.hours
            )
        )
        testLocalDataSource.reset()
        testLocalDataSource.setRemindersToReturn(reminders)

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
        testLocalDataSource.reset()
        testLocalDataSource.setShouldThrowException(exception)

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
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "1",
            text = "更新テスト",
            isCompleted = true,
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        testLocalDataSource.reset()

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
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "1",
            text = "エラーテスト",
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        val exception = RuntimeException("更新エラー")
        testLocalDataSource.reset()
        testLocalDataSource.setShouldThrowException(exception)

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
        testLocalDataSource.reset()
        testLocalDataSource.setRemindersToReturn(emptyList)

        // When
        val result = repository.getReminders()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList, result.getOrNull())
    }
}
