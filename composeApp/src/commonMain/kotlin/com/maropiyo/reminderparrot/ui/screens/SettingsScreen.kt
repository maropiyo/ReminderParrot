package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.presentation.viewmodel.SettingsViewModel
import com.maropiyo.reminderparrot.ui.components.AccountCreationBottomSheet
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.CardBackgroundColor
import com.maropiyo.reminderparrot.ui.theme.Primary
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.White
import com.maropiyo.reminderparrot.util.BuildConfig
import com.maropiyo.reminderparrot.util.ParrotNameGenerator
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * 設定画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val viewModel = koinInject<SettingsViewModel>()
    val settings by viewModel.settings.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val accountCreationError by viewModel.accountCreationError.collectAsState()
    val scope = rememberCoroutineScope()

    // リマインコの名前表示状態
    var parrotNameDisplay by remember { mutableStateOf("") }

    // 設定が読み込まれたときに表示フィールドを更新
    LaunchedEffect(settings.parrotName) {
        parrotNameDisplay = settings.parrotName
    }

    // アカウント作成ボトムシートの状態
    var showAccountCreationBottomSheet by remember { mutableStateOf(false) }
    val accountCreationSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // アカウント作成成功時にボトムシートを閉じる
    LaunchedEffect(userId) {
        if (userId != null && showAccountCreationBottomSheet) {
            scope.launch {
                accountCreationSheetState.hide()
                showAccountCreationBottomSheet = false
                viewModel.clearAccountCreationError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "せってい",
                        color = Secondary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // インコ情報カード
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "リマインコについて",
                        style = MaterialTheme.typography.titleMedium,
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // リマインコの名前設定
                    Column {
                        Text(
                            text = "リマインコのなまえ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Secondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = parrotNameDisplay.ifEmpty { "リマインコ" },
                                style = MaterialTheme.typography.bodyLarge,
                                color = Secondary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = Secondary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            )

                            IconButton(
                                onClick = {
                                    val newName = ParrotNameGenerator.generateRandomName()
                                    parrotNameDisplay = newName
                                    viewModel.updateParrotName(newName)
                                },
                                modifier = Modifier
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "なまえをかえる",
                                    tint = Secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // インコID表示
                    Column {
                        Text(
                            text = "リマインコID",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Secondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userId ?: "みしゅとく",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // リマインネット設定
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "リマインネット",
                        style = MaterialTheme.typography.titleMedium,
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // リマインネット投稿設定
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "こうかいする",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Secondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "おしえたことばをみんなにみせる",
                                style = MaterialTheme.typography.bodySmall,
                                color = Secondary.copy(alpha = 0.7f)
                            )
                        }

                        Switch(
                            checked = settings.isRemindNetSharingEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled && userId == null) {
                                    // アカウントが未作成の場合はアカウント作成ボトムシートを表示
                                    showAccountCreationBottomSheet = true
                                } else {
                                    viewModel.updateRemindNetSharingEnabled(enabled)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Primary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Secondary.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            // デバッグ設定（デバッグビルド時のみ表示）
            if (BuildConfig.isDebug) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "デバッグせってい",
                            style = MaterialTheme.typography.titleMedium,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 高速記憶設定
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "すぐわすれるモード",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Secondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${settings.debugForgetTimeSeconds}びょうですぐにわすれるよ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Secondary.copy(alpha = 0.7f)
                                )
                            }

                            Switch(
                                checked = settings.isDebugFastMemoryEnabled,
                                onCheckedChange = { viewModel.updateDebugFastMemoryEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Primary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Secondary.copy(alpha = 0.3f)
                                )
                            )
                        }

                        // スライダー（すぐわすれるモードが有効な場合のみ表示）
                        if (settings.isDebugFastMemoryEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "わすれるまでの時間",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Secondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${settings.debugForgetTimeSeconds}びょう",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Slider(
                                    value = settings.debugForgetTimeSeconds.toFloat(),
                                    onValueChange = { value ->
                                        // 10秒刻みに丸める
                                        val roundedValue = (value / 10f).toInt() * 10
                                        viewModel.updateDebugForgetTimeSeconds(roundedValue)
                                    },
                                    valueRange = 10f..60f,
                                    steps = 4,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Primary,
                                        activeTrackColor = Primary,
                                        inactiveTrackColor = Secondary.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "10びょう",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Secondary.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "60びょう",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Secondary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // アカウント作成ボトムシート
            if (showAccountCreationBottomSheet) {
                AccountCreationBottomSheet(
                    onDismiss = {
                        scope.launch {
                            accountCreationSheetState.hide()
                            showAccountCreationBottomSheet = false
                            viewModel.clearAccountCreationError()
                        }
                    },
                    onCreateAccount = {
                        // アカウント作成処理を実行
                        viewModel.createAccount()
                    },
                    sheetState = accountCreationSheetState,
                    errorMessage = accountCreationError
                )
            }
        }
    }
}
