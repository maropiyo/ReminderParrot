package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * リマインネット画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindNetScreen() {
    Scaffold(
        topBar = { RemindNetTopBar() }
    ) { paddingValues ->
        RemindNetContent(paddingValues)
    }
}

/**
 * リマインネット画面のトップバー
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemindNetTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "リマインネット",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    )
}

/**
 * リマインネット画面のコンテンツ
 */
@Composable
private fun RemindNetContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "リマインネット画面",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
