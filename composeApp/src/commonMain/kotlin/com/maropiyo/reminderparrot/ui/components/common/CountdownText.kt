package com.maropiyo.reminderparrot.ui.components.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.maropiyo.reminderparrot.ui.util.TimeFormatUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * カウントダウンテキスト
 * リアルタイムで残り時間を更新表示するテキストコンポーネント
 *
 * @param forgetAt 忘却時刻
 * @param textStyle テキストスタイル
 * @param color テキストカラー
 * @param modifier 修飾子
 */
@Composable
fun CountdownText(forgetAt: Instant, textStyle: TextStyle, color: Color, modifier: Modifier = Modifier) {
    // 現在の残り時間テキストを保持
    var timeText by remember { mutableStateOf(TimeFormatUtil.formatTimeUntilForget(forgetAt)) }

    // 1秒ごとに更新
    LaunchedEffect(forgetAt) {
        while (isActive) {
            timeText = TimeFormatUtil.formatTimeUntilForget(forgetAt)

            // 既に忘れた場合は更新を停止
            if (forgetAt <= Clock.System.now()) {
                break
            }

            // 1秒待機
            delay(1000)
        }
    }

    // コンポーネントが破棄される際のクリーンアップ
    DisposableEffect(forgetAt) {
        onDispose {
            // LaunchedEffectは自動的にキャンセルされるため特別な処理は不要
        }
    }

    Text(
        text = timeText,
        style = textStyle,
        color = color,
        modifier = modifier
    )
}
