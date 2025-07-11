package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.maropiyo.reminderparrot.data.database.DatabaseInitializer
import com.maropiyo.reminderparrot.domain.usecase.RegisterPushNotificationTokenUseCase
import com.maropiyo.reminderparrot.domain.usecase.RequestNotificationPermissionUseCase
import com.maropiyo.reminderparrot.ui.navigation.BottomNavigation
import com.maropiyo.reminderparrot.ui.navigation.NavigationItem
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * メイン画面（ナビゲーションを含む）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // 現在選択されているナビゲーション項目を保持
    var currentRoute by remember { mutableStateOf(NavigationItem.Home.route) }

    // 通知権限要求
    val requestNotificationPermission = koinInject<RequestNotificationPermissionUseCase>()

    // プッシュ通知トークン登録
    val registerPushNotificationToken = koinInject<RegisterPushNotificationTokenUseCase>()

    // データベース初期化
    val databaseInitializer = koinInject<DatabaseInitializer>()

    // アプリ起動時にデータベース初期化と通知権限を要求
    LaunchedEffect(Unit) {
        launch {
            // データベース初期化を先に実行
            databaseInitializer.initialize()
            // その後通知権限を要求
            requestNotificationPermission()
            // プッシュ通知トークンを登録
            registerPushNotificationToken()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            NavigationItem.Home.route -> "こんにちは"
                            NavigationItem.RemindNet.route -> "リマインネット"
                            NavigationItem.Settings.route -> "せってい"
                            else -> ""
                        },
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            )
        },
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
                        .padding(paddingValues)
                )
            }
            NavigationItem.RemindNet.route -> {
                RemindNetScreen()
            }
            NavigationItem.Settings.route -> {
                SettingsScreen()
            }
        }
    }
}
