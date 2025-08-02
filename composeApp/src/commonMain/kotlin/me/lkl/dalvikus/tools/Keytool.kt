package me.lkl.dalvikus.tools

import co.touchlab.kermit.Logger
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.snackbarManager
import java.io.File

class Keytool() {
    fun createKeystore(
        keystorePassword: String,
        distinguishedName: String = "CN=dalvikus"
    ) {
        if (!isKeytoolAvailable()) {
            snackbarManager?.showMessage("Keytool is not installed or not found in PATH.")
            return
        }

        val keystoreFile = dalvikusSettings["keystore_file"] as? File
            ?: run {
                snackbarManager?.showMessage("Keystore file not configured.")
                return
            }

        val keyAlias = dalvikusSettings["key_alias"] as? String
            ?: run {
                snackbarManager?.showMessage("Key alias not configured.")
                return
            }

        val command = listOf(
            "keytool", "-genkeypair",
            "-v",
            "-keystore", keystoreFile.absolutePath,
            "-keyalg", "RSA",
            "-keysize", "2048",
            "-validity", "10000",
            "-alias", keyAlias,
            "-storetype", "PKCS12",
            "-storepass", keystorePassword,
            "-dname", distinguishedName
        )

        runCommand(command)
    }

    private fun runCommand(command: List<String>) {
        try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            val exitCode = process.waitFor()

            Logger.i("Keytool output:\n$output")
            Logger.i("Keytool process exited with code $exitCode")

            val finalOutput = if (exitCode == 2) {
                "$output\nPlease make sure keytool is installed."
            } else output

            snackbarManager?.showMessage(finalOutput)
        } catch (e: Exception) {
            Logger.e("Failed to run keytool", e)
            snackbarManager?.showError(e)
        }
    }

    private fun isKeytoolAvailable(): Boolean {
        return try {
            val process = ProcessBuilder("keytool", "--help")
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            val exitCode = process.waitFor()

            exitCode == 0
        } catch (e: Exception) {
            Logger.e("Keytool is not installed or not found in PATH", e)
            false
        }
    }
}
