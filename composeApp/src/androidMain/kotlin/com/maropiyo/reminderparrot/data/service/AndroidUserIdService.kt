package com.maropiyo.reminderparrot.data.service

import android.content.Context
import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.service.UserIdService

/**
 * Android用ユーザーID管理サービス
 */
class AndroidUserIdService(
    private val context: Context,
    private val uuidGenerator: UuidGenerator
) : UserIdService {
    
    companion object {
        private const val PREFS_NAME = "reminder_parrot_user"
        private const val KEY_USER_ID = "user_id"
    }
    
    override suspend fun getUserId(): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // 既存のユーザーIDがあれば返す
        val existingUserId = prefs.getString(KEY_USER_ID, null)
        if (existingUserId != null) {
            return existingUserId
        }
        
        // 新しいユーザーIDを生成して保存
        val newUserId = uuidGenerator.generateId()
        prefs.edit()
            .putString(KEY_USER_ID, newUserId)
            .apply()
        
        return newUserId
    }
}