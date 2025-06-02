package com.maropiyo.reminderparrot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.graphics.vector.ImageVector

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
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    /**
     * ホーム画面
     */
    data object Home : NavigationItem(
        route = "home",
        label = "ホーム",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    /**
     * リマインネット画面
     */
    data object RemindNet : NavigationItem(
        route = "remind_net",
        label = "リマインネット",
        selectedIcon = Icons.Filled.Share,
        unselectedIcon = Icons.Outlined.Share
    )

    companion object {
        /**
         * すべてのナビゲーション項目を取得
         */
        fun getItems(): List<NavigationItem> = listOf(Home, RemindNet)
    }
}
