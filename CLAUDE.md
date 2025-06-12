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

## 🧠 AI Assistant Guidelines

### Efficient Professional Workflow
**Smart Explore-Plan-Code-Commit with quality automation**

#### 1. EXPLORE Phase
- **Use tools to quickly scan and understand codebase structure**
- **Auto-identify dependencies and impact areas**
- **Present findings concisely with actionable insights**

#### 2. PLAN Phase
- **Generate multiple implementation approaches when applicable**
- **Auto-create test scenarios from requirements**
- **Predict potential issues using pattern analysis**
- **Provide time estimates for each approach**

#### 3. CODE Phase
- **Generate code following project conventions**
- **Auto-complete repetitive patterns**
- **Real-time error detection and fixes**
- **Follow Clean Architecture principles**

#### 4. COMMIT Phase
**Always run quality checks before completing tasks:**
```bash
# Kotlin quality checks
./gradlew ktlintCheck
./gradlew ktlintFormat
./gradlew :composeApp:lintDebug
```

## 🚫 Security and Quality Standards

### NEVER Rules (Non-negotiable)
- **NEVER: Delete production data without explicit confirmation**
- **NEVER: Hardcode API keys, passwords, or secrets**
- **NEVER: Commit code with failing tests or linting errors**
- **NEVER: Push directly to main/master branch**
- **NEVER: Skip security reviews for authentication/authorization code**

### YOU MUST Rules (Required Standards)
- **YOU MUST: Write tests for new features and bug fixes**
- **YOU MUST: Run quality checks before marking tasks complete**
- **YOU MUST: Follow semantic versioning for releases**
- **YOU MUST: Document breaking changes**
- **YOU MUST: Use feature branches for all development**
- **YOU MUST: Add comprehensive KDoc comments to public APIs**

## 📚 Development Standards

### Code Quality Requirements
- **Generate comprehensive KDoc documentation for every public function**
- **Add clear comments explaining business logic**
- **Follow Kotlin conventions and Clean Architecture patterns**
- **Auto-fix all linting/formatting issues before completion**

### KDoc Template (Kotlin)
```kotlin
/**
 * Brief description of what the function does
 *
 * Detailed explanation of the business logic and purpose
 * 
 * @param paramName Description of what this parameter represents
 * @return Description of what the function returns and why
 * @throws ExceptionType When this error occurs
 * @sample com.example.SampleClass.sampleUsage
 * @since 1.0.0
 */
fun functionName(paramName: ParamType): ReturnType {
    // Implementation
}
```

### Best Practices
- **Coroutines**: Use structured concurrency with proper scopes
- **Flow**: Prefer Flow over LiveData for reactive streams  
- **Dependency Injection**: Use Koin following project patterns
- **Error Handling**: Use sealed classes for state management
- **Testing**: Write unit tests using JUnit and MockK
- **Architecture**: Follow Clean Architecture with clear layer separation

## 🔧 Commit Standards

### Conventional Commits
```bash
# Format: <type>(<scope>): <subject>
git commit -m "feat(reminder): add notification scheduling"
git commit -m "fix(ui): handle null reminder state correctly"
git commit -m "docs(readme): update build instructions"
git commit -m "refactor(data): extract common repository logic"
```

### Common Scopes for This Project
- `reminder`: Reminder-related functionality
- `parrot`: Parrot/pet system features
- `ui`: UI components and screens
- `data`: Data layer and repositories
- `domain`: Business logic and use cases
- `di`: Dependency injection setup
- `config`: Configuration and setup