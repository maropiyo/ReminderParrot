package com.maropiyo.reminderparrot.data.local

import com.maropiyo.reminderparrot.domain.entity.Parrot

/**
 * データベース初期化クラス
 * * アプリ起動時にデータベースの初期化を行う
 * * @property parrotLocalDataSource インコのローカルデータソース
 */
class DatabaseInitializer(
    private val parrotLocalDataSource: ParrotLocalDataSource
) {
    /**
     * データベースを初期化する
     * Parrotテーブルにデータが存在しない場合、初期データを挿入する
     */
    fun initialize() {
        val existingParrot = parrotLocalDataSource.getParrot()
        if (existingParrot == null) {
            // Parrotテーブルが空の場合、初期データを作成
            val initialParrot = Parrot(
                level = 1,
                currentExperience = 0,
                maxExperience = 1,
                memorizedWords = 1,
                memoryTimeHours = 1
            )
            parrotLocalDataSource.saveParrot(initialParrot)
        }
    }
}
