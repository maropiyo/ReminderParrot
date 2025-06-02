package com.maropiyo.reminderparrot.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.maropiyo.reminderparrot.ui.theme.ParrotYellow
import com.maropiyo.reminderparrot.ui.theme.Secondary

/**
 * ボトムナビゲーションバー
 *
 * @param selectedRoute 現在選択されているルート
 * @param onNavigate ナビゲーション項目が選択されたときのコールバック
 */
@Composable
fun BottomNavigation(selectedRoute: String, onNavigate: (NavigationItem) -> Unit) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationItem.getItems().forEach { item ->
            val isSelected = selectedRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item) },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Secondary else Color.Gray
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = if (isSelected) Secondary else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Secondary,
                    selectedTextColor = Secondary,
                    indicatorColor = ParrotYellow.copy(alpha = 0.1f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
