package me.lkl.dalvikus.decompiler

import com.android.tools.smali.dexlib2.iface.ClassDef

interface Decompiler {
    suspend fun decompile(classDef: ClassDef): String
}