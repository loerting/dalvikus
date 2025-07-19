package me.lkl.dalvikus.settings

import com.android.tools.smali.dexlib2.Opcodes
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import dalvikus.composeapp.generated.resources.*
import java.net.URI
import java.util.prefs.Preferences

class DalvikusSettings(val bled: Object) {

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
        BooleanSetting(
            key = "decompiler_verbose",
            category = SettingsCategory.DECOMPILER,
            nameRes = Res.string.settings_decompiler_verbose,
            defaultValue = false
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

    operator fun <T> get(key: String): T {
        return when (val setting = settingsList.find { it.key == key }) {
            is IntSetting -> setting.value as T
            is BooleanSetting -> setting.value as T
            else -> throw IllegalArgumentException("Unsupported setting type for key: $key")
        }
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
