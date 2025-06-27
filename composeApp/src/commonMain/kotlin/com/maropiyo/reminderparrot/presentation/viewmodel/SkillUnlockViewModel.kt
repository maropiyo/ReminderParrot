package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.entity.ParrotSkill
import com.maropiyo.reminderparrot.domain.entity.SkillCategory
import com.maropiyo.reminderparrot.presentation.state.AnimationPhase
import com.maropiyo.reminderparrot.presentation.state.SkillUnlockState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * スキル解放演出のViewModel
 *
 * レベルアップ時のスキル解放アニメーションと
 * ユーザーへの通知を管理する
 */
class SkillUnlockViewModel : ViewModel() {

    private val _state = MutableStateFlow(SkillUnlockState())
    val state: StateFlow<SkillUnlockState> = _state.asStateFlow()

    /**
     * スキル解放演出を開始
     *
     * @param oldLevel 前のレベル
     * @param newLevel 新しいレベル
     * @param unlockedSkills 解放されたスキル一覧
     */
    fun startSkillUnlockPresentation(oldLevel: Int, newLevel: Int, unlockedSkills: List<ParrotSkill>) {
        if (unlockedSkills.isEmpty()) return

        viewModelScope.launch {
            _state.value = SkillUnlockState(
                isVisible = true,
                unlockedSkills = unlockedSkills,
                currentSkillIndex = 0,
                animationPhase = AnimationPhase.LEVEL_UP,
                isLevelUpAnimation = true
            )

            // レベルアップアニメーション
            delay(1500)

            // 各スキルの解放演出
            for (index in unlockedSkills.indices) {
                _state.value = _state.value.copy(
                    currentSkillIndex = index,
                    animationPhase = AnimationPhase.SKILL_REVEAL,
                    isLevelUpAnimation = false
                )

                // スキル解放演出
                delay(800)

                _state.value = _state.value.copy(
                    animationPhase = AnimationPhase.SKILL_DISPLAY
                )

                // スキル情報表示
                delay(2000)

                // 次のスキルへの遷移（最後でなければ）
                if (index < unlockedSkills.size - 1) {
                    _state.value = _state.value.copy(
                        animationPhase = AnimationPhase.TRANSITION
                    )
                    delay(500)
                }
            }

            // 演出完了
            _state.value = _state.value.copy(
                animationPhase = AnimationPhase.IDLE
            )
        }
    }

    /**
     * 演出をスキップ
     */
    fun skipPresentation() {
        _state.value = _state.value.copy(
            currentSkillIndex = _state.value.unlockedSkills.size,
            animationPhase = AnimationPhase.IDLE
        )
    }

    /**
     * 演出を終了
     */
    fun dismissPresentation() {
        _state.value = SkillUnlockState()
    }

    /**
     * 次のスキルへ手動で進む
     */
    fun proceedToNextSkill() {
        val currentState = _state.value
        if (currentState.hasNextSkill) {
            _state.value = currentState.copy(
                currentSkillIndex = currentState.currentSkillIndex + 1,
                animationPhase = AnimationPhase.SKILL_REVEAL
            )

            viewModelScope.launch {
                delay(800)
                _state.value = _state.value.copy(
                    animationPhase = AnimationPhase.SKILL_DISPLAY
                )
            }
        } else {
            dismissPresentation()
        }
    }

    /**
     * スキルカテゴリに応じたアニメーションタイプを取得
     */
    fun getAnimationTypeForSkill(skill: ParrotSkill): SkillAnimationType {
        return when (skill.category) {
            SkillCategory.EXPRESSION -> SkillAnimationType.EXPRESSION
            SkillCategory.INTELLIGENCE -> SkillAnimationType.INTELLIGENCE
            SkillCategory.SOCIAL -> SkillAnimationType.SOCIAL
            SkillCategory.SPECIAL -> SkillAnimationType.SPECIAL
        }
    }

    /**
     * スキルカテゴリに応じたメッセージを取得
     */
    fun getMessageForSkill(skill: ParrotSkill): String {
        return when (skill.category) {
            SkillCategory.EXPRESSION -> "新しい表情を覚えました！"
            SkillCategory.INTELLIGENCE -> "賢くなりました！"
            SkillCategory.SOCIAL -> "新しい仲間と出会えます！"
            SkillCategory.SPECIAL -> "特別な力を身につけました！"
        }
    }
}

/**
 * スキル解放アニメーションのタイプ
 */
enum class SkillAnimationType {
    EXPRESSION, // 表情・感情系
    INTELLIGENCE, // 知性・学習系
    SOCIAL, // 社会性・連携系
    SPECIAL // 特殊・魔法系
}
