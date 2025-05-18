package com.maropiyo.reminderparrot.config

/**
 * Supabaseの設定
 */
data class SupabaseConfig(
    val url: String,
    val key: String
)

/**
 * Supabaseの設定を取得する
 *
 * @return SupabaseConfig
 */
expect fun getSupabaseConfig(): SupabaseConfig
