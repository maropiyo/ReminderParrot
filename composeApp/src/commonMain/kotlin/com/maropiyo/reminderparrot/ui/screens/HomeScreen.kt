package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.presentation.state.HomeState
import com.maropiyo.reminderparrot.presentation.viewmodel.HomeViewModel
import com.maropiyo.reminderparrot.ui.components.reminder.ReminderList
import com.maropiyo.reminderparrot.ui.components.state.EmptyState
import com.maropiyo.reminderparrot.ui.components.state.ErrorState
import com.maropiyo.reminderparrot.ui.components.state.LoadingState
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.DisableSecondary
import com.maropiyo.reminderparrot.ui.theme.ParrotYellow
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_face
import reminderparrot.composeapp.generated.resources.reminko_raising_hand

/**
 * ホーム画面
 * @param viewModel ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    // ViewModelの状態を取得
    val state by viewModel.state.collectAsState()

    // UIの状態
    var isShowBottomSheet by remember { mutableStateOf(false) }
    var reminderText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
            ReminderInputBottomSheet(
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
 * トップバー
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
 * メインコンテンツ
 */
@Composable
private fun HomeContent(state: HomeState, paddingValues: androidx.compose.foundation.layout.PaddingValues) {
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

/**
 * リマインダー追加用ボトムシート
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderInputBottomSheet(
    reminderText: String,
    onReminderTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSaveReminder: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    ModalBottomSheet(
        dragHandle = null,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .imePadding()
        ) {
            ReminderInputCard(
                reminderText = reminderText,
                onReminderTextChange = onReminderTextChange,
                onSaveReminder = onSaveReminder,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 104.dp)
            )

            // オーバーレイ画像
            Image(
                painter = painterResource(Res.drawable.reminko_raising_hand),
                contentDescription = "Parrot",
                modifier =
                Modifier
                    .size(128.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * リマインダー入力カード
 */
@Composable
private fun ReminderInputCard(
    reminderText: String,
    onReminderTextChange: (String) -> Unit,
    onSaveReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors =
        CardDefaults.cardColors(
            containerColor = Background
        ),
        shape = Shapes.extraLarge
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            // タイトル
            Text(
                text = "よんだ？",
                color = Secondary,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.size(16.dp))

            // テキスト入力フィールド
            ReminderTextField(
                value = reminderText,
                onValueChange = onReminderTextChange,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.size(16.dp))

            // 送信ボタン
            SaveReminderButton(
                onClick = onSaveReminder,
                enabled = reminderText.isNotBlank(),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            )
        }
    }
}

@Composable
private fun ReminderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.titleMedium,
        placeholder = {
            Text(
                text = "おしえることばをかいてね",
                style = MaterialTheme.typography.titleMedium,
                color = DisableSecondary
            )
        },
        modifier = modifier,
        colors =
        TextFieldDefaults.colors(
            focusedTextColor = Secondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = White,
            unfocusedContainerColor = White
        ),
        shape = Shapes.large,
        singleLine = true
    )
}

@Composable
private fun SaveReminderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        shape = Shapes.large,
        colors =
        ButtonDefaults.elevatedButtonColors(
            containerColor = Secondary,
            contentColor = White,
            disabledContainerColor = DisableSecondary,
            disabledContentColor = White
        ),
        enabled = enabled
    ) {
        Text(
            text = "おしえる",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
