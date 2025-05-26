package com.maropiyo.reminderparrot

import android.app.Application
import com.maropiyo.reminderparrot.config.getSupabaseConfig
import com.maropiyo.reminderparrot.di.databaseModule
import com.maropiyo.reminderparrot.di.initKoin
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Koinの初期化
        initKoin(
            supabaseConfig = getSupabaseConfig(),
            additionalModules =
            listOf(
                databaseModule
            )
        ).apply {
            // AndroidContextの設定
            androidContext(this@AndroidApp)
        }
    }
}
