package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.service.NotificationService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * CancelForgetNotificationUseCaseのテストクラス
 */
class CancelForgetNotificationUseCaseTest {

    /**
     * テスト用のNotificationServiceのテストダブル
     */
    private class TestNotificationService : NotificationService {
        private var shouldThrowException = false
        private var exceptionToThrow: Exception? = null
        var lastCancelledReminderId: String? = null
        var allNotificationsCancelled = false

        fun setShouldThrowException(exception: Exception) {
            this.shouldThrowException = true
            this.exceptionToThrow = exception
        }

        fun reset() {
            shouldThrowException = false
            exceptionToThrow = null
            lastCancelledReminderId = null
            allNotificationsCancelled = false
        }

        override suspend fun requestNotificationPermission(): Boolean {
            return true
        }

        override suspend fun isNotificationPermissionGranted(): Boolean {
            return true
        }

        override suspend fun scheduleForgetNotification(reminder: Reminder) {
            // テスト用の実装
        }

        override suspend fun cancelForgetNotification(reminderId: String) {
            if (shouldThrowException) {
                throw exceptionToThrow!!
            }
            lastCancelledReminderId = reminderId
        }

        override suspend fun cancelAllForgetNotifications() {
            if (shouldThrowException) {
                throw exceptionToThrow!!
            }
            allNotificationsCancelled = true
        }

        override suspend fun getPushNotificationToken(): String? {
            return "test-token"
        }

        override suspend fun refreshPushNotificationToken(): String? {
            return "refreshed-test-token"
        }
    }

    private val testNotificationService = TestNotificationService()
    private val useCase = CancelForgetNotificationUseCase(testNotificationService)

    /**
     * 個別の通知キャンセルが成功する場合のテスト
     */
    @Test
    fun `個別の通知キャンセルが正常に実行される`() = runTest {
        // Given
        val reminderId = "test-reminder-123"
        testNotificationService.reset()

        // When
        val result = useCase(reminderId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(reminderId, testNotificationService.lastCancelledReminderId)
    }

    /**
     * 個別キャンセル時にエラーが発生する場合のテスト
     */
    @Test
    fun `個別キャンセル時のエラーはFailureが返される`() = runTest {
        // Given
        val reminderId = "error-reminder"
        val exception = RuntimeException("キャンセルエラー")
        testNotificationService.reset()
        testNotificationService.setShouldThrowException(exception)

        // When
        val result = useCase(reminderId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    /**
     * 全通知のキャンセルが成功する場合のテスト
     */
    @Test
    fun `全通知のキャンセルが正常に実行される`() = runTest {
        // Given
        testNotificationService.reset()

        // When
        val result = useCase.cancelAll()

        // Then
        assertTrue(result.isSuccess)
        assertTrue(testNotificationService.allNotificationsCancelled)
    }

    /**
     * 全通知キャンセル時にエラーが発生する場合のテスト
     */
    @Test
    fun `全通知キャンセル時のエラーはFailureが返される`() = runTest {
        // Given
        val exception = RuntimeException("全キャンセルエラー")
        testNotificationService.reset()
        testNotificationService.setShouldThrowException(exception)

        // When
        val result = useCase.cancelAll()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
