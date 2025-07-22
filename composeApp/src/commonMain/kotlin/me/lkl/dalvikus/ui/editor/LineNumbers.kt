package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.theme.JetBrainsMono


@Composable
fun LineNumberColumn(
    code: String,
    scrollState: ScrollState,
    fontSize: TextUnit,
) {
    val lines = code.lines().size
    val lineHeightDp = with(LocalDensity.current) { fontSize.toDp() * 1.5f }
    val maxNumDigits = lines.toString().length

    DisableSelection {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(top = 8.dp, end = 8.dp)
                .width(IntrinsicSize.Min)
        ) {
            repeat(lines) { i ->

                Box(modifier = Modifier.height(lineHeightDp)) {
                    // to account for the width of the line numbers
                    LineNumber(
                        number = "9".repeat(maxNumDigits),
                        modifier = Modifier.alpha(0f),
                        fontSize = fontSize,
                    )
                    LineNumber(
                        number = "${i + 1}",
                        modifier = Modifier.align(Alignment.CenterEnd),
                        fontSize = fontSize,
                    )
                }

            }
        }
    }
}

@Composable
private fun LineNumber(number: String, modifier: Modifier, fontSize: TextUnit) {
    androidx.compose.material.Text(
        text = number,
        fontFamily = JetBrainsMono(),
        fontSize = fontSize,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        modifier = modifier.padding(start = 12.dp)
    )
}