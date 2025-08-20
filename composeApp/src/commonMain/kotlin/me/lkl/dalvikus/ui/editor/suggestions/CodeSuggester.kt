package me.lkl.dalvikus.ui.editor.suggestions

import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.*
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.lexer.getSmaliTokenStyle
import me.lkl.dalvikus.theme.CodeHighlightColors
import org.antlr.runtime.CommonToken
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.Token

val outsideMethodDirectives = listOf(
    ".class" to Res.string.suggestion_class,
    ".super" to Res.string.suggestion_super,
    ".implements" to Res.string.suggestion_implements,
    ".source" to Res.string.suggestion_source,
    ".field" to Res.string.suggestion_field,
    ".end field" to Res.string.suggestion_end_field,
    ".annotation" to Res.string.suggestion_annotation,
    ".end annotation" to Res.string.suggestion_end_annotation,
    ".subannotation" to Res.string.suggestion_subannotation,
    ".end subannotation" to Res.string.suggestion_end_subannotation,
    ".enum" to Res.string.suggestion_enum,
    ".method" to Res.string.suggestion_method,
    ".end method" to Res.string.suggestion_end_method
)

val insideMethodDirectives = listOf(
    ".method" to Res.string.suggestion_method,
    ".end method" to Res.string.suggestion_end_method,
    ".annotation" to Res.string.suggestion_annotation,
    ".end annotation" to Res.string.suggestion_end_annotation,
    ".registers" to Res.string.suggestion_registers,
    ".locals" to Res.string.suggestion_locals,
    ".param" to Res.string.suggestion_param,
    ".end param" to Res.string.suggestion_end_param,
    ".local" to Res.string.suggestion_local,
    ".end local" to Res.string.suggestion_end_local,
    ".restart local" to Res.string.suggestion_restart_local,
    ".line" to Res.string.suggestion_line,
    ".prologue" to Res.string.suggestion_prologue,
    ".epilogue" to Res.string.suggestion_epilogue,
    ".catch" to Res.string.suggestion_catch,
    ".catchall" to Res.string.suggestion_catchall,
    ".array-data" to Res.string.suggestion_array_data,
    ".end array-data" to Res.string.suggestion_end_array_data,
    ".packed-switch" to Res.string.suggestion_packed_switch,
    ".end packed-switch" to Res.string.suggestion_end_packed_switch,
    ".sparse-switch" to Res.string.suggestion_sparse_switch,
    ".end sparse-switch" to Res.string.suggestion_end_sparse_switch
)

val accessList = listOf(
    "public" to Res.string.suggestion_public,
    "private" to Res.string.suggestion_private,
    "protected" to Res.string.suggestion_protected,
    "static" to Res.string.suggestion_static,
    "final" to Res.string.suggestion_final,
    "synchronized" to Res.string.suggestion_synchronized,
    "bridge" to Res.string.suggestion_bridge,
    "varargs" to Res.string.suggestion_varargs,
    "native" to Res.string.suggestion_native,
    "abstract" to Res.string.suggestion_abstract,
    "strictfp" to Res.string.suggestion_strictfp,
    "synthetic" to Res.string.suggestion_synthetic,
    "constructor" to Res.string.suggestion_constructor,
    "declared-synchronized" to Res.string.suggestion_declared_synchronized,
    "interface" to Res.string.suggestion_interface,
    "enum" to Res.string.suggestion_enum,
    "annotation" to Res.string.suggestion_annotation,
    "volatile" to Res.string.suggestion_volatile,
    "transient" to Res.string.suggestion_transient
)

