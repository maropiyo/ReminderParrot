package com.maropiyo.reminderparrot

import android.app.Application
import com.maropiyo.reminderparrot.di.initKoin
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Supabase情報の取得
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_KEY

        // Koinの初期化
        initKoin(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ).apply {
            // AndroidContextの設定
            androidContext(this@AndroidApp)
        }
    }
}
