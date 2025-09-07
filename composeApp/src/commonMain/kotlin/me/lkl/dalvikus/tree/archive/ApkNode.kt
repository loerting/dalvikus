package me.lkl.dalvikus.tree.archive

import brut.androlib.exceptions.AndrolibException
import brut.androlib.meta.ApkInfo
import brut.androlib.res.data.ResID
import brut.androlib.res.data.ResResSpec
import brut.androlib.res.data.ResTable
import brut.directory.ExtFile
import co.touchlab.kermit.Logger
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.backing.Backing
import me.lkl.dalvikus.util.getApkToolConfig
import java.io.File

class ApkNode(
    override val name: String,
    override val backing: Backing,
    override val parent: ContainerNode?
) : ZipNode(name, backing, parent) {

    private var _resTable: ResTable? = null
    private var apkFile: File? = null

    val resTable: ResTable
        get() = _resTable ?: throw IllegalStateException("Resources not loaded. Call loadChildrenInternal() first.")

    init {
        require(name.endsWith(".apk", ignoreCase = true))
    }

    override suspend fun loadChildrenInternal(): List<Node> {
        apkFile = backing.getFileOrCreateTemp(".apk")

        // Initialize ResTable with the APK file
        _resTable = ResTable(ApkInfo(ExtFile(apkFile!!)), getApkToolConfig())

        try {
            if(!resTable.isMainPackageLoaded && resTable.apkInfo.hasResources())
                resTable.loadMainPackage()
        } catch (ale: AndrolibException) {
            Logger.e("Failed to load resources from APK: ${ale.message}", ale)
        }

        return super.loadChildrenInternal()
    }

    fun getAndroidPackageName(): String? {
        if (_resTable?.isMainPackageLoaded != true) return null
        return resTable.mainPackage.name
    }

    fun getResourceSpecs(): List<ResResSpec?>? {
        if (_resTable?.isMainPackageLoaded != true) return null
        return resTable.mainPackage.listResSpecs()
    }

    fun getResourceById(unsignedValue: Int): ResResSpec? {
        if (_resTable?.isMainPackageLoaded != true) return null
        val resId = ResID(unsignedValue)
        val resSpec = resTable.mainPackage.getResSpec(resId)
        return resSpec
    }
}