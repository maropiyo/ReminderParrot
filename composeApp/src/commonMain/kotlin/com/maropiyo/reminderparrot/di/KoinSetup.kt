package com.maropiyo.reminderparrot.di

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
 * @param supabaseUrl SupabaseのURL
 * @param supabaseKey SupabaseのAPIキー
 * @param additionalModules 追加のモジュール
 * @return KoinApplication
 */
fun initKoin(supabaseUrl: String, supabaseKey: String, additionalModules: List<Module> = emptyList()): KoinApplication =
    startKoin {
        modules(
            listOf(
                appModule,
                createSupabaseModule(supabaseUrl, supabaseKey)
            ) + additionalModules
        )
    }

/**
 * Supabase関連のモジュールを作成する
 *
 * @param supabaseUrl SupabaseのURL
 * @param supabaseKey SupabaseのAPIキー
 */
private fun createSupabaseModule(supabaseUrl: String, supabaseKey: String): Module = module {
    single {
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Postgrest)
        }
    }

    // RemoteDataSource
    single { ReminderRemoteDataSource(get(), get()) }
}
