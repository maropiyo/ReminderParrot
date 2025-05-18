package com.maropiyo.reminderparrot.config

import platform.Foundation.NSBundle

/**
 * Supabaseの設定を取得する
 *
 * @return SupabaseConfig
 */
actual fun getSupabaseConfig(): SupabaseConfig {
    val bundle = NSBundle.mainBundle
    return SupabaseConfig(
        url = bundle.objectForInfoDictionaryKey("SUPABASE_URL") as? String ?: "",
        key = bundle.objectForInfoDictionaryKey("SUPABASE_KEY") as? String ?: ""
    )
}
