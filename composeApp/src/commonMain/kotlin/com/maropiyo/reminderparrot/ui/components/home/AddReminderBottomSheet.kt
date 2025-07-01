package com.maropiyo.reminderparrot.ui.components.home

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.DisableSecondary
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import org.jetbrains.compose.resources.painterResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_raising_hand

/**
 * リマインダー追加ボトムシート
 *
 * @param reminderText リマインダーのテキスト
 * @param onReminderTextChange リマインダーテキストが変更されたときのコールバック
 * @param onDismiss ボトムシートが閉じられたときのコールバック
 * @param onSaveReminder リマインダーが保存されたときのコールバック
 * @param sheetState ボトムシートの状態
 * @param memorizedWords インコが覚えられることばの数
 * @param currentReminderCount 現在のリマインダー数
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderBottomSheet(
    reminderText: String,
    onReminderTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSaveReminder: (Boolean) -> Unit,
    sheetState: androidx.compose.material3.SheetState,
    memorizedWords: Int,
    currentReminderCount: Int
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
                onSaveReminder = { onSaveReminder(true) },
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 104.dp),
                memorizedWords = memorizedWords,
                currentReminderCount = currentReminderCount
            )

            // リマインコの画像
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
    modifier: Modifier = Modifier,
    memorizedWords: Int,
    currentReminderCount: Int
) {
    // リマインダー数が上限に達しているかチェック
    val isReachedLimit = currentReminderCount >= memorizedWords
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
                text = if (isReachedLimit) "もうおぼえられないよ〜" else "よんだ？",
                color = Secondary,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.size(16.dp))

            // テキスト入力フィールド
            ReminderTextField(
                reminderText = reminderText,
                onValueChange = onReminderTextChange,
                enabled = !isReachedLimit,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.size(16.dp))

            // 送信ボタン
            SaveReminderButton(
                onClick = onSaveReminder,
                enabled = reminderText.isNotBlank() && !isReachedLimit,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            )
        }
    }
}

/**
 * リマインダーテキスト入力フィールド
 *
 * @param reminderText リマインダーテキスト
 * @param onValueChange テキストが変更されたときのコールバック
 * @param enabled 入力フィールドが有効かどうか
 * @param modifier 修飾子
 */
@Composable
private fun ReminderTextField(
    reminderText: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
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

    TextField(
        value = textFieldValue,
        onValueChange = { changedValue ->
            if (enabled) {
                textFieldValue = changedValue
                onValueChange(changedValue.text)
            }
        },
        modifier = modifier,
        enabled = enabled,
        colors =
        TextFieldDefaults.colors(
            focusedTextColor = Secondary,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            disabledTextColor = Secondary.copy(alpha = 0.3f),
            disabledContainerColor = White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        textStyle =
        MaterialTheme.typography.bodyLarge.copy(
            color = if (enabled) Secondary else Secondary.copy(alpha = 0.3f),
            fontWeight = FontWeight.Bold
        ),
        placeholder =
        {
            Text(
                text = if (enabled) "おしえることばをかいてね" else "レベルをあげてもっとかしこくなろう！",
                style = MaterialTheme.typography.bodyLarge,
                color = Secondary.copy(alpha = 0.5f)
            )
        },
        singleLine = true,
        shape = Shapes.large
    )
}

/**
 * リマインダー保存ボタン
 *
 * @param onClick ボタンがクリックされたときの処理
 * @param enabled ボタンが有効かどうか
 * @param modifier ボタンの修飾子
 */
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
