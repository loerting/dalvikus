import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.settings.Setting
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsView() {
    val grouped = dalvikusSettings.groupedByCategory()

    // Track expanded/collapsed state for each category
    val expandedStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            grouped.keys.forEach { category -> put(category.nameRes.toString(), true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        grouped.forEach { (category, settings) ->

            val key = category.nameRes.toString()
            val expanded = expandedStates[key] ?: true

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedStates[key] = !(expandedStates[key] ?: true)
                        }
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(category.nameRes),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }

            // Animate visibility of settings items
            item {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        settings.forEach { setting ->
                            SettingRow(setting)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(setting: Setting<*>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(setting.nameRes),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 32.dp),
            softWrap = false
        )
        setting.Editor()
        Spacer(modifier = Modifier.height(8.dp))
    }
}
