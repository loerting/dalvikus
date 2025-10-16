package me.lkl.dalvikus.settings

import com.android.tools.smali.baksmali.BaksmaliOptions

class DalvikusBaksmaliOptions(private val dalvikusSettings: DalvikusSettings) : BaksmaliOptions() {

    init {
        loadSettings()
    }

    fun loadSettings() {
        apiLevel = dalvikusSettings["api_level"] as Int
        parameterRegisters = dalvikusSettings["smali_parameter_registers"] as Boolean
        localsDirective = dalvikusSettings["smali_locals_directive"] as Boolean
        sequentialLabels = dalvikusSettings["smali_sequential_labels"] as Boolean
        debugInfo = dalvikusSettings["smali_debug_info"] as Boolean
        codeOffsets = dalvikusSettings["smali_code_offsets"] as Boolean
        accessorComments = dalvikusSettings["smali_accessor_comments"] as Boolean
        allowOdex = dalvikusSettings["smali_allow_odex"] as Boolean
        deodex = dalvikusSettings["smali_deodex"] as Boolean
        implicitReferences = dalvikusSettings["smali_implicit_references"] as Boolean
        normalizeVirtualMethods = dalvikusSettings["smali_normalize_virtual_methods"] as Boolean
        //registerInfo = ... TODO maybe implement this.
    }

    fun reloadSettings() {
        loadSettings()
    }
}