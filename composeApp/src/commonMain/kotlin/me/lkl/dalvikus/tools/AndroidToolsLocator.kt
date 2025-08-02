package me.lkl.dalvikus.tools

import co.touchlab.kermit.Logger
import java.io.File

class AndroidToolsLocator() {

    fun findZipalign(): File =
        findAndroidTool(
            envSubPath = "build-tools${File.separator}zipalign",
            fallbackPaths = mapOf(
                "win" to "Android\\build-tools\\zipalign.exe",
                "mac" to "/usr/local/bin/zipalign",
                "darwin" to "/usr/local/bin/zipalign",
                "default" to "/usr/bin/zipalign"
            ),
            toolName = "zipalign"
        )

    fun findAdb(): File =
        findAndroidTool(
            envSubPath = "platform-tools${File.separator}adb",
            fallbackPaths = mapOf(
                "win" to "Android\\platform-tools\\adb.exe",
                "mac" to "/usr/local/bin/adb",
                "darwin" to "/usr/local/bin/adb",
                "default" to "/usr/bin/adb"
            ),
            toolName = "adb"
        )

    private fun findAndroidTool(envSubPath: String, fallbackPaths: Map<String, String>, toolName: String): File {
        val os = System.getProperty("os.name").lowercase()
        val isWindows = os.contains("win")
        val sdkPath = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")

        sdkPath?.let {
            val envToolPath = File(it, envSubPath + if (isWindows) ".exe" else "")
            if (envToolPath.exists() && envToolPath.canExecute()) {
                return envToolPath
            }
            Logger.w("Found ANDROID_HOME or ANDROID_SDK_ROOT environment variable, but $toolName not found at $envToolPath. Falling back to common locations.")
        } ?: Logger.w("ANDROID_HOME or ANDROID_SDK_ROOT environment variable not set. Falling back to common locations.")

        val fallbackPath = when {
            isWindows -> fallbackPaths["win"]
            os.contains("mac") -> fallbackPaths["mac"] ?: fallbackPaths["default"]
            os.contains("darwin") -> fallbackPaths["darwin"] ?: fallbackPaths["default"]
            else -> fallbackPaths["default"]
        } ?: fallbackPaths["default"]!!

        val fallbackFile = File(
            if (isWindows && fallbackPath.contains("\\")) {
                System.getenv("ProgramFiles") ?: "C:\\Program Files"
            } else {
                ""
            },
            fallbackPath.removePrefix(if (isWindows) "" else "/")
        )

        if (fallbackFile.exists() && fallbackFile.canExecute()) {
            return fallbackFile
        }

        Logger.e("No valid $toolName installation found. Please set ANDROID_HOME or ANDROID_SDK_ROOT environment variable or specify $toolName path in settings.")
        return fallbackFile
    }
}
