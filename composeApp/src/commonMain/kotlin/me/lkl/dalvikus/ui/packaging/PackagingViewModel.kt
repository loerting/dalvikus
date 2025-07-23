package me.lkl.dalvikus.ui.packaging

import co.touchlab.kermit.Logger
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import com.android.apksig.KeyConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

class PackagingViewModel() {
    suspend fun checkSignature(apk: File): ApkVerifier.Result? = withContext(Dispatchers.IO) {
        try {
            val verifier = ApkVerifier.Builder(apk).build()
            verifier.verify()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun signApk(
        keystoreFile: File,
        keystorePassword: CharArray,
        keyAlias: String,
        keyPassword: CharArray,
        outputApk: File,
        apk: File,
        onError: (Throwable) -> Unit,
        onSuccess: (ApkVerifier.Result) -> Unit,
    ) = withContext(Dispatchers.IO) {
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

            val signer =
                ApkSigner.Builder(listOf(ApkSigner.SignerConfig.Builder("signer", KeyConfig.Jca(privateKey), listOf(*certs)).build()))
                    .setInputApk(apk)
                    .setOutputApk(outputApk)
                    .setV1SigningEnabled(true)
                    .setV2SigningEnabled(true)
                    .setV3SigningEnabled(true)
                    .build()

            signer.sign()

            val verifier = ApkVerifier.Builder(outputApk).build()
            val result = verifier.verify()
            if (result.isVerified) {
                onSuccess(result)
            } else {
                onError(Exception("APK signature verification failed: ${result.errors}"))
            }
        } catch (e: Exception) {
            Logger.e("Error signing APK: ${e.message}", e)
            onError(e)
        }
    }
}
