package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.config.SupabaseConfig
import com.maropiyo.reminderparrot.data.remote.ReminderRemoteDataSource
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koinの初期化を行う
 *
 * @param supabaseConfig Supabaseの設定
 * @param additionalModules 追加のモジュール
 * @return KoinApplication
 */
fun initKoin(
    supabaseConfig: SupabaseConfig,
    additionalModules: List<Module> = emptyList()
): KoinApplication =
    startKoin {
        modules(
            listOf(
                appModule,
                createSupabaseModule(supabaseConfig)
            ) + additionalModules
        )
    }

/**
 * Supabase関連のモジュールを作成する
 *
 * @param supabaseConfig Supabaseの設定
 */
private fun createSupabaseModule(supabaseConfig: SupabaseConfig): Module =
    module {
        single {
            createSupabaseClient(
                supabaseUrl = supabaseConfig.url,
                supabaseKey = supabaseConfig.key
            ) {
                install(Postgrest)
            }
        }

        // RemoteDataSource
        single { ReminderRemoteDataSource(get(), get()) }
    }
