import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import me.lkl.dalvikus.App
import me.lkl.dalvikus.keyActionMap
import me.lkl.dalvikus.ui.util.ctrlShortcuts
import java.awt.Dimension
import javax.swing.UIManager

fun main() = application {
    try {
        System.setProperty("compose.interop.blending", "true")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val showExitDialog = remember { mutableStateOf(false) }

    Window(
        title = "dalvikus",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = {
            showExitDialog.value = true
        },
        onKeyEvent = ctrlShortcuts(keyActionMap)
    ) {
        window.minimumSize = Dimension(350, 600)
        App(showExitDialog, onExitConfirmed = { exitApplication() })
    }
}
