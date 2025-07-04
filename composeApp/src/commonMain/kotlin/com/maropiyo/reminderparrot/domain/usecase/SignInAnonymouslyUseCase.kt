package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.service.AuthService

/**
 * 匿名認証を実行するユースケース
 */
class SignInAnonymouslyUseCase(
    private val authService: AuthService
) {
    /**
     * 匿名ユーザーとしてサインインする
     * @return ユーザーID
     */
    suspend operator fun invoke(): Result<String> {
        return try {
            val userId = authService.getUserId()
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
