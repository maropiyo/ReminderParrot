package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.config.SupabaseConfig
import com.maropiyo.reminderparrot.data.remote.ReminderRemoteDataSource
import com.maropiyo.reminderparrot.data.service.AuthServiceImpl
import com.maropiyo.reminderparrot.domain.service.AuthService
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
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
fun initKoin(supabaseConfig: SupabaseConfig, additionalModules: List<Module> = emptyList()): KoinApplication =
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
private fun createSupabaseModule(supabaseConfig: SupabaseConfig): Module = module {
    single {
        createSupabaseClient(
            supabaseUrl = supabaseConfig.url,
            supabaseKey = supabaseConfig.key
        ) {
            install(Postgrest)
            install(Auth)
            install(Functions)
        }
    }

    // Services
    single<AuthService> { AuthServiceImpl(get()) }

    // RemoteDataSource
    single { ReminderRemoteDataSource(get(), get()) }
}
