package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.mapper.ReminderMapper
import com.maropiyo.reminderparrot.data.repository.ReminderRepositoryImpl
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koinモジュールをまとめて管理するオブジェクト
 */
val appModule =
    module {
        // UseCase
        single<CreateReminderUseCase> { CreateReminderUseCase(get()) }
        single<GetRemindersUseCase> { GetRemindersUseCase(get()) }

        // Repository
        single<ReminderRepository> { ReminderRepositoryImpl(get()) }

        // Mapper
        single { ReminderMapper() }
    }

/**
 * プラットフォームごとのモジュールを定義する
 *
 * @return プラットフォームごとのモジュール
 */
expect fun getPlatformModule(): Module
