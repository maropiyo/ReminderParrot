package com.maropiyo.reminderparrot.presentation.state

import com.maropiyo.reminderparrot.domain.entity.ParrotSkill

/**
 * スキル解放の演出状態
 */
data class SkillUnlockState(
    val isVisible: Boolean = false,
    val unlockedSkills: List<ParrotSkill> = emptyList(),
    val currentSkillIndex: Int = 0,
    val animationPhase: AnimationPhase = AnimationPhase.IDLE,
    val isLevelUpAnimation: Boolean = false
) {
    /**
     * 現在表示中のスキル
     */
    val currentSkill: ParrotSkill?
        get() = unlockedSkills.getOrNull(currentSkillIndex)

    /**
     * 次のスキルがあるかどうか
     */
    val hasNextSkill: Boolean
        get() = currentSkillIndex < unlockedSkills.size - 1

    /**
     * 演出が完了したかどうか
     */
    val isComplete: Boolean
        get() = currentSkillIndex >= unlockedSkills.size && animationPhase == AnimationPhase.IDLE
}

/**
 * アニメーション段階
 */
enum class AnimationPhase {
    IDLE, // 待機
    LEVEL_UP, // レベルアップアニメーション
    SKILL_REVEAL, // スキル解放演出
    SKILL_DISPLAY, // スキル情報表示
    TRANSITION // 次のスキルへの遷移
}
