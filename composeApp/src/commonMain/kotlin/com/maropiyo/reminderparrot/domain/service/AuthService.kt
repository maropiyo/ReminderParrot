package com.maropiyo.reminderparrot.domain.service

/**
 * 認証サービスインターフェース
 */
interface AuthService {
    /**
     * 匿名認証でユーザーIDを取得する
     * 未認証の場合は自動的に匿名ユーザーを作成する
     * * @return ユーザーID
     */
    suspend fun getUserId(): String

    /**
     * 現在のユーザーIDを取得する（認証済みの場合のみ）
     * * @return ユーザーID、未認証の場合はnull
     */
    suspend fun getCurrentUserId(): String?

    /**
     * 認証状態をリセットする
     * 新しい匿名ユーザーでの認証を強制する
     */
    suspend fun resetAuth()
}
