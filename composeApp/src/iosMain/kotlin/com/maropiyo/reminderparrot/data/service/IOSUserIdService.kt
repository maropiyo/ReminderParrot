package com.maropiyo.reminderparrot.data.service

import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.service.UserIdService
import platform.Foundation.NSUserDefaults

/**
 * iOS用ユーザーID管理サービス
 */
class IOSUserIdService(
    private val uuidGenerator: UuidGenerator
) : UserIdService {
    
    companion object {
        private const val KEY_USER_ID = "reminder_parrot_user_id"
    }
    
    override suspend fun getUserId(): String {
        val userDefaults = NSUserDefaults.standardUserDefaults
        
        // 既存のユーザーIDがあれば返す
        val existingUserId = userDefaults.stringForKey(KEY_USER_ID)
        if (existingUserId != null) {
            return existingUserId
        }
        
        // 新しいユーザーIDを生成して保存
        val newUserId = uuidGenerator.generateId()
        userDefaults.setObject(newUserId, KEY_USER_ID)
        
        return newUserId
    }
}