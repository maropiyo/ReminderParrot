# CLAUDE.md

Claude Codeでの開発時のガイドライン

## プロジェクト概要

ReminderParrot(リマインコ) は Kotlin Multiplatform Mobile (KMM) アプリケーション。Android と iOS の両方で動作し、Jetpack Compose Multiplatform を使用してUIを構築。
ユーザーは3歳から8歳の子供向けを意識しており、シンプルかつ直感的なUIが理想。
なるべく漢字を利用しないように意識する。
インコに言葉を覚えさせるコンセプトに沿うこと。

## 基本コマンド

### コード品質
```bash
./gradlew ktlintCheck      # コードスタイルチェック
./gradlew ktlintFormat     # コードフォーマット
./gradlew :composeApp:lintDebug  # Android Lint
```

### ビルド
```bash
# Android
./gradlew :composeApp:assembleDebug          # Debug APK
./gradlew :composeApp:assembleRelease        # Release APK
./gradlew :composeApp:bundle                 # AAB (App Bundle)

# iOS
./gradlew :composeApp:compileKotlinIosX64            # iOS x64 framework
./gradlew :composeApp:compileKotlinIosArm64          # iOS ARM64 framework  
./gradlew :composeApp:compileKotlinIosSimulatorArm64 # iOS Simulator ARM64
```

### テスト実行
```bash
# 全プラットフォーム
./gradlew allTests                           # 全テスト実行
./gradlew cleanAllTests                      # テスト結果をクリーン

# Android専用
./gradlew :composeApp:testDebugUnitTest      # Debug単体テスト
./gradlew :composeApp:testReleaseUnitTest    # Release単体テスト
./gradlew :composeApp:test                   # 全variant単体テスト
./gradlew :composeApp:connectedDebugAndroidTest  # 実機/エミュレータテスト

# iOS専用
./gradlew :composeApp:iosSimulatorArm64Test  # iOS Simulatorテスト
./gradlew :composeApp:iosX64Test             # iOS x64テスト

# 共通テスト
./gradlew :composeApp:testDebugUnitTest :composeApp:testReleaseUnitTest
```

### デバッグ・開発
```bash
# インストール/アンインストール
./gradlew :composeApp:installDebug           # Debugビルドをインストール
./gradlew :composeApp:uninstallDebug         # Debugビルドをアンインストール

# クリーン
./gradlew clean                              # ビルドディレクトリ削除
./gradlew cleanAllTests                      # テスト結果削除

# 依存関係
./gradlew :composeApp:dependencies           # 依存関係ツリー表示
./gradlew :composeApp:resolveIdeDependencies # IDE用依存関係解決

# SQLDelight
./gradlew generateSqlDelightInterface        # DB インターフェース生成
./gradlew verifySqlDelightMigration         # マイグレーション検証
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
# Supabase設定（BuildConfigで利用）
SUPABASE_URL=your_supabase_url
SUPABASE_KEY=your_anon_key
```

### iOS  
`iosApp/Configuration/Config.xcconfig` を `Config.xcconfig.template` からコピーして設定:
```xcconfig
SUPABASE_URL=your_supabase_url
SUPABASE_ANON_KEY=your_anon_key
```

### Gradle設定
`gradle.properties`:
```properties
# メモリ設定（必要に応じて調整）
org.gradle.jvmargs=-Xmx2048M -Dfile.encoding=UTF-8
kotlin.daemon.jvmargs=-Xmx2048M
```

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

## CI/CD 設定

### GitHub Actions ワークフロー
- **Lint** (`lint.yml`): ktlint + Android Lint
- **Test** (`test.yml`): 共通テスト + iOS テスト + 全プラットフォームテスト
- **Build** (`build.yml`): Android APK + iOS framework ビルド

### ローカルでCI相当のチェック
```bash
# CI前の確認コマンド（推奨）
./gradlew ktlintCheck && \
./gradlew :composeApp:lintDebug && \
./gradlew allTests

# 問題を自動修正
./gradlew ktlintFormat
```

## トラブルシューティング

### ビルドエラー時
```bash
# キャッシュクリア
./gradlew clean
rm -rf ~/.gradle/caches/
rm -rf .gradle/

# 依存関係の再取得
./gradlew --refresh-dependencies
```

### テスト関連
```bash
# 特定のテストクラスを実行（Android）
./gradlew :composeApp:testDebugUnitTest --tests="com.maropiyo.reminderparrot.*TestClassName"

# テストレポート確認
open composeApp/build/reports/tests/testDebugUnitTest/index.html
```

### iOS ビルド問題
```bash
# Kotlin/Native キャッシュクリア
rm -rf ~/.konan

# iOS シミュレータ向けビルド
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```
