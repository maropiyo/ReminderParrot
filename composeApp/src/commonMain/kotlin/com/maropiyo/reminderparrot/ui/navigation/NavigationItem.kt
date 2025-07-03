package com.maropiyo.reminderparrot.ui.navigation

import org.jetbrains.compose.resources.DrawableResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.home
import reminderparrot.composeapp.generated.resources.network
import reminderparrot.composeapp.generated.resources.settings

/**
 * ボトムナビゲーションの項目を定義
 *
 * @property route ナビゲーションのルート名
 * @property label 表示ラベル
 * @property selectedIcon 選択時のアイコン
 * @property unselectedIcon 非選択時のアイコン
 */
sealed class NavigationItem(
    val route: String,
    val label: String,
    val selectedIcon: DrawableResource,
    val unselectedIcon: DrawableResource
) {
    /**
     * ホーム画面
     */
    data object Home : NavigationItem(
        route = "home",
        label = "ホーム",
        selectedIcon = Res.drawable.home,
        unselectedIcon = Res.drawable.home
    )

    /**
     * リマインネット画面
     */
    data object RemindNet : NavigationItem(
        route = "remind_net",
        label = "リマインネット",
        selectedIcon = Res.drawable.network,
        unselectedIcon = Res.drawable.network
    )

    /**
     * 設定画面
     */
    data object Settings : NavigationItem(
        route = "settings",
        label = "せってい",
        selectedIcon = Res.drawable.settings,
        unselectedIcon = Res.drawable.settings
    )

    companion object {
        /**
         * すべてのナビゲーション項目を取得
         */
        fun getItems(): List<NavigationItem> = listOf(Home, RemindNet, Settings)
    }
}
