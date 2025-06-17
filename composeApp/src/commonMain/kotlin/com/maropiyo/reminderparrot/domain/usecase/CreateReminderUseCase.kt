package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

/**
 * リマインダー作成ユースケース
 *
 * @property reminderRepository リマインダーリポジトリ
 * @property parrotRepository パロットリポジトリ
 * @property uuidGenerator UUIDジェネレーター
 */
class CreateReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val parrotRepository: ParrotRepository,
    private val uuidGenerator: UuidGenerator
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
            val forgetTime = currentTime + 70.seconds

            val reminder =
                Reminder(
                    id = uuidGenerator.generateId(),
                    text = text,
                    createdAt = currentTime,
                    forgetAt = forgetTime
                )

            reminderRepository.createReminder(reminder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
