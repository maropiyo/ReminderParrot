# Reminder Parrot 🦜

リマインダー管理のためのクロスプラットフォームモバイルアプリケーション。Kotlin Multiplatform Mobile (KMM) と Jetpack Compose Multiplatform を使用して、Android と iOS の両方で動作します。

## 🎯 概要

Reminder Parrotは、シンプルで使いやすいリマインダー管理アプリです。かわいいインコのキャラクター「リマインコ」と一緒に、大切なことを忘れないようにしましょう。

## 🛠 技術スタック

- **言語**: Kotlin
- **フレームワーク**: Kotlin Multiplatform Mobile (KMM)
- **UI**: Jetpack Compose Multiplatform
- **アーキテクチャ**: Clean Architecture
- **DI**: Koin
- **データベース**: SQLDelight
- **バックエンド**: Supabase
- **非同期処理**: Kotlin Coroutines & Flow

## 📱 対応プラットフォーム

- Android (API 31+)
- iOS (iOS 14+)

## ✨ 機能

### 実装済み
- ✅ リマインダーの作成・表示・編集・削除
- ✅ オフラインストレージ
- ✅ 縦画面固定
- ✅ プッシュ通知（忘却時）
- ✅ 自動忘却機能

### 開発予定
- 🚧 クラウド同期
- 🚧 カスタム通知設定

## 🚀 セットアップ

### 前提条件
- JDK 11以上
- Android Studio (KMMプラグイン推奨)
- Xcode (iOS開発用)

### 環境設定

#### Android
`local.properties` ファイルを作成：
```properties
supabase.url=your_supabase_url
supabase.key=your_anon_key
```

#### iOS
`iosApp/Configuration/Config.xcconfig.template` をコピーして `Config.xcconfig` を作成し、必要な値を設定

## 🔨 ビルド & 実行

### コマンド
```bash
# コード品質チェック
./gradlew ktlintCheck
./gradlew ktlintFormat

# ビルド
./gradlew :composeApp:assembleDebug                 # Android
./gradlew :composeApp:compileKotlinIosX64          # iOS

# テスト実行
./gradlew allTests
```

### 実行方法
- **Android**: Android Studioで `composeApp` 設定を選択して実行
- **iOS**: `iosApp/iosApp.xcodeproj` をXcodeで開いて実行

## 🏛 アーキテクチャ

Clean Architectureに基づく3層構造：

- **Domain層**: ビジネスロジック（Entity、UseCase、Repository Interface）
- **Data層**: データ操作（Repository実装、Local/Remote DataSource）
- **Presentation層**: UI管理（ViewModel、UIState、Compose UI）

## 🧪 テスト

KMP環境での手動テストダブルを使用したテスト戦略を採用：

### 実装済みテスト
- ✅ **Domain層**: UseCase（Create/Get/Update）
- ✅ **Data層**: Repository実装  
- ✅ **Presentation層**: ViewModel

### テストライブラリ
- `kotlin.test` - KMP公式テストライブラリ
- `kotlinx.coroutines.test` - 非同期処理テスト
- `koin.test` - 依存性注入テスト
- **手動テストダブル** - KMP互換のMock実装

## 🚨 CI/CD

GitHub Actionsで3段階の品質チェックを実装：

1. **Lint**: KtLint + Android Lint
2. **Tests**: 全プラットフォームテスト実行
3. **Build**: Android APK + iOS Framework生成

## 📄 ライセンス

*ライセンス情報は後日追加予定*
