package com.maropiyo.reminderparrot.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import org.jetbrains.compose.resources.painterResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_raising_hand

/**
 * アカウント作成ボトムシート
 *
 * @param onDismiss ボトムシートが閉じられたときのコールバック
 * @param onCreateAccount アカウント作成ボタンが押されたときのコールバック
 * @param sheetState ボトムシートの状態
 * @param errorMessage エラーメッセージ（nullの場合は表示しない）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountCreationBottomSheet(
    onDismiss: () -> Unit,
    onCreateAccount: () -> Unit,
    sheetState: androidx.compose.material3.SheetState,
    errorMessage: String? = null
) {
    ModalBottomSheet(
        dragHandle = null,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .imePadding()
        ) {
            AccountCreationCard(
                onCreateAccount = onCreateAccount,
                errorMessage = errorMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 104.dp)
            )

            // リマインコの画像
            Image(
                painter = painterResource(Res.drawable.reminko_raising_hand),
                contentDescription = "リマインコ",
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * アカウント作成カード
 */
@Composable
private fun AccountCreationCard(
    onCreateAccount: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Background
        ),
        shape = Shapes.extraLarge
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            // タイトルテキスト
            Text(
                text = "リマインネットをつかうよ！",
                color = Secondary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            // 説明テキスト
            Text(
                text = "リマインネットはインコどうしで\nことばをきょうゆうするきのうだよ！\n\nつかうまえにアカウントをつくろう",
                color = Secondary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))

            // アカウント作成ボタン
            CreateAccountButton(
                onClick = onCreateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            )

            // エラーメッセージ表示
            errorMessage?.let { error ->
                Spacer(Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * アカウント作成ボタン
 */
@Composable
private fun CreateAccountButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        shape = Shapes.large,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Secondary,
            contentColor = White
        )
    ) {
        Text(
            text = "つかってみる",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
