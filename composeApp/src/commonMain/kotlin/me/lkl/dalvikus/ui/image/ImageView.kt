package me.lkl.dalvikus.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.mouse.mouseScrollFilter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.tabs.ImageTab
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun ImageView(tab: ImageTab) {
    val imageByteArray by tab.contentProvider.contentFlow.collectAsState()
    var image by remember(imageByteArray) { mutableStateOf<ImageBitmap?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(tab) {
        tab.contentProvider.loadContent()
    }

    LaunchedEffect(imageByteArray) {
        image = withContext(Dispatchers.Default) {
            runCatching {
                SkiaImage.makeFromEncoded(imageByteArray).toComposeImageBitmap()
            }.getOrNull()
        }
        scale = 1f
        offset = Offset.Zero
    }

    val gestureModifier = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.type == PointerEventType.Scroll) {
                    val scrollDelta = event.changes.first().scrollDelta
                    val zoomFactor1 = 1.1f
                    scale = if (scrollDelta.y > 0) {
                        (scale * zoomFactor1).coerceIn(0.1f, 10f)
                    } else {
                        (scale / zoomFactor1).coerceIn(0.1f, 10f)
                    }
                    if (true) {
                        event.changes.first().consume()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        image?.let { img ->
            val imageWidth = img.width
            val imageHeight = img.height

            Box(
                modifier = gestureModifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .align(Alignment.Center)
            ) {
                Image(
                    bitmap = img,
                    filterQuality = FilterQuality.None,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            Text(
                text = "$imageWidth x $imageHeight",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        } ?: run {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
