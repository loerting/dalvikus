package me.lkl.dalvikus.tools

import co.touchlab.kermit.Logger
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import com.android.apksig.KeyConfig
import kotlinx.coroutines.*
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.ui.packaging.KeystoreInfo
import me.lkl.dalvikus.ui.snackbar.SnackbarManager
import java.io.File
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.cert.X509Certificate

class ApkSigner(val snackbarManager: SnackbarManager) {
    suspend fun checkSignature(apk: File): ApkVerifier.Result? = withContext(Dispatchers.IO) {
        try {
            val verifier = ApkVerifier.Builder(apk).build()
            val verify = verifier.verify()
            Logger.i("APK signature verification result for ${apk.absolutePath}: ${verify.isVerified}, errors: ${verify.errors}")
            verify
        } catch (e: Exception) {
            Logger.e("Error verifying APK signature: ${e.message}", e)
            snackbarManager?.showError(e)
            null
        }
    }

    suspend fun signApk(
        keystoreInfo: KeystoreInfo,
        outputApk: File,
        apk: File,
        onFinish: (success: Boolean) -> Unit
    ) = withContext(Dispatchers.Default) {
        try {
            val keystoreFile = keystoreInfo.keystoreFile
            val keyAlias = keystoreInfo.keyAlias
            val keystorePassword = keystoreInfo.keystorePassword

            val ks = KeyStore.getInstance("JKS").apply {
                load(keystoreFile.inputStream(), keystorePassword)
            }

            val privateKey = ks.getKey(keyAlias, keystorePassword) as? PrivateKey
                ?: throw IllegalArgumentException("Private key not found or wrong password")

            val certs = ks.getCertificateChain(keyAlias)
                ?.map { it as X509Certificate }
                ?.toTypedArray()
                ?: throw IllegalArgumentException("Certificate chain missing")

            zipAlignIfNeeded(apk)

            val tempOutputApk = File.createTempFile("signed_", ".apk")

            val signer = ApkSigner.Builder(
                listOf(
                    ApkSigner.SignerConfig.Builder(
                        "signer",
                        KeyConfig.Jca(privateKey),
                        listOf(*certs)
                    ).build()
                )
            )
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
                tempOutputApk.copyTo(outputApk, overwrite = true)
                onFinish(true)
            } else {
                Logger.e("APK signature verification failed: ${result.errors}")
                onFinish(false)
            }

            if (tempOutputApk.exists()) {
                tempOutputApk.delete()
            }
        } catch (e: Exception) {
            Logger.e("Error signing APK: ${e.message}", e)
            snackbarManager?.showError(e)
            onFinish(false)
        }
    }


    private suspend fun zipAlignIfNeeded(apk: File) {
        val zipAlignPath = dalvikusSettings["zipalign_path"] as? File
            ?: throw IllegalStateException("Zipalign path is not set in settings.")

        if (!zipAlignPath.exists() || !zipAlignPath.canExecute()) {
            throw IllegalStateException("Zipalign executable not found or not executable: ${zipAlignPath.absolutePath}.")
        }

        val alignedApk = File.createTempFile("aligned", ".apk", apk.parentFile)

        try {
            withTimeout(10_000) {
                val command = listOf(
                    zipAlignPath.absolutePath,
                    "-v", "-f",
                    "4",
                    apk.absolutePath,
                    alignedApk.absolutePath
                )
                val process = ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start()

                val output = process.inputStream.bufferedReader().use { it.readText() }
                val result = process.waitFor()

                if (result != 0) {
                    throw RuntimeException("zipalign failed: $output")
                }
            }

            Logger.i("APK zipaligned successfully: ${alignedApk.absolutePath}")

            alignedApk.copyTo(apk, overwrite = true)
            alignedApk.delete()
        } catch (e: Exception) {
            Logger.e("Failed to zipalign APK: ${e.message}", e)
            snackbarManager.showError(e)
        }
    }
}

fun ByteArray.sha256Fingerprint(): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(this)
    return digest.joinToString(":") { "%02X".format(it) }
}