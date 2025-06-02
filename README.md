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

*テストは現在実装中です*

## 📄 ライセンス

*ライセンス情報は後日追加予定*