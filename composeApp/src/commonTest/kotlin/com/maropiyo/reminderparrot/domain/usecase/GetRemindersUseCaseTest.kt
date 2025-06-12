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
 * GetRemindersUseCaseのテストクラス
 *
 * リマインダー取得ユースケースの動作を検証します
 */
class GetRemindersUseCaseTest {

    private val mockRepository = mockk<ReminderRepository>()
    private val useCase = GetRemindersUseCase(mockRepository)

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
        coEvery { mockRepository.getReminders() } returns Result.success(expectedReminders)

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
        coEvery { mockRepository.getReminders() } returns Result.failure(exception)

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
        coEvery { mockRepository.getReminders() } returns Result.success(emptyList)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList, result.getOrNull())
    }
}
