import androidx.compose.animation.*
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.settings.Setting
import me.lkl.dalvikus.ui.util.DefaultCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsView() {
    val grouped = dalvikusSettings.groupedByCategory()

    var expandedState by remember {
        mutableStateOf(grouped.keys.first().nameRes.toString())
    }

    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 12.dp),
            state = listState
        ) {
            grouped.forEach { (category, settings) ->
                val key = category.nameRes.toString()
                val expanded = expandedState == key

                item {
                    DefaultCard(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedState = key
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
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (expanded) "Collapse" else "Expand"
                                )
                            }

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
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState = listState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(vertical = 8.dp, horizontal = 8.dp)
        )
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
