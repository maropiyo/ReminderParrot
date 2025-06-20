package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.service.AndroidNotificationService
import com.maropiyo.reminderparrot.domain.service.NotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android固有のKoinモジュール
 */
val platformModule = module {
    single<NotificationService> { AndroidNotificationService(androidContext()) }
}
