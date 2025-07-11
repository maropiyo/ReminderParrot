package com.maropiyo.reminderparrot.data.service

import com.maropiyo.reminderparrot.domain.service.AuthService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * SupabaseAuthを使用した認証サービス実装
 */
class AuthServiceImpl(
    private val supabaseClient: SupabaseClient
) : AuthService {

    private val authMutex = Mutex()

    override suspend fun getUserId(): String = authMutex.withLock {
        val currentUser = supabaseClient.auth.currentUserOrNull()
        if (currentUser != null) {
            return@withLock currentUser.id
        }

        // 匿名認証を実行
        supabaseClient.auth.signInAnonymously()
        val authenticatedUser = supabaseClient.auth.currentUserOrNull()
        return@withLock authenticatedUser?.id ?: throw IllegalStateException("匿名認証に失敗しました")
    }

    override suspend fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    override suspend fun resetAuth() = authMutex.withLock {
        try {
            supabaseClient.auth.signOut()
        } catch (e: Exception) {
            // サインアウトに失敗しても継続
        }
    }

    override suspend fun getDisplayName(): String? {
        // 最新のユーザー情報を取得するために、セッションを更新
        try {
            supabaseClient.auth.refreshCurrentSession()
        } catch (e: Exception) {
            // セッション更新失敗は無視して継続
        }

        val currentUser = supabaseClient.auth.currentUserOrNull()

        // JsonObjectからdisplay_nameを取得
        return try {
            val userMetadata = currentUser?.userMetadata
            if (userMetadata != null) {
                // JsonObjectからJsonPrimitiveを取得し、文字列として変換
                val displayNameElement = userMetadata["display_name"]

                // JsonPrimitiveの場合は文字列として取得
                if (displayNameElement is JsonPrimitive && displayNameElement.isString) {
                    displayNameElement.content
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateDisplayName(displayName: String) {
        val currentUser = supabaseClient.auth.currentUserOrNull()
            ?: throw IllegalStateException("ユーザーが認証されていません")

        supabaseClient.auth.updateUser {
            data = buildJsonObject {
                put("display_name", displayName)
            }
        }
    }

    override suspend fun logout() = authMutex.withLock {
        try {
            supabaseClient.auth.signOut()
        } catch (e: Exception) {
            // ログアウトに失敗してもエラーを投げずに処理を継続
            println("AuthServiceImpl: ログアウトエラー - $e")
        }
    }
}
