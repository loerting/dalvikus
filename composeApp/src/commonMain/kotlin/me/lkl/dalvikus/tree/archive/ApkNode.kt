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
import me.lkl.dalvikus.util.getApkToolConfig
import java.io.File

class ApkNode(
    override val name: String,
    override val zipFile: File,
    override val parent: ContainerNode?
) : ZipNode(name, zipFile, parent) {

    val resTable: ResTable = ResTable(ApkInfo(ExtFile(zipFile)), getApkToolConfig())

    init {
        require(name.endsWith(".apk", ignoreCase = true))
    }

    override suspend fun loadChildrenInternal(): List<Node> {
        try {
            if(!resTable.isMainPackageLoaded && resTable.apkInfo.hasResources())
                resTable.loadMainPackage()
        } catch (ale: AndrolibException) {
            Logger.e("Failed to load resources from APK: ${ale.message}", ale)
        }

        return super.loadChildrenInternal()
    }

    fun getAndroidPackageName(): String? {
        if (!resTable.isMainPackageLoaded) return null
        return resTable.mainPackage.name
    }

    fun getResourceSpecs(): List<ResResSpec?>? {
        if (!resTable.isMainPackageLoaded) return null
        return resTable.mainPackage.listResSpecs()
    }
    fun getResourceById(unsignedValue: Int): ResResSpec? {
        if (!resTable.isMainPackageLoaded) return null
        val resId = ResID(unsignedValue)
        val resSpec = resTable.mainPackage.getResSpec(resId)
        return resSpec
    }
}

