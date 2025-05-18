package com.maropiyo.reminderparrot.config

import com.maropiyo.reminderparrot.BuildConfig

/**
 * Supabaseの設定を取得する(Android)
 *
 * @return SupabaseConfig
 */
actual fun getSupabaseConfig(): SupabaseConfig = SupabaseConfig(
    url = BuildConfig.SUPABASE_URL,
    key = BuildConfig.SUPABASE_KEY
)
