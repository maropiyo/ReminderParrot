package com.maropiyo.reminderparrot.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 形状定義
 */
val Shapes =
    Shapes(
        // カード、ボタンなどの角丸
        small = RoundedCornerShape(8.dp),
        // コンテナの角丸
        medium = RoundedCornerShape(12.dp),
        // モーダルシートなどの大きい要素の角丸
        large = RoundedCornerShape(16.dp)
    )
