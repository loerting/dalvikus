package me.lkl.dalvikus.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.android.tools.smali.dexlib2.Opcodes

class DalvikusSettings {
    var fontSize by mutableStateOf(14.sp)
    var apiLevel = Opcodes.getDefault().api
}