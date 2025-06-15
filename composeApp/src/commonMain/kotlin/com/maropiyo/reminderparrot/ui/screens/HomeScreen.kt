package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.presentation.viewmodel.ParrotViewModel
import com.maropiyo.reminderparrot.presentation.viewmodel.ReminderListViewModel
import com.maropiyo.reminderparrot.ui.components.home.LevelUpDialog
import com.maropiyo.reminderparrot.ui.components.home.ParrotContent
import com.maropiyo.reminderparrot.ui.components.home.ReminderContent
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

/**
 * ホーム画面
 * Parrotコンテンツとリマインダーコンテンツを含む統合画面
 *
 * @param parrotViewModel インコのViewModel
 * @param reminderListViewModel リマインダーリストのViewModel
 * @param modifier 修飾子
 */
@Composable
fun HomeScreen(
    parrotViewModel: ParrotViewModel = koinViewModel(),
    reminderListViewModel: ReminderListViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    // ViewModelの状態を取得
    val parrotState by parrotViewModel.state.collectAsState()
    val state by reminderListViewModel.state.collectAsState()

    // レベルアップポップアップの表示状態
    var showLevelUpPopup by remember { mutableStateOf(false) }
    var previousLevel by remember { mutableStateOf(parrotState.parrot.level) }

    // レベルアップを検出
    LaunchedEffect(parrotState.parrot.level) {
        if (parrotState.parrot.level > previousLevel) {
            // 経験値ゲージのアニメーション完了を待つ
            delay(1000)

            // レベルアップポップアップを表示
            showLevelUpPopup = true
            previousLevel = parrotState.parrot.level
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Parrotコンテンツ
            ParrotContent(
                state = parrotState,
                modifier = Modifier.fillMaxWidth()
            )

            // リマインダーコンテンツ
            ReminderContent(
                state = state,
                onToggleCompletion = { reminderId ->
                    reminderListViewModel.toggleReminderCompletion(reminderId)
                    // リマインダー完了後にインコの状態を再読み込み
                    parrotViewModel.loadParrot()
                },
                onCreateReminder = { text ->
                    reminderListViewModel.createReminder(text)
                    // リマインダー作成後にインコの状態を再読み込み
                    parrotViewModel.loadParrot()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        // レベルアップダイアログ（画面全体をマスク）
        LevelUpDialog(
            isVisible = showLevelUpPopup,
            parrot = parrotState.parrot,
            onDismiss = { showLevelUpPopup = false }
        )
    }
}
