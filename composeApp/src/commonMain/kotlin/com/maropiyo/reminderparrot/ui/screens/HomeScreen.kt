package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.presentation.state.ReminderListState
import com.maropiyo.reminderparrot.presentation.viewmodel.ReminderListViewModel
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
 * @param modifier 修飾子
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ReminderListViewModel = koinViewModel(), modifier: Modifier = Modifier) {
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

    Box(modifier = modifier) {
        // メインコンテンツの表示
        HomeContent(
            state = state,
            onToggleCompletion = viewModel::toggleReminderCompletion,
            modifier = Modifier.fillMaxSize()
        )

        // フローティングアクションボタン
        HomeFloatingActionButton(
            onClick = {
                reminderText = ""
                isShowBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
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
 * フローティングアクションボタン
 */
@Composable
private fun HomeFloatingActionButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = ParrotYellow,
        modifier = modifier
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
private fun HomeContent(state: ReminderListState, onToggleCompletion: (String) -> Unit, modifier: Modifier = Modifier) {
    when {
        state.isLoading -> {
            LoadingState(modifier = modifier)
        }
        state.error != null -> {
            ErrorState(state.error, modifier = modifier)
        }
        state.reminders.isEmpty() -> {
            EmptyState("リマインダーがありません", modifier = modifier)
        }
        else -> {
            ReminderList(
                reminders = state.reminders,
                onToggleCompletion = onToggleCompletion,
                modifier = modifier
            )
        }
    }
}
