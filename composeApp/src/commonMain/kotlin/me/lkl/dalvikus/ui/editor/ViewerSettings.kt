package me.lkl.dalvikus.ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ViewerSettings {
    var fontSize by mutableStateOf(14.sp)
    val fontSizeDp get() = fontSize.value.dp
}