package com.maropiyo.reminderparrot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.ui.icons.CustomIcons
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.ParrotYellow
import com.maropiyo.reminderparrot.ui.theme.Primary
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
    // 段階管理のstate
    var currentStep by remember { mutableStateOf(1) }

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
            when (currentStep) {
                1 ->
                    ParticipationConfirmationCard(
                        onNext = { currentStep = 2 },
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 104.dp)
                    )
                2 ->
                    FunctionExplanationCard(
                        onCreateAccount = onCreateAccount,
                        errorMessage = errorMessage,
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 104.dp)
                    )
            }

            // インコの画像
            Image(
                painter = painterResource(Res.drawable.reminko_raising_hand),
                contentDescription = "インコ",
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
 * 第1段階：参加確認カード
 */
@Composable
private fun ParticipationConfirmationCard(onNext: () -> Unit, modifier: Modifier = Modifier) {
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
                text = "リマインネットに参加する？",
                color = Secondary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            // 説明テキスト
            Text(
                text = "リマインネットはインコたちが\nおぼえたことばを共有する場所だよ！",
                color = Secondary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))

            // つぎボタン
            NextButton(
                onClick = onNext,
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
 * 第2段階：機能説明カード
 */
@Composable
private fun FunctionExplanationCard(
    onCreateAccount: () -> Unit,
    errorMessage: String? = null,
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
            // タイトルテキスト
            Text(
                text = "つかいかた",
                color = Secondary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // 機能説明
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // リマインドボタンの説明
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier =
                        Modifier
                            .size(40.dp)
                            .background(
                                color = ParrotYellow,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "ほかのインコにリマインドを\nおくることができるよ",
                        color = Secondary.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }

                // おぼえるボタンの説明
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier =
                        Modifier
                            .size(40.dp)
                            .background(
                                color = White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // 点線の円を描画
                        Canvas(modifier = Modifier.size(40.dp)) {
                            val strokeWidth = 2.dp.toPx()
                            val radius = (size.minDimension - strokeWidth) / 2
                            val center = Offset(size.width / 2, size.height / 2)

                            drawCircle(
                                color = Primary,
                                radius = radius,
                                center = center,
                                style =
                                Stroke(
                                    width = strokeWidth,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                                )
                            )
                        }

                        // 下矢印アイコン
                        Icon(
                            imageVector = CustomIcons.ArrowDownward,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "おもいだしたことばを\nじぶんのインコにおぼえさせられるよ",
                        color = Secondary.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // さんかするボタン
            CreateAccountButton(
                onClick = onCreateAccount,
                modifier =
                Modifier
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
 * つぎボタン
 */
@Composable
private fun NextButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        shape = Shapes.large,
        colors =
        ButtonDefaults.elevatedButtonColors(
            containerColor = Primary,
            contentColor = White
        )
    ) {
        Text(
            text = "つぎ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * さんかするボタン
 */
@Composable
private fun CreateAccountButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        shape = Shapes.large,
        colors =
        ButtonDefaults.elevatedButtonColors(
            containerColor = Primary,
            contentColor = White
        )
    ) {
        Text(
            text = "さんかする",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
