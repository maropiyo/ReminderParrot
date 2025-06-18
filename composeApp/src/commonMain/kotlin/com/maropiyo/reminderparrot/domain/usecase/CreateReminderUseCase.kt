package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import com.maropiyo.reminderparrot.domain.service.NotificationService
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock

/**
 * リマインダー作成ユースケース
 *
 * @property reminderRepository リマインダーリポジトリ
 * @property parrotRepository パロットリポジトリ
 * @property uuidGenerator UUIDジェネレーター
 * @property notificationService 通知サービス
 */
class CreateReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val parrotRepository: ParrotRepository,
    private val uuidGenerator: UuidGenerator,
    private val notificationService: NotificationService
) {
    /**
     * リマインダーを作成する
     *
     * @param text リマインダーテキスト
     * @return 作成したリマインダー
     */
    suspend operator fun invoke(text: String): Result<Reminder> {
        return try {
            // インコの記憶時間を取得
            val parrotResult = parrotRepository.getParrot()
            if (parrotResult.isFailure) {
                return Result.failure(parrotResult.exceptionOrNull()!!)
            }

            val parrot = parrotResult.getOrThrow()
            val currentTime = Clock.System.now()
            // デバッグ用: 30秒後に忘却
            val forgetTime = currentTime + 30.seconds

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
