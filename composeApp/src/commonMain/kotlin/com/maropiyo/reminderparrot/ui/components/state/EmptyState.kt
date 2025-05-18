package com.maropiyo.reminderparrot.ui.components.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 空の状態を表示するコンポーネント
 * @param emptyMessage 空の状態のメッセージ
 * @param paddingValues パディング値
 */
@Composable
fun EmptyState(emptyMessage: String, paddingValues: PaddingValues) {
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emptyMessage,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
