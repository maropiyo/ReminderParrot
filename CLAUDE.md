# CLAUDE.md

Claude Codeでの開発時のガイドライン

## プロジェクト概要

ReminderParrot は Kotlin Multiplatform Mobile (KMM) アプリケーション。Android と iOS の両方で動作し、Jetpack Compose Multiplatform を使用してUIを構築。

## 基本コマンド

### コード品質
```bash
./gradlew ktlintCheck      # コードスタイルチェック
./gradlew ktlintFormat     # コードフォーマット
./gradlew :composeApp:lintDebug  # Android Lint
```

### ビルド・テスト
```bash
./gradlew :composeApp:assembleDebug          # Android APK
./gradlew :composeApp:compileKotlinIosX64    # iOS framework
./gradlew allTests                           # 全テスト実行
```

## アーキテクチャ

Clean Architecture の3層構造：
- **Domain層**: Entity、UseCase、Repository Interface
- **Data層**: Repository実装、Local/Remote DataSource
- **Presentation層**: ViewModel、UIState、Compose UI

## 🧠 AI開発ガイドライン

### 🔄 Git ワークフロー
- **mainブランチに直接コミット禁止**
- **すべての変更はfeatureブランチ経由**
- **Pull Requestで変更管理**
- ブランチ命名: `feature/description-of-change`

### 🇯🇵 日本語コミュニケーション
- **会話**: 常に日本語
- **コミットメッセージ**: 日本語（技術用語は英語可）
- **コメント**: 日本語でコード説明
- **ドキュメント**: 日本語で作成

### 品質保証ルール

#### 必須実行項目
```bash
# タスク完了前に必ず実行
./gradlew ktlintCheck && ./gradlew ktlintFormat
./gradlew allTests
```

#### 絶対禁止事項
- ❌ mainブランチへの直接Push
- ❌ テスト失敗状態でのコミット
- ❌ API キーやパスワードのハードコード
- ❌ プロダクションデータの無確認削除

#### 必須事項
- ✅ 新機能・バグ修正時のテスト作成
- ✅ 品質チェック完了後のタスク完了
- ✅ featureブランチでの開発
- ✅ public APIのKDoc追加

### 開発標準

#### テスト戦略
- **TDD**: テスト先行開発
- **手動テストダブル**: KMP互換のためMockKは使用しない
- **kotlin.test**: 公式テストライブラリ使用

#### コード品質
- **KDoc**: public関数に包括的なドキュメント
- **Clean Architecture**: 層の分離を厳守
- **既存パターン**: コードベースの既存実装を参考

## 🔧 コミット規約

### メッセージ形式
```bash
<type>(<scope>): <subject>

# 例
feat(reminder): 通知機能を追加
fix(ui): null状態の適切な処理
docs: README更新
```

### スコープ
- `reminder`: リマインダー機能
- `ui`: UIコンポーネント
- `data`: データ層
- `domain`: ビジネスロジック
- `config`: 設定・構成

## 環境設定

### Android
`local.properties`:
```properties
supabase.url=your_supabase_url
supabase.key=your_anon_key
```

### iOS  
`iosApp/Configuration/Config.xcconfig` を `Config.xcconfig.template` からコピーして設定

## KMP開発のポイント

### プラットフォーム固有実装
- `commonMain`: 共通ロジック
- `androidMain`: Android固有実装
- `iosMain`: iOS固有実装

### 主要依存関係
- **DI**: Koin
- **DB**: SQLDelight
- **Backend**: Supabase + Ktor
- **非同期**: Coroutines + Flow

### パターン探索
```bash
# 既存パターンを参考にする
find . -name "*UseCase.kt"      # UseCase実装
find . -name "*ViewModel.kt"    # ViewModel実装
find . -name "*Repository*.kt"  # Repository実装
```