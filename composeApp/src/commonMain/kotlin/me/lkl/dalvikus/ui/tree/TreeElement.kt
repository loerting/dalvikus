package me.lkl.dalvikus.ui.tree

import androidx.compose.ui.graphics.vector.ImageVector

interface TreeElement {
    val name: String
    val icon: ImageVector
    val isContainer: Boolean
    suspend fun getChildren(): List<TreeElement>
}