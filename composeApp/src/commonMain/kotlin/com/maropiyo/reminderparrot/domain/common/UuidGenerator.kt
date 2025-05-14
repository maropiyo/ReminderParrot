package com.maropiyo.reminderparrot.domain.common

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * IDジェネレーター
 */
class IdGenerator {
    /**
     * IDを生成する
     *
     * @return 生成されたUUID
     */
    fun generateId(): String {
        @OptIn(ExperimentalUuidApi::class)
        return Uuid.random().toString()
    }
}
