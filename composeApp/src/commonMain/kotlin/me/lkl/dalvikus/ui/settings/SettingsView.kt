import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.settings.Setting
import me.lkl.dalvikus.ui.util.CollapseCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsView() {
    val grouped = dalvikusSettings.groupedByCategory()
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 12.dp),
            state = listState
        ) {
            grouped.forEach { (category, settings) ->
                item {
                    CollapseCard(
                        title = stringResource(category.nameRes),
                        icon = category.icon,
                        defaultState = false,
                        modifier = Modifier.fillMaxWidth()
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

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState = listState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(vertical = 8.dp, horizontal = 8.dp)
        )
    }
}

val settingPadVer = 8.dp
val settingPadHor = 16.dp

@Composable
fun SettingRow(setting: Setting<*>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = settingPadVer, horizontal = settingPadHor)
    ) {
        Text(
            text = stringResource(setting.nameRes),
            style = MaterialTheme.typography.bodyLarge,
            softWrap = false
        )
        setting.Editor()
        Spacer(modifier = Modifier.height(settingPadVer))
    }
}
