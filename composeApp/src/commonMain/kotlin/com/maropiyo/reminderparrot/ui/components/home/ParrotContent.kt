package com.maropiyo.reminderparrot.ui.components.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maropiyo.reminderparrot.presentation.state.ParrotState
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.Gray
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko

/**
 * インココンテンツ
 * ホーム画面内のインコ関連コンテンツを管理するコンポーネント
 *
 * @param modifier 修飾子
 */
@Composable
fun ParrotContent(state: ParrotState, modifier: Modifier = Modifier) {
    Card(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = Shapes.extraLarge,
        colors =
        CardDefaults.cardColors(
            containerColor = White
        ),
        elevation =
        CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // レベル表示
                Text(
                    text = "レベル${state.parrot.level}",
                    style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Secondary
                )
                // インコと丸型経験値インジケータ
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 前回のレベルを記憶
                    var previousLevel by remember { mutableStateOf(state.parrot.level) }
                    var displayProgress by remember {
                        mutableStateOf(
                            if (state.parrot.maxExperience > 0) {
                                state.parrot.currentExperience.toFloat() / state.parrot.maxExperience.toFloat()
                            } else {
                                0f
                            }
                        )
                    }
                    var skipAnimation by remember { mutableStateOf(false) }

                    // 現在の実際の進捗を計算
                    val actualProgress =
                        if (state.parrot.maxExperience > 0) {
                            state.parrot.currentExperience.toFloat() / state.parrot.maxExperience.toFloat()
                        } else {
                            0f
                        }

                    // レベルアップを検出
                    LaunchedEffect(state.parrot.level, state.parrot.currentExperience) {
                        if (state.parrot.level > previousLevel) {
                            // レベルアップした場合、まず100%まで上昇させる
                            skipAnimation = false
                            displayProgress = 1f
                            delay(1000) // アニメーション完了まで待機
                            previousLevel = state.parrot.level
                            // 新レベルの初期値に即座にリセット（アニメーションなし）
                            skipAnimation = true
                            displayProgress = actualProgress
                            // 次回のアニメーションを有効にする
                            delay(50) // 状態更新を確実にするための短い遅延
                            skipAnimation = false
                        } else {
                            // 通常の経験値増加
                            displayProgress = actualProgress
                        }
                    }

                    // アニメーション付き進捗
                    val animatedProgress by animateFloatAsState(
                        targetValue = displayProgress,
                        animationSpec =
                        if (skipAnimation) {
                            tween(durationMillis = 0) // 即座に変更
                        } else {
                            tween(durationMillis = 1000) // 通常のアニメーション
                        },
                        label = "experience_progress"
                    )

                    // 丸型経験値インジケータ
                    Canvas(
                        modifier = Modifier.size(100.dp)
                    ) {
                        val strokeWidth = 4.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)

                        // 背景円
                        drawCircle(
                            color = Background,
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // 経験値円弧
                        if (animatedProgress > 0) {
                            drawArc(
                                color = Secondary,
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                topLeft =
                                Offset(
                                    center.x - radius,
                                    center.y - radius
                                ),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // インコの画像
                    Image(
                        painter = painterResource(Res.drawable.reminko),
                        contentDescription = "Parrot",
                        modifier = Modifier.size(84.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // ステータス情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // かしこさタイトル
                Text(
                    text = "できること",
                    style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Secondary
                )

                // ステータス表示（縦並び）
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // おぼえられることば
                    Row(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(Shapes.large)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "おぼえられることば",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${state.parrot.memorizedWords}こ",
                            style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Secondary,
                            fontSize = 16.sp
                        )
                    }

                    // きおくじかん
                    Row(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(Shapes.large)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "きおくじかん",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${state.parrot.memoryTimeHours}じかん",
                            style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Secondary,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
