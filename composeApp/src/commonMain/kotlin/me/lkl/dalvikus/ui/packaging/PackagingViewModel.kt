package me.lkl.dalvikus.ui.packaging

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tools.AdbDeployer
import me.lkl.dalvikus.tools.ApkSigner
import me.lkl.dalvikus.tools.Keytool
import me.lkl.dalvikus.ui.snackbar.SnackbarManager
import java.io.File

class PackagingViewModel(snackbarManager: SnackbarManager) {
    val adbDeployer = AdbDeployer(snackbarManager)
    val apkSigner = ApkSigner(snackbarManager)
    val keytool = Keytool(snackbarManager)
}


var keystorePasswordField by mutableStateOf(CharArray(0))

fun getKeystoreInfo(): KeystoreInfo {
    val keystoreFile = dalvikusSettings["keystore_file"] as File
    val keyAlias = dalvikusSettings["key_alias"] as String

    return KeystoreInfo(
        keystoreFile = keystoreFile,
        keyAlias = keyAlias,
        keystorePassword = keystorePasswordField
    )
}

data class KeystoreInfo(
    val keystoreFile: File,
    val keyAlias: String,
    val keystorePassword: CharArray
) {
    fun seemsValid(): Boolean {
        return keystoreFile.exists() && keystoreFile.canRead() &&
                keyAlias.isNotBlank()
    }

    fun isPasswordFilled(): Boolean {
        return keystorePassword.size >= 6
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeystoreInfo

        if (keystoreFile != other.keystoreFile) return false
        if (keyAlias != other.keyAlias) return false
        if (!keystorePassword.contentEquals(other.keystorePassword)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keystoreFile.hashCode()
        result = 31 * result + keyAlias.hashCode()
        result = 31 * result + keystorePassword.contentHashCode()
        return result
    }
}