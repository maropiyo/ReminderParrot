package com.maropiyo.reminderparrot.data.datasource.remote

import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * リマインネット投稿のリモートデータソース
 */
class RemindNetRemoteDataSource(
    private val supabaseClient: SupabaseClient
) {
    /**
     * リマインネットに投稿を作成する
     */
    suspend fun createPost(
        reminderText: String,
        forgetAt: Instant,
        userId: String?,
        userName: String?
    ): Result<RemindNetPost> {
        return try {
            val dto = RemindNetPostDto(
                reminderText = reminderText,
                userId = userId,
                userName = userName ?: "Anonymous",
                forgetAt = forgetAt.toString()
            )

            val result = supabaseClient.from("remind_net_posts")
                .insert(dto) {
                    select()
                }
                .decodeSingle<RemindNetPostResponseDto>()

            Result.success(result.toEntity())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * すべての投稿を取得する（Flowとして）
     */
    fun getAllPosts(): Flow<List<RemindNetPost>> {
        return flow {
            emit(fetchAllPosts())
        }
    }

    /**
     * すべての投稿を取得する（単発）
     */
    suspend fun fetchAllPosts(): List<RemindNetPost> {
        return try {
            supabaseClient.from("remind_net_posts")
                .select(Columns.ALL) {
                    filter {
                        eq("is_deleted", false)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<RemindNetPostResponseDto>()
                .map { it.toEntity() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 投稿にいいねをする
     */
    suspend fun likePost(postId: String): Result<Unit> {
        return try {
            // いいね数をインクリメント
            supabaseClient.from("remind_net_posts")
                .update({
                    set("likes_count", "likes_count + 1")
                }) {
                    filter {
                        eq("id", postId)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
data class RemindNetPostDto(
    @SerialName("reminder_text") val reminderText: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("user_name") val userName: String = "Anonymous",
    @SerialName("forget_at") val forgetAt: String
)

@Serializable
data class RemindNetPostResponseDto(
    val id: String,
    @SerialName("reminder_text") val reminderText: String,
    @SerialName("user_id") val userId: String?,
    @SerialName("user_name") val userName: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("forget_at") val forgetAt: String,
    @SerialName("likes_count") val likesCount: Int,
    @SerialName("is_deleted") val isDeleted: Boolean
) {
    fun toEntity(): RemindNetPost {
        return RemindNetPost(
            id = id,
            reminderText = reminderText,
            userId = userId,
            userName = userName,
            createdAt = Instant.parse(createdAt),
            forgetAt = Instant.parse(forgetAt),
            likesCount = likesCount,
            isDeleted = isDeleted
        )
    }
}
