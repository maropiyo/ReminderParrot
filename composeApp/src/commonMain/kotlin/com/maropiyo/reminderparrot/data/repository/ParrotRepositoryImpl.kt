package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.datasource.local.ParrotLocalDataSource
import com.maropiyo.reminderparrot.domain.entity.Parrot
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository

/**
 * インコリポジトリの実装
 *
 * @property localDataSource ローカルデータソース
 */
class ParrotRepositoryImpl(
    private val localDataSource: ParrotLocalDataSource
) : ParrotRepository {

    /**
     * インコの状態を取得する
     *
     * @return インコの状態
     */
    override suspend fun getParrot(): Result<Parrot> = try {
        val parrot = localDataSource.getParrot() ?: Parrot().also {
            // 初回起動時はデフォルトのインコを保存
            localDataSource.saveParrot(it)
        }
        Result.success(parrot)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * インコの状態を更新する
     *
     * @param parrot 更新するインコの状態
     * @return 更新結果
     */
    override suspend fun updateParrot(parrot: Parrot): Result<Unit> = try {
        localDataSource.updateParrot(parrot)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * 経験値を追加する
     *
     * @param experience 追加する経験値
     * @return 更新されたインコの状態
     */
    override suspend fun addExperience(experience: Int): Result<Parrot> = try {
        val currentParrot = localDataSource.getParrot() ?: Parrot()
        val newExperience = currentParrot.currentExperience + experience
        val updatedParrot = if (newExperience >= currentParrot.maxExperience) {
            // レベルアップロジック
            val newLevel = currentParrot.level + 1
            val remainingExperience = newExperience - currentParrot.maxExperience
            currentParrot.copy(
                level = newLevel,
                currentExperience = remainingExperience,
                maxExperience = calculateMaxExperience(newLevel),
                memorizedWords = calculateMemorizedWords(newLevel),
                memoryTimeHours = calculateMemoryTimeHours(newLevel)
            )
        } else {
            currentParrot.copy(currentExperience = newExperience)
        }
        localDataSource.updateParrot(updatedParrot)
        Result.success(updatedParrot)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * レベルに応じた最大経験値を計算する
     *
     * @param level レベル
     * @return 最大経験値
     */
    private fun calculateMaxExperience(level: Int): Int = level

    /**
     * レベルに応じた記憶できる単語数を計算する
     *
     * @param level レベル
     * @return 記憶できる単語数
     */
    private fun calculateMemorizedWords(level: Int): Int = level

    /**
     * レベルに応じた記憶時間を計算する
     *
     * @param level レベル
     * @return 記憶時間（時間）
     */
    private fun calculateMemoryTimeHours(level: Int): Int = level
}
