package me.lkl.dalvikus.settings

import com.android.tools.smali.dexlib2.Opcodes
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import dalvikus.composeapp.generated.resources.*
import jadx.core.Jadx
import me.lkl.dalvikus.tools.AndroidToolsLocator
import org.benf.cfr.reader.util.CfrVersionInfo
import java.io.File
import java.net.URI
import java.util.prefs.Preferences

class DalvikusSettings() {

    private val preferences = Preferences.userRoot().node("me.lkl.dalvikus.settings")
    private val settings: Settings = PreferencesSettings(preferences)
    private val androidToolsLocator = AndroidToolsLocator()

    val settingsList: List<Setting<*>> = listOf(
        IntSetting(
            key = "font_size",
            category = SettingsCategory.EDITOR,
            nameRes = Res.string.settings_font_size,
            defaultValue = 14,
            min = 8,
            max = 30,
            step = 1,
            unit = "sp"
        ),
        BooleanSetting(
            key = "save_automatically",
            category = SettingsCategory.EDITOR,
            nameRes = Res.string.settings_save_automatically,
            defaultValue = false
        ),
        IntSetting(
            key = "api_level",
            category = SettingsCategory.SMALI,
            nameRes = Res.string.settings_api_level,
            defaultValue = Opcodes.getDefault().api,
            min = 1,
            max = 34
        ),
        StringOptionSetting(
            key = "decompiler_implementation",
            category = SettingsCategory.DECOMPILER,
            nameRes = Res.string.settings_decompiler_implementation,
            "jadx",
            options = listOf(
                Pair("jadx", "JADX ${Jadx.getVersion()}"),
                Pair("cfr", "CFR ${CfrVersionInfo.VERSION_INFO}"),
                Pair("vineflower", "Vineflower") // TODO find a way to get Fernflower version
            )
        ),
        BooleanSetting(
            key = "decompiler_verbose",
            category = SettingsCategory.DECOMPILER,
            nameRes = Res.string.settings_decompiler_verbose,
            defaultValue = false
        ),
        FileSetting(
            key = "adb_path",
            category = SettingsCategory.SIGNING,
            nameRes = Res.string.settings_adb_path,
            dialogRes = Res.string.dialog_select_executable,
            defaultPath = androidToolsLocator.findAdb().absolutePath,
            extensions = listOf("", "exe", "bat", "sh", "cmd"),
        ),
        FileSetting(
            key = "zipalign_path",
            category = SettingsCategory.SIGNING,
            nameRes = Res.string.settings_zipalign_path,
            dialogRes = Res.string.dialog_select_executable,
            defaultPath = androidToolsLocator.findZipalign().absolutePath,
            extensions = listOf("", "exe", "bat", "sh", "cmd"),
        ),
        FileSetting(
            key = "keystore_file",
            category = SettingsCategory.SIGNING,
            nameRes = Res.string.settings_keystore_path,
            dialogRes = Res.string.dialog_select_keystore_message,
            defaultPath = File(System.getProperty("user.home"), ".dalvikus.keystore").absolutePath,
            extensions = listOf("keystore", "p12", "pfx"),
        ),
        StringSetting(
            key = "key_alias",
            category = SettingsCategory.SIGNING,
            nameRes = Res.string.settings_keystore_alias,
            defaultValue = "dalvikus",
            regex = "^[a-zA-Z0-9_\\-]+$",
        ),
    )


    init {
        loadAll()
    }

    fun loadAll() {
        settingsList.forEach { it.load(settings) }
    }

    fun saveAll() {
        settingsList.forEach { it.save(settings) }
    }

    inline operator fun <reified T> get(key: String): T {
        val setting = settingsList.find { it.key == key }
            ?: throw IllegalArgumentException("Setting not found for key: $key")

        val value = when (setting) {
            is IntSetting -> setting.value
            is BooleanSetting -> setting.value
            is StringSetting -> setting.value
            is StringOptionSetting -> setting.value
            is FileSetting -> File(setting.value)
        }

        return value as? T
            ?: throw IllegalArgumentException("Expected type ${T::class}, but got ${value::class}")
    }

    fun getSetting(key: String): Setting<*> {
        return settingsList.find { it.key == key }
            ?: throw IllegalArgumentException("Setting not found for key: $key")
    }

    fun groupedByCategory(): Map<SettingsCategory, List<Setting<*>>> {
        return settingsList.groupBy { it.category }
    }

    fun getVersion(): String {
        val property = System.getProperty("app.version")
        if (property != null && property != "null") {
            return property
        }
        // try to get implementation version from the resources
        return DalvikusSettings::class.java.`package`.implementationVersion ?: "(dev)"
    }

    companion object {
        fun getRepoURI(): URI {
            return URI("https://github.com/loerting/dalvikus")
        }
    }
}
