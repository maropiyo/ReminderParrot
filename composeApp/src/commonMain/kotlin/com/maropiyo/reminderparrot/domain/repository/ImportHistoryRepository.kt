package com.maropiyo.reminderparrot.domain.repository

import com.maropiyo.reminderparrot.domain.entity.ImportHistory

/**
 * インポート履歴リポジトリ
 */
interface ImportHistoryRepository {
    /**
     * インポート履歴を記録する
     *
     * @param postId 投稿ID
     * @param importerUserId インポートしたユーザーID
     * @return 記録したインポート履歴
     */
    fun recordImportHistory(postId: String, importerUserId: String): ImportHistory

    /**
     * インポート履歴を確認する（インポート済みかチェック）
     *
     * @param postId 投稿ID
     * @param importerUserId インポートしたユーザーID
     * @return インポート済みの場合true
     */
    fun hasAlreadyImported(postId: String, importerUserId: String): Boolean

    /**
     * 特定投稿のインポート履歴を削除する
     *
     * @param postId 投稿ID
     */
    fun deleteHistoryForPost(postId: String)
}
