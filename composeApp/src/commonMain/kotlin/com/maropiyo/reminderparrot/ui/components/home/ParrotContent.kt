package com.maropiyo.reminderparrot.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.presentation.state.ParrotState
import org.jetbrains.compose.resources.painterResource
import reminderparrot.composeapp.generated.resources.Res
import reminderparrot.composeapp.generated.resources.reminko

/**
 * インココンテンツ
 * ホーム画面内のインコ関連コンテンツを管理するコンポーネント
 *
 * @param modifier 修飾子
 */
@Composable
fun ParrotContent(
    state: ParrotState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.reminko),
            contentDescription = "Reminko Parrot",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )
    }
}
