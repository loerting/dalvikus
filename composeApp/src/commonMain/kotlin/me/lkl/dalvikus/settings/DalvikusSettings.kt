package me.lkl.dalvikus.settings

import com.android.tools.smali.dexlib2.Opcodes
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import dalvikus.composeapp.generated.resources.*
import java.io.File
import java.net.URI
import java.util.prefs.Preferences

class DalvikusSettings() {

    private val preferences = Preferences.userRoot().node("me.lkl.dalvikus.settings")
    private val settings: Settings = PreferencesSettings(preferences)

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
        // TODO actually implement CFR decompiler.
        StringOptionSetting(
            key = "decompiler_implementation",
            category = SettingsCategory.DECOMPILER,
            nameRes = Res.string.settings_decompiler_implementation,
            "jadx",
            options = listOf(Pair("jadx", "JADX 1.5.2"), Pair("cfr", "CFR 0.152"))
        ),
        BooleanSetting(
            key = "decompiler_verbose",
            category = SettingsCategory.DECOMPILER,
            nameRes = Res.string.settings_decompiler_verbose,
            defaultValue = false
        ),
        FileSetting(
            key = "keystore_file",
            category = SettingsCategory.SIGNING,
            nameRes = Res.string.settings_keystore_path,
            defaultPath = File(System.getProperty("user.home"), ".dalvikus.keystore").absolutePath,
            extensions = listOf("jks", "keystore"),
        ),
        StringSetting(
            key = "key_alias",
            category = SettingsCategory.SIGNING,
            nameRes = Res.string.settings_keystore_alias,
            defaultValue = "dalvikus"
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
            else -> throw IllegalArgumentException("Unsupported setting type for key: $key")
        }

        return value as? T
            ?: throw IllegalArgumentException("Expected type ${T::class}, but got ${value::class}")
    }

    fun groupedByCategory(): Map<SettingsCategory, List<Setting<*>>> {
        return settingsList.groupBy { it.category }
    }

    companion object {
        fun getRepoURI(): URI {
            return URI("https://github.com/loerting/dalvikus")
        }
    }
}
