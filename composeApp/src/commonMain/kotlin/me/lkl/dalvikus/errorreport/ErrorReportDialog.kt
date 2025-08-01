package me.lkl.dalvikus.errorreport

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.settings.DalvikusSettings
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URI
import java.net.URLEncoder
import javax.swing.*

val crtExHandler = CoroutineExceptionHandler { context, throwable ->
    if (throwable is Exception) {
        Logger.e(throwable) { "Caught exception in coroutine: $context" }
        SwingUtilities.invokeLater {
            ErrorReportDialog(throwable).apply {
                isVisible = true
            }
        }
    } else {
        throw throwable
    }
}

fun handleUncaughtExceptions() {
    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
        if (throwable is Exception) {
            Logger.e(throwable) { "Caught exception in thread: ${Thread.currentThread().name}" }
            SwingUtilities.invokeLater {
                ErrorReportDialog(throwable).apply {
                    isVisible = true
                }
            }
        } else {
            // This does not re-trigger the uncaught exception handler
            throw throwable
        }
    }
}

class ErrorReportDialog(
    private val exception: Exception
) : JDialog() {

    init {
        title = "Error Report"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(700, 500)
        setLocationRelativeTo(null)

        val contentPane = JPanel(BorderLayout(10, 10))
        contentPane.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

        // Icon + title
        val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val icon = UIManager.getIcon("OptionPane.errorIcon")
        val iconLabel = JLabel(icon)
        val titleLabel = JLabel("An unexpected error occurred")
        titleLabel.font = titleLabel.font.deriveFont(titleLabel.font.size + 6f)
        titleLabel.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
        titlePanel.add(iconLabel)
        titlePanel.add(titleLabel)
        contentPane.add(titlePanel, BorderLayout.NORTH)

        // Exception stack trace text area
        val stackTrace = StringWriter().also {
            exception.printStackTrace(PrintWriter(it))
        }.toString()

        val textArea = JTextArea(stackTrace)
        textArea.isEditable = false
        textArea.lineWrap = false
        textArea.font = Font("Monospaced", Font.PLAIN, 12)
        val scrollPane = JScrollPane(textArea)
        scrollPane.preferredSize = Dimension(650, 350)
        contentPane.add(scrollPane, BorderLayout.CENTER)

        // Buttons panel
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 20, 5))

        val reportButton = JButton("Report Issue")
        reportButton.addActionListener {
            openGithubIssue(exception)
        }
        val recoverButton = JButton("Close")
        recoverButton.addActionListener {
            dispose()
        }

        buttonPanel.add(reportButton)
        buttonPanel.add(recoverButton)

        contentPane.add(buttonPanel, BorderLayout.SOUTH)

        setContentPane(contentPane)
        isModal = true
    }

    private fun openGithubIssue(exception: Exception) {
        val repoUri = DalvikusSettings.getRepoURI()

        val stackTraceString = StringWriter().also {
            exception.printStackTrace(PrintWriter(it))
        }.toString()

        fun encodeForUrl(text: String) =
            URLEncoder.encode(text, Charsets.UTF_8.toString())

        val issueBody = """
            |**Describe the bug**
            |A clear and concise description of what the bug is.
            |
            |**To Reproduce**
            |Steps to reproduce the behavior:
            |1. ...
            |2. ...
            |
            |**Expected behavior**
            |A clear and concise description of what you expected to happen.
            |
            |**Exception**
            |```
            |${exception.message ?: "No exception message"}
            |${stackTraceString.take(2000)}
            |```
            |
            |**Environment:**
            |- App version: ${dalvikusSettings.getVersion()}
            |- OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")}
            |- Java: ${System.getProperty("java.version")}
            |
            |**Additional context**
            |Add any other context about the problem here.
            |
        """.trimMargin()

        val encodedTitle = encodeForUrl(exception.message ?: "Exception Report")
        val encodedBody = encodeForUrl(issueBody)

        val issueUrl = "$repoUri/issues/new?title=$encodedTitle&body=$encodedBody"

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI(issueUrl))
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to open browser:\n${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Desktop is not supported on this platform.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }
}
