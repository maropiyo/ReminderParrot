package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ImportHistoryRepository
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import com.maropiyo.reminderparrot.domain.service.AuthService
import com.maropiyo.reminderparrot.domain.service.NotificationService
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock

/**
 * リマインネット投稿をリマインダーとしてインポートするユースケース
 * 他のインコの投稿を自分のインコに覚えさせる機能
 *
 * @property reminderRepository リマインダーリポジトリ
 * @property parrotRepository パロットリポジトリ
 * @property uuidGenerator UUIDジェネレーター
 * @property notificationService 通知サービス
 * @property getUserSettingsUseCase ユーザー設定取得ユースケース
 * @property addParrotExperienceUseCase インコ経験値追加ユースケース
 * @property authService 認証サービス
 * @property importHistoryRepository インポート履歴リポジトリ
 */
class ImportRemindNetPostUseCase(
    private val reminderRepository: ReminderRepository,
    private val parrotRepository: ParrotRepository,
    private val uuidGenerator: UuidGenerator,
    private val notificationService: NotificationService,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val addParrotExperienceUseCase: AddParrotExperienceUseCase,
    private val authService: AuthService,
    private val importHistoryRepository: ImportHistoryRepository
) {
    /**
     * リマインネット投稿をリマインダーとしてインポートする
     *
     * @param post インポートするリマインネット投稿
     * @return インポートしたリマインダー
     */
    suspend operator fun invoke(post: RemindNetPost): Result<Reminder> {
        return try {
            // 現在のユーザーIDを取得
            val currentUserId = authService.getCurrentUserId()
                ?: return Result.failure(IllegalStateException("ユーザーが認証されていません"))

            // 既にインポート済みかチェック
            if (importHistoryRepository.hasAlreadyImported(post.id, currentUserId)) {
                return Result.failure(IllegalStateException("すでにおぼえているよ"))
            }

            // 記憶容量制限チェック
            val remindersResult = reminderRepository.getReminders()
            val parrotResult = parrotRepository.getParrot()
            if (remindersResult.isFailure || parrotResult.isFailure) {
                return Result.failure(Exception("記憶容量チェックに失敗しました"))
            }
            
            val currentReminderCount = remindersResult.getOrThrow().size
            val memorizedWords = parrotResult.getOrThrow().memorizedWords
            if (currentReminderCount >= memorizedWords) {
                return Result.failure(IllegalStateException("もうおぼえられないよ〜"))
            }

            // ユーザー設定を取得
            val userSettings = getUserSettingsUseCase()

            val currentTime = Clock.System.now()
            val forgetTime = if (userSettings.isDebugFastMemoryEnabled) {
                // デバッグモードが有効な場合は設定された秒数後に忘却
                currentTime + userSettings.debugForgetTimeSeconds.seconds
            } else {
                // 通常モードの場合はインコの記憶時間を使用
                val parrotResult = parrotRepository.getParrot()
                if (parrotResult.isFailure) {
                    return Result.failure(parrotResult.exceptionOrNull()!!)
                }
                val parrot = parrotResult.getOrThrow()
                // インコの記憶時間に基づいて忘却時刻を計算
                currentTime + parrot.memoryTimeHours.hours
            }

            val reminder = Reminder(
                id = uuidGenerator.generateId(),
                text = post.reminderText,
                createdAt = currentTime,
                forgetAt = forgetTime
            )

            val createResult = reminderRepository.createReminder(reminder)

            // リマインダーの作成が成功した場合
            if (createResult.isSuccess) {
                try {
                    // インポート履歴を記録
                    importHistoryRepository.recordImportHistory(post.id, currentUserId)

                    // 忘却通知をスケジュール
                    notificationService.scheduleForgetNotification(createResult.getOrThrow())

                    // インポート成功時に経験値+1を獲得
                    addParrotExperienceUseCase(1)
                        .onSuccess { updatedParrot ->
                            // 経験値追加成功
                        }
                        .onFailure { error ->
                            // 経験値追加エラー（ログなし）
                        }
                } catch (e: Exception) {
                    // 通知のスケジューリングに失敗してもリマインダー作成は成功とする
                }
            }

            createResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
