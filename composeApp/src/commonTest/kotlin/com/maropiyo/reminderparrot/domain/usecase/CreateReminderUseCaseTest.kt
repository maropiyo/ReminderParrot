package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * CreateReminderUseCaseのテストクラス
 *
 * リマインダー作成ユースケースの動作を検証します
 */
class CreateReminderUseCaseTest {

    private val mockRepository = mockk<ReminderRepository>()
    private val mockUuidGenerator = mockk<UuidGenerator>()
    private val useCase = CreateReminderUseCase(mockRepository, mockUuidGenerator)

    /**
     * リマインダー作成が成功する場合のテスト
     */
    @Test
    fun `正常な場合はリマインダーが作成される`() = runTest {
        // Given
        val testText = "新しいリマインダー"
        val testId = "test-uuid-123"
        val expectedReminder = Reminder(id = testId, text = testText)

        every { mockUuidGenerator.generateId() } returns testId
        coEvery { mockRepository.createReminder(expectedReminder) } returns Result.success(expectedReminder)

        // When
        val result = useCase(testText)

        // Then
        assertTrue(result.isSuccess)
        val createdReminder = result.getOrNull()!!
        assertEquals(testId, createdReminder.id)
        assertEquals(testText, createdReminder.text)
        assertFalse(createdReminder.isCompleted)
    }

    /**
     * リポジトリからエラーが返される場合のテスト
     */
    @Test
    fun `リポジトリエラーの場合はFailureが返される`() = runTest {
        // Given
        val testText = "エラーテスト"
        val testId = "test-uuid-456"
        val expectedReminder = Reminder(id = testId, text = testText)
        val exception = RuntimeException("保存エラー")

        every { mockUuidGenerator.generateId() } returns testId
        coEvery { mockRepository.createReminder(expectedReminder) } returns Result.failure(exception)

        // When
        val result = useCase(testText)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    /**
     * 空文字列のテキストでリマインダーを作成する場合のテスト
     */
    @Test
    fun `空文字列のテキストでもリマインダーが作成される`() = runTest {
        // Given
        val testText = ""
        val testId = "test-uuid-789"
        val expectedReminder = Reminder(id = testId, text = testText)

        every { mockUuidGenerator.generateId() } returns testId
        coEvery { mockRepository.createReminder(expectedReminder) } returns Result.success(expectedReminder)

        // When
        val result = useCase(testText)

        // Then
        assertTrue(result.isSuccess)
        val createdReminder = result.getOrNull()!!
        assertEquals(testId, createdReminder.id)
        assertEquals(testText, createdReminder.text)
        assertFalse(createdReminder.isCompleted)
    }
}
