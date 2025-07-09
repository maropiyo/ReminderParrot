package com.maropiyo.reminderparrot.data.datasource.remote

import com.maropiyo.reminderparrot.domain.entity.Platform
import com.maropiyo.reminderparrot.domain.entity.RemindNetNotification
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
        reminderId: String,
        reminderText: String,
        forgetAt: Instant,
        userId: String?,
        userName: String?
    ): Result<RemindNetPost> {
        return try {
            val dto = RemindNetPostDto(
                id = reminderId,
                reminderText = reminderText,
                userId = userId,
                userName = userName ?: "名無しのインコ",
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

    /**
     * リマインド通知を送信する
     */
    suspend fun sendRemindNotification(notification: RemindNetNotification): Result<Unit> {
        return try {
            // 通知メッセージのバリエーション
            val messages = listOf(
                "${notification.reminderText}を早くやるっぴ！",
                "${notification.reminderText}を忘れてないっぴ？",
                "${notification.reminderText}は終わったっぴ？"
            )
            val message = messages.random()

            val notificationDto = RemindNetNotificationDto(
                postId = notification.postId,
                postUserId = notification.postUserId,
                senderUserId = notification.senderUserId,
                senderUserName = notification.senderUserName,
                title = notification.senderUserName,
                body = message,
                notificationType = notification.notificationType.name
            )

            // Supabaseのedge functionを呼び出して通知を送信
            val result = supabaseClient.functions.invoke(
                function = "send-push-notification-v1",
                body = buildJsonObject {
                    put("postId", notification.postId)
                    put("postUserId", notification.postUserId)
                    put("senderUserId", notification.senderUserId)
                    put("senderUserName", notification.senderUserName)
                    put("title", notification.senderUserName)
                    put("body", message)
                    put("notificationType", notification.notificationType.name)
                }
            )

            println("プッシュ通知送信結果: $result")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * プッシュ通知トークンを登録する
     */
    suspend fun registerPushNotificationToken(userId: String, token: String, platform: Platform): Result<Unit> {
        return try {
            val dto = PushNotificationTokenDto(
                userId = userId,
                token = token,
                platform = platform.name,
                updatedAt = Clock.System.now().toString()
            )

            // upsert（存在する場合は更新、なければ挿入）
            supabaseClient.from("push_tokens")
                .upsert(dto) {
                    onConflict = "user_id,platform"
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
data class RemindNetPostDto(
    val id: String,
    @SerialName("reminder_text") val reminderText: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("user_name") val userName: String = "名無しのインコ",
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

@Serializable
data class RemindNetNotificationDto(
    @SerialName("post_id") val postId: String,
    @SerialName("post_user_id") val postUserId: String,
    @SerialName("sender_user_id") val senderUserId: String,
    @SerialName("sender_user_name") val senderUserName: String,
    val title: String,
    val body: String,
    @SerialName("notification_type") val notificationType: String
)

@Serializable
data class PushNotificationTokenDto(
    @SerialName("user_id") val userId: String,
    val token: String,
    val platform: String,
    @SerialName("updated_at") val updatedAt: String
)
