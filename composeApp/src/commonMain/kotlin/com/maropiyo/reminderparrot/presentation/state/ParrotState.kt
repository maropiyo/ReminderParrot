package com.maropiyo.reminderparrot.presentation.state

import com.maropiyo.reminderparrot.domain.entity.Parrot

/**
 * インコの状態
 *
 * @property parrot インコの状態
 * @property isLoading ローディング中かどうか
 * @property error エラーメッセージ
 */
data class ParrotState(
    val parrot: Parrot = Parrot(),
    val isLoading: Boolean = false,
    val error: String? = null
)
