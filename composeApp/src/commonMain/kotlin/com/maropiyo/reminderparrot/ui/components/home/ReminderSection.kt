package com.maropiyo.reminderparrot.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.maropiyo.reminderparrot.ui.components.state.EmptyState
import com.maropiyo.reminderparrot.ui.components.state.ErrorState
import com.maropiyo.reminderparrot.ui.components.state.LoadingState
import com.maropiyo.reminderparrot.ui.theme.ParrotYellow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_face

/**
 * リマインダーセクション
 * リマインダー一覧の表示と操作を担当するコンポーネント
 *
 * @param state リマインダーリストの状態
 * @param onToggleCompletion リマインダー完了切り替えコールバック
 * @param onCreateReminder 新しいリマインダー作成コールバック
 * @param modifier 修飾子
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSection(
    state: ReminderListState,
    onToggleCompletion: (String) -> Unit,
    onCreateReminder: suspend (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
        ReminderContent(
            state = state,
            onToggleCompletion = onToggleCompletion,
            modifier = Modifier.fillMaxSize()
        )

        // フローティングアクションボタン
        ReminderFloatingActionButton(
            onClick = {
                reminderText = ""
                isShowBottomSheet = true
            },
            modifier =
                Modifier
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
                        onCreateReminder(reminderText)
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
 * リマインダー用フローティングアクションボタン
 */
@Composable
private fun ReminderFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
 * リマインダーコンテンツ
 */
@Composable
private fun ReminderContent(
    state: ReminderListState,
    onToggleCompletion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
