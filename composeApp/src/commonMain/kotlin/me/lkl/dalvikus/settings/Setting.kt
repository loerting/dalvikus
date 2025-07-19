package me.lkl.dalvikus.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.settings_category_decompiler
import dalvikus.composeapp.generated.resources.settings_category_editor
import dalvikus.composeapp.generated.resources.settings_category_general
import dalvikus.composeapp.generated.resources.settings_category_smali
import org.jetbrains.compose.resources.StringResource


enum class SettingsCategory(val nameRes: StringResource, val icon: ImageVector) {
    GENERAL(Res.string.settings_category_general, Icons.Outlined.Settings),
    EDITOR(Res.string.settings_category_editor, Icons.Outlined.EditNote),
    SMALI(Res.string.settings_category_smali, Icons.Outlined.Android),
    DECOMPILER(Res.string.settings_category_decompiler, Icons.Outlined.Code)
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
    val step: Int = 1,
    val unit: String = ""
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
            modifier = Modifier.width(400.dp).defaultMinSize(minWidth = 200.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    value = (value - step).coerceAtLeast(min)
                },
                enabled = value > min
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }

            OutlinedTextField(
                value = value.toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { input ->
                        value = input.coerceIn(min, max)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(100.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                suffix = {
                    Text(
                        unit,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            )


            IconButton(
                onClick = {
                    value = (value + step).coerceAtMost(max)
                },
                enabled = value < max
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
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