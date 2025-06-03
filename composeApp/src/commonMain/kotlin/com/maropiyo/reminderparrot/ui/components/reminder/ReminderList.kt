package com.maropiyo.reminderparrot.ui.components.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.ui.theme.Secondary
import com.maropiyo.reminderparrot.ui.theme.White

@Composable
fun ReminderList(reminders: List<Reminder>, onToggleCompletion: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier =
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(reminders) { reminder ->
            ReminderCard(
                reminder = reminder,
                onToggleCompletion = { onToggleCompletion(reminder.id) },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ReminderCard(reminder: Reminder, onToggleCompletion: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = White
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // テキスト
            Text(
                text = reminder.text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Secondary,
                textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
            )

            // 丸いチェックボックス
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = { onToggleCompletion() },
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                colors = CheckboxDefaults.colors(
                    checkedColor = Secondary,
                    uncheckedColor = Secondary,
                    checkmarkColor = White
                )
            )
        }
    }
}
