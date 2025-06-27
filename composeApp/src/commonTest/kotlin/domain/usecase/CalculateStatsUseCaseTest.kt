package domain.usecase

import com.maropiyo.reminderparrot.domain.entity.EnhancedParrot
import com.maropiyo.reminderparrot.domain.entity.ParrotSkills
import com.maropiyo.reminderparrot.domain.usecase.CalculateStatsUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculateStatsUseCaseTest {

    private val calculateStatsUseCase = CalculateStatsUseCase()

    @Test
    fun `基本統計の計算が正しく動作する`() {
        // Given
        val parrot = EnhancedParrot(level = 5)

        // When
        val stats = calculateStatsUseCase.execute(parrot)

        // Then
        assertTrue(stats.memorizedWords >= 1, "記憶語数は1以上である必要があります")
        assertTrue(stats.memoryTimeHours >= 1, "記憶時間は1時間以上である必要があります")
        assertEquals(1, stats.emotionalRange, "基本の感情表現は1種類です")
        assertEquals(0, stats.socialConnections, "基本の社会的つながりは0です")
        assertEquals(1.0f, stats.learningSpeed, "基本の学習速度は1.0です")
    }

    @Test
    fun `基本感情スキルにより感情表現が拡張される`() {
        // Given
        val parrot = EnhancedParrot(
            level = 5,
            unlockedSkills = setOf(ParrotSkills.BASIC_EMOTIONS.id)
        )

        // When
        val stats = calculateStatsUseCase.execute(parrot)

        // Then
        assertEquals(3, stats.emotionalRange, "基本感情スキルで感情表現は3種類になります")
    }

    @Test
    fun `パターン学習スキルにより学習能力が向上する`() {
        // Given
        val baseParrot = EnhancedParrot(level = 10)
        val skillParrot = EnhancedParrot(
            level = 10,
            unlockedSkills = setOf(ParrotSkills.PATTERN_LEARNING.id)
        )

        // When
        val baseStats = calculateStatsUseCase.execute(baseParrot)
        val skillStats = calculateStatsUseCase.execute(skillParrot)

        // Then
        assertTrue(
            skillStats.learningSpeed > baseStats.learningSpeed,
            "パターン学習スキルで学習速度が向上します"
        )
        assertTrue(
            skillStats.memorizedWords > baseStats.memorizedWords,
            "パターン学習スキルで記憶語数が増加します"
        )
    }

    @Test
    fun `家族絆スキルにより社会的つながりが増加する`() {
        // Given
        val parrot = EnhancedParrot(
            level = 20,
            unlockedSkills = setOf(ParrotSkills.FAMILY_BONDING.id)
        )

        // When
        val stats = calculateStatsUseCase.execute(parrot)

        // Then
        assertEquals(5, stats.socialConnections, "家族絆スキルで社会的つながりが5増加します")
    }

    @Test
    fun `複数スキルの効果が重複して適用される`() {
        // Given
        val parrot = EnhancedParrot(
            level = 35,
            unlockedSkills = setOf(
                ParrotSkills.BASIC_EMOTIONS.id,
                ParrotSkills.PATTERN_LEARNING.id,
                ParrotSkills.FAMILY_BONDING.id,
                ParrotSkills.RICH_EMOTIONS.id
            )
        )

        // When
        val stats = calculateStatsUseCase.execute(parrot)

        // Then
        assertEquals(10, stats.emotionalRange, "豊かな感情スキルで感情表現は最大10種類になります")
        assertEquals(5, stats.socialConnections, "家族絆スキルで社会的つながりが増加します")
        assertTrue(stats.learningSpeed > 1.0f, "パターン学習スキルで学習速度が向上します")
    }

    @Test
    fun `マスターコンパニオンスキルで全能力が大幅向上する`() {
        // Given
        val baseParrot = EnhancedParrot(level = 50)
        val masterParrot = EnhancedParrot(
            level = 50,
            unlockedSkills = setOf(ParrotSkills.MASTER_COMPANION.id)
        )

        // When
        val baseStats = calculateStatsUseCase.execute(baseParrot)
        val masterStats = calculateStatsUseCase.execute(masterParrot)

        // Then
        assertTrue(
            masterStats.memorizedWords >= baseStats.memorizedWords * 2,
            "マスタースキルで記憶語数が2倍以上になります"
        )
        assertTrue(
            masterStats.memoryTimeHours >= baseStats.memoryTimeHours * 2,
            "マスタースキルで記憶時間が2倍以上になります"
        )
        assertEquals(15, masterStats.emotionalRange, "マスタースキルで感情表現は究極の15種類になります")
        assertTrue(masterStats.socialConnections >= 10, "マスタースキルで社会的つながりが大幅増加します")
        assertTrue(masterStats.learningSpeed >= 3.0f, "マスタースキルで学習速度が3倍以上になります")
    }

    @Test
    fun `最大経験値の計算が正しく動作する`() {
        // Given & When & Then
        assertEquals(10, calculateStatsUseCase.calculateMaxExperience(1), "レベル1の必要経験値")
        assertEquals(50, calculateStatsUseCase.calculateMaxExperience(5), "レベル5の必要経験値")
        assertEquals(100, calculateStatsUseCase.calculateMaxExperience(10), "レベル10の必要経験値")
        assertEquals(120, calculateStatsUseCase.calculateMaxExperience(11), "レベル11の必要経験値")

        // 学習ボーナス有り
        val withBonus = calculateStatsUseCase.calculateMaxExperience(10, hasLearningBonus = true)
        val withoutBonus = calculateStatsUseCase.calculateMaxExperience(10, hasLearningBonus = false)
        assertTrue(withBonus < withoutBonus, "学習ボーナスがある場合は必要経験値が減少します")
    }
}
