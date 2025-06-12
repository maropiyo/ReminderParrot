package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * UpdateReminderUseCaseのテストクラス
 *
 * リマインダー更新ユースケースの動作を検証します
 */
class UpdateReminderUseCaseTest {

    private val mockRepository = mockk<ReminderRepository>()
    private val useCase = UpdateReminderUseCase(mockRepository)

    /**
     * リマインダー更新が成功する場合のテスト
     */
    @Test
    fun `正常な場合はリマインダーが更新される`() = runTest {
        // Given
        val reminder = Reminder(id = "1", text = "更新テスト", isCompleted = true)
        coEvery { mockRepository.updateReminder(reminder) } returns Result.success(Unit)

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
        val reminder = Reminder(id = "1", text = "エラーテスト")
        val exception = RuntimeException("更新エラー")
        coEvery { mockRepository.updateReminder(reminder) } returns Result.failure(exception)

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
        val completedReminder = Reminder(id = "2", text = "完了済み", isCompleted = true)
        coEvery { mockRepository.updateReminder(completedReminder) } returns Result.success(Unit)

        // When
        val result = useCase(completedReminder)

        // Then
        assertTrue(result.isSuccess)
    }
}
