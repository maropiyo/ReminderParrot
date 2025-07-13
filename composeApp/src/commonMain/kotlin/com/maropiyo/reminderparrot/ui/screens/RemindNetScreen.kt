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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.derivedStateOf
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
import com.maropiyo.reminderparrot.domain.usecase.RegisterPushNotificationTokenUseCase
import com.maropiyo.reminderparrot.presentation.viewmodel.RemindNetViewModel
import com.maropiyo.reminderparrot.ui.components.AccountCreationBottomSheet
import com.maropiyo.reminderparrot.ui.theme.Background
import com.maropiyo.reminderparrot.ui.theme.Error
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
import reminderparrot.composeapp.generated.resources.reminko_raising_hand

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

    // リスト表示用のスクロール状態
    val listState = rememberLazyListState()

    // ユーザーの手動スクロール状態を管理
    var isUserScrolling by remember { mutableStateOf(false) }
    var lastPostCount by remember { mutableStateOf(0) }
    var newPostNotificationCount by remember { mutableStateOf(0) }
    var showNewPostNotification by remember { mutableStateOf(false) }

    // ユーザーがスクロール中かどうかを判定
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    // プッシュ通知トークン登録
    val registerPushNotificationToken = koinInject<RegisterPushNotificationTokenUseCase>()

    // アカウント作成ボトムシートの状態
    var showAccountCreationBottomSheet by remember { mutableStateOf(false) }
    val accountCreationSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    // 投稿詳細ボトムシートの状態
    var selectedPost by remember { mutableStateOf<RemindNetPost?>(null) }
    var showPostDetailBottomSheet by remember { mutableStateOf(false) }
    val postDetailSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    // 画面に遷移した時に投稿を再取得
    LaunchedEffect(Unit) {
        remindNetViewModel.onScreenEntered()
    }

    // ユーザーがスクロール位置を変更したことを検出
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress && !isAtTop) {
            isUserScrolling = true
        }
    }

    // 一定時間後にユーザースクロール状態をリセット
    LaunchedEffect(isUserScrolling) {
        if (isUserScrolling) {
            kotlinx.coroutines.delay(3000) // 3秒後にリセット
            if (isAtTop) {
                isUserScrolling = false
            }
        }
    }

    // ユーザーが手動で一番上にスクロールした時に通知ボタンを非表示
    LaunchedEffect(isAtTop) {
        if (isAtTop && showNewPostNotification) {
            showNewPostNotification = false
            newPostNotificationCount = 0
        }
    }

    // 投稿リストに新しい投稿が追加された時のスクロール制御
    LaunchedEffect(state.posts.size) {
        val currentPostCount = state.posts.size

        if (lastPostCount > 0 && currentPostCount > lastPostCount) {
            val newPostCount = currentPostCount - lastPostCount

            when {
                // ユーザーがスクロール中の場合は自動スクロールしない
                isUserScrolling -> {
                    // 何もしない（ユーザーの読書体験を尊重）
                }
                // 大量の新規投稿（10件以上）の場合は通知のみ
                newPostCount >= 10 -> {
                    newPostNotificationCount = newPostCount
                    showNewPostNotification = true
                }
                // 少数の新規投稿の場合は自動スクロール
                currentPostCount > 0 -> {
                    listState.animateScrollToItem(0)
                }
            }
        }

        lastPostCount = currentPostCount
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

                // アカウント作成後にプッシュ通知トークンを登録
                scope.launch {
                    registerPushNotificationToken()
                }
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
                    state = listState,
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
                            onCardClick = { clickedPost ->
                                selectedPost = clickedPost
                                showPostDetailBottomSheet = true
                            },
                            isAlreadySent = state.sentPostIds.contains(post.id),
                            isMyPost = state.myPostIds.contains(post.id)
                        )
                    }
                }
            }

            // 新規投稿通知フローティングボタン
            if (showNewPostNotification) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NewPostNotificationButton(
                        count = newPostNotificationCount,
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                                showNewPostNotification = false
                                newPostNotificationCount = 0
                            }
                        }
                    )
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

        // 投稿詳細ボトムシート
        if (showPostDetailBottomSheet && selectedPost != null) {
            RemindNetPostDetailBottomSheet(
                post = selectedPost!!,
                onDismiss = {
                    scope.launch {
                        postDetailSheetState.hide()
                        showPostDetailBottomSheet = false
                        selectedPost = null
                    }
                },
                onBellClick = { clickedPost ->
                    remindNetViewModel.sendRemindNotification(clickedPost)
                    scope.launch {
                        postDetailSheetState.hide()
                        showPostDetailBottomSheet = false
                        selectedPost = null
                    }
                },
                onDeleteClick = { clickedPost ->
                    println("RemindNetScreen: 削除ボタンクリック - postId: ${clickedPost.id}")
                    remindNetViewModel.deletePost(clickedPost.id) {
                        println("RemindNetScreen: 削除成功コールバック実行")
                        // 削除成功時にボトムシートを閉じる
                        scope.launch {
                            postDetailSheetState.hide()
                            showPostDetailBottomSheet = false
                            selectedPost = null
                        }
                    }
                },
                sheetState = postDetailSheetState,
                isAlreadySent = selectedPost?.let { state.sentPostIds.contains(it.id) } ?: false,
                isMyPost = selectedPost?.let { state.myPostIds.contains(it.id) } ?: false
            )
        }
    }
}

