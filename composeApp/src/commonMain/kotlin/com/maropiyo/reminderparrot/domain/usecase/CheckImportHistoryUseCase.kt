package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.repository.ImportHistoryRepository

/**
 * インポート履歴確認ユースケース
 * 特定の投稿が既にインポート済みかどうかを確認する
 */
class CheckImportHistoryUseCase(
    private val importHistoryRepository: ImportHistoryRepository
) {
    /**
     * インポート履歴を確認する
     *
     * @param postId 投稿ID
     * @param importerUserId インポートしたユーザーID
     * @return インポート済みの場合true
     */
    operator fun invoke(postId: String, importerUserId: String): Boolean {
        return importHistoryRepository.hasAlreadyImported(postId, importerUserId)
    }
}
