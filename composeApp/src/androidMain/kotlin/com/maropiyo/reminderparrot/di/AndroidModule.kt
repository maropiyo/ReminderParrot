package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.remote.ReminderRemoteDataSource
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * プラットフォームごとのモジュールを定義する(Android)
 */
actual fun getPlatformModule(): Module =
    module {
        single {
            createSupabaseClient(
                // TODO: SupabaseのURLとAPIキーを取得する
                supabaseUrl = "",
                supabaseKey = ""
            ) {
                install(Postgrest)
            }
        }

        // DataSource
        factory { ReminderRemoteDataSource(get(), get()) }
    }
