package me.lkl.dalvikus.ui.packaging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tools.AdbDeployer
import me.lkl.dalvikus.tools.ApkSigner
import me.lkl.dalvikus.tools.Keytool
import java.io.File

class PackagingViewModel() {
    private val _keystorePassword = MutableStateFlow("")
    val keystorePassword: StateFlow<String> = _keystorePassword

    private val _keyPassword = MutableStateFlow("")
    val keyPassword: StateFlow<String> = _keyPassword

    val adbDeployer = AdbDeployer()
    val apkSigner = ApkSigner()
    val keytool = Keytool()

    fun updateKeystorePassword(value: String) {
        _keystorePassword.value = value
    }

    fun updateKeyPassword(value: String) {
        _keyPassword.value = value
    }

    fun getKeystoreInfo(): KeystoreInfo {
        val keystoreFile = dalvikusSettings["keystore_file"] as File
        val keyAlias = dalvikusSettings["key_alias"] as String
        val keystorePassword = _keystorePassword
        val keyPassword = _keyPassword

        return KeystoreInfo(
            keystoreFile = keystoreFile,
            keyAlias = keyAlias,
            keystorePassword = keystorePassword,
            keyPassword = keyPassword
        )
    }
}

data class KeystoreInfo(
    val keystoreFile: File,
    val keyAlias: String,
    val keystorePassword: MutableStateFlow<String>,
    val keyPassword: MutableStateFlow<String>
) {
    @Composable
    fun seemsValid(): Boolean {
        return keystoreFile.exists() && keystoreFile.canRead() &&
                keyAlias.isNotBlank()
    }

    @Composable
    fun passwordsFilled(): Boolean {
        val keystorePassword by keystorePassword.collectAsState()
        val keyPassword by keyPassword.collectAsState()

        return keystorePassword.length >= 6 && keyPassword.length >= 6
    }
}