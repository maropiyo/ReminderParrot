package com.maropiyo.reminderparrot.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.LightGray
import com.maropiyo.reminderparrot.ui.theme.Secondary
import org.jetbrains.compose.resources.painterResource

/**
 * ボトムナビゲーションバー
 *
 * @param selectedRoute 現在選択されているルート
 * @param onNavigate ナビゲーション項目が選択されたときのコールバック
 */
@Composable
fun BottomNavigation(
    selectedRoute: String,
    onNavigate: (NavigationItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationItem.getItems().forEach { item ->
            val isSelected = selectedRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item) },
                icon = {
                    Icon(
                        painter =
                            painterResource(
                                if (isSelected) item.selectedIcon else item.unselectedIcon
                            ),
                        contentDescription = item.label,
                        modifier = Modifier.size(40.dp).padding(2.dp)
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = Secondary,
                        selectedTextColor = Secondary,
                        indicatorColor = Background,
                        unselectedIconColor = LightGray,
                        unselectedTextColor = LightGray
                    ),
            )
        }
    }
}
