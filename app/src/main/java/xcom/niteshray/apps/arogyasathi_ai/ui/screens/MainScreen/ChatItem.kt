package xcom.niteshray.apps.arogyasathi_ai.ui.screens.MainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatBubble(message: String, isUser: Boolean) {
    val brush = if (isUser) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF1E90FF),
                Color(0xFF42A0FD)
            ),
            startX = 0f,
            endX = Float.POSITIVE_INFINITY
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF383838),
                Color(0xFF383838),
            ),
            startX = 0f,
            endX = Float.POSITIVE_INFINITY
        )
    }
    val alignment = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}