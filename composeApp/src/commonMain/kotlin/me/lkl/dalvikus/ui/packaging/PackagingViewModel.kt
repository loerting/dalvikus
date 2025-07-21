package me.lkl.dalvikus.ui.packaging

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import me.lkl.dalvikus.tree.archive.ArchiveTreeNode
import java.io.File
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

class PackagingViewModel(val apk: ArchiveTreeNode?) {
    var signatureResult: ApkVerifier.Result? by mutableStateOf(null)
    var signSuccess: Boolean by mutableStateOf(false)
    var signError: String? by mutableStateOf(null)

    fun checkSignature() {
        if (apk == null) {
            signatureResult = null
            return
        }
        val verifier: ApkVerifier = ApkVerifier.Builder(apk.file).build()
        signatureResult = verifier.verify()
    }

    fun signApk(
        keystoreFile: File,
        keystorePassword: CharArray,
        keyAlias: String,
        keyPassword: CharArray,
        outputApk: File
    ) {
        try {
            if (apk == null) {
                signError = "APK not loaded"
                signSuccess = false
                return
            }

            // Load keystore
            val ks = KeyStore.getInstance("JKS").apply {
                load(keystoreFile.inputStream(), keystorePassword)
            }

            val privateKey = ks.getKey(keyAlias, keyPassword) as? PrivateKey
                ?: throw IllegalArgumentException("Private key not found or wrong password")

            val certs = ks.getCertificateChain(keyAlias)
                ?.map { it as X509Certificate }
                ?.toTypedArray() ?: throw IllegalArgumentException("Certificate chain missing")

            // Sign the APK
            val signer = ApkSigner.Builder(listOf(ApkSigner.SignerConfig.Builder("signer", privateKey, listOf(*certs)).build()))
                .setInputApk(apk.file)
                .setOutputApk(outputApk)
                .setV1SigningEnabled(true)
                .setV2SigningEnabled(true)
                .setV3SigningEnabled(true)
                .build()

            signer.sign()

            // Re-verify
            val verifier = ApkVerifier.Builder(outputApk).build()
            signatureResult = verifier.verify()

            signSuccess = signatureResult?.isVerified == true
            signError = null

        } catch (e: Exception) {
            signSuccess = false
            signError = "Signing failed: ${e.message}"
            e.printStackTrace()
        }
    }
}
