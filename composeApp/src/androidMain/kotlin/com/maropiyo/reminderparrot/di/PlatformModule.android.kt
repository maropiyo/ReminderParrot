package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.service.AndroidNotificationService
import com.maropiyo.reminderparrot.data.service.AndroidUserIdService
import com.maropiyo.reminderparrot.data.service.AndroidUserSettingsService
import com.maropiyo.reminderparrot.domain.service.NotificationService
import com.maropiyo.reminderparrot.domain.service.UserIdService
import com.maropiyo.reminderparrot.domain.service.UserSettingsService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android固有のKoinモジュール
 */
val platformModule = module {
    single<NotificationService> { AndroidNotificationService(androidContext()) }
    single<UserIdService> { AndroidUserIdService(androidContext(), get()) }
    single<UserSettingsService> { AndroidUserSettingsService(androidContext()) }
}
