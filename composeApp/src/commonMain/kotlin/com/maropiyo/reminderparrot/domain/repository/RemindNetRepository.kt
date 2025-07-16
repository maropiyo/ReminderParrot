package com.maropiyo.reminderparrot.domain.repository

import com.maropiyo.reminderparrot.domain.entity.Platform
import com.maropiyo.reminderparrot.domain.entity.RemindNetNotification
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
        userName: String? = null,
        userLevel: Int? = null
    ): Result<RemindNetPost>

    /**
     * すべての投稿を取得する（削除されていないもの）
     */
    fun getAllPosts(): Flow<List<RemindNetPost>>

    /**
     * 投稿を削除する
     */
    suspend fun deletePost(postId: String, userId: String): Result<Unit>

    /**
     * リマインド通知を送信する
     */
    suspend fun sendRemindNotification(notification: RemindNetNotification): Result<Unit>

    /**
     * プッシュ通知トークンを登録する
     */
    suspend fun registerPushNotificationToken(userId: String, token: String, platform: Platform): Result<Unit>
}
