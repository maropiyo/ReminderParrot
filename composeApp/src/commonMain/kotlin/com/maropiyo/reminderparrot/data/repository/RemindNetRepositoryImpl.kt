package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.datasource.remote.RemindNetRemoteDataSource
import com.maropiyo.reminderparrot.domain.entity.Platform
import com.maropiyo.reminderparrot.domain.entity.RemindNetNotification
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * リマインネット投稿リポジトリの実装
 */
class RemindNetRepositoryImpl(
    private val remoteDataSource: RemindNetRemoteDataSource
) : RemindNetRepository {

    override suspend fun createPost(
        reminderId: String,
        reminderText: String,
        forgetAt: Instant,
        userId: String?,
        userName: String?
    ): Result<RemindNetPost> {
        return remoteDataSource.createPost(reminderId, reminderText, forgetAt, userId, userName)
    }

    override fun getAllPosts(): Flow<List<RemindNetPost>> {
        return remoteDataSource.getAllPosts()
    }

    override suspend fun likePost(postId: String): Result<Unit> {
        return remoteDataSource.likePost(postId)
    }

    override suspend fun sendRemindNotification(notification: RemindNetNotification): Result<Unit> {
        return remoteDataSource.sendRemindNotification(notification)
    }

    override suspend fun registerPushNotificationToken(
        userId: String,
        token: String,
        platform: Platform
    ): Result<Unit> {
        return remoteDataSource.registerPushNotificationToken(userId, token, platform)
    }
}
