package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
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

    /**
     * テスト用のリポジトリのテストダブル
     */
    private class TestReminderRepository : ReminderRepository {
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

        override suspend fun createReminder(reminder: Reminder): Result<Reminder> {
            return if (shouldReturnFailure) {
                Result.failure(exceptionToThrow!!)
            } else {
                Result.success(reminderToReturn ?: reminder)
            }
        }

        override suspend fun getReminders(): Result<List<Reminder>> {
            return Result.success(emptyList())
        }

        override suspend fun updateReminder(reminder: Reminder): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun deleteReminder(reminderId: String): Result<Unit> {
            return Result.success(Unit)
        }
    }

    /**
     * テスト用のUuidGeneratorのテストダブル
     */
    private class TestUuidGenerator {
        private var idToReturn: String = "default-id"

        fun setIdToReturn(id: String) {
            this.idToReturn = id
        }

        fun generateId(): String {
            return idToReturn
        }
    }

    /**
     * テスト用のCreateReminderUseCaseの実装
     */
    private class TestCreateReminderUseCase(
        private val repository: TestReminderRepository,
        private val uuidGenerator: TestUuidGenerator
    ) {
        suspend operator fun invoke(text: String): Result<Reminder> {
            val id = uuidGenerator.generateId()
            val reminder = Reminder(id = id, text = text)
            return repository.createReminder(reminder)
        }
    }

    private val testRepository = TestReminderRepository()
    private val testUuidGenerator = TestUuidGenerator()
    private val useCase = TestCreateReminderUseCase(testRepository, testUuidGenerator)

    /**
     * リマインダー作成が成功する場合のテスト
     */
    @Test
    fun `正常な場合はリマインダーが作成される`() = runTest {
        // Given
        val testText = "新しいリマインダー"
        val testId = "test-uuid-123"
        val expectedReminder = Reminder(id = testId, text = testText)

        testRepository.reset()
        testUuidGenerator.setIdToReturn(testId)
        testRepository.setReminderToReturn(expectedReminder)

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
        val exception = RuntimeException("保存エラー")

        testRepository.reset()
        testUuidGenerator.setIdToReturn(testId)
        testRepository.setShouldReturnFailure(exception)

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

        testRepository.reset()
        testUuidGenerator.setIdToReturn(testId)
        testRepository.setReminderToReturn(expectedReminder)

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
