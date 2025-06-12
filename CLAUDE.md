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

### 🇯🇵 日本語コミュニケーション
- **会話**: 常に日本語で対話する
- **コメント**: コードコメントは日本語で記述
- **コミットメッセージ**: 日本語でコミットメッセージを作成
- **プルリクエスト**: タイトルと説明は日本語で記述
- **ドキュメント**: README、CHANGELOG等も日本語で作成
- **例外**: 技術的なキーワードや固有名詞は英語のまま使用可能

### Efficient Professional Workflow
**Smart Explore-Plan-Code-Test-Commit with quality automation**

#### 1. EXPLORE Phase
- **Use tools to quickly scan and understand codebase structure**
- **Auto-identify dependencies and impact areas**
- **Present findings concisely with actionable insights**
- **Leverage search tools in parallel for comprehensive exploration**
- **Use Task tool for complex multi-file investigations**

#### 2. PLAN Phase
- **Generate multiple implementation approaches when applicable**
- **Auto-create test scenarios from requirements**
- **Predict potential issues using pattern analysis**
- **Provide time estimates for each approach**
- **Use `think` mode for complex architectural decisions**
- **Create comprehensive todo lists for multi-step tasks**

#### 3. TEST Phase (TDD Workflow)
- **Write tests BEFORE implementation**
- **Generate test cases covering edge cases and happy paths**
- **Use MockK for mocking dependencies**
- **Ensure platform-specific test coverage**

#### 4. CODE Phase
- **Generate code following project conventions**
- **Follow existing patterns for consistency**
- **Real-time error detection and fixes**
- **Follow Clean Architecture principles**
- **Reference existing implementations for patterns**
- **Implement iteratively to pass all tests**

#### 5. COMMIT Phase
**Always run quality checks before completing tasks:**
```bash
# Kotlin quality checks
./gradlew ktlintCheck
./gradlew ktlintFormat
./gradlew :composeApp:lintDebug

# Run tests
./gradlew test
./gradlew :composeApp:testDebugUnitTest
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
 * @throws IllegalArgumentException When invalid parameters are provided
 * @sample com.example.SampleClass.sampleUsage
 * @since 1.0.0
 */
fun functionName(paramName: String): String {
    return paramName
}
```

### Best Practices
- **Coroutines**: Use structured concurrency with proper scopes
- **Flow**: Prefer Flow over LiveData for reactive streams  
- **Dependency Injection**: Use Koin following project patterns
- **Error Handling**: Use sealed classes for state management
- **Testing**: Write unit tests using JUnit and MockK
- **Architecture**: Follow Clean Architecture with clear layer separation
- **Exploration**: Use multiple search tools in parallel for efficiency
- **Iteration**: Improve solutions through multiple Claude interactions
- **Specificity**: Provide exact requirements to reduce ambiguity

## 🧪 Test-Driven Development (TDD)

### TDD Workflow
1. **Write failing test first** - Define expected behavior
2. **Implement minimal code** - Make test pass
3. **Refactor** - Improve code while keeping tests green
4. **Repeat** - Continue for each feature

### Test Templates

#### Unit Test Template
```kotlin
class ReminderUseCaseTest {
    private val mockRepository = mockk<ReminderRepository>()
    private val useCase = CreateReminderUseCase(mockRepository)
    
    @Test
    fun `should create reminder with valid data`() = runTest {
        // Given
        val reminder = Reminder(title = "Test", dueDate = Clock.System.now())
        coEvery { mockRepository.createReminder(any()) } returns Result.success(reminder)
        
        // When
        val result = useCase.execute(reminder)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify { mockRepository.createReminder(reminder) }
    }
}
```

#### UI Test Template (Compose Multiplatform)
```kotlin
class ReminderScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `should display reminder list`() {
        // Given
        val reminders = listOf(
            Reminder(id = "1", title = "Test 1"),
            Reminder(id = "2", title = "Test 2")
        )
        
        // When
        composeTestRule.setContent {
            ReminderListScreen(reminders = reminders)
        }
        
        // Then
        composeTestRule.onNodeWithText("Test 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test 2").assertIsDisplayed()
    }
}
```

### Platform-Specific Testing
- **Android**: Use `androidTest` for instrumented tests
- **iOS**: Create XCTest cases in Xcode
- **Common**: Use `commonTest` for shared logic tests

## 🛠️ Code Patterns and Examples

Instead of templates, follow existing patterns in the codebase:

### Following Existing Patterns
- **Use Cases**: Look at `GetRemindersUseCase` or `CreateReminderUseCase` for patterns
- **ViewModels**: Follow `ReminderListViewModel` for state management patterns  
- **Repositories**: Check `ReminderRepositoryImpl` for data layer patterns
- **UI Components**: Examine existing screens in `presentation/` for Compose patterns

