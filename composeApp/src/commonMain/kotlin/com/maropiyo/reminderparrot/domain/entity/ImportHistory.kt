package com.maropiyo.reminderparrot.domain.entity

import kotlinx.datetime.Instant

/**
 * インポート履歴エンティティ
 * 1投稿につき1ユーザー1回までインポート可能な制限を管理
 */
data class ImportHistory(
    val id: String,
    val postId: String,
    val importerUserId: String,
    val importedAt: Instant
)
