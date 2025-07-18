package me.lkl.dalvikus.ui.nav

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

data class NavItem(
    val key: String,
    val icon: ImageVector,
    val labelRes: StringResource
)