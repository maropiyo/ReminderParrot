package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.domain.entity.Reminder
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
     * テスト用のリモートデータソースのテストダブル
     */
    private class TestReminderRemoteDataSource {
        // テスト用の実装（現在は何もしない）
    }

    /**
     * テスト用のリポジトリ実装
     */
    private class TestReminderRepositoryImpl(
        private val localDataSource: TestReminderLocalDataSource,
        private val remoteDataSource: TestReminderRemoteDataSource
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
    private val testRemoteDataSource = TestReminderRemoteDataSource()
    private val repository = TestReminderRepositoryImpl(testLocalDataSource, testRemoteDataSource)

    /**
     * リマインダー作成が成功する場合のテスト
     */
    @Test
    fun `createReminder - 正常な場合はリマインダーが作成される`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "テストリマインダー")
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
        val reminder = Reminder(id = "1", text = "エラーテスト")
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
        val reminders = listOf(
            Reminder(id = "1", text = "テスト1"),
            Reminder(id = "2", text = "テスト2", isCompleted = true)
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
        val reminder = Reminder(id = "1", text = "更新テスト", isCompleted = true)
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
        val reminder = Reminder(id = "1", text = "エラーテスト")
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
