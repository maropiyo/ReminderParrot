package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.datasource.remote.RemindNetRemoteDataSource
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
        reminderText: String,
        forgetAt: Instant,
        userId: String?,
        userName: String?
    ): Result<RemindNetPost> {
        return remoteDataSource.createPost(reminderText, forgetAt, userId, userName)
    }

    override fun getAllPosts(): Flow<List<RemindNetPost>> {
        return remoteDataSource.getAllPosts()
    }

    override suspend fun likePost(postId: String): Result<Unit> {
        return remoteDataSource.likePost(postId)
    }
}