val dalvikOpcodes = mapOf(
    "goto" to Res.string.suggestion_goto,
    "return-void" to Res.string.suggestion_return_void,
    "nop" to Res.string.suggestion_nop,
    "return-void-barrier" to Res.string.suggestion_return_void_barrier,
    "return-void-no-barrier" to Res.string.suggestion_return_void_no_barrier,
    "const/4" to Res.string.suggestion_const_4,
    "move-result" to Res.string.suggestion_move_result,
    "move-result-wide" to Res.string.suggestion_move_result_wide,
    "move-result-object" to Res.string.suggestion_move_result_object,
    "move-exception" to Res.string.suggestion_move_exception,
    "return" to Res.string.suggestion_return,
    "return-wide" to Res.string.suggestion_return_wide,
    "return-object" to Res.string.suggestion_return_object,
    "monitor-enter" to Res.string.suggestion_monitor_enter,
    "monitor-exit" to Res.string.suggestion_monitor_exit,
    "throw" to Res.string.suggestion_throw,
    "move" to Res.string.suggestion_move,
    "move-wide" to Res.string.suggestion_move_wide,
    "move-object" to Res.string.suggestion_move_object,
    "array-length" to Res.string.suggestion_array_length,
    "neg-int" to Res.string.suggestion_neg_int,
    "not-int" to Res.string.suggestion_not_int,
    "neg-long" to Res.string.suggestion_neg_long,
    "not-long" to Res.string.suggestion_not_long,
    "neg-float" to Res.string.suggestion_neg_float,
    "neg-double" to Res.string.suggestion_neg_double,
    "int-to-long" to Res.string.suggestion_int_to_long,
    "int-to-float" to Res.string.suggestion_int_to_float,
    "int-to-double" to Res.string.suggestion_int_to_double,
    "long-to-int" to Res.string.suggestion_long_to_int,
    "long-to-float" to Res.string.suggestion_long_to_float,
    "long-to-double" to Res.string.suggestion_long_to_double,
    "float-to-int" to Res.string.suggestion_float_to_int,
    "float-to-long" to Res.string.suggestion_float_to_long,
    "float-to-double" to Res.string.suggestion_float_to_double,
    "double-to-int" to Res.string.suggestion_double_to_int,
    "double-to-long" to Res.string.suggestion_double_to_long,
    "double-to-float" to Res.string.suggestion_double_to_float,
    "int-to-byte" to Res.string.suggestion_int_to_byte,
    "int-to-char" to Res.string.suggestion_int_to_char,
    "int-to-short" to Res.string.suggestion_int_to_short,
    "add-int/2addr" to Res.string.suggestion_add_int_2addr,
    "sub-int/2addr" to Res.string.suggestion_sub_int_2addr,
    "mul-int/2addr" to Res.string.suggestion_mul_int_2addr,
    "div-int/2addr" to Res.string.suggestion_div_int_2addr,
    "rem-int/2addr" to Res.string.suggestion_rem_int_2addr,
    "and-int/2addr" to Res.string.suggestion_and_int_2addr,
    "or-int/2addr" to Res.string.suggestion_or_int_2addr,
    "xor-int/2addr" to Res.string.suggestion_xor_int_2addr,
    "shl-int/2addr" to Res.string.suggestion_shl_int_2addr,
    "shr-int/2addr" to Res.string.suggestion_shr_int_2addr,
    "ushr-int/2addr" to Res.string.suggestion_ushr_int_2addr,
    "add-long/2addr" to Res.string.suggestion_add_long_2addr,
    "sub-long/2addr" to Res.string.suggestion_sub_long_2addr,
    "mul-long/2addr" to Res.string.suggestion_mul_long_2addr,
    "div-long/2addr" to Res.string.suggestion_div_long_2addr,
    "rem-long/2addr" to Res.string.suggestion_rem_long_2addr,
    "and-long/2addr" to Res.string.suggestion_and_long_2addr,
    "or-long/2addr" to Res.string.suggestion_or_long_2addr,
    "xor-long/2addr" to Res.string.suggestion_xor_long_2addr,
    "shl-long/2addr" to Res.string.suggestion_shl_long_2addr,
    "shr-long/2addr" to Res.string.suggestion_shr_long_2addr,
    "ushr-long/2addr" to Res.string.suggestion_ushr_long_2addr,
    "add-float/2addr" to Res.string.suggestion_add_float_2addr,
    "sub-float/2addr" to Res.string.suggestion_sub_float_2addr,
    "mul-float/2addr" to Res.string.suggestion_mul_float_2addr,
    "div-float/2addr" to Res.string.suggestion_div_float_2addr,
    "rem-float/2addr" to Res.string.suggestion_rem_float_2addr,
    "add-double/2addr" to Res.string.suggestion_add_double_2addr,
    "sub-double/2addr" to Res.string.suggestion_sub_double_2addr,
    "mul-double/2addr" to Res.string.suggestion_mul_double_2addr,
    "div-double/2addr" to Res.string.suggestion_div_double_2addr,
    "rem-double/2addr" to Res.string.suggestion_rem_double_2addr,
    "throw-verification-error" to Res.string.suggestion_throw_verification_error,
    "goto/16" to Res.string.suggestion_goto_16,
    "sget" to Res.string.suggestion_sget,
    "sget-wide" to Res.string.suggestion_sget_wide,
    "sget-object" to Res.string.suggestion_sget_object,
    "sget-boolean" to Res.string.suggestion_sget_boolean,
    "sget-byte" to Res.string.suggestion_sget_byte,
    "sget-char" to Res.string.suggestion_sget_char,
    "sget-short" to Res.string.suggestion_sget_short,
    "sput" to Res.string.suggestion_sput,
    "sput-wide" to Res.string.suggestion_sput_wide,
    "sput-object" to Res.string.suggestion_sput_object,
    "sput-boolean" to Res.string.suggestion_sput_boolean,
    "sput-byte" to Res.string.suggestion_sput_byte,
    "sput-char" to Res.string.suggestion_sput_char,
    "sput-short" to Res.string.suggestion_sput_short,
    "sget-volatile" to Res.string.suggestion_sget_volatile,
    "sget-wide-volatile" to Res.string.suggestion_sget_wide_volatile,
    "sget-object-volatile" to Res.string.suggestion_sget_object_volatile,
    "sput-volatile" to Res.string.suggestion_sput_volatile,
    "sput-wide-volatile" to Res.string.suggestion_sput_wide_volatile,
    "sput-object-volatile" to Res.string.suggestion_sput_object_volatile,
    "const-string" to Res.string.suggestion_const_string,
    "check-cast" to Res.string.suggestion_check_cast,
    "new-instance" to Res.string.suggestion_new_instance,
    "const-class" to Res.string.suggestion_const_class,
    "const-method-handle" to Res.string.suggestion_const_method_handle,
    "const-method-type" to Res.string.suggestion_const_method_type,
    "const/high16" to Res.string.suggestion_const_high16,
    "const-wide/high16" to Res.string.suggestion_const_wide_high16,
    "const/16" to Res.string.suggestion_const_16,
    "const-wide/16" to Res.string.suggestion_const_wide_16,
    "if-eqz" to Res.string.suggestion_if_eqz,
    "if-nez" to Res.string.suggestion_if_nez,
    "if-ltz" to Res.string.suggestion_if_ltz,
    "if-gez" to Res.string.suggestion_if_gez,
    "if-gtz" to Res.string.suggestion_if_gtz,
    "if-lez" to Res.string.suggestion_if_lez,
    "add-int/lit8" to Res.string.suggestion_add_int_lit8,
    "rsub-int/lit8" to Res.string.suggestion_rsub_int_lit8,
    "mul-int/lit8" to Res.string.suggestion_mul_int_lit8,
    "div-int/lit8" to Res.string.suggestion_div_int_lit8,
    "rem-int/lit8" to Res.string.suggestion_rem_int_lit8,
    "and-int/lit8" to Res.string.suggestion_and_int_lit8,
    "or-int/lit8" to Res.string.suggestion_or_int_lit8,
    "xor-int/lit8" to Res.string.suggestion_xor_int_lit8,
    "shl-int/lit8" to Res.string.suggestion_shl_int_lit8,
    "shr-int/lit8" to Res.string.suggestion_shr_int_lit8,
    "ushr-int/lit8" to Res.string.suggestion_ushr_int_lit8,
    "iget" to Res.string.suggestion_iget,
    "iget-wide" to Res.string.suggestion_iget_wide,
    "iget-object" to Res.string.suggestion_iget_object,
    "iget-boolean" to Res.string.suggestion_iget_boolean,
    "iget-byte" to Res.string.suggestion_iget_byte,
    "iget-char" to Res.string.suggestion_iget_char,
    "iget-short" to Res.string.suggestion_iget_short,
    "iput" to Res.string.suggestion_iput,
    "iput-wide" to Res.string.suggestion_iput_wide,
    "iput-object" to Res.string.suggestion_iput_object,
    "iput-boolean" to Res.string.suggestion_iput_boolean,
    "iput-byte" to Res.string.suggestion_iput_byte,
    "iput-char" to Res.string.suggestion_iput_char,
    "iput-short" to Res.string.suggestion_iput_short,
    "iget-volatile" to Res.string.suggestion_iget_volatile,
    "iget-wide-volatile" to Res.string.suggestion_iget_wide_volatile,
    "iget-object-volatile" to Res.string.suggestion_iget_object_volatile,
    "iput-volatile" to Res.string.suggestion_iput_volatile,
    "iput-wide-volatile" to Res.string.suggestion_iput_wide_volatile,
    "iput-object-volatile" to Res.string.suggestion_iput_object_volatile,
    "instance-of" to Res.string.suggestion_instance_of,
    "new-array" to Res.string.suggestion_new_array,
    "iget-quick" to Res.string.suggestion_iget_quick,
    "iget-wide-quick" to Res.string.suggestion_iget_wide_quick,
    "iget-object-quick" to Res.string.suggestion_iget_object_quick,
    "iput-quick" to Res.string.suggestion_iput_quick,
    "iput-wide-quick" to Res.string.suggestion_iput_wide_quick,
    "iput-object-quick" to Res.string.suggestion_iput_object_quick,
    "iput-boolean-quick" to Res.string.suggestion_iput_boolean_quick,
    "iput-byte-quick" to Res.string.suggestion_iput_byte_quick,
    "iput-char-quick" to Res.string.suggestion_iput_char_quick,
    "iput-short-quick" to Res.string.suggestion_iput_short_quick,
    "rsub-int" to Res.string.suggestion_rsub_int,
    "add-int/lit16" to Res.string.suggestion_add_int_lit16,
    "mul-int/lit16" to Res.string.suggestion_mul_int_lit16,
    "div-int/lit16" to Res.string.suggestion_div_int_lit16,
    "rem-int/lit16" to Res.string.suggestion_rem_int_lit16,
    "and-int/lit16" to Res.string.suggestion_and_int_lit16,
    "or-int/lit16" to Res.string.suggestion_or_int_lit16,
    "xor-int/lit16" to Res.string.suggestion_xor_int_lit16,
    "if-eq" to Res.string.suggestion_if_eq,
    "if-ne" to Res.string.suggestion_if_ne,
    "if-lt" to Res.string.suggestion_if_lt,
    "if-ge" to Res.string.suggestion_if_ge,
    "if-gt" to Res.string.suggestion_if_gt,
    "if-le" to Res.string.suggestion_if_le,
    "move/from16" to Res.string.suggestion_move_from16,
    "move-wide/from16" to Res.string.suggestion_move_wide_from16,
    "move-object/from16" to Res.string.suggestion_move_object_from16,
    "cmpl-float" to Res.string.suggestion_cmpl_float,
    "cmpg-float" to Res.string.suggestion_cmpg_float,
    "cmpl-double" to Res.string.suggestion_cmpl_double,
    "cmpg-double" to Res.string.suggestion_cmpg_double,
    "cmp-long" to Res.string.suggestion_cmp_long,
    "aget" to Res.string.suggestion_aget,
    "aget-wide" to Res.string.suggestion_aget_wide,
    "aget-object" to Res.string.suggestion_aget_object,
    "aget-boolean" to Res.string.suggestion_aget_boolean,
    "aget-byte" to Res.string.suggestion_aget_byte,
    "aget-char" to Res.string.suggestion_aget_char,
    "aget-short" to Res.string.suggestion_aget_short,
    "aput" to Res.string.suggestion_aput,
    "aput-wide" to Res.string.suggestion_aput_wide,
    "aput-object" to Res.string.suggestion_aput_object,
    "aput-boolean" to Res.string.suggestion_aput_boolean,
    "aput-byte" to Res.string.suggestion_aput_byte,
    "aput-char" to Res.string.suggestion_aput_char,
    "aput-short" to Res.string.suggestion_aput_short,
    "add-int" to Res.string.suggestion_add_int,
    "sub-int" to Res.string.suggestion_sub_int,
    "mul-int" to Res.string.suggestion_mul_int,
    "div-int" to Res.string.suggestion_div_int,
    "rem-int" to Res.string.suggestion_rem_int,
    "and-int" to Res.string.suggestion_and_int,
    "or-int" to Res.string.suggestion_or_int,
    "xor-int" to Res.string.suggestion_xor_int,
    "shl-int" to Res.string.suggestion_shl_int,
    "shr-int" to Res.string.suggestion_shr_int,
    "ushr-int" to Res.string.suggestion_ushr_int,
    "add-long" to Res.string.suggestion_add_long,
    "sub-long" to Res.string.suggestion_sub_long,
    "mul-long" to Res.string.suggestion_mul_long,
    "div-long" to Res.string.suggestion_div_long,
    "rem-long" to Res.string.suggestion_rem_long,
    "and-long" to Res.string.suggestion_and_long,
    "or-long" to Res.string.suggestion_or_long,
    "xor-long" to Res.string.suggestion_xor_long,
    "shl-long" to Res.string.suggestion_shl_long,
    "shr-long" to Res.string.suggestion_shr_long,
    "ushr-long" to Res.string.suggestion_ushr_long,
    "add-float" to Res.string.suggestion_add_float,
    "sub-float" to Res.string.suggestion_sub_float,
    "mul-float" to Res.string.suggestion_mul_float,
    "div-float" to Res.string.suggestion_div_float,
    "rem-float" to Res.string.suggestion_rem_float,
    "add-double" to Res.string.suggestion_add_double,
    "sub-double" to Res.string.suggestion_sub_double,
    "mul-double" to Res.string.suggestion_mul_double,
    "div-double" to Res.string.suggestion_div_double,
    "rem-double" to Res.string.suggestion_rem_double,
    "goto/32" to Res.string.suggestion_goto_32,
    "const-string/jumbo" to Res.string.suggestion_const_string_jumbo,
    "const" to Res.string.suggestion_const,
    "const-wide/32" to Res.string.suggestion_const_wide_32,
    "fill-array-data" to Res.string.suggestion_fill_array_data,
    "packed-switch" to Res.string.suggestion_packed_switch,
    "sparse-switch" to Res.string.suggestion_sparse_switch,
    "move/16" to Res.string.suggestion_move_16,
    "move-wide/16" to Res.string.suggestion_move_wide_16,
    "move-object/16" to Res.string.suggestion_move_object_16,
    "invoke-custom" to Res.string.suggestion_invoke_custom,
    "invoke-virtual" to Res.string.suggestion_invoke_virtual,
    "invoke-super" to Res.string.suggestion_invoke_super,
    "invoke-direct" to Res.string.suggestion_invoke_direct,
    "invoke-static" to Res.string.suggestion_invoke_static,
    "invoke-interface" to Res.string.suggestion_invoke_interface,
    "invoke-direct-empty" to Res.string.suggestion_invoke_direct_empty,
    "filled-new-array" to Res.string.suggestion_filled_new_array,
    "execute-inline" to Res.string.suggestion_execute_inline,
    "invoke-virtual-quick" to Res.string.suggestion_invoke_virtual_quick,
    "invoke-super-quick" to Res.string.suggestion_invoke_super_quick,
    "invoke-custom/range" to Res.string.suggestion_invoke_custom_range,
    "invoke-virtual/range" to Res.string.suggestion_invoke_virtual_range,
    "invoke-super/range" to Res.string.suggestion_invoke_super_range,
    "invoke-direct/range" to Res.string.suggestion_invoke_direct_range,
    "invoke-static/range" to Res.string.suggestion_invoke_static_range,
    "invoke-interface/range" to Res.string.suggestion_invoke_interface_range,
    "invoke-object-init/range" to Res.string.suggestion_invoke_object_init_range,
    "filled-new-array/range" to Res.string.suggestion_filled_new_array_range,
    "execute-inline/range" to Res.string.suggestion_execute_inline_range,
    "invoke-virtual-quick/range" to Res.string.suggestion_invoke_virtual_quick_range,
    "invoke-super-quick/range" to Res.string.suggestion_invoke_super_quick_range,
    "invoke-polymorphic" to Res.string.suggestion_invoke_polymorphic,
    "invoke-polymorphic/range" to Res.string.suggestion_invoke_polymorphic_range,
    "const-wide" to Res.string.suggestion_const_wide
)

