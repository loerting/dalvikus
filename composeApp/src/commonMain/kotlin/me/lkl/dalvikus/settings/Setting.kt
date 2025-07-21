package me.lkl.dalvikus.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import dalvikus.composeapp.generated.resources.*
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
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        softWrap = false
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

class StringOptionSetting(
    key: String,
    category: SettingsCategory,
    nameRes: StringResource,
    defaultKey: String,
    val options: List<Pair<String, String>> // key to label
) : Setting<String>(key, category, nameRes, defaultKey) {

    override fun save(settings: Settings) {
        settings.putString(key, value)
    }

    override fun load(settings: Settings) {
        value = settings.getString(key, defaultValue)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Editor() {
        var expanded by remember { mutableStateOf(false) }

        // Text state for the dropdown — initialized from current value
        val currentLabel = options.find { it.first == value }?.second ?: value
        val textFieldState = rememberTextFieldState(currentLabel)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                state = textFieldState,
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                lineLimits = TextFieldLineLimits.SingleLine,
                trailingIcon = { TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (optionKey, optionLabel) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = optionLabel,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Start
                            )
                        },
                        onClick = {
                            value = optionKey
                            textFieldState.setTextAndPlaceCursorAtEnd(optionLabel)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
