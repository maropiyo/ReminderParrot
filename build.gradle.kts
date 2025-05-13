plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    // ktlintを適用
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    // ktlintの設定
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        // デバッグモードをオフに設定（詳細なデバッグ情報を出力しない）
        debug.set(false)
        // 詳細な出力を有効化（実行時により多くの情報を表示）
        verbose.set(true)
        // Androidプロジェクト向けのルールを適用（Androidの命名規則などを考慮）
        android.set(true)
        // 結果をコンソールに出力（CI環境でも結果が見やすくなる）
        outputToConsole.set(true)
        // エラー出力の色を赤に設定（視認性向上のため）
        outputColorName.set("RED")
        // スタイル違反時にビルドを失敗させる（厳格なコード品質を維持するため）
        ignoreFailures.set(false)
        // 実験的なスタイルルールも適用（最新の推奨事項も取り入れる）
        enableExperimentalRules.set(true)

        filter {
            // ビルド生成物ディレクトリは除外
            exclude { element -> element.file.path.contains("build/") }
            // 自動生成されたコードを除外
            exclude { element -> element.file.path.contains("generated/") }
        }
    }
}
