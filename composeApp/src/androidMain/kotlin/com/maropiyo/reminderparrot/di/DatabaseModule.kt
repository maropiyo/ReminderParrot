package com.maropiyo.reminderparrot.di

import app.cash.sqldelight.db.SqlDriver
import com.maropiyo.reminderparrot.data.local.db.DatabaseDriverFactory
import com.maropiyo.reminderparrot.db.ReminderParrotDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule =
    module {
        single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
        single<ReminderParrotDatabase> { ReminderParrotDatabase(get()) }
    }
