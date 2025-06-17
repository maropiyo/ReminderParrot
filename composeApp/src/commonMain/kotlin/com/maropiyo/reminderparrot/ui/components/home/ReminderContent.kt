package com.maropiyo.reminderparrot.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.presentation.state.ParrotState
import com.maropiyo.reminderparrot.presentation.state.ReminderListState
import com.maropiyo.reminderparrot.ui.components.state.EmptyState
import com.maropiyo.reminderparrot.ui.components.state.ErrorState
import com.maropiyo.reminderparrot.ui.components.state.LoadingState
import com.maropiyo.reminderparrot.ui.theme.ParrotYellow
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import com.maropiyo.reminderparrot.ui.util.TimeFormatUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_face

/**
 * リマインダーコンテンツ
 * ホーム画面内のリマインダー関連機能を管理するコンポーネント
 *
 * @param state リマインダーリストの状態
 * @param parrotState インコの状態
 * @param onToggleCompletion リマインダー完了切り替えコールバック
 * @param onCreateReminder 新しいリマインダー作成コールバック
 * @param onUpdateReminder リマインダー更新コールバック
 * @param onDeleteReminder リマインダー削除コールバック
 * @param modifier 修飾子
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderContent(
    state: ReminderListState,
    parrotState: ParrotState,
    onToggleCompletion: (String) -> Unit,
    onCreateReminder: suspend (String) -> Unit,
    onUpdateReminder: (String, String) -> Unit,
    onDeleteReminder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // ボトムシートの表示状態
    var isShowBottomSheet by remember { mutableStateOf(false) }
    // 編集ボトムシートの表示状態
    var isShowEditBottomSheet by remember { mutableStateOf(false) }
    // 編集中のリマインダー
    var editingReminder by remember { mutableStateOf<Reminder?>(null) }
    // リマインダーテキスト
    var reminderText by remember { mutableStateOf("") }
    // ボトムシートの状態
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // 編集ボトムシートの状態
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // コルーチンスコープとキーボードコントローラーの取得
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // リマインダーセクションタイトル
            Text(
                text = "おぼえていることば",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Secondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // リマインダーアイテムの表示
            ReminderItems(
                state = state,
                onToggleCompletion = onToggleCompletion,
                onReminderClick = { reminder ->
                    editingReminder = reminder
                    isShowEditBottomSheet = true
                },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
        }

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
                sheetState = sheetState,
                memorizedWords = parrotState.parrot.memorizedWords,
                currentReminderCount = state.reminders.size
            )
        }

        // リマインダー編集用ボトムシート
        if (isShowEditBottomSheet && editingReminder != null) {
            EditReminderBottomSheet(
                reminder = editingReminder!!,
                onDismiss = {
                    keyboardController?.hide()
                    isShowEditBottomSheet = false
                    editingReminder = null
                },
                onUpdateReminder = { newText ->
                    scope.launch {
                        onUpdateReminder(editingReminder!!.id, newText)
                        isShowEditBottomSheet = false
                        editingReminder = null
                        editSheetState.hide()
                    }
                },
                onDeleteReminder = {
                    scope.launch {
                        onDeleteReminder(editingReminder!!.id)
                        isShowEditBottomSheet = false
                        editingReminder = null
                        editSheetState.hide()
                    }
                },
                sheetState = editSheetState
            )
        }
    }
}

/**
 * リマインダー用フローティングアクションボタン
 */
@Composable
private fun ReminderFloatingActionButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
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
 * リマインダーアイテム一覧
 * リマインダーの状態に応じて適切なUIを表示
 */
@Composable
private fun ReminderItems(
    state: ReminderListState,
    onToggleCompletion: (String) -> Unit,
    onReminderClick: (Reminder) -> Unit,
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
            EmptyState("なにもおぼえていないよ", modifier = modifier)
        }
        else -> {
            LazyColumn(
                modifier =
                modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = state.reminders,
                    key = { it.id }
                ) { reminder ->
                    AnimatedReminderCard(
                        reminder = reminder,
                        onToggleCompletion = { onToggleCompletion(reminder.id) },
                        onCardClick = { onReminderClick(reminder) },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * アニメーション付きリマインダーカード
 * 完了時にフェードアウトしてから削除される
 */
@Composable
private fun AnimatedReminderCard(
    reminder: Reminder,
    onToggleCompletion: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300)),
        modifier = modifier
    ) {
        ReminderCard(
            reminder = reminder,
            onToggleCompletion = {
                if (!reminder.isCompleted) {
                    // 完了にする場合はアニメーション後に削除
                    scope.launch {
                        onToggleCompletion()
                        delay(100) // チェックマークアニメーションを少し見せる
                        isVisible = false
                        delay(300) // アニメーション完了まで待機
                    }
                } else {
                    // 未完了に戻す場合は即座に実行
                    onToggleCompletion()
                }
            },
            onCardClick = onCardClick
        )
    }
}

/**
 * リマインダーカード
 * 個々のリマインダーを表示するカードコンポーネント
 */
@Composable
private fun ReminderCard(
    reminder: Reminder,
    onToggleCompletion: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 残り時間と透過度を計算
    val timeUntilForget = TimeFormatUtil.formatTimeUntilForget(reminder.forgetAt)
    val alpha = TimeFormatUtil.calculateAlpha(reminder.forgetAt, reminder.createdAt)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors =
        CardDefaults.cardColors(
            containerColor = White.copy(alpha = alpha)
        ),
        shape = Shapes.extraLarge,
        elevation =
        CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // テキスト
                Text(
                    text = reminder.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Secondary,
                    textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.weight(1f)
                )

                // 丸いチェックボックス
                CircularCheckbox(
                    checked = reminder.isCompleted,
                    onCheckedChange = { onToggleCompletion() },
                    modifier = Modifier.size(32.dp)
                )
            }

            // 残り時間表示
            Text(
                text = timeUntilForget,
                style = MaterialTheme.typography.bodySmall,
                color = Secondary.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * 円形のチェックボックス
 */
@Composable
private fun CircularCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 0.9f else 0f,
        label = "checkmark_scale"
    )

    Box(
        modifier =
        modifier
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = Secondary,
                shape = CircleShape
            ).background(
                color = if (checked) Secondary else White,
                shape = CircleShape
            ).clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = White,
                modifier =
                Modifier
                    .size(20.dp)
                    .scale(scale)
            )
        }
    }
}