class CodeSuggester(
    private val cursorIndex: Int,
    private val currentText: String,
    private val highlightColors: CodeHighlightColors
) {
    private var currentTokenIndex: Int
    private var estimatedMethodRange: IntRange
    private var tokenList: List<Token>

    var currentToken: CommonToken
    var cleanedText: String

    init {
        val lexer = smaliFlexLexer(currentText.reader(), dalvikusSettings["api_level"] as Int)
        lexer.setSuppressErrors(true)
        val tokens = CommonTokenStream(lexer)
        tokens.fill()

        tokenList = tokens.tokens

        currentTokenIndex = tokenList.indexOfLast {
            it is CommonToken &&
                    it.startIndex <= cursorIndex &&
                    it.type != smaliParser.WHITE_SPACE
        }
        if (currentTokenIndex < 0) {
            currentToken = CommonToken(smaliParser.EOF, "")
            estimatedMethodRange = IntRange.EMPTY
        } else {
            currentToken = tokenList.getOrNull(currentTokenIndex) as CommonToken
            estimatedMethodRange = getEstimatedMethodRange(tokenList, currentTokenIndex)
        }

        cleanedText = currentToken.text
            .substringAfterLast("(")
            .substringAfterLast("{")
            .substringBeforeLast(")")
            .substringBeforeLast("}")
            .substringBeforeLast(";")
    }

    fun suggestNext(): List<AssistSuggestion> {
        if (cleanedText.isEmpty()) return emptyList()
        val suggestions = mutableListOf<AssistSuggestion>()

        when {
            cleanedText.startsWith(".") -> {
                val spanStyle = getSmaliTokenStyle(
                    smaliParser.METHOD_DIRECTIVE, highlightColors
                )
                // directive suggestions
                val directives = if (estimatedMethodRange.isEmpty()) outsideMethodDirectives else insideMethodDirectives
                directives.forEach { (directive, description) ->
                    if (typingStartsWith(directive)) {
                        suggestions.add(AssistSuggestion(directive, description, SuggestionType.Directive, spanStyle))
                    }
                }
            }

            !cleanedText.isEmpty() -> {
                findAllTokensOfType(
                    smaliParser.SIMPLE_NAME,
                    smaliParser.CLASS_DESCRIPTOR,
                    smaliParser.ARRAY_TYPE_PREFIX,
                    smaliParser.PRIMITIVE_TYPE
                ).distinctBy { it.text }.forEach { token ->
                    if(token == currentToken) return@forEach
                    if (typingStartsWith(token.text)) {
                        val spanStyle = getSmaliTokenStyle(
                            token.type, highlightColors
                        )
                        suggestions.add(AssistSuggestion(token.text, Res.string.suggestion_reuse, SuggestionType.LabelOrType, spanStyle))
                    }
                }
            }
        }

        if (!estimatedMethodRange.isEmpty()) {
            if (cleanedText.startsWith("v") || cleanedText.startsWith("p")) {
                val spanStyle = getSmaliTokenStyle(
                    smaliParser.REGISTER, highlightColors
                )
                val registerCount = getRegisterCount()
                if (registerCount > 0) {
                    for (i in 0 until registerCount) {
                        val registerName = cleanedText.substring(0, 1) + i
                        if (typingStartsWith(registerName)) {
                            suggestions.add(
                                AssistSuggestion(
                                    registerName,
                                    Res.string.suggestion_load_register,
                                    SuggestionType.Register,
                                    spanStyle
                                )
                            )
                        }
                    }
                }
            }
            val opcodeSpanStyle = getSmaliTokenStyle(
                smaliParser.INSTRUCTION_FORMAT10t, highlightColors
            )
            dalvikOpcodes.forEach { (opcode, description) ->
                if (typingStartsWith(opcode)) {
                    suggestions.add(AssistSuggestion(opcode, description, SuggestionType.Instruction, opcodeSpanStyle))
                }
            }
        }
        getSurroundingToken(false, currentTokenIndex)?.let { surroundingToken ->
            val spanStyle = getSmaliTokenStyle(
                smaliParser.ACCESS_SPEC, highlightColors
            )
            if (surroundingToken.type in listOf(
                    smaliParser.CLASS_DIRECTIVE,
                    smaliParser.FIELD_DIRECTIVE,
                    smaliParser.METHOD_DIRECTIVE,
                    smaliParser.ACCESS_SPEC
                )
            ) {
                // method access specifier suggestions
                accessList.forEach { (access, description) ->
                    if (typingStartsWith(access)) {
                        suggestions.add(AssistSuggestion(access, description, SuggestionType.Access, spanStyle))
                    }
                }
            }
        }

        return suggestions
    }

    private fun typingStartsWith(directive: String): Boolean {
        return directive.startsWith(cleanedText, ignoreCase = true)
    }

    private fun findAllTokensOfType(vararg tokenType: Int): List<CommonToken> {
        return tokenList.filterIsInstance<CommonToken>()
            .filter { it.type in tokenType.toList() }
    }

    fun getRegisterCount(): Int {
        if (estimatedMethodRange.isEmpty()) return 0

        for (i in estimatedMethodRange) {
            val token = tokenList[i]
            if (token.type == smaliParser.REGISTERS_DIRECTIVE) {
                val nextToken = getSurroundingToken(true, i)
                if (nextToken == null || nextToken.type != smaliParser.POSITIVE_INTEGER_LITERAL) {
                    return 0
                }
                return nextToken.text.toIntOrNull() ?: 0
            }
        }
        return 0
    }

    fun getSurroundingToken(forward: Boolean, index: Int): Token? {
        if (index < 0 || index >= tokenList.size) return null

        var currentIndex = index + if (forward) 1 else -1
        while (currentIndex in tokenList.indices) {
            val token = tokenList[currentIndex]
            if (token.type !in listOf(
                    smaliParser.WHITE_SPACE,
                    smaliParser.EOF
                )
            ) {
                return token
            }
            currentIndex += if (forward) 1 else -1
        }
        return null
    }

    /**
     * Returns an int range of token indices from the .method token to .end method, or null if the current token is not within a method.
     */
    fun getEstimatedMethodRange(
        tokenList: List<Token>,
        currentTokenIndex: Int
    ): IntRange {
        if (currentTokenIndex < 0 || currentTokenIndex >= tokenList.size) return IntRange.EMPTY

        var start = -1

        for (i in currentTokenIndex downTo 0) {
            val token = tokenList[i]
            if (token.type == smaliParser.METHOD_DIRECTIVE) {
                start = i
                break
            }
        }
        if (start == -1) return IntRange.EMPTY

        var end = tokenList.size - 1

        for (i in currentTokenIndex until tokenList.size) {
            val token = tokenList[i]
            // be less strict about the end token. maybe the user has not placed the .end method yet.
            if (token.type == smaliParser.END_METHOD_DIRECTIVE
                || token.type == smaliParser.METHOD_DIRECTIVE
            ) {
                end = i
                break
            }
        }

        return IntRange(start, end)
    }
}