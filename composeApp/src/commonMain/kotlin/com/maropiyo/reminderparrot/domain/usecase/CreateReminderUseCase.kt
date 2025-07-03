package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import com.maropiyo.reminderparrot.domain.service.NotificationService
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock

/**
 * リマインダー作成ユースケース
 *
 * @property reminderRepository リマインダーリポジトリ
 * @property parrotRepository パロットリポジトリ
 * @property uuidGenerator UUIDジェネレーター
 * @property notificationService 通知サービス
 * @property getUserSettingsUseCase ユーザー設定取得ユースケース
 */
class CreateReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val parrotRepository: ParrotRepository,
    private val uuidGenerator: UuidGenerator,
    private val notificationService: NotificationService,
    private val getUserSettingsUseCase: GetUserSettingsUseCase
) {
    /**
     * リマインダーを作成する
     *
     * @param text リマインダーテキスト
     * @return 作成したリマインダー
     */
    suspend operator fun invoke(text: String): Result<Reminder> {
        return try {
            // ユーザー設定を取得
            val userSettings = getUserSettingsUseCase()

            // デバッグ用ログ
            println("CreateReminderUseCase: getUserSettings called")
            println("  isDebugFastMemoryEnabled: ${userSettings.isDebugFastMemoryEnabled}")

            val currentTime = Clock.System.now()
            val forgetTime = if (userSettings.isDebugFastMemoryEnabled) {
                // デバッグモードが有効な場合は5秒後に忘却
                println("CreateReminderUseCase: Debug mode enabled - using 5 seconds")
                currentTime + 5.seconds
            } else {
                // 通常モードの場合はインコの記憶時間を使用
                println("CreateReminderUseCase: Normal mode - using parrot memory time")
                val parrotResult = parrotRepository.getParrot()
                if (parrotResult.isFailure) {
                    return Result.failure(parrotResult.exceptionOrNull()!!)
                }
                val parrot = parrotResult.getOrThrow()
                // インコの記憶時間に基づいて忘却時刻を計算
                println("CreateReminderUseCase: Parrot memory time: ${parrot.memoryTimeHours} hours")
                currentTime + parrot.memoryTimeHours.hours
            }

            val reminder =
                Reminder(
                    id = uuidGenerator.generateId(),
                    text = text,
                    createdAt = currentTime,
                    forgetAt = forgetTime
                )

            val createResult = reminderRepository.createReminder(reminder)

            // リマインダーの作成が成功した場合、忘却通知をスケジュール
            if (createResult.isSuccess) {
                try {
                    notificationService.scheduleForgetNotification(createResult.getOrThrow())
                } catch (e: Exception) {
                    // 通知のスケジューリングに失敗してもリマインダー作成は成功とする
                    // ログ出力などのエラーハンドリングは実装に応じて追加
                }
            }

            createResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
