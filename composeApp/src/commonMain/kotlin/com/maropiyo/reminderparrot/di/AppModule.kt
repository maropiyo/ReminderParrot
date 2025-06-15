package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.local.ParrotLocalDataSource
import com.maropiyo.reminderparrot.data.local.ReminderLocalDataSource
import com.maropiyo.reminderparrot.data.mapper.ParrotMapper
import com.maropiyo.reminderparrot.data.mapper.ReminderMapper
import com.maropiyo.reminderparrot.data.repository.ParrotRepositoryImpl
import com.maropiyo.reminderparrot.data.repository.ReminderRepositoryImpl
import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import com.maropiyo.reminderparrot.domain.usecase.AddParrotExperienceUseCase
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetParrotUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.UpdateReminderUseCase
import com.maropiyo.reminderparrot.presentation.viewmodel.ParrotViewModel
import com.maropiyo.reminderparrot.presentation.viewmodel.ReminderListViewModel
import org.koin.dsl.module

/**
 * Koinモジュールをまとめて管理するオブジェクト
 */
val appModule =
    module {
        // ViewModel
        single<ReminderListViewModel> { ReminderListViewModel(get(), get(), get(), get()) }
        single<ParrotViewModel> { ParrotViewModel(get()) }

        // UseCase
        single<CreateReminderUseCase> { CreateReminderUseCase(get(), get()) }
        single<GetRemindersUseCase> { GetRemindersUseCase(get()) }
        single<UpdateReminderUseCase> { UpdateReminderUseCase(get()) }
        single<GetParrotUseCase> { GetParrotUseCase(get()) }
        single<AddParrotExperienceUseCase> { AddParrotExperienceUseCase(get()) }

        // Repository
        single<ReminderRepository> { ReminderRepositoryImpl(get(), get()) }
        single<ParrotRepository> { ParrotRepositoryImpl(get()) }

        // Mapper
        single { ReminderMapper() }
        single { ParrotMapper() }

        // LocalDataSource
        single<ReminderLocalDataSource> { ReminderLocalDataSource(get(), get()) }
        single<ParrotLocalDataSource> { ParrotLocalDataSource(get(), get()) }

        // Common
        single<UuidGenerator> { UuidGenerator() }
    }
