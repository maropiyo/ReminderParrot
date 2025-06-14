package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Parrot
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository

/**
 * インコの経験値を追加するユースケース
 *
 * リマインダー追加時などに経験値を加算する
 *
 * @property parrotRepository インコリポジトリ
 */
class AddParrotExperienceUseCase(
    private val parrotRepository: ParrotRepository
) {
    /**
     * 経験値を追加する
     *
     * デフォルトで1ポイントの経験値を追加
     *
     * @param experience 追加する経験値（デフォルト: 1）
     * @return 更新されたインコの状態
     */
    suspend operator fun invoke(experience: Int = 1): Result<Parrot> {
        return parrotRepository.addExperience(experience)
    }
}
