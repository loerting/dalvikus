package me.lkl.dalvikus.ui.packaging

import co.touchlab.kermit.Logger
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import me.lkl.dalvikus.tree.TreeElement
import java.io.File
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

class PackagingViewModel() {
    fun checkSignature(apk: File): ApkVerifier.Result? {
        val verifier: ApkVerifier = ApkVerifier.Builder(apk).build()
        return verifier.verify()
    }

    fun signApk(
        keystoreFile: File,
        keystorePassword: CharArray,
        keyAlias: String,
        keyPassword: CharArray,
        outputApk: File,
        apk: File
    ): ApkVerifier.Result? {
        try {
            // Load keystore
            val ks = KeyStore.getInstance("JKS").apply {
                load(keystoreFile.inputStream(), keystorePassword)
            }

            val privateKey = ks.getKey(keyAlias, keyPassword) as? PrivateKey
                ?: throw IllegalArgumentException("Private key not found or wrong password")

            val certs = ks.getCertificateChain(keyAlias)
                ?.map { it as X509Certificate }
                ?.toTypedArray() ?: throw IllegalArgumentException("Certificate chain missing")

            val signer = ApkSigner.Builder(listOf(ApkSigner.SignerConfig.Builder("signer", privateKey, listOf(*certs)).build()))
                .setInputApk(apk)
                .setOutputApk(outputApk)
                .setV1SigningEnabled(true)
                .setV2SigningEnabled(true)
                .setV3SigningEnabled(true)
                .build()

            signer.sign()

            val verifier = ApkVerifier.Builder(outputApk).build()
            return verifier.verify()
        } catch (e: Exception) {
            Logger.e("Error signing APK: ${e.message}", e)
        }
        return null
    }
}
