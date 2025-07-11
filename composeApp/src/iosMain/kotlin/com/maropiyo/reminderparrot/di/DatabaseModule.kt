package com.maropiyo.reminderparrot.di

import app.cash.sqldelight.db.SqlDriver
import com.maropiyo.reminderparrot.data.database.DatabaseDriverFactory
import com.maropiyo.reminderparrot.db.ReminderParrotDatabase
import org.koin.dsl.module

val databaseModule =
    module {

        single<SqlDriver> { DatabaseDriverFactory().createDriver() }

        single<ReminderParrotDatabase> { ReminderParrotDatabase(get()) }
    }
