package com.maropiyo.reminderparrot.ui.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * 時間フォーマットユーティリティ
 */
object TimeFormatUtil {
    /**
     * 忘却までの残り時間を子供にも分かりやすいフォーマットで返す
     *
     * @param forgetAt 忘却時刻
     * @param currentTime 現在時刻
     * @return フォーマットされた時間文字列
     */
    fun formatTimeUntilForget(forgetAt: Instant, currentTime: Instant = Clock.System.now()): String {
        val remainingDuration = forgetAt - currentTime

        return when {
            remainingDuration <= Duration.ZERO -> "もうわすれちゃった"
            remainingDuration >= 1.days -> {
                val days = remainingDuration.inWholeDays
                val hours = (remainingDuration - days.days).inWholeHours
                if (hours > 0) {
                    "わすれるまであと${days}日と${hours}時間"
                } else {
                    "わすれるまであと${days}日"
                }
            }
            remainingDuration >= 1.hours -> {
                val hours = remainingDuration.inWholeHours
                val minutes = (remainingDuration - hours.hours).inWholeMinutes
                "わすれるまであと${hours}時間${minutes}分"
            }
            remainingDuration >= 1.minutes -> {
                val minutes = remainingDuration.inWholeMinutes
                val seconds = (remainingDuration - minutes.minutes).inWholeSeconds
                "わすれるまであと${minutes}分${seconds}秒"
            }
            else -> {
                val seconds = remainingDuration.inWholeSeconds
                val displaySeconds = maxOf(1, seconds) // 最低1秒と表示
                "わすれるまであと${displaySeconds}秒"
            }
        }
    }

    /**
     * 残り時間に基づく透過度を計算する
     * * @param forgetAt 忘却時刻
     * @param createdAt 作成時刻
     * @param currentTime 現在時刻
     * @return 透過度（0.2〜1.0）
     */
    fun calculateAlpha(forgetAt: Instant, createdAt: Instant, currentTime: Instant = Clock.System.now()): Float {
        val totalDuration = forgetAt - createdAt
        val remainingDuration = forgetAt - currentTime

        return when {
            remainingDuration <= Duration.ZERO -> 0.2f
            remainingDuration >= totalDuration -> 1.0f
            else -> {
                val progress = remainingDuration.inWholeMilliseconds.toFloat() /
                    totalDuration.inWholeMilliseconds.toFloat()
                0.2f + (0.8f * progress) // 0.2から1.0の範囲で透過度を計算
            }
        }
    }
}
