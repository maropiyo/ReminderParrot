package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.database.DatabaseInitializer
import com.maropiyo.reminderparrot.data.datasource.local.ImportHistoryLocalDataSource
import com.maropiyo.reminderparrot.data.datasource.local.NotificationHistoryLocalDataSource
import com.maropiyo.reminderparrot.data.datasource.local.ParrotLocalDataSource
import com.maropiyo.reminderparrot.data.datasource.local.ReminderLocalDataSource
import com.maropiyo.reminderparrot.data.datasource.remote.RemindNetRemoteDataSource
import com.maropiyo.reminderparrot.data.mapper.ParrotMapper
import com.maropiyo.reminderparrot.data.mapper.ReminderMapper
import com.maropiyo.reminderparrot.data.repository.ParrotRepositoryImpl
import com.maropiyo.reminderparrot.data.repository.RemindNetRepositoryImpl
import com.maropiyo.reminderparrot.data.repository.ReminderRepositoryImpl
import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository
import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import com.maropiyo.reminderparrot.domain.usecase.AddParrotExperienceUseCase
import com.maropiyo.reminderparrot.domain.usecase.CancelForgetNotificationUseCase
import com.maropiyo.reminderparrot.domain.usecase.CreateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.DeleteExpiredRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.DeleteReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetParrotUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetRemindersUseCase
import com.maropiyo.reminderparrot.domain.usecase.GetUserSettingsUseCase
import com.maropiyo.reminderparrot.domain.usecase.ImportRemindNetPostUseCase
import com.maropiyo.reminderparrot.domain.usecase.RegisterPushNotificationTokenUseCase
import com.maropiyo.reminderparrot.domain.usecase.RequestNotificationPermissionUseCase
import com.maropiyo.reminderparrot.domain.usecase.SaveUserSettingsUseCase
import com.maropiyo.reminderparrot.domain.usecase.ScheduleForgetNotificationUseCase
import com.maropiyo.reminderparrot.domain.usecase.SendRemindNotificationUseCase
import com.maropiyo.reminderparrot.domain.usecase.SignInAnonymouslyUseCase
import com.maropiyo.reminderparrot.domain.usecase.UpdateReminderUseCase
import com.maropiyo.reminderparrot.domain.usecase.remindnet.CreateRemindNetPostUseCase
import com.maropiyo.reminderparrot.domain.usecase.remindnet.DeleteRemindNetPostUseCase
import com.maropiyo.reminderparrot.domain.usecase.remindnet.GetRemindNetPostsUseCase
import com.maropiyo.reminderparrot.presentation.viewmodel.ParrotViewModel
import com.maropiyo.reminderparrot.presentation.viewmodel.RemindNetViewModel
import com.maropiyo.reminderparrot.presentation.viewmodel.ReminderListViewModel
import com.maropiyo.reminderparrot.presentation.viewmodel.SettingsViewModel
import org.koin.dsl.module

/**
 * Koinモジュールをまとめて管理するオブジェクト
 */
val appModule =
    module {
        // ViewModel
        single<ReminderListViewModel> {
            ReminderListViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
        }
        single<ParrotViewModel> { ParrotViewModel(get()) }
        single<RemindNetViewModel> { RemindNetViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
        single<SettingsViewModel> { SettingsViewModel(get(), get(), get()) }

        // UseCase
        single<CreateReminderUseCase> { CreateReminderUseCase(get(), get(), get(), get(), get()) }
        single<GetRemindersUseCase> { GetRemindersUseCase(get()) }
        single<UpdateReminderUseCase> { UpdateReminderUseCase(get()) }
        single<DeleteReminderUseCase> { DeleteReminderUseCase(get()) }
        single<DeleteExpiredRemindersUseCase> { DeleteExpiredRemindersUseCase(get(), get(), get()) }
        single<GetParrotUseCase> { GetParrotUseCase(get()) }
        single<AddParrotExperienceUseCase> { AddParrotExperienceUseCase(get()) }
        single<ScheduleForgetNotificationUseCase> { ScheduleForgetNotificationUseCase(get()) }
        single<CancelForgetNotificationUseCase> { CancelForgetNotificationUseCase(get()) }
        single<RequestNotificationPermissionUseCase> { RequestNotificationPermissionUseCase(get()) }
        single<CreateRemindNetPostUseCase> { CreateRemindNetPostUseCase(get(), get()) }
        single<DeleteRemindNetPostUseCase> { DeleteRemindNetPostUseCase(get()) }
        single<GetRemindNetPostsUseCase> { GetRemindNetPostsUseCase(get()) }
        single<SendRemindNotificationUseCase> { SendRemindNotificationUseCase(get(), get(), get()) }
        single<RegisterPushNotificationTokenUseCase> {
            RegisterPushNotificationTokenUseCase(
                get(),
                get(),
                get(),
                get()
            )
        }
        single<GetUserSettingsUseCase> { GetUserSettingsUseCase(get()) }
        single<SaveUserSettingsUseCase> { SaveUserSettingsUseCase(get()) }
        single<SignInAnonymouslyUseCase> { SignInAnonymouslyUseCase(get()) }
        single<ImportRemindNetPostUseCase> {
            ImportRemindNetPostUseCase(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }

        // Repository
        single<ReminderRepository> { ReminderRepositoryImpl(get()) }
        single<ParrotRepository> { ParrotRepositoryImpl(get()) }
        single<RemindNetRepository> { RemindNetRepositoryImpl(get()) }

        // Mapper
        single { ReminderMapper() }
        single { ParrotMapper() }

        // LocalDataSource
        single<ReminderLocalDataSource> { ReminderLocalDataSource(get(), get()) }
        single<ParrotLocalDataSource> { ParrotLocalDataSource(get(), get()) }
        single<NotificationHistoryLocalDataSource> { NotificationHistoryLocalDataSource(get()) }
        single<ImportHistoryLocalDataSource> { ImportHistoryLocalDataSource(get(), get()) }

        // Database
        single<DatabaseInitializer> { DatabaseInitializer(get()) }

        // RemoteDataSource
        single<RemindNetRemoteDataSource> { RemindNetRemoteDataSource(get()) }

        // Common
        single<UuidGenerator> { UuidGenerator() }
    }
