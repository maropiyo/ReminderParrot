# GitHub Actions ワークフロー修正ガイド

## 概要
無効化されていたBuildとTestワークフローを修正し、基本的なビルドエラーチェックができるように復旧します。

## 問題の原因
1. iOS ビルドがLinux環境で実行しようとしていた
2. 適切なランナー環境の分離ができていなかった
3. 警告メッセージが大量に出力されていた

## 修正内容

### build.yml の修正
- ✅ Android ビルド: ubuntu-latest で実行（成功確認済み）
- ✅ iOS フレームワークコンパイル: macOS-latest で実行
- ✅ 警告メッセージの抑制設定を追加
- ✅ テストも同時実行してより包括的なチェック
- ⚠️ フルiOSアプリビルドは設定ファイル依存のためコメントアウト

### test.yml の修正  
- ✅ Android・共通テスト: ubuntu-latest で実行
- ✅ iOS テスト: macOS-latest で実行
- ✅ 適切なテストレポートの生成とアップロード

## 適用方法

1. 現在の `.github/workflows/build.yml` を `workflow-fixes/build.yml` の内容で置き換える
2. 現在の `.github/workflows/test.yml` を `workflow-fixes/test.yml` の内容で置き換える

## 段階的な復旧アプローチ

### フェーズ1: 基本ビルドチェック（今回の修正）
- Androidビルドとテストの復旧
- iOSフレームワークコンパイルチェック
- 基本的なエラー検出

### フェーズ2: フルiOSビルド（将来の改善）
- iOS設定ファイル（Config.xcconfig）の適切な管理
- Xcodeビルドの設定
- 署名やプロビジョニングプロファイルの設定

## 確認済み動作

ローカル環境でのテスト結果:
- ✅ `./gradlew :composeApp:assembleDebug` - Android Debug ビルド成功
- ✅ `./gradlew :composeApp:testDebugUnitTest` - Android 単体テスト成功
- ✅ `./gradlew :composeApp:compileCommonMainKotlinMetadata` - 共通コード成功

## 注意事項

- iOS関連のビルドはmacOS環境が必要
- Gradleプロパティに `kotlin.native.ignoreDisabledTargets=true` を設定して警告を抑制
- フルiOSアプリビルドには追加の設定ファイルとシークレットが必要