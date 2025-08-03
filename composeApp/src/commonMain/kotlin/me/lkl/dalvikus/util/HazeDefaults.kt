package me.lkl.dalvikus.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint

@Composable
fun defaultHazeStyle(
    containerColor: Color = MaterialTheme.colorScheme.surface,
): HazeStyle = hazeMaterial(
    containerColor = containerColor,
    lightAlpha = 0.1f,
    darkAlpha = 0.3f,
)

private fun hazeMaterial(
    containerColor: Color,
    lightAlpha: Float,
    darkAlpha: Float,
): HazeStyle = HazeStyle(
    blurRadius = 10.dp,
    backgroundColor = containerColor,
    tint = HazeTint(
        containerColor.copy(alpha = if (containerColor.luminance() >= 0.5) lightAlpha else darkAlpha),
    ),
)