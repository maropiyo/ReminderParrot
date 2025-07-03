package com.maropiyo.reminderparrot.domain.repository

import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import kotlinx.coroutines.flow.Flow

/**
 * リマインネット投稿のリポジトリインターフェース
 */
interface RemindNetRepository {
    /**
     * リマインネットに投稿を作成する
     */
    suspend fun createPost(
        reminderId: String,
        reminderText: String,
        forgetAt: kotlinx.datetime.Instant,
        userId: String? = null,
        userName: String? = null
    ): Result<RemindNetPost>

    /**
     * すべての投稿を取得する（削除されていないもの）
     */
    fun getAllPosts(): Flow<List<RemindNetPost>>

    /**
     * 投稿にいいねをする
     */
    suspend fun likePost(postId: String): Result<Unit>
}
