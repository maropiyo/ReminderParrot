package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.remote.ReminderRemoteDataSource
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * プラットフォームごとのモジュールを定義する(iOS)
 */
actual fun getPlatformModule(): Module =
    module {
        // Supabaseクライアント
        single { params ->
            // TODO: iOSの環境変数からSupabaseのURLとAPIキーを取得する
            val supabaseUrl = params.get<String>()
            val supabaseKey = params.get<String>()

            createSupabaseClient(
                supabaseUrl = supabaseUrl,
                supabaseKey = supabaseKey
            ) {
                install(Postgrest)
            }
        }

        // DataSource
        factory { ReminderRemoteDataSource(get(), get()) }
    }
