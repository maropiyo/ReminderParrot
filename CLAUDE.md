# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ReminderParrot is a Kotlin Multiplatform Mobile (KMM) application for managing reminders, supporting both Android and iOS platforms with a shared codebase using Jetpack Compose Multiplatform.

## Commands

### Code Quality
- **Lint check**: `./gradlew ktlintCheck`
- **Format code**: `./gradlew ktlintFormat`
- **Android lint**: `./gradlew :composeApp:lintDebug`

### Build
- **Android debug APK**: `./gradlew :composeApp:assembleDebug`
- **iOS framework**: `./gradlew :composeApp:compileKotlinIosX64`
- **iOS app**: Open `iosApp/iosApp.xcodeproj` in Xcode and build

## Architecture

The project follows Clean Architecture with three distinct layers:

### Presentation Layer (`presentation/`)
- **ViewModels**: Handle UI logic and state management using StateFlow
- **UI States**: Data classes representing screen states
- **Compose UI**: Shared UI components using Compose Multiplatform

### Domain Layer (`domain/`)
- **Entities**: Core business models (e.g., `Reminder`)
- **Use Cases**: Business logic operations (`CreateReminderUseCase`, `GetRemindersUseCase`)
- **Repository Interfaces**: Contracts for data operations

### Data Layer (`data/`)
- **Repository Implementations**: Concrete implementations combining local and remote sources
- **Local Storage**: SQLDelight for offline database with platform-specific drivers
- **Remote Storage**: Supabase integration for cloud sync
- **DTOs and Mappers**: Data transfer objects and conversion logic

## Platform-Specific Code

- **commonMain**: Shared code for all platforms
- **androidMain**: Android-specific implementations (e.g., `DatabaseDriverFactory.android.kt`)
- **iosMain**: iOS-specific implementations (e.g., `DatabaseDriverFactory.ios.kt`)

## Key Dependencies

- **DI**: Koin for dependency injection across platforms
- **Database**: SQLDelight with schema in `sqldelight/` directory
- **Backend**: Supabase with Ktor client
- **Async**: Kotlin Coroutines and Flow

## Environment Setup

### Android
Supabase credentials are loaded from `local.properties`:
```
supabase.url=your_supabase_url
supabase.key=your_anon_key
```

### iOS
Create `iosApp/Configuration/Config.xcconfig` from template with:
- TEAM_ID
- BUNDLE_ID
- APP_NAME
- SUPABASE_URL
- SUPABASE_KEY