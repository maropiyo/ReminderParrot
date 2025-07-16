package com.maropiyo.reminderparrot.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.usecase.RegisterPushNotificationTokenUseCase
import com.maropiyo.reminderparrot.presentation.viewmodel.ParrotViewModel
import com.maropiyo.reminderparrot.presentation.viewmodel.RemindNetViewModel
import com.maropiyo.reminderparrot.ui.components.AccountCreationBottomSheet
import com.maropiyo.reminderparrot.ui.components.ErrorMessageBottomSheet
import com.maropiyo.reminderparrot.ui.components.home.LevelUpDialog
import com.maropiyo.reminderparrot.ui.icons.CustomIcons
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
fun RemindNetScreen(
    remindNetViewModel: RemindNetViewModel = koinInject(),
    parrotViewModel: ParrotViewModel = koinInject(),
    onReminderImported: () -> Unit = {}
) {
    val state by remindNetViewModel.state.collectAsState()
    val needsAccountCreation by remindNetViewModel.needsAccountCreation.collectAsState()
    val accountCreationError by remindNetViewModel.accountCreationError.collectAsState()
    val parrotState by parrotViewModel.state.collectAsState()
    val displayName by remindNetViewModel.displayName.collectAsState()
    val isLoadingDisplayName by remindNetViewModel.isLoadingDisplayName.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // リスト表示用のスクロール状態
    val listState = rememberLazyListState()

    // ユーザーの手動スクロール状態を管理
    var isUserScrolling by remember { mutableStateOf(false) }
    var lastPostCount by remember { mutableStateOf(0) }
    var newPostNotificationCount by remember { mutableStateOf(0) }
    var showNewPostNotification by remember { mutableStateOf(false) }

    // レベルアップポップアップの表示状態
    var showLevelUpPopup by remember { mutableStateOf(false) }
    var previousLevel by remember { mutableStateOf(parrotState.parrot.level) }

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

    // エラーメッセージボトムシートの状態
    var showErrorBottomSheet by remember { mutableStateOf(false) }
    var errorTitle by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val errorSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    // 画面に遷移した時に投稿を再取得
    LaunchedEffect(Unit) {
        remindNetViewModel.onScreenEntered()
        // 他の画面で名前が変更されている可能性があるため強制的に再読み込み
        remindNetViewModel.forceReloadDisplayName()
    }

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
            when (error) {
                "もうおぼえられないよ〜" -> {
                    errorTitle = "もうおぼえられないよ〜"
                    errorMessage = "レベルをあげてもっとかしこくなろう！"
                }
                else -> {
                    errorTitle = "あれれ？"
                    errorMessage = error
                }
            }
            showErrorBottomSheet = true
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
            Column {
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
                // インコ情報表示（アカウントがある場合のみ）
                if (parrotState.parrot != null && !needsAccountCreation) {
                    SimpleParrotInfoDisplay(
                        parrot = parrotState.parrot!!,
                        displayName = displayName?.takeIf { it.isNotBlank() } ?: "ひよっこインコ",
                        isLoadingDisplayName = isLoadingDisplayName,
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
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
            } else {
                Column(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    // タイムライン見出し
                    Text(
                        text = "タイムライン",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Secondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // 投稿リストまたは空状態
                    if (state.posts.isEmpty() && !state.isLoading) {
                        // 投稿がない場合の空状態表示
                        Column(
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
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
                            Spacer(modifier = Modifier.weight(1.5f))
                        }
                    } else {
                        // 投稿リスト
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxHeight(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.posts, key = { it.id }) { post ->
                                RemindNetPostCard(
                                    post = post,
                                    onBellClick = { clickedPost ->
                                        remindNetViewModel.sendRemindNotification(clickedPost)
                                    },
                                    onImportClick = { clickedPost ->
                                        remindNetViewModel.importPost(clickedPost) {
                                            // インポート成功時のコールバック
                                            onReminderImported()
                                        }
                                    },
                                    onCardClick = { clickedPost ->
                                        selectedPost = clickedPost
                                        showPostDetailBottomSheet = true
                                    },
                                    isAlreadySent = state.sentPostIds.contains(post.id),
                                    isMyPost = state.myPostIds.contains(post.id),
                                    isAlreadyImported = state.importedPostIds.contains(post.id)
                                )
                            }
                        }
                    }
                }
            }

            // 新規投稿通知フローティングボタン
            if (showNewPostNotification) {
                Box(
                    modifier =
                    Modifier
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
                onImportClick = { clickedPost ->
                    remindNetViewModel.importPost(clickedPost) {
                        // インポート成功時のコールバック
                        onReminderImported()
                    }
                    scope.launch {
                        postDetailSheetState.hide()
                        showPostDetailBottomSheet = false
                        selectedPost = null
                    }
                },
                onDeleteClick = { clickedPost ->
                    println("RemindNetScreen: 削除ボタンクリック - postId: ${clickedPost.id}")
                    // 削除処理開始時に即座にボトムシートを閉じる
                    scope.launch {
                        postDetailSheetState.hide()
                        showPostDetailBottomSheet = false
                        selectedPost = null
                    }
                    remindNetViewModel.deletePost(clickedPost.id) {
                        println("RemindNetScreen: 削除成功コールバック実行")
                    }
                },
                sheetState = postDetailSheetState,
                isAlreadySent = selectedPost?.let { state.sentPostIds.contains(it.id) } ?: false,
                isMyPost = selectedPost?.let { state.myPostIds.contains(it.id) } ?: false,
                isAlreadyImported = selectedPost?.let { state.importedPostIds.contains(it.id) } ?: false
            )
        }

        // エラーメッセージボトムシート
        if (showErrorBottomSheet) {
            ErrorMessageBottomSheet(
                title = errorTitle,
                message = errorMessage,
                onDismiss = {
                    scope.launch {
                        errorSheetState.hide()
                        showErrorBottomSheet = false
                        remindNetViewModel.clearError()
                    }
                },
                sheetState = errorSheetState
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

/**
 * 新規投稿通知フローティングボタン
 */
@Composable
private fun NewPostNotificationButton(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier =
        modifier
            .wrapContentSize()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        colors =
        ButtonDefaults.buttonColors(
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
    onImportClick: (RemindNetPost) -> Unit,
    onCardClick: (RemindNetPost) -> Unit,
    isAlreadySent: Boolean,
    isMyPost: Boolean,
    isAlreadyImported: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier =
        modifier
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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

                        // あなたの投稿
                        if (isMyPost) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Secondary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "あなた",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Secondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

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

                // アクションボタン（自分の投稿以外のみ表示）
                if (!isMyPost) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // インポートボタン（未インポートのみ表示）
                        if (!isAlreadyImported) {
                            CircularImportButton(
                                onClick = {
                                    onImportClick(post)
                                },
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // ベルマークボタン（未送信のみ表示）
                        if (!isAlreadySent) {
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
        modifier =
        modifier
            .scale(scale)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = ParrotYellow.copy(alpha = 0.2f)
            ).clip(CircleShape)
            .background(ParrotYellow)
            .clickable {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "リマインドをおくる",
            tint = White,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * 点線円形のインポートボタン
 * フェッチするような下矢印アイコンとスタイリッシュなデザイン
 */
@Composable
private fun CircularImportButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
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
        modifier =
        modifier
            .scale(scale)
            .clip(CircleShape)
            .clickable {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // 点線の円を描画
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 2.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            drawCircle(
                color = Primary,
                radius = radius,
                center = center,
                style =
                Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )
            )
        }

        // 下矢印アイコン
        Icon(
            imageVector = CustomIcons.ArrowDownward,
            contentDescription = "ことばをおぼえる",
            tint = Primary,
            modifier = Modifier.size(20.dp)
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
    onImportClick: (RemindNetPost) -> Unit,
    onDeleteClick: (RemindNetPost) -> Unit,
    sheetState: androidx.compose.material3.SheetState,
    isAlreadySent: Boolean,
    isMyPost: Boolean,
    isAlreadyImported: Boolean
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
                onImportClick = onImportClick,
                onDeleteClick = onDeleteClick,
                isAlreadySent = isAlreadySent,
                isMyPost = isMyPost,
                isAlreadyImported = isAlreadyImported,
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
    onImportClick: (RemindNetPost) -> Unit,
    onDeleteClick: (RemindNetPost) -> Unit,
    isAlreadySent: Boolean,
    isMyPost: Boolean,
    isAlreadyImported: Boolean,
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
                modifier =
                Modifier
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // ユーザー名
                        Text(
                            text = post.userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Secondary
                        )

                        // スマートな投稿者表示
                        if (isMyPost) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Secondary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "あなた",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Secondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

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
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors =
                CardDefaults.cardColors(
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
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ベルボタンとインポートボタン（自分の投稿以外のみ表示）
            if (!isMyPost) {
                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // リマインドボタン
                    ElevatedButton(
                        onClick = {
                            if (!isAlreadySent) {
                                onBellClick(post)
                            }
                        },
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = Shapes.large,
                        enabled = !isAlreadySent,
                        colors =
                        ButtonDefaults.elevatedButtonColors(
                            containerColor = if (isAlreadySent) ParrotYellow.copy(alpha = 0.3f) else ParrotYellow,
                            contentColor = if (isAlreadySent) White.copy(alpha = 0.5f) else White,
                            disabledContainerColor = ParrotYellow.copy(alpha = 0.3f),
                            disabledContentColor = White.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "リマインドをおくる",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAlreadySent) "リマインドそうしんずみ" else "リマインドをおくる",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // インポートボタン
                    ElevatedButton(
                        onClick = {
                            if (!isAlreadyImported) {
                                onImportClick(post)
                            }
                        },
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = Shapes.large,
                        enabled = !isAlreadyImported,
                        colors =
                        ButtonDefaults.elevatedButtonColors(
                            containerColor = if (isAlreadyImported) Primary.copy(alpha = 0.3f) else Primary,
                            contentColor = if (isAlreadyImported) White.copy(alpha = 0.5f) else White,
                            disabledContainerColor = Primary.copy(alpha = 0.3f),
                            disabledContentColor = White.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = CustomIcons.ArrowDownward,
                            contentDescription = "ことばをおぼえる",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAlreadyImported) "おぼえてるよ" else "このことばをおぼえる",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 編集ボトムシートスタイルの削除ボタン
                    ElevatedButton(
                        onClick = { onDeleteClick(post) },
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = Shapes.large,
                        colors =
                        ButtonDefaults.elevatedButtonColors(
                            containerColor = Error,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = "さくじょ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 簡易インコ情報表示コンポーネント
 */
@Composable
private fun SimpleParrotInfoDisplay(
    parrot: com.maropiyo.reminderparrot.domain.entity.Parrot,
    displayName: String,
    isLoadingDisplayName: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors =
        CardDefaults.cardColors(
            containerColor = White
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // インコアイコン
            Image(
                painter = painterResource(Res.drawable.reminko_face),
                contentDescription = "リマインコ",
                modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ParrotYellow, CircleShape),
                contentScale = ContentScale.Crop
            )

            // レベルと名前表示
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // レベルと名前を横並び
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier =
                        Modifier
                            .background(
                                Primary.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            ).padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Lv.${parrot.level}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                    if (isLoadingDisplayName && displayName == null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(16.dp)
                                .background(
                                    Secondary.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                        )
                    } else {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 経験値ゲージ（アニメーション付き）
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Secondary.copy(alpha = 0.1f))
                ) {
                    // 前回のレベルを記憶
                    var previousLevel by remember { mutableStateOf(parrot.level) }
                    var displayProgress by remember {
                        mutableStateOf(
                            if (parrot.maxExperience > 0) {
                                parrot.currentExperience.toFloat() / parrot.maxExperience.toFloat()
                            } else {
                                0f
                            }
                        )
                    }
                    var skipAnimation by remember { mutableStateOf(false) }

                    // 現在の実際の進捗を計算
                    val actualProgress =
                        if (parrot.maxExperience > 0) {
                            (parrot.currentExperience.toFloat() / parrot.maxExperience.toFloat()).coerceIn(0f, 1f)
                        } else {
                            0f
                        }

                    // レベルアップを検出
                    LaunchedEffect(parrot.level, parrot.currentExperience) {
                        if (parrot.level > previousLevel) {
                            // レベルアップした場合、まず100%まで上昇させる
                            skipAnimation = false
                            displayProgress = 1f
                            kotlinx.coroutines.delay(800) // アニメーション完了まで待機

                            previousLevel = parrot.level
                            // 新レベルの初期値に即座にリセット（アニメーションなし）
                            skipAnimation = true
                            displayProgress = actualProgress
                            // 次回のアニメーションを有効にする
                            kotlinx.coroutines.delay(50)
                            skipAnimation = false
                        } else {
                            // 通常の経験値増加
                            displayProgress = actualProgress
                        }
                    }

                    // アニメーション付き進捗
                    val animatedProgress by animateFloatAsState(
                        targetValue = displayProgress,
                        animationSpec =
                        if (skipAnimation) {
                            tween(durationMillis = 0) // 即座に変更
                        } else {
                            tween(durationMillis = 800) // 通常のアニメーション
                        },
                        label = "experience_progress_remindnet"
                    )

                    Box(
                        modifier =
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = animatedProgress)
                            .background(
                                brush =
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors =
                                    listOf(
                                        Secondary.copy(alpha = 0.8f),
                                        Secondary
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
        }
    }
}
