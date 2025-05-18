package com.maropiyo.reminderparrot.ui.components.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * エラーメッセージを表示するコンポーネント
 */
@Composable
fun ErrorState(
    errorMessage: String,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        // TODO: エラーメッセージを表示
        Text(text = errorMessage)
    }
}
