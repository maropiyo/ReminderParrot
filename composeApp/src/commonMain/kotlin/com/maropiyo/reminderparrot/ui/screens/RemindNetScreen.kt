package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.presentation.viewmodel.RemindNetViewModel
import com.maropiyo.reminderparrot.ui.components.AccountCreationBottomSheet
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.ParrotYellow
import com.maropiyo.reminderparrot.ui.theme.Primary
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.Shapes
import com.maropiyo.reminderparrot.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko_face

/**
 * リマインネット画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindNetScreen(remindNetViewModel: RemindNetViewModel = koinInject()) {
    val state by remindNetViewModel.state.collectAsState()
    val needsAccountCreation by remindNetViewModel.needsAccountCreation.collectAsState()
    val accountCreationError by remindNetViewModel.accountCreationError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // アカウント作成ボトムシートの状態
    var showAccountCreationBottomSheet by remember { mutableStateOf(false) }
    val accountCreationSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    // 画面に遷移した時に投稿を再取得
    LaunchedEffect(Unit) {
        remindNetViewModel.onScreenEntered()
    }

    // エラー表示
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            remindNetViewModel.clearError()
        }
    }

    // アカウント作成が必要な場合はボトムシートを表示
    LaunchedEffect(needsAccountCreation) {
        if (needsAccountCreation) {
            showAccountCreationBottomSheet = true
        } else {
            // アカウント作成成功時にボトムシートを閉じる
            if (showAccountCreationBottomSheet) {
                accountCreationSheetState.hide()
                showAccountCreationBottomSheet = false
            }
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
                colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
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
                            text = "リマインネットに接続中...",
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
                            text = if (needsAccountCreation) {
                                "リマインネットに参加していません"
                            } else {
                                "だれかいませんか？"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // 投稿リスト
                LazyColumn(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.posts, key = { it.id }) { post ->
                        RemindNetPostCard(
                            post = post,
                            onBellClick = { clickedPost ->
                                remindNetViewModel.sendRemindNotification(clickedPost)
                            },
                            isAlreadySent = state.sentPostIds.contains(post.id),
                            isMyPost = state.myPostIds.contains(post.id)
                        )
                    }
                }
            }
        }

        // アカウント作成ボトムシート
        if (showAccountCreationBottomSheet) {
            AccountCreationBottomSheet(
                onDismiss = {
                    scope.launch {
                        accountCreationSheetState.hide()
                        showAccountCreationBottomSheet = false
                        remindNetViewModel.clearAccountCreationError()
                    }
                },
                onCreateAccount = {
                    // アカウント作成処理を実行
                    remindNetViewModel.createAccount()
                },
                sheetState = accountCreationSheetState,
                errorMessage = accountCreationError
            )
        }
    }
}

/**
 * リマインネット投稿カード
 */
@Composable
private fun RemindNetPostCard(
    post: RemindNetPost,
    onBellClick: (RemindNetPost) -> Unit,
    isAlreadySent: Boolean,
    isMyPost: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = White
        ),
        shape = Shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // アイコンとユーザー名
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // リマインコアイコン
                Image(
                    painter = painterResource(Res.drawable.reminko_face),
                    contentDescription = "リマインコ",
                    modifier =
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ParrotYellow, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // ユーザー名
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 投稿時間
                    Text(
                        text = post.timeAgoText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // リマインダーテキストとベルマークボタン
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // リマインダーテキスト（メイン）
                Text(
                    text = post.reminderText,
                    style = MaterialTheme.typography.titleMedium,
                    color = Secondary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = MaterialTheme.typography.titleMedium.lineHeight,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // ベルマークボタン（自分の投稿以外かつ未送信のみ表示）
                if (!isMyPost && !isAlreadySent) {
                    CircularBellButton(
                        onClick = { onBellClick(post) },
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

/**
 * シンプルで可愛い円形ベルボタン
 * 軽やかなアニメーションと影効果でスタイリッシュに
 */
@Composable
private fun CircularBellButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    // プレス時のスケールアニメーション
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "scale"
    )

    // プレス状態を自動的にリセット
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = ParrotYellow.copy(alpha = 0.2f)
            )
            .clip(CircleShape)
            .background(ParrotYellow)
            .clickable {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "リマインドを送る",
            tint = White,
            modifier = Modifier.size(18.dp)
        )
    }
}
