package com.maropiyo.reminderparrot

import androidx.compose.runtime.Composable
import com.maropiyo.reminderparrot.ui.screens.HomeScreen
import com.maropiyo.reminderparrot.ui.theme.ReminderParrotTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    ReminderParrotTheme {
        HomeScreen()
    }
}
