package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Parrot
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository

/**
 * インコ取得ユースケース
 *
 * @property parrotRepository インコリポジトリ
 */
class GetParrotUseCase(
    private val parrotRepository: ParrotRepository
) {
    /**
     * インコの状態を取得する
     *
     * @return インコの状態
     */
    suspend operator fun invoke(): Result<Parrot> = parrotRepository.getParrot()
}
