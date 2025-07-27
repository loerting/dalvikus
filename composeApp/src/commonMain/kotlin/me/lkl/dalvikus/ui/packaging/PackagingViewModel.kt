package me.lkl.dalvikus.ui.packaging

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import co.touchlab.kermit.Logger
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import com.android.apksig.KeyConfig
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.ui.snackbar.showSnackbarError
import me.lkl.dalvikus.ui.snackbar.showSnackbarMessage
import me.lkl.dalvikus.ui.snackbar.snackbarHostState
import java.io.File
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit

class PackagingViewModel() {
    private val _keystorePassword = MutableStateFlow("")
    val keystorePassword: StateFlow<String> = _keystorePassword

    private val _keyPassword = MutableStateFlow("")
    val keyPassword: StateFlow<String> = _keyPassword

    fun updateKeystorePassword(value: String) {
        _keystorePassword.value = value
    }

    fun updateKeyPassword(value: String) {
        _keyPassword.value = value
    }
    suspend fun checkSignature(apk: File): ApkVerifier.Result? = withContext(Dispatchers.IO) {
        try {
            val verifier = ApkVerifier.Builder(apk).build()
            verifier.verify()
        } catch (e: Exception) {
            Logger.e("Error verifying APK signature: ${e.message}", e)
            null
        }
    }

    suspend fun signApk(
        keystoreInfo: KeystoreInfo,
        outputApk: File,
        apk: File,
        onError: (Throwable) -> Unit,
        onSuccess: (ApkVerifier.Result) -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            val keystoreFile = keystoreInfo.keystoreFile
            val keyAlias = keystoreInfo.keyAlias
            val keystorePassword = keystoreInfo.keystorePassword.value.toCharArray()
            val keyPassword = keystoreInfo.keyPassword.value.toCharArray()
            // Load keystore
            val ks = KeyStore.getInstance("JKS").apply {
                load(keystoreFile.inputStream(), keystorePassword)
            }

            val privateKey = ks.getKey(keyAlias, keyPassword) as? PrivateKey
                ?: throw IllegalArgumentException("Private key not found or wrong password")

            val certs = ks.getCertificateChain(keyAlias)
                ?.map { it as X509Certificate }
                ?.toTypedArray() ?: throw IllegalArgumentException("Certificate chain missing")


            val tempOutputApk = File.createTempFile("signed_", ".apk")

            val signer =
                ApkSigner.Builder(listOf(ApkSigner.SignerConfig.Builder("signer", KeyConfig.Jca(privateKey), listOf(*certs)).build()))
                    .setInputApk(apk)
                    .setOutputApk(tempOutputApk)
                    .setV1SigningEnabled(true)
                    .setV2SigningEnabled(true)
                    .setV3SigningEnabled(true)
                    .build()

            signer.sign()

            val verifier = ApkVerifier.Builder(tempOutputApk).build()
            val result = verifier.verify()
            if (result.isVerified) {
                // Move the signed APK to the final output location
                tempOutputApk.copyTo(outputApk, overwrite = true)
                tempOutputApk.delete()
                onSuccess(result)
            } else {
                onError(Exception("APK signature verification failed: ${result.errors}"))
            }
        } catch (e: Exception) {
            Logger.e("Error signing APK: ${e.message}", e)
            onError(e)
        }
    }

    fun openConsoleCreateKeystore(scope: CoroutineScope, keystorePw: String, keyPw: String) {
        Logger.i("Opening console to create keystore...")

        val keystoreFile = dalvikusSettings["keystore_file"] as File
        val keyAlias = dalvikusSettings["key_alias"] as String
        val command = listOf(
            "keytool", "-genkeypair",
            "-v",
            "-keystore", keystoreFile.absolutePath,
            "-keyalg", "RSA",
            "-keysize", "2048",
            "-validity", "10000",
            "-alias", keyAlias,
            "-storepass", keystorePw,
            "-keypass", keyPw,
            "-dname", "CN=dalvikus"
        )

        try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            var output = process.inputStream.bufferedReader().use { it.readText() }
            Logger.i("Keytool output:\n$output")

            val exitCode = process.waitFor()

            if (exitCode == 2) {
                output += "\nPlease make sure keytool is installed."
            }

            Logger.i("Keytool process exited with code $exitCode")
            showSnackbarMessage(output)
        } catch (e: Exception) {
            Logger.e("Failed to run keytool", e)
            showSnackbarError(e)
        }
    }

    suspend fun deployApk(
        apk: File,
        onError: (Throwable) -> Unit,
        onSuccess: () -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            AndroidDebugBridge.init(false)
            val adbLocation = dalvikusSettings["adb_path"] as File
            if (!adbLocation.exists() || !adbLocation.canExecute()) {
                throw Exception("ADB executable not found or not executable: ${adbLocation.absolutePath}. Change the path in settings.")
            }
            val bridge = AndroidDebugBridge.createBridge(adbLocation.absolutePath, false, 10000, TimeUnit.MILLISECONDS)

            if(!bridge.isConnected) {
                throw Exception("Failed to connect to Android Debug Bridge. Ensure adb is installed.")
            }

            // Wait up to 10 seconds for devices to appear
            var attempts = 0
            var devices: Array<IDevice> = emptyArray()
            while (attempts < 20) { // 20 * 500ms = 10s
                devices = bridge.devices
                if (devices.isNotEmpty()) break
                delay(500)
                attempts++
            }

            if (devices.isEmpty()) {
                throw Exception("No connected Android devices found. Enable USB debugging and connect a device.")
            }

            // Install APK on all devices
            for (device in devices) {
                try {
                    // The second param 'true' means reinstall if app exists
                    device.installPackage(apk.absolutePath, true)
                } catch (e: Exception) {
                    throw Exception("Failed to install APK on device ${device.serialNumber}: ${e.message}", e)
                }
            }
            // TODO launch the app (on all devices). this requires the package name.

            onSuccess()
        } catch (e: Exception) {
            onError(e)
        } finally {
            AndroidDebugBridge.disconnectBridge(1000, TimeUnit.MILLISECONDS)
            AndroidDebugBridge.terminate()
        }
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
    fun isValid(): Boolean {
        val keystorePassword by keystorePassword.collectAsState()
        val keyPassword by keyPassword.collectAsState()

        return keystoreFile.exists() && keystoreFile.canRead() &&
                keyAlias.isNotBlank() &&
                keystorePassword.length >= 6 &&
                keyPassword.length >= 6
    }
}