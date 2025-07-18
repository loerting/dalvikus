package me.lkl.dalvikus.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.settings.Setting
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(dalvikusSettings: DalvikusSettings) {
    val grouped = dalvikusSettings.groupedByCategory()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        grouped.forEach { (category, settings) ->
            item {
                // Category headline
                Text(
                    text = stringResource(category.nameRes),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }

            items(settings) { setting ->
                SettingRow(setting)
            }
        }
    }
}

@Composable
private fun SettingRow(setting: Setting<*>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Setting name aligned to start
        Text(
            text = stringResource(setting.nameRes),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        // Editor aligned to end
        Box(
            modifier = Modifier
                .wrapContentWidth()
        ) {
            setting.Editor()
        }
    }
}
