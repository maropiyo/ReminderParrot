package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.EnhancedParrot
import com.maropiyo.reminderparrot.domain.entity.ParrotSkill
import com.maropiyo.reminderparrot.domain.entity.SkillCategory
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository

/**
 * スキル状態の分類
 */
enum class SkillState {
    UNLOCKED, // 解放済み
    AVAILABLE, // 解放可能
    LOCKED // ロック中
}

/**
 * スキルノードの情報
 */
data class SkillNode(
    val skill: ParrotSkill,
    val state: SkillState
)

/**
 * カテゴリ別スキルツリー
 */
data class SkillTree(
    val category: SkillCategory,
    val skills: List<SkillNode>
)

/**
 * スキルツリー情報取得UseCase
 *
 * 現在のインコの状態に基づいて、スキルツリーの
 * 解放状態を取得する
 *
 * @property parrotRepository インコリポジトリ
 */
class GetSkillTreeUseCase(
    private val parrotRepository: ParrotRepository
) {

    /**
     * スキルツリー情報を取得
     *
     * @return カテゴリ別のスキルツリー情報
     */
    suspend fun execute(): Result<List<SkillTree>> = try {
        val currentParrotResult = parrotRepository.getParrot()
        if (currentParrotResult.isFailure) {
            return Result.failure(currentParrotResult.exceptionOrNull() ?: Exception("Failed to get parrot"))
        }

        // 現在のインコ情報を取得（仮でEnhancedParrotに変換）
        val currentParrot = currentParrotResult.getOrThrow()
        val enhancedParrot = EnhancedParrot(
            level = currentParrot.level,
            currentExperience = currentParrot.currentExperience,
            maxExperience = currentParrot.maxExperience,
            unlockedSkills = emptySet() // TODO: データベースから取得
        )

        // カテゴリ別にスキルツリーを構築
        val skillTrees = SkillCategory.values().map { category ->
            val categorySkills = enhancedParrot.getSkillsByCategory(category)
            val skillNodes = categorySkills.map { skill ->
                val state = determineSkillState(enhancedParrot, skill)
                SkillNode(skill, state)
            }
            SkillTree(category, skillNodes)
        }

        Result.success(skillTrees)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * スキルの状態を判定
     *
     * @param parrot インコの状態
     * @param skill 判定するスキル
     * @return スキルの状態
     */
    private fun determineSkillState(parrot: EnhancedParrot, skill: ParrotSkill): SkillState {
        return when {
            parrot.hasSkill(skill.id) -> SkillState.UNLOCKED
            canUnlockSkill(parrot, skill) -> SkillState.AVAILABLE
            else -> SkillState.LOCKED
        }
    }

    /**
     * スキルが解放可能かチェック
     */
    private fun canUnlockSkill(parrot: EnhancedParrot, skill: ParrotSkill): Boolean {
        if (parrot.hasSkill(skill.id)) return false
        if (parrot.level < skill.requiredLevel) return false
        return skill.prerequisites.all { prereqId -> parrot.hasSkill(prereqId) }
    }

    /**
     * EnhancedParrotの拡張関数：カテゴリ別スキル取得
     */
    private fun EnhancedParrot.getSkillsByCategory(category: SkillCategory): List<ParrotSkill> {
        return com.maropiyo.reminderparrot.domain.entity.ParrotSkills.getSkillsByCategory(category)
    }
}
