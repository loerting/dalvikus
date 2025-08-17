package me.lkl.dalvikus.tools

import co.touchlab.kermit.Logger
import java.io.File

/**
 * Locates the Android SDK installation.
 */
class AndroidSdkLocator {

    fun findSdkPath(): File? {
        System.getenv().forEach { (key, value) ->
            Logger.d("Environment variable: $key = $value")
        }
        val sdkPath = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        return if (sdkPath != null) {
            val sdkDir = File(sdkPath)
            if (sdkDir.exists() && sdkDir.isDirectory) {
                sdkDir
            } else {
                Logger.e("ANDROID_HOME/ANDROID_SDK_ROOT points to invalid directory: $sdkPath")
                null
            }
        } else {
            Logger.w("ANDROID_HOME or ANDROID_SDK_ROOT environment variable not set.")
            null
        }
    }

    fun isSdkInstalled(): Boolean = findSdkPath() != null
}
