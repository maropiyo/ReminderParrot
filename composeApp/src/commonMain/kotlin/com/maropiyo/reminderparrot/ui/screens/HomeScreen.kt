package com.maropiyo.reminderparrot.ui.screens

import ReminderList
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.maropiyo.reminderparrot.presentation.viewmodel.HomeViewModel
import com.maropiyo.reminderparrot.ui.components.state.EmptyState
import com.maropiyo.reminderparrot.ui.components.state.ErrorState
import com.maropiyo.reminderparrot.ui.components.state.LoadingState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    // ViewModelの状態を取得
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ホーム画面",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.createReminder("リマインダー")
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "おしえる",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                LoadingState(paddingValues)
            }
            state.error != null -> {
                ErrorState(state.error ?: "", paddingValues)
            }
            state.reminders.isEmpty() -> {
                EmptyState("リマインダーがありません", paddingValues)
            }
            else -> {
                ReminderList(
                    reminders = state.reminders,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
