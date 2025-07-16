package com.maropiyo.reminderparrot.ui.components.home

import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.maropiyo.reminderparrot.domain.entity.Parrot
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import org.jetbrains.compose.resources.painterResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_jump

/**
 * レベルアップ時に表示するお祝いダイアログ
 *
 * @param isVisible ダイアログの表示状態
 * @param parrot パロットの状態情報
 * @param onDismiss ダイアログを閉じる時のコールバック
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpDialog(isVisible: Boolean, parrot: Parrot, onDismiss: () -> Unit) {
    if (isVisible) {
        BasicAlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(min = 300.dp, max = 400.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E1)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // お祝いメッセージ
                    Text(
                        text = "レベルアップ！",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    // ジャンプしているレミンコ
                    val transition = updateTransition(
                        targetState = isVisible,
                        label = "parrot_animation"
                    )

                    val rotation by transition.animateFloat(
                        label = "rotation",
                        transitionSpec = {
                            if (targetState) {
                                tween(
                                    durationMillis = 1000,
                                    easing = EaseOutBounce
                                )
                            } else {
                                tween(300)
                            }
                        }
                    ) { visible ->
                        if (visible) 360f else 0f
                    }

                    Image(
                        painter = painterResource(Res.drawable.reminko_jump),
                        contentDescription = "喜ぶレミンコ",
                        modifier = Modifier
                            .size(120.dp)
                            .rotate(rotation)
                    )

                    // レベル表示
                    Text(
                        text = "Lv.${parrot.level}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 能力向上表示
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "かしこくなったよ！",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF795548)
                        )

                        Text(
                            text = "おぼえられることば: ${parrot.memorizedWords}こ",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "きおくじかん: ${parrot.memoryTimeHours}じかん",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // やったー！ボタン
                    ElevatedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = Shapes.large,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Secondary,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = "やったー！",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
