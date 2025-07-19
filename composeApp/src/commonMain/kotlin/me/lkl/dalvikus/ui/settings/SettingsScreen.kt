import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.settings.Setting
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen() {
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

            // Use category.nameRes.toString() as a key for state map
            val key = category.nameRes.toString()
            val expanded = expandedStates[key] ?: true

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Toggle expanded state on header click
                            expandedStates[key] = !(expandedStates[key] ?: true)
                        }
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(category.nameRes),
                        style = MaterialTheme.typography.bodyLarge,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(setting.nameRes),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 32.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.wrapContentWidth()
        ) {
            setting.Editor()
        }
    }
}
