package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * UpdateReminderUseCaseのテストクラス
 *
 * リマインダー更新ユースケースの動作を検証します
 */
class UpdateReminderUseCaseTest {

    /**
     * テスト用のリポジトリのテストダブル
     */
    private class TestReminderRepository : ReminderRepository {
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

        override suspend fun createReminder(reminder: Reminder): Result<Reminder> {
            return Result.success(reminder)
        }

        override suspend fun getReminders(): Result<List<Reminder>> {
            return Result.success(emptyList())
        }

        override suspend fun updateReminder(reminder: Reminder): Result<Unit> {
            return if (shouldReturnFailure) {
                Result.failure(exceptionToThrow!!)
            } else {
                Result.success(Unit)
            }
        }

        override suspend fun deleteReminder(reminderId: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun getExpiredReminderIds(currentTime: Instant): List<String> {
            return emptyList()
        }

        override suspend fun deleteExpiredReminders(currentTime: Instant): Int {
            return 0
        }
    }

    /**
     * テスト用のUpdateReminderUseCaseの実装
     */
    private class TestUpdateReminderUseCase(
        private val repository: TestReminderRepository
    ) {
        suspend operator fun invoke(reminder: Reminder): Result<Unit> {
            return repository.updateReminder(reminder)
        }
    }

    private val testRepository = TestReminderRepository()
    private val useCase = TestUpdateReminderUseCase(testRepository)

    /**
     * リマインダー更新が成功する場合のテスト
     */
    @Test
    fun `正常な場合はリマインダーが更新される`() = runTest {
        // Given
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "1",
            text = "更新テスト",
            isCompleted = true,
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        testRepository.reset()
        testRepository.setShouldReturnSuccess()

        // When
        val result = useCase(reminder)

        // Then
        assertTrue(result.isSuccess)
    }

    /**
     * リポジトリからエラーが返される場合のテスト
     */
    @Test
    fun `リポジトリエラーの場合はFailureが返される`() = runTest {
        // Given
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "1",
            text = "エラーテスト",
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        val exception = RuntimeException("更新エラー")
        testRepository.reset()
        testRepository.setShouldReturnFailure(exception)

        // When
        val result = useCase(reminder)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    /**
     * 完了状態のリマインダーを更新する場合のテスト
     */
    @Test
    fun `完了状態のリマインダーも正常に更新される`() = runTest {
        // Given
        val currentTime = Clock.System.now()
        val completedReminder = Reminder(
            id = "2",
            text = "完了済み",
            isCompleted = true,
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        testRepository.reset()
        testRepository.setShouldReturnSuccess()

        // When
        val result = useCase(completedReminder)

        // Then
        assertTrue(result.isSuccess)
    }
}
