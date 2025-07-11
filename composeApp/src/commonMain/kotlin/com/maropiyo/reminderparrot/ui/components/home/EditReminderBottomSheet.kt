package com.maropiyo.reminderparrot.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.DisableSecondary
import com.maropiyo.reminderparrot.ui.theme.Error
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import org.jetbrains.compose.resources.painterResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_raising_hand

/**
 * リマインダー編集ボトムシート
 *
 * @param reminder 編集対象のリマインダー
 * @param onDismiss ボトムシートが閉じられたときのコールバック
 * @param onUpdateReminder リマインダーが更新されたときのコールバック
 * @param onDeleteReminder リマインダーが削除されたときのコールバック
 * @param sheetState ボトムシートの状態
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderBottomSheet(
    reminder: Reminder,
    onDismiss: () -> Unit,
    onUpdateReminder: (String) -> Unit,
    onDeleteReminder: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    var reminderText by remember { mutableStateOf(reminder.text) }

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
            EditReminderInputCard(
                reminderText = reminderText,
                onReminderTextChange = { reminderText = it },
                onUpdateReminder = {
                    if (reminderText.isNotBlank() && reminderText != reminder.text) {
                        onUpdateReminder(reminderText)
                    }
                },
                onDeleteReminder = onDeleteReminder,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 104.dp),
                isUpdateEnabled = reminderText.isNotBlank() && reminderText != reminder.text
            )

            // インコの画像
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
 * リマインダー編集入力カード
 */
@Composable
private fun EditReminderInputCard(
    reminderText: String,
    onReminderTextChange: (String) -> Unit,
    onUpdateReminder: () -> Unit,
    onDeleteReminder: () -> Unit,
    modifier: Modifier = Modifier,
    isUpdateEnabled: Boolean
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
            // タイトルテキスト
            Text(
                text = "どうした？",
                color = Secondary,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.size(16.dp))

            // テキスト入力フィールド
            EditReminderTextField(
                reminderText = reminderText,
                onValueChange = onReminderTextChange,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.size(16.dp))

            // ボタンエリア
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // わすれるボタン
                DeleteReminderButton(
                    onClick = onDeleteReminder,
                    modifier =
                    Modifier
                        .weight(1f)
                        .height(50.dp)
                )

                Spacer(Modifier.width(8.dp))

                // 更新ボタン
                UpdateReminderButton(
                    onClick = onUpdateReminder,
                    enabled = isUpdateEnabled,
                    modifier =
                    Modifier
                        .weight(1f)
                        .height(50.dp)
                )
            }
        }
    }
}

/**
 * リマインダー編集テキスト入力フィールド
 *
 * @param reminderText リマインダーテキスト
 * @param onValueChange テキストが変更されたときのコールバック
 * @param modifier 修飾子
 */
@Composable
private fun EditReminderTextField(
    reminderText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 初期テキストとカーソル位置を保持するための状態
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = reminderText,
                selection = TextRange(reminderText.length)
            )
        )
    }

    // リマインダーテキストが外部から変更された場合に同期
    LaunchedEffect(reminderText) {
        if (textFieldValue.text != reminderText) {
            textFieldValue =
                TextFieldValue(
                    text = reminderText,
                    selection = TextRange(reminderText.length)
                )
        }
    }

    TextField(
        value = textFieldValue,
        onValueChange = { changedValue ->
            textFieldValue = changedValue
            onValueChange(changedValue.text)
        },
        modifier = modifier,
        colors =
        TextFieldDefaults.colors(
            focusedTextColor = Secondary,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle =
        MaterialTheme.typography.bodyLarge.copy(
            color = Secondary,
            fontWeight = FontWeight.Bold
        ),
        placeholder =
        {
            Text(
                text = "あたらしいことばをかいてね",
                style = MaterialTheme.typography.bodyLarge,
                color = Secondary.copy(alpha = 0.5f)
            )
        },
        singleLine = true,
        shape = Shapes.large
    )
}

/**
 * リマインダー更新ボタン
 *
 * @param onClick ボタンがクリックされたときの処理
 * @param enabled ボタンが有効かどうか
 * @param modifier ボタンの修飾子
 */
@Composable
private fun UpdateReminderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
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
            text = "おぼえなおす",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * リマインダー削除ボタン
 *
 * @param onClick ボタンがクリックされたときの処理
 * @param modifier ボタンの修飾子
 */
@Composable
private fun DeleteReminderButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        shape = Shapes.large,
        colors =
        ButtonDefaults.elevatedButtonColors(
            containerColor = Error,
            contentColor = White
        )
    ) {
        Text(
            text = "わすれる",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
