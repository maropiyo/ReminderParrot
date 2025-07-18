package com.maropiyo.reminderparrot.di

import com.maropiyo.reminderparrot.data.service.AndroidNotificationService
import com.maropiyo.reminderparrot.data.service.AndroidUserSettingsService
import com.maropiyo.reminderparrot.domain.service.NotificationService
import com.maropiyo.reminderparrot.domain.service.UserSettingsService
import com.maropiyo.reminderparrot.ui.components.common.ad.AdFactory
import com.maropiyo.reminderparrot.ui.components.common.ad.AndroidAdFactory
import com.maropiyo.reminderparrot.util.PlatformUtil
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android固有のKoinモジュール
 */
val platformModule =
    module {
        single<NotificationService> { AndroidNotificationService(androidContext()) }
        single<UserSettingsService> { AndroidUserSettingsService(androidContext()) }
        single<AdFactory> { AndroidAdFactory(get()) }
        single<PlatformUtil> { PlatformUtil() }
    }
