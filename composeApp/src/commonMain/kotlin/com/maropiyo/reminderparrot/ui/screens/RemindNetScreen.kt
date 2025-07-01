package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.presentation.viewmodel.RemindNetViewModel
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.Primary
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

/**
 * リマインネット画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindNetScreen(remindNetViewModel: RemindNetViewModel = koinInject()) {
    val state by remindNetViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // エラー表示
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            remindNetViewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "リマインネット",
                        color = Secondary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                ),
                actions = {
                    IconButton(
                        onClick = { remindNetViewModel.refresh() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "リフレッシュ",
                            tint = Secondary
                        )
                    }
                }
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isLoading && state.posts.isEmpty()) {
                // 初回ローディング
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "投稿を読み込み中...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Secondary
                        )
                    }
                }
            } else if (state.posts.isEmpty() && !state.isLoading) {
                // 投稿がない場合
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "まだ投稿がありません",
                            style = MaterialTheme.typography.titleMedium,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "最初の投稿をしてみましょう！",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Secondary.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // 投稿リスト
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.posts, key = { it.id }) { post ->
                        RemindNetPostCard(post = post)
                    }
                }
            }
        }
    }
}

/**
 * リマインネット投稿カード
 */
@Composable
private fun RemindNetPostCard(post: RemindNetPost, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        shape = Shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ユーザー名と投稿時間
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.userName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatDateTime(post.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondary.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // リマインダーテキスト
            Text(
                text = post.reminderText,
                style = MaterialTheme.typography.bodyLarge,
                color = Secondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 忘却時間
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "忘却時間:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDateTime(post.forgetAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 日時をフォーマットする
 */
private fun formatDateTime(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.monthNumber}/${localDateTime.dayOfMonth} ${
    localDateTime.hour.toString().padStart(2, '0')
    }:${localDateTime.minute.toString().padStart(2, '0')}"
}
