package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.service.IOSNotificationService
import com.maropiyo.reminderparrot.data.service.IOSUserSettingsService
import com.maropiyo.reminderparrot.domain.service.NotificationService
import com.maropiyo.reminderparrot.domain.service.UserSettingsService
import com.maropiyo.reminderparrot.util.PlatformUtil
import org.koin.dsl.module

/**
 * iOS固有のKoinモジュール
 */
val platformModule = module {
    single<NotificationService> { IOSNotificationService() }
    single<UserSettingsService> { IOSUserSettingsService() }
    single<PlatformUtil> { PlatformUtil() }
}
