package me.lkl.dalvikus.tools

import co.touchlab.kermit.Logger
import com.android.ddmlib.AdbCommandRejectedException
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.InstallException
import com.android.ddmlib.NullOutputReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.snackbarManager
import java.io.File
import java.util.concurrent.TimeUnit

class AdbDeployer() {

    suspend fun deployApk(
        apk: File,
        packageName: String? = null,
        onFinish: (Boolean) -> Unit
    ) = withContext(Dispatchers.Default) {
        try {
            val bridge = initializeAdbBridge()
            if (!waitForConnection(bridge)) {
                throw IllegalStateException("Timeout while waiting for device connection.")
            }

            val devices = bridge.devices
            if (devices.isEmpty()) {
                throw IllegalStateException("No connected Android devices found.")
            }

            notifyDevicesFound(devices)

            installApkOnDevices(devices, apk)

            packageName?.let {
                launchAppOnDevices(devices, it)
            } ?: snackbarManager?.showMessage("APK installed, but no package name provided to launch the app.")

            onFinish(true)
        } catch (timeout: TimeoutCancellationException) {
            Logger.e("Deployment timed out: ${timeout.message}", timeout)
            snackbarManager?.showMessage("Operation timed out. Please check your device connection and try again.")
            onFinish(false)
        } catch (e: Exception) {
            Logger.e("Error deploying APK: ${e.message}", e)
            snackbarManager?.showError(e)
            onFinish(false)
        } finally {
            cleanupAdbBridge()
        }
    }

    private fun initializeAdbBridge(): AndroidDebugBridge {
        AndroidDebugBridge.init(false)

        val adbExecutable = dalvikusSettings["adb_path"] as? File
            ?: throw IllegalStateException("ADB path is not set in settings.")

        if (!adbExecutable.exists() || !adbExecutable.canExecute()) {
            throw IllegalStateException("ADB executable not found or not executable: ${adbExecutable.absolutePath}.")
        }

        return AndroidDebugBridge.createBridge(adbExecutable.absolutePath, false, 10000, TimeUnit.MILLISECONDS)
                ?: throw IllegalStateException("Failed to create ADB bridge.")
    }

    private suspend fun waitForConnection(bridge: AndroidDebugBridge): Boolean {
        repeat(20) { // check every 500ms for 10s total
            if (bridge.isConnected && bridge.hasInitialDeviceList() && bridge.devices.isNotEmpty()) return true
            delay(500)
        }
        return false
    }

    private fun notifyDevicesFound(devices: Array<IDevice>) {
        val message = "Device(s) found: ${devices.joinToString { it.serialNumber }}"
        snackbarManager?.showMessage(message)
    }

    private suspend fun installApkOnDevices(devices: Array<IDevice>, apk: File) {
        for (device in devices) {
            try {
                withTimeout(15_000) {
                    device.installPackage(apk.absolutePath, true)
                }
            } catch (ie: InstallException) {
                val cause = ie.cause
                if (cause is AdbCommandRejectedException && !cause.isDeviceOffline) {
                    snackbarManager?.showMessage("Check for a confirmation dialog on the device.")
                } else {
                    throw Exception("Failed to install APK on ${device.serialNumber}: ${ie.message}", cause ?: ie)
                }
            }
        }
    }

    private suspend fun launchAppOnDevices(devices: Array<IDevice>, packageName: String) {
        for (device in devices) {
            try {
                withTimeout(5_000) {
                    val launchCommand = "monkey -p $packageName -c android.intent.category.LAUNCHER 1"
                    device.executeShellCommand(launchCommand, NullOutputReceiver())
                }
            } catch (e: Exception) {
                snackbarManager?.showMessage("Failed to launch app on ${device.serialNumber}: ${e.message}")
            }
        }
    }

    private fun cleanupAdbBridge() {
        AndroidDebugBridge.disconnectBridge(1000, TimeUnit.MILLISECONDS)
        AndroidDebugBridge.terminate()
    }
}
