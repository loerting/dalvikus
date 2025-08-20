package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.ui.text.SpanStyle
import org.jetbrains.compose.resources.StringResource

enum class SuggestionType {
    Instruction,
    Directive,
    Register,
    Access,
    LabelOrType,
}

data class AssistSuggestion(
    val text: String,
    val description: StringResource? = null,
    val type: SuggestionType,
    val spanStyle: SpanStyle? = null
)