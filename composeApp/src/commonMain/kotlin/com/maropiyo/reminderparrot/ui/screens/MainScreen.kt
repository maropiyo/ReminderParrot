package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.maropiyo.reminderparrot.ui.navigation.BottomNavigation
import com.maropiyo.reminderparrot.ui.navigation.NavigationItem

/**
 * メイン画面（ナビゲーションを含む）
 */
@Composable
fun MainScreen() {
    // 現在選択されているナビゲーション項目を保持
    var currentRoute by remember { mutableStateOf(NavigationItem.Home.route) }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                selectedRoute = currentRoute,
                onNavigate = { item ->
                    currentRoute = item.route
                }
            )
        }
    ) { paddingValues ->
        // 選択されたルートに応じて画面を表示
        when (currentRoute) {
            NavigationItem.Home.route -> {
                HomeScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                )
            }
            NavigationItem.RemindNet.route -> {
                RemindNetScreen()
            }
        }
    }
}
