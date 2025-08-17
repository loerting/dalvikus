package me.lkl.dalvikus.tools

import co.touchlab.kermit.Logger
import java.io.File

/**
 * Locates the Android SDK installation
 */
class AndroidSdkLocator {

    fun findSdkPath(): File? {
        // Try environment variables first
        listOf("ANDROID_HOME", "ANDROID_SDK_ROOT").forEach { envVar ->
            System.getenv(envVar)?.let { path ->
                val sdkDir = File(path)
                if (isValidSdk(sdkDir)) {
                    Logger.i("Found Android SDK via $envVar: $path")
                    return sdkDir
                }
            }
        }

        // Search common locations
        getCommonSdkPaths().forEach { path ->
            val sdkDir = File(path)
            if (isValidSdk(sdkDir)) {
                Logger.i("Found Android SDK at: $path")
                return sdkDir
            }
        }

        Logger.w("Android SDK not found")
        return null
    }

    private fun isValidSdk(dir: File): Boolean {
        return dir.exists() && dir.isDirectory &&
                (File(dir, "platform-tools").exists() || File(dir, "build-tools").exists())
    }

    private fun getCommonSdkPaths(): List<String> {
        val userHome = System.getProperty("user.home")
        val os = System.getProperty("os.name").lowercase()

        return when {
            os.contains("win") -> listOf(
                "${System.getenv("LOCALAPPDATA")}\\Android\\Sdk",
                "$userHome\\AppData\\Local\\Android\\Sdk",
                "C:\\Android\\Sdk"
            )
            os.contains("mac") || os.contains("darwin") -> listOf(
                "$userHome/Library/Android/sdk",
                "$userHome/Android/Sdk",
                "/usr/local/android-sdk"
            )
            else -> listOf( // Linux
                "$userHome/Android/Sdk",
                "$userHome/android-sdk",
                "/usr/local/android-sdk"
            )
        }
    }

    fun isSdkInstalled(): Boolean = findSdkPath() != null
}

/**
 * Locates Android tools (adb, zipalign)
 */
class AndroidToolsLocator(private val sdkLocator: AndroidSdkLocator = AndroidSdkLocator()) {

    fun findAdb(): File? = findTool("adb", "platform-tools")

    fun findZipalign(): File? = findTool("zipalign", "build-tools")

    private fun findTool(toolName: String, sdkSubdir: String): File? {
        val isWindows = System.getProperty("os.name").lowercase().contains("win")
        val execName = if (isWindows) "$toolName.exe" else toolName

        // 1. Try Android SDK
        sdkLocator.findSdkPath()?.let { sdkPath ->
            val toolsDir = File(sdkPath, sdkSubdir)

            if (sdkSubdir == "build-tools") {
                // Find latest build-tools version
                toolsDir.listFiles()
                    ?.filter { it.isDirectory }
                    ?.maxByOrNull { it.name }
                    ?.let { versionDir ->
                        val tool = File(versionDir, execName)
                        if (tool.exists() && tool.canExecute()) {
                            Logger.i("Found $toolName in build-tools ${versionDir.name}")
                            return tool
                        }
                    }
            } else {
                // Direct lookup in platform-tools
                val tool = File(toolsDir, execName)
                if (tool.exists() && tool.canExecute()) {
                    Logger.i("Found $toolName in $sdkSubdir")
                    return tool
                }
            }
        }

        // 2. Try system PATH
        findInPath(execName)?.let {
            Logger.i("Found $toolName in PATH")
            return it
        }

        // 3. Try common system locations
        getCommonToolPaths().forEach { path ->
            val tool = File(path, execName)
            if (tool.exists() && tool.canExecute()) {
                Logger.i("Found $toolName at: $path")
                return tool
            }
        }

        Logger.w("$toolName not found")
        return null
    }

    private fun findInPath(execName: String): File? {
        val pathEnv = System.getenv("PATH") ?: return null
        val separator = if (System.getProperty("os.name").lowercase().contains("win")) ";" else ":"

        return pathEnv.split(separator)
            .map { File(it.trim(), execName) }
            .firstOrNull { it.exists() && it.canExecute() }
    }

    private fun getCommonToolPaths(): List<String> {
        val os = System.getProperty("os.name").lowercase()

        return when {
            os.contains("win") -> listOf(
                "${System.getenv("LOCALAPPDATA")}\\Android\\Sdk\\platform-tools",
                "C:\\Android\\platform-tools"
            )
            os.contains("mac") || os.contains("darwin") -> listOf(
                "/usr/local/bin",
                "/opt/homebrew/bin"
            )
            else -> listOf( // Linux
                "/usr/bin",
                "/usr/local/bin"
            )
        }
    }
}