/**
 * 新規投稿通知フローティングボタン
 */
@Composable
private fun NewPostNotificationButton(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .wrapContentSize()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = White
        ),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "新しい投稿を見る",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${count}件の新しい投稿",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * リマインネット投稿カード
 */
@Composable
private fun RemindNetPostCard(
    post: RemindNetPost,
    onBellClick: (RemindNetPost) -> Unit,
    onCardClick: (RemindNetPost) -> Unit,
    isAlreadySent: Boolean,
    isMyPost: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick(post) },
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
                // インコアイコン
                Image(
                    painter = painterResource(Res.drawable.reminko_face),
                    contentDescription = "インコ",
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
                        onClick = {
                            onBellClick(post)
                        },
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

/**
 * リマインネット投稿詳細ボトムシート
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemindNetPostDetailBottomSheet(
    post: RemindNetPost,
    onDismiss: () -> Unit,
    onBellClick: (RemindNetPost) -> Unit,
    onDeleteClick: (RemindNetPost) -> Unit,
    sheetState: androidx.compose.material3.SheetState,
    isAlreadySent: Boolean,
    isMyPost: Boolean
) {
    ModalBottomSheet(
        dragHandle = null,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            PostDetailCard(
                post = post,
                onBellClick = onBellClick,
                onDeleteClick = onDeleteClick,
                isAlreadySent = isAlreadySent,
                isMyPost = isMyPost,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 104.dp)
            )

            // インコの画像
            Image(
                painter = painterResource(Res.drawable.reminko_raising_hand),
                contentDescription = "Parrot",
                modifier =
                Modifier
                    .size(128.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * 投稿詳細カード
 */
@Composable
private fun PostDetailCard(
    post: RemindNetPost,
    onBellClick: (RemindNetPost) -> Unit,
    onDeleteClick: (RemindNetPost) -> Unit,
    isAlreadySent: Boolean,
    isMyPost: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors =
        CardDefaults.cardColors(
            containerColor = Background
        ),
        shape = Shapes.extraLarge
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            // タイトルテキスト
            Text(
                text = "どうする？",
                color = Secondary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // ユーザー情報
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // インコアイコン
                Image(
                    painter = painterResource(Res.drawable.reminko_face),
                    contentDescription = "インコ",
                    modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ParrotYellow, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // ユーザー名
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Secondary
                    )

                    // 投稿時間
                    Text(
                        text = post.timeAgoText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Secondary.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // リマインダーテキスト
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Secondary.copy(alpha = 0.08f) // 薄いグレー背景で読み取り専用感を演出
                ),
                shape = Shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // 影を削除
            ) {
                Text(
                    text = post.reminderText,
                    style = MaterialTheme.typography.titleMedium,
                    color = Secondary,
                    fontWeight = FontWeight.Bold,
                    lineHeight = MaterialTheme.typography.titleMedium.lineHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ベルボタン（自分の投稿以外かつ未送信のみ表示）
            if (!isMyPost && !isAlreadySent) {
                ElevatedButton(
                    onClick = { onBellClick(post) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = Shapes.large,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = ParrotYellow,
                        contentColor = White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "リマインドを送る",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "リマインドを送る",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (isAlreadySent) {
                Text(
                    text = "リマインドを送信済み",
                    style = MaterialTheme.typography.titleMedium,
                    color = Secondary.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else if (isMyPost) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // スマートな投稿者表示
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "あなたの投稿",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // 編集ボトムシートスタイルの削除ボタン
                    ElevatedButton(
                        onClick = { onDeleteClick(post) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = Shapes.large,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Error,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = "わすれる",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
