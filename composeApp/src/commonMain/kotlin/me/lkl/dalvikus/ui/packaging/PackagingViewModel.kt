package me.lkl.dalvikus.ui.packaging

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.apksig.ApkVerifier
import me.lkl.dalvikus.tree.archive.ArchiveTreeNode


class PackagingViewModel(val apk: ArchiveTreeNode?) {
    public var signatureResult: ApkVerifier.Result? by mutableStateOf(null)

    fun checkSignature() {
        if (apk == null) {
            signatureResult = null
            return
        }
        val verifier: ApkVerifier = ApkVerifier.Builder(apk.file).build()
        signatureResult = verifier.verify()
    }
}
