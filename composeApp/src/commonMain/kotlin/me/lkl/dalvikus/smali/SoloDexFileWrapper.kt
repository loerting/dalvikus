package me.lkl.dalvikus.smali

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.DexFile
import me.lkl.dalvikus.dalvikusSettings

class SoloDexFileWrapper(
    private val classDef: ClassDef
) : DexFile {
    override fun getClasses(): Set<ClassDef> = setOf(classDef)
    override fun getOpcodes(): Opcodes = Opcodes.forApi(dalvikusSettings["api_level"] as Int)
}