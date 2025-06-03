package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.local.ReminderLocalDataSource
import com.maropiyo.reminderparrot.data.mapper.ReminderMapper
import com.maropiyo.reminderparrot.data.repository.ReminderRepositoryImpl
import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.UpdateReminderUseCase
import com.maropiyo.reminderparrot.presentation.viewmodel.ReminderListViewModel
import org.koin.dsl.module

/**
 * Koinモジュールをまとめて管理するオブジェクト
 */
val appModule =
    module {
        // ViewModel
        single<ReminderListViewModel> { ReminderListViewModel(get(), get(), get()) }

        // UseCase
        single<CreateReminderUseCase> { CreateReminderUseCase(get(), get()) }
        single<GetRemindersUseCase> { GetRemindersUseCase(get()) }
        single<UpdateReminderUseCase> { UpdateReminderUseCase(get()) }

        // Repository
        single<ReminderRepository> { ReminderRepositoryImpl(get(), get()) }

        // Mapper
        single { ReminderMapper() }

        // LocalDataSource
        single<ReminderLocalDataSource> { ReminderLocalDataSource(get(), get()) }

        // Common
        single<UuidGenerator> { UuidGenerator() }
    }
