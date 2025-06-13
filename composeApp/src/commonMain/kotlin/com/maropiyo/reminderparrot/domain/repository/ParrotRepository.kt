package com.maropiyo.reminderparrot.domain.repository

import com.maropiyo.reminderparrot.domain.entity.Parrot

/**
 * インコリポジトリ
 */
interface ParrotRepository {
    /**
     * インコの状態を取得する
     *
     * @return インコの状態
     */
    suspend fun getParrot(): Result<Parrot>

    /**
     * インコの状態を更新する
     *
     * @param parrot 更新するインコの状態
     * @return 更新結果
     */
    suspend fun updateParrot(parrot: Parrot): Result<Unit>

    /**
     * 経験値を追加する
     *
     * @param experience 追加する経験値
     * @return 更新されたインコの状態
     */
    suspend fun addExperience(experience: Int): Result<Parrot>
}