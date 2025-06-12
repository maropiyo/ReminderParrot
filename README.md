# Reminder Parrot 🦜

リマインダー管理のためのクロスプラットフォームモバイルアプリケーション。Kotlin Multiplatform Mobile (KMM) と Jetpack Compose Multiplatform を使用して、Android と iOS の両方で動作します。

## 🎯 概要

Reminder Parrotは、シンプルで使いやすいリマインダー管理アプリです。かわいいインコのキャラクター「リマインコ」と一緒に、大切なことを忘れないようにしましょう。

## 🛠 技術スタック

- **言語**: Kotlin
- **フレームワーク**: Kotlin Multiplatform Mobile (KMM)
- **UI**: Jetpack Compose Multiplatform
- **アーキテクチャ**: Clean Architecture + MVVM
- **DI**: Koin
- **ローカルDB**: SQLDelight
- **バックエンド**: Supabase（実装済み、未接続）
- **非同期処理**: Kotlin Coroutines & Flow
- **ビルドツール**: Gradle with Version Catalogs

## 📱 対応プラットフォーム

- Android (API 31+)
- iOS

## 🏗 プロジェクト構造

```
composeApp/
├── src/
│   ├── commonMain/          # 共通コード
│   │   ├── kotlin/
│   │   │   ├── domain/      # ビジネスロジック層
│   │   │   │   ├── entity/  # エンティティ
│   │   │   │   ├── usecase/ # ユースケース
│   │   │   │   └── repository/ # リポジトリインターフェース
│   │   │   ├── data/        # データ層
│   │   │   │   ├── local/   # ローカルデータソース
│   │   │   │   ├── remote/  # リモートデータソース
│   │   │   │   └── repository/ # リポジトリ実装
│   │   │   ├── presentation/ # プレゼンテーション層
│   │   │   │   ├── viewmodel/ # ViewModels
│   │   │   │   └── state/    # UI状態
│   │   │   └── ui/          # UIコンポーネント
│   │   │       ├── screens/  # 画面
│   │   │       ├── components/ # 再利用可能なコンポーネント
│   │   │       └── theme/    # テーマ定義
│   │   └── sqldelight/      # SQLDelightスキーマ
│   ├── androidMain/         # Android固有の実装
│   └── iosMain/            # iOS固有の実装
```

## 🚀 セットアップ

### 前提条件

- JDK 11以上
- Android Studio (KMMプラグイン推奨)
- Xcode (iOS開発用)
- Kotlin Multiplatform Mobile プラグイン

### 環境設定

#### Android
1. `local.properties` ファイルを作成し、以下を追加：
```properties
supabase.url=your_supabase_url
supabase.key=your_anon_key
```

#### iOS
1. `iosApp/Configuration/Config.xcconfig.template` をコピーして `Config.xcconfig` を作成
2. 以下の値を設定：
   - `TEAM_ID`
   - `BUNDLE_ID`
   - `APP_NAME`
   - `SUPABASE_URL`
   - `SUPABASE_KEY`

## 🔨 ビルド & 実行

### コード品質チェック

```bash
# Ktlintでコードスタイルをチェック
./gradlew ktlintCheck

# コードスタイルを自動修正
./gradlew ktlintFormat

# Android Lintを実行
./gradlew :composeApp:lintDebug
```

### Android

```bash
# デバッグビルド
./gradlew :composeApp:assembleDebug

# Android Studioから実行
# 1. プロジェクトを開く
# 2. "composeApp" 設定を選択
# 3. 実行ボタンをクリック
```

### iOS

```bash
# iOSフレームワークをビルド
./gradlew :composeApp:compileKotlinIosX64

# Xcodeから実行
# 1. iosApp/iosApp.xcodeproj を開く
# 2. ターゲットデバイスを選択
# 3. 実行ボタンをクリック
```

## ✨ 機能

- ✅ リマインダーの作成
- ✅ リマインダー一覧の表示
- ✅ オフラインストレージ（SQLDelight）
- 🚧 クラウド同期（Supabase）- 実装済み、未接続
- 🚧 リマインダーの編集・削除
- 🚧 通知機能

## 🏛 アーキテクチャ

このプロジェクトはClean Architectureの原則に従っています：

### Domain層
- **Entities**: ビジネスモデル（`Reminder`）
- **Use Cases**: ビジネスロジック（`CreateReminderUseCase`, `GetRemindersUseCase`）
- **Repository Interfaces**: データ操作の抽象化

### Data層
- **Repository Implementations**: 具体的なデータ操作の実装
- **Data Sources**: ローカル（SQLDelight）とリモート（Supabase）のデータソース
- **DTOs & Mappers**: データ変換ロジック

### Presentation層
- **ViewModels**: UI状態管理とビジネスロジックの橋渡し
- **UI States**: 画面の状態を表現
- **Compose UI**: 宣言的UIコンポーネント

## 🧪 テスト

### テスト戦略
Kotlin Multiplatform環境に最適化されたテスト戦略を採用しています：

#### 実装済みテスト
- **commonTest**: プラットフォーム非依存テスト ✅
  - `kotlin.test` - KMP公式テストライブラリ
  - `kotlinx.coroutines.test` - 非同期処理テスト
  - `koin.test` - 依存性注入テスト
  - **手動テストダブル** - MockKの代替（KMP互換）

#### テストカバレッジ（現在）
- ✅ **Domain層**: UseCase（Create/Get/Update）
- ✅ **Data層**: Repository実装
- ✅ **Presentation層**: ViewModel
- 📝 **Android固有テスト**: 未実装
- 📝 **UI層**: Compose UIテスト未実装

#### テスト実行

```bash
# 全プラットフォームテスト実行
./gradlew allTests

# commonTestのみ実行
./gradlew :composeApp:cleanAllTests :composeApp:allTests
```

#### 手動テストダブルの採用理由
MockKはKotlin/Native（iOS）をサポートしていないため、KMP公式推奨の手動テストダブルを採用：
- 全プラットフォームで安定したテスト実行
- プラットフォーム非依存の原則に準拠
- 将来的なプラットフォーム拡張への対応

#### 今後の実装予定
- Android固有テスト（`androidUnitTest`）
- Compose UIテスト（`androidInstrumentedTest`）
- iOS固有テスト（`iosTest`）

## 🚨 CI/CD

### GitHub Actions ワークフロー

このプロジェクトでは、品質保証のために3つのワークフローを使用：

#### 🔍 Lint（コード品質チェック）
```yaml
# .github/workflows/lint.yml
```
- **KtLint**: Kotlinコードスタイルチェック
- **Android Lint**: Android固有の問題検出
- **実行タイミング**: push/PR時に自動実行

#### 🧪 Tests（テスト実行）
```yaml
# .github/workflows/test.yml  
```
- **共通テスト**: Ubuntu環境でcommonTest実行
- **iOSテスト**: macOS環境でKotlin/Nativeテスト実行
- **全プラットフォーム**: Android + iOS同時テスト実行

#### 🏗️ Build（ビルド確認）
```yaml
# .github/workflows/build.yml
```
- **Androidビルド**: APK生成確認
- **iOSビルド**: フレームワーク + Xcodeビルド確認

### ワークフロー実行順序
1. **Lint** → コード品質チェック
2. **Tests** → 機能テスト実行  
3. **Build** → 最終ビルド確認

## 📄 ライセンス

*ライセンス情報は後日追加予定*