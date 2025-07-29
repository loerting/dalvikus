package me.lkl.dalvikus.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.*
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
import me.lkl.dalvikus.tree.filesystem.FileSystemFileNode
import me.lkl.dalvikus.ui.tree.FileSelectorDialog
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.io.File


enum class SettingsCategory(val nameRes: StringResource, val icon: ImageVector) {
    GENERAL(Res.string.settings_category_general, Icons.Outlined.Settings),
    EDITOR(Res.string.settings_category_editor, Icons.Outlined.EditNote),
    SMALI(Res.string.settings_category_smali, Icons.Outlined.Android),
    DECOMPILER(Res.string.settings_category_decompiler, Icons.Outlined.Code),
    SIGNING(Res.string.settings_category_signing, Icons.Outlined.Draw),
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
        ) {
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
            OutlinedCard(
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Row {
                    IconButton(
                        onClick = {
                            value = (value - step).coerceAtLeast(min)
                        },
                        enabled = value > min
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
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


class FileSetting(
    key: String,
    category: SettingsCategory,
    nameRes: StringResource,
    val dialogRes: StringResource,
    defaultPath: String,
    val extensions: List<String>? = null,
) : Setting<String>(key, category, nameRes, defaultPath) {

    override fun save(settings: Settings) {
        settings.putString(key, value)
    }

    override fun load(settings: Settings) {
        value = settings.getString(key, defaultValue)
    }

    fun isValid(): Boolean {
        if (value.isBlank()) return false
        val file = File(value)
        return file.exists() && !file.isDirectory && (extensions == null || file.extension.lowercase() in extensions)
    }

    @Composable
    override fun Editor() {
        var showFilePicker by remember { mutableStateOf(false) }

        if (showFilePicker) {
            FileSelectorDialog(
                title = stringResource(Res.string.dialog_select_file),
                message = stringResource(dialogRes),
                filePredicate = { it is FileSystemFileNode && (extensions == null || it.file.extension.lowercase() in extensions) },
                onDismissRequest = {
                    showFilePicker = false
                }) { node ->
                if (node !is FileSystemFileNode) return@FileSelectorDialog
                value = node.file.absolutePath
                showFilePicker = false
            }
        }
        OutlinedTextField(
            value = value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { value = it },
            keyboardOptions = KeyboardOptions.Default,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (isValid()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        showFilePicker = true
                    }
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null)
                }
            }
        )

    }
}

class StringSetting(
    key: String,
    category: SettingsCategory,
    nameRes: StringResource,
    defaultValue: String,
    regex: String
) : Setting<String>(key, category, nameRes, defaultValue) {

    private val pattern = Regex(regex)
    var errorMessage: String? = null

    override fun save(settings: Settings) {
        settings.putString(key, value)
    }

    override fun load(settings: Settings) {
        value = settings.getString(key, defaultValue)
    }

    @Composable
    override fun Editor() {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    if (pattern.matches(it)) {
                        value = it
                        errorMessage = null
                    } else {
                        errorMessage = "Invalid input format"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
