package com.techullurgy.chess.presentation.game_room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun GameTopSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        AvatarImage()
        Timer(
            whiteTimer = "18:11",
            blackTimer = "14:47"
        )
        AvatarImage()
    }
}

@Composable
fun Timer(
    whiteTimer: String,
    blackTimer: String,
    modifier: Modifier = Modifier
) {
    val timerAnnotatedString = buildAnnotatedString {
        val activeStyle = SpanStyle(color = Color.Green, fontSize = 32.sp, fontWeight = FontWeight.Bold)

        withStyle(activeStyle) {
            append(whiteTimer)
        }
        append("/")
        append(blackTimer)
    }
    Text(
        text = timerAnnotatedString,
        fontSize = 24.sp,
        modifier = modifier
    )
}

@Composable
private fun AvatarImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.Magenta)
    )
}