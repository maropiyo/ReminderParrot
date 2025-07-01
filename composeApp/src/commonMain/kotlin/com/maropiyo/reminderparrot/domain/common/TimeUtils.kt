package com.maropiyo.reminderparrot.domain.common

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * 時間関連のユーティリティ
 */
object TimeUtils {
    
    /**
     * 指定された時間から現在時刻までの経過時間を日本語で表示
     * 
     * @param pastTime 過去の時刻
     * @return 経過時間の文字列（例: "3分前", "2時間前", "1日前"）
     */
    fun getTimeAgoText(pastTime: Instant): String {
        val now = Clock.System.now()
        val duration = now - pastTime
        
        return when {
            duration.inWholeSeconds < 60 -> "${duration.inWholeSeconds}秒前"
            duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}分前"
            duration.inWholeHours < 24 -> "${duration.inWholeHours}時間前"
            else -> "${duration.inWholeDays}日前"
        }
    }
}