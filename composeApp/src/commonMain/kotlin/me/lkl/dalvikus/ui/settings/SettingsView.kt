import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.credits_and_version
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.settings.Setting
import me.lkl.dalvikus.util.CollapseCard
import me.lkl.dalvikus.util.CollapseCardMaxWidth
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsView() {
    val grouped = dalvikusSettings.groupedByCategory()
    val gridState = rememberLazyGridState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = CollapseCardMaxWidth),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 12.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            grouped.forEach { (category, settings) ->
                item {
                    CollapseCard(
                        title = stringResource(category.nameRes),
                        icon = category.icon,
                        defaultState = false
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
            adapter = rememberScrollbarAdapter(scrollState = gridState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(vertical = 8.dp, horizontal = 8.dp)
        )

        Text(
            text = stringResource(Res.string.credits_and_version, dalvikusSettings.getVersion()),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
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
