package com.maropiyo.reminderparrot.data.service

import kotlin.coroutines.resume
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSUserDefaults

/**
 * KMPからFirebaseManagerを呼び出すためのブリッジ
 * * FirebaseManagerがUserDefaultsに保存するFCMトークンを読み取ります
 */
@OptIn(ExperimentalForeignApi::class)
object FirebaseManagerBridge {

    /**
     * FirebaseManagerからFCMトークンを取得
     * FirebaseManagerがUserDefaultsに保存したトークンを読み取ります
     */
    suspend fun getFCMToken(): String? {
        return suspendCancellableCoroutine { continuation ->
            try {
                // UserDefaultsからトークンを読み取る
                val userDefaults = NSUserDefaults.standardUserDefaults
                val storedToken = userDefaults.stringForKey("FCMToken")

                if (storedToken != null) {
                    println("リマインコ(iOS): FCMトークン取得成功")
                    continuation.resume(storedToken)
                } else {
                    println("リマインコ(iOS): FCMトークンがUserDefaultsに見つかりません")
                    continuation.resume(null)
                }
            } catch (e: Exception) {
                println("リマインコ(iOS): FCMトークン取得エラー - ${e.message}")
                continuation.resume(null)
            }
        }
    }

    /**
     * FirebaseManagerでFCMトークンをリフレッシュ
     * FirebaseManagerのrefreshFCMTokenメソッドを呼び出すシミュレーション
     */
    suspend fun refreshFCMToken(): String? {
        return suspendCancellableCoroutine { continuation ->
            try {
                // UserDefaultsから最新のトークンを取得
                val userDefaults = NSUserDefaults.standardUserDefaults
                val currentToken = userDefaults.stringForKey("FCMToken")

                continuation.resume(currentToken)
            } catch (e: Exception) {
                println("リマインコ(iOS): FCMトークンリフレッシュエラー - ${e.message}")
                continuation.resume(null)
            }
        }
    }
}
