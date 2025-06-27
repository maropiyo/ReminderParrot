package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.EnhancedParrot
import com.maropiyo.reminderparrot.domain.entity.ParrotSkill
import com.maropiyo.reminderparrot.domain.entity.ParrotSkills
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository

/**
 * スキル解放UseCase
 *
 * レベルアップ時に解放可能なスキルを自動的に解放し、
 * 解放されたスキルの情報を返す
 *
 * @property parrotRepository インコリポジトリ
 */
class UnlockSkillUseCase(
    private val parrotRepository: ParrotRepository
) {

    /**
     * レベルアップ時のスキル解放処理
     *
     * @param oldLevel 前のレベル
     * @param newLevel 新しいレベル
     * @return 解放されたスキルのリスト
     */
    suspend fun execute(oldLevel: Int, newLevel: Int): Result<List<ParrotSkill>> = try {
        val currentParrotResult = parrotRepository.getParrot()
        if (currentParrotResult.isFailure) {
            return Result.failure(currentParrotResult.exceptionOrNull() ?: Exception("Failed to get parrot"))
        }

        // 現在のインコ情報を取得（仮でEnhancedParrotに変換）
        val currentParrot = currentParrotResult.getOrThrow()
        val enhancedParrot = EnhancedParrot(
            level = newLevel,
            currentExperience = currentParrot.currentExperience,
            maxExperience = currentParrot.maxExperience,
            unlockedSkills = emptySet() // TODO: データベースから取得
        )

        // レベル範囲内で解放可能なスキルを取得
        val skillsToUnlock = mutableListOf<ParrotSkill>()

        for (level in (oldLevel + 1)..newLevel) {
            val levelSkills = ParrotSkills.getSkillsForLevel(level)
            for (skill in levelSkills) {
                if (canUnlockSkill(enhancedParrot, skill)) {
                    skillsToUnlock.add(skill)
                }
            }
        }

        // TODO: 解放されたスキルをデータベースに保存

        Result.success(skillsToUnlock)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * スキルが解放可能かチェック
     *
     * @param parrot インコの状態
     * @param skill チェックするスキル
     * @return 解放可能かどうか
     */
    private fun canUnlockSkill(parrot: EnhancedParrot, skill: ParrotSkill): Boolean {
        // 既に解放済みの場合は false
        if (parrot.hasSkill(skill.id)) return false

        // レベル要件チェック
        if (parrot.level < skill.requiredLevel) return false

        // 前提スキル要件チェック
        return skill.prerequisites.all { prereqId -> parrot.hasSkill(prereqId) }
    }
}
