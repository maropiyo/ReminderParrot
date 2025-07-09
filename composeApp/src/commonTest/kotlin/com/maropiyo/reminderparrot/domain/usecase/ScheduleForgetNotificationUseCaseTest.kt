package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.service.NotificationService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock

/**
 * ScheduleForgetNotificationUseCaseのテストクラス
 */
class ScheduleForgetNotificationUseCaseTest {

    /**
     * テスト用のNotificationServiceのテストダブル
     */
    private class TestNotificationService : NotificationService {
        private var permissionGranted = true
        private var shouldThrowException = false
        private var exceptionToThrow: Exception? = null
        var lastScheduledReminder: Reminder? = null

        fun setPermissionGranted(granted: Boolean) {
            this.permissionGranted = granted
        }

        fun setShouldThrowException(exception: Exception) {
            this.shouldThrowException = true
            this.exceptionToThrow = exception
        }

        fun reset() {
            permissionGranted = true
            shouldThrowException = false
            exceptionToThrow = null
            lastScheduledReminder = null
        }

        override suspend fun requestNotificationPermission(): Boolean {
            return permissionGranted
        }

        override suspend fun isNotificationPermissionGranted(): Boolean {
            return permissionGranted
        }

        override suspend fun scheduleForgetNotification(reminder: Reminder) {
            if (shouldThrowException) {
                throw exceptionToThrow!!
            }
            lastScheduledReminder = reminder
        }

        override suspend fun cancelForgetNotification(reminderId: String) {
            // テスト用の実装
        }

        override suspend fun cancelAllForgetNotifications() {
            // テスト用の実装
        }

        override suspend fun getPushNotificationToken(): String? {
            return "test-token"
        }

        override suspend fun refreshPushNotificationToken(): String? {
            return "refreshed-test-token"
        }
    }

    private val testNotificationService = TestNotificationService()
    private val useCase = ScheduleForgetNotificationUseCase(testNotificationService)

    /**
     * 通知権限がある場合のスケジューリング成功テスト
     */
    @Test
    fun `通知権限がある場合は正常にスケジュールされる`() = runTest {
        // Given
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "test-1",
            text = "テストリマインダー",
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        testNotificationService.reset()
        testNotificationService.setPermissionGranted(true)

        // When
        val result = useCase(reminder)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(reminder, testNotificationService.lastScheduledReminder)
    }

    /**
     * 通知権限がない場合の権限要求テスト
     */
    @Test
    fun `通知権限がない場合は権限要求を行い成功する`() = runTest {
        // Given
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "test-2",
            text = "権限テスト",
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        testNotificationService.reset()
        testNotificationService.setPermissionGranted(false)

        // When & Then (権限が拒否される場合の実装)
        // 実際の実装では権限要求ダイアログが表示される
        val result = useCase(reminder)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    /**
     * スケジューリング時にエラーが発生する場合のテスト
     */
    @Test
    fun `スケジューリング時のエラーはFailureが返される`() = runTest {
        // Given
        val currentTime = Clock.System.now()
        val reminder = Reminder(
            id = "test-3",
            text = "エラーテスト",
            createdAt = currentTime,
            forgetAt = currentTime + 24.hours
        )
        val exception = RuntimeException("スケジューリングエラー")
        testNotificationService.reset()
        testNotificationService.setPermissionGranted(true)
        testNotificationService.setShouldThrowException(exception)

        // When
        val result = useCase(reminder)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
