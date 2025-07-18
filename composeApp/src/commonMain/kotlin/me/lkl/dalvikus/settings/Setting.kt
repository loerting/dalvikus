package me.lkl.dalvikus.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.settings_category_decompiler
import dalvikus.composeapp.generated.resources.settings_category_general
import dalvikus.composeapp.generated.resources.settings_category_smali
import org.jetbrains.compose.resources.StringResource


enum class SettingsCategory(val nameRes: StringResource) {
    GENERAL(Res.string.settings_category_general),
    SMALI(Res.string.settings_category_smali),
    DECOMPILER(Res.string.settings_category_decompiler)
}

// Base sealed class for settings
sealed class Setting<T>(
    val key: String,
    val category: SettingsCategory,
    val nameRes: StringResource,
    val defaultValue: T
) {
    // Value backing - this will be loaded and saved via multiplatform-settings
    var value: T by mutableStateOf(defaultValue)

    abstract fun save(settings: Settings)
    abstract fun load(settings: Settings)

    @Composable
    abstract fun Editor()
}

class IntSetting(
    key: String,
    category: SettingsCategory,
    nameRes: StringResource,
    defaultValue: Int,
    val min: Int,
    val max: Int,
    val step: Int = 1
) : Setting<Int>(key, category, nameRes, defaultValue) {

    override fun save(settings: Settings) {
        settings.putInt(key, value)
    }

    override fun load(settings: Settings) {
        value = settings.getInt(key, defaultValue)
    }

    @Composable
    override fun Editor() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(400.dp)
                .defaultMinSize(minWidth = 200.dp) ,
            horizontalArrangement = Arrangement.Start
        ) {
            Slider(
                value = value.toFloat(),
                onValueChange = { newValue ->
                    val stepped = ((newValue - min) / step).toInt() * step + min
                    value = stepped.coerceIn(min, max)
                },
                valueRange = min.toFloat()..max.toFloat(),
                steps = (max - min) / step - 1,
                modifier = Modifier
                    .weight(1f, fill = true) // fill available space
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "$value",
                maxLines = 1,
                modifier = Modifier.defaultMinSize(minWidth = 40.dp) // keep text from shrinking too much
            )
        }
    }

}

class BooleanSetting(
    key: String,
    category: SettingsCategory,
    nameRes: StringResource,
    defaultValue: Boolean
) : Setting<Boolean>(key, category, nameRes, defaultValue) {

    override fun save(settings: Settings) {
        settings.putBoolean(key, value)
    }

    override fun load(settings: Settings) {
        value = settings.getBoolean(key, defaultValue)
    }

    @Composable
    override fun Editor() {
        Column {
            Switch(
                checked = value,
                onCheckedChange = { checked -> value = checked }
            )
        }
    }
}