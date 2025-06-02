package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.presentation.state.HomeState
import com.maropiyo.reminderparrot.presentation.viewmodel.HomeViewModel
import com.maropiyo.reminderparrot.ui.components.reminder.AddReminderBottomSheet
import com.maropiyo.reminderparrot.ui.components.reminder.ReminderList
import com.maropiyo.reminderparrot.ui.components.state.EmptyState
import com.maropiyo.reminderparrot.ui.components.state.ErrorState
import com.maropiyo.reminderparrot.ui.components.state.LoadingState
import com.maropiyo.reminderparrot.ui.theme.ParrotYellow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_face

/**
 * ホーム画面
 * @param viewModel ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    // ViewModelの状態を取得
    val state by viewModel.state.collectAsState()

    // ボトムシートの表示状態
    var isShowBottomSheet by remember { mutableStateOf(false) }
    // リマインダーテキスト
    var reminderText by remember { mutableStateOf("") }
    // ボトムシートの状態
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // コルーチンスコープとキーボードコントローラーの取得
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = { HomeTopBar() },
        floatingActionButton = {
            HomeFloatingActionButton(onClick = { isShowBottomSheet = true })
        }
    ) { paddingValues ->
        // メインコンテンツの表示
        HomeContent(
            state = state,
            paddingValues = paddingValues
        )

        // リマインダー追加用ボトムシート
        if (isShowBottomSheet) {
            AddReminderBottomSheet(
                reminderText = reminderText,
                onReminderTextChange = { reminderText = it },
                onDismiss = {
                    keyboardController?.hide()
                    isShowBottomSheet = false
                    reminderText = ""
                },
                onSaveReminder = {
                    scope.launch {
                        viewModel.createReminder(reminderText)
                        isShowBottomSheet = false
                        reminderText = ""
                        sheetState.hide()
                    }
                },
                sheetState = sheetState
            )
        }
    }
}

/**
 * トップアプリバー
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "こんにちは",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    )
}

/**
 * フローティングアクションボタン
 */
@Composable
private fun HomeFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = ParrotYellow
    ) {
        Image(
            painter = painterResource(Res.drawable.reminko_face),
            contentDescription = "Parrot",
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * コンテンツ
 */
@Composable
private fun HomeContent(state: HomeState, paddingValues: PaddingValues) {
    when {
        state.isLoading -> {
            LoadingState(paddingValues)
        }
        state.error != null -> {
            ErrorState(state.error, paddingValues)
        }
        state.reminders.isEmpty() -> {
            EmptyState("リマインダーがありません", paddingValues)
        }
        else -> {
            ReminderList(
                reminders = state.reminders.reversed(),
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
