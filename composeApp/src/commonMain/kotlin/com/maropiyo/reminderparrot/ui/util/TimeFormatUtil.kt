package com.maropiyo.reminderparrot.ui.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * 時間フォーマットユーティリティ
 */
object TimeFormatUtil {
    /**
     * 忘却までの残り時間を返す
     *
     * @param forgetAt 忘却時刻
     * @param currentTime 現在時刻
     * @return フォーマットされた時間文字列
     */
    fun formatTimeUntilForget(
        forgetAt: Instant,
        currentTime: Instant = Clock.System.now()
    ): String {
        val remainingDuration = forgetAt - currentTime

        return when {
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
                if (seconds <= 0) {
                    "ぽかん！"
                } else {
                    "わすれるまであと${seconds}秒"
                }
            }
        }
    }
}
