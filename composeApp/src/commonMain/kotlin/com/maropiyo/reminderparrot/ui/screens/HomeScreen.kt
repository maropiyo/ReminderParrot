package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.presentation.viewmodel.ReminderListViewModel
import com.maropiyo.reminderparrot.ui.components.home.ParrotSection
import com.maropiyo.reminderparrot.ui.components.home.ReminderSection
import org.koin.compose.viewmodel.koinViewModel

/**
 * ホーム画面
 * Parrotセクションとリマインダーセクションを含む統合画面
 *
 * @param reminderListViewModel リマインダーリストのViewModel
 * @param modifier 修飾子
 */
@Composable
fun HomeScreen(reminderListViewModel: ReminderListViewModel = koinViewModel(), modifier: Modifier = Modifier) {
    // ViewModelの状態を取得
    val state by reminderListViewModel.state.collectAsState()

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Parrotセクション（reminko.pngを表示）
        ParrotSection(
            modifier = Modifier.fillMaxWidth()
        )

        // リマインダーセクション
        ReminderSection(
            state = state,
            onToggleCompletion = reminderListViewModel::toggleReminderCompletion,
            onCreateReminder = { text ->
                reminderListViewModel.createReminder(text)
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
