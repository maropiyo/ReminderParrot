package com.maropiyo.reminderparrot.domain.service

/**
 * ユーザーID管理サービス
 */
interface UserIdService {
    /**
     * デバイス固有のユーザーIDを取得する
     * 初回の場合は新しいUUIDを生成し、以降は保存されたIDを返す
     */
    suspend fun getUserId(): String
}
