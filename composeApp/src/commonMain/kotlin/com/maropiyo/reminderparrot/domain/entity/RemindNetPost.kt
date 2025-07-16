package com.maropiyo.reminderparrot.domain.entity

import com.maropiyo.reminderparrot.domain.common.TimeUtils
import kotlinx.datetime.Instant

/**
 * リマインネット投稿エンティティ
 * SNS機能で共有されるリマインダー投稿を表す
 */
data class RemindNetPost(
    val id: String,
    val reminderText: String,
    val userId: String?,
    val userName: String = "ひよっこインコ",
    val userLevel: Int? = null,
    val createdAt: Instant,
    val forgetAt: Instant,
    val likesCount: Int = 0,
    val isDeleted: Boolean = false
) {
    /**
     * 投稿からの経過時間を取得する
     * @return 経過時間の文字列（例: "3分前", "2時間前"）
     */
    val timeAgoText: String
        get() = TimeUtils.getTimeAgoText(createdAt)
}
