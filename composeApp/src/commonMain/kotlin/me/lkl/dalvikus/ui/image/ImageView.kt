package me.lkl.dalvikus.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeImageBitmap
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
        detectTransformGestures { _, pan, zoom, _ ->
            scale = (scale * zoom).coerceIn(0.1f, 10f)
            offset += pan
        }
    }

    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier.fillMaxSize().background(colors.background)
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
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(colors.surfaceVariant)
                )
            }

            Text(
                text = "$imageWidth x $imageHeight",
                color = colors.onBackground,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(colors.surface.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        } ?: run {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colors.primary
            )
        }
    }
}
