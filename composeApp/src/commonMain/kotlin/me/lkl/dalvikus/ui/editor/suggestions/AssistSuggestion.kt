package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.ui.text.SpanStyle

enum class SuggestionType {
    Instruction,
    Directive,
    Register,
    Access,
    LabelOrType,
}

data class AssistSuggestion(
    val text: String,
    val description: String? = null,
    val type: SuggestionType,
    val spanStyle: SpanStyle? = null
)