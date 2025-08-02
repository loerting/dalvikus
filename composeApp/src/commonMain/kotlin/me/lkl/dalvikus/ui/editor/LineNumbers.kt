package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp


@Composable
fun LineNumberColumn(
    code: TextLayoutResult?,
    scrollState: ScrollState,
    textContentPadding: PaddingValues,
    textStyle: TextStyle,
) {
    // wait until the code is loaded
    if(code == null) return
    val lines = code.lineCount
    val lineHeightDp = with(LocalDensity.current) {
        textStyle.lineHeight.toDp()
    }
    val maxNumDigits = lines.toString().length

    // TODO we can do this lazily too.
    DisableSelection {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(top = textContentPadding.calculateTopPadding(),
                    bottom = textContentPadding.calculateBottomPadding(),
                    start = 8.dp,
                    end = 8.dp)
                .width(IntrinsicSize.Min)
        ) {
            repeat(lines) { i ->

                Box(modifier = Modifier.height(lineHeightDp)) {
                    // to account for the width of the line numbers
                    LineNumber(
                        number = "9".repeat(maxNumDigits),
                        modifier = Modifier.alpha(0f),
                        textStyle = textStyle
                    )
                    LineNumber(
                        number = "${i + 1}",
                        modifier = Modifier.align(Alignment.CenterEnd),
                        textStyle = textStyle,
                    )
                }

            }
        }
    }
}

@Composable
private fun LineNumber(number: String, modifier: Modifier, textStyle: TextStyle) {
    Text(
        text = number,
        style = textStyle,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        modifier = modifier
    )
}