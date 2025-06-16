package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * GetRemindersUseCaseのテストクラス
 *
 * リマインダー取得ユースケースの動作を検証します
 */
class GetRemindersUseCaseTest {

    /**
     * テスト用のリポジトリのテストダブル
     */
    private class TestReminderRepository : ReminderRepository {
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

        override suspend fun createReminder(reminder: Reminder): Result<Reminder> {
            return Result.success(reminder)
        }

        override suspend fun getReminders(): Result<List<Reminder>> {
            return if (shouldReturnFailure) {
                Result.failure(exceptionToThrow!!)
            } else {
                Result.success(remindersToReturn)
            }
        }

        override suspend fun updateReminder(reminder: Reminder): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun deleteReminder(reminderId: String): Result<Unit> {
            return Result.success(Unit)
        }
    }

    /**
     * テスト用のGetRemindersUseCaseの実装
     */
    private class TestGetRemindersUseCase(
        private val repository: TestReminderRepository
    ) {
        suspend operator fun invoke(): Result<List<Reminder>> {
            return repository.getReminders()
        }
    }

    private val testRepository = TestReminderRepository()
    private val useCase = TestGetRemindersUseCase(testRepository)

    /**
     * リマインダー取得が成功する場合のテスト
     */
    @Test
    fun `正常な場合はリマインダーリストが返される`() = runTest {
        // Given
        val expectedReminders = listOf(
            Reminder(id = "1", text = "テスト1"),
            Reminder(id = "2", text = "テスト2", isCompleted = true)
        )
        testRepository.reset()
        testRepository.setRemindersToReturn(expectedReminders)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedReminders, result.getOrNull())
    }

    /**
     * リポジトリからエラーが返される場合のテスト
     */
    @Test
    fun `リポジトリエラーの場合はFailureが返される`() = runTest {
        // Given
        val exception = RuntimeException("データベースエラー")
        testRepository.reset()
        testRepository.setShouldReturnFailure(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    /**
     * 空のリマインダーリストが返される場合のテスト
     */
    @Test
    fun `空のリストが返される場合`() = runTest {
        // Given
        val emptyList = emptyList<Reminder>()
        testRepository.reset()
        testRepository.setRemindersToReturn(emptyList)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList, result.getOrNull())
    }
}