### Pattern Discovery Commands
```bash
# Find all use cases
find . -name "*UseCase.kt" -type f

# Find all ViewModels  
find . -name "*ViewModel.kt" -type f

# Find all repository implementations
find . -name "*RepositoryImpl.kt" -type f

# Find UI state classes
find . -name "*UiState.kt" -type f
```

### Architecture Guidelines
1. **Domain Layer**: Pure business logic, no platform dependencies
2. **Data Layer**: Repository pattern with local/remote data sources
3. **Presentation Layer**: StateFlow for state management, Compose for UI
4. **Dependency Injection**: Use Koin modules following existing patterns

## 🎨 UI/UX Development Guidelines

### Compose UI Workflow
1. **Design Review** - Analyze mockups/screenshots
2. **Component Planning** - Identify reusable components
3. **State Management** - Define UI states
4. **Implementation** - Build with Compose Multiplatform
5. **Platform Testing** - Verify on Android & iOS

### UI Component Patterns
- **Existing Screens**: Follow patterns in `presentation/screens/`
- **Components**: Check `presentation/components/` for reusable elements
- **State Management**: Use existing `*UiState` and `*ViewModel` patterns
- **Navigation**: Follow `presentation/navigation/` patterns

### UI Development Commands
```bash
# Find existing UI components
find . -path "*/presentation/components/*.kt" -type f

# Find existing screens
find . -path "*/presentation/screens/*.kt" -type f

# Find UI state classes
find . -name "*UiState.kt" -type f

# Find ViewModels
find . -name "*ViewModel.kt" -type f
```

## 🚀 Performance Optimization

### Key Strategies
1. **Lazy Loading** - Use LazyColumn/LazyRow for lists
2. **State Hoisting** - Minimize recompositions
3. **Remember** - Cache expensive computations
4. **Coroutine Scopes** - Proper lifecycle management
5. **Image Loading** - Use Coil for async image loading

## 🔧 コミット標準

### 日本語コンベンショナルコミット
```bash
# フォーマット: <type>(<scope>): <subject>
git commit -m "feat(reminder): 通知スケジュール機能を追加"
git commit -m "fix(ui): リマインダーのnull状態を適切に処理"
git commit -m "docs(readme): ビルド手順を更新"
git commit -m "refactor(data): 共通リポジトリロジックを抽出"
```

### プロジェクト固有のスコープ
- `reminder`: リマインダー関連機能
- `parrot`: パロット/ペットシステム機能
- `ui`: UIコンポーネントと画面
- `data`: データレイヤーとリポジトリ
- `domain`: ビジネスロジックとユースケース
- `di`: 依存性注入セットアップ
- `config`: 設定と構成

### コミットメッセージガイドライン
- **件名**: 日本語で簡潔に（50文字以内推奨）
- **本文**: 必要に応じて詳細を日本語で説明
- **理由**: 「何を」ではなく「なぜ」を重視
- **技術用語**: 英語のまま使用可能（StateFlow、ViewModel等）

## 🔍 KMM-Specific Exploration Techniques

### Efficient Codebase Exploration
```bash
# Find platform-specific implementations
- Search pattern: "actual" for platform implementations
- Search pattern: "expect" for shared interfaces
- Use Glob: "**/*.android.kt" or "**/*.ios.kt"

# Locate shared/platform code
- commonMain: Shared business logic
- androidMain: Android-specific code
- iosMain: iOS-specific code
```

### Complex Problem Solving with Think Mode
Use `think` mode for:
1. **Architecture Decisions** - Choosing between patterns
2. **Platform Differences** - Handling iOS/Android specifics
3. **Performance Issues** - Analyzing bottlenecks
4. **State Management** - Complex UI state flows
5. **Migration Planning** - Upgrading dependencies

### Parallel Tool Usage
```
# Example: Comprehensive feature exploration
1. Glob("**/*Reminder*.kt") - Find all reminder files
2. Grep("ReminderRepository") - Find repository usage
3. Read specific files identified
4. Use Task for complex multi-file analysis
```

## 📦 Dependency Management

### Adding New Dependencies
1. Check compatibility with KMM
2. Add to appropriate source sets
3. Verify platform-specific requirements
4. Update both `build.gradle.kts` files

### Common KMM Dependencies
```kotlin
// commonMain dependencies
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:version")
implementation("io.insert-koin:koin-core:version")

// androidMain dependencies
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:version")

// iosMain dependencies
// Platform-specific implementations
```

## 🆘 Troubleshooting Guide

### Common KMM Issues
1. **Class not found on iOS**
   - Check if class is in commonMain
   - Verify iOS framework generation
   
2. **Coroutine crashes on iOS**
   - Ensure proper dispatcher usage
   - Check for frozen object issues
   
3. **SQLDelight schema issues**
   - Run `./gradlew generateSqlDelightInterface`
   - Check schema version migrations

### Debug Commands
```bash
# Clean build
./gradlew clean

# Check dependencies
./gradlew dependencies

# iOS framework issues
./gradlew linkDebugFrameworkIosX64
```