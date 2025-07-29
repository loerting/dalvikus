package me.lkl.dalvikus.ui.editor.suggestions

import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.lexer.getSmaliTokenStyle
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors
import org.antlr.runtime.CommonToken
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.Token

val outsideMethodDirectives = listOf(
    ".class" to "Defines the class name and access flags.",
    ".super" to "Specifies the superclass of the class.",
    ".implements" to "Specifies an interface implemented by the class.",
    ".source" to "Specifies the source file for debugging.",
    ".field" to "Declares a field in the class.",
    ".end field" to "Ends a field declaration block.",
    ".annotation" to "Starts an annotation block.",
    ".end annotation" to "Ends an annotation block.",
    ".subannotation" to "Starts a sub-annotation block.",
    ".end subannotation" to "Ends a sub-annotation block.",
    ".enum" to "Declares an enum value (within a field).",
    ".method" to "Starts a method declaration block.",
    ".end method" to "Ends a method declaration block."
)

val insideMethodDirectives = listOf(
    ".method" to "Starts a method declaration block.",
    ".end method" to "Ends a method declaration block.",
    ".annotation" to "Starts an annotation block.",
    ".end annotation" to "Ends an annotation block.",
    ".registers" to "Specifies the total number of registers used in the method.",
    ".locals" to "Specifies the number of local registers in the method (debug info).",
    ".param" to "Declares a method parameter name (debug info).",
    ".end param" to "Ends a method parameter declaration.",
    ".local" to "Declares a local variable name (debug info).",
    ".end local" to "Ends a local variable declaration.",
    ".restart local" to "Restarts the scope of a local variable.",
    ".line" to "Specifies the source code line number for debugging.",
    ".prologue" to "Marks the start of method prologue code.",
    ".epilogue" to "Marks the start of method epilogue code.",
    ".catch" to "Declares an exception handler.",
    ".catchall" to "Declares a catch-all exception handler.",
    ".array-data" to "Starts an array data block.",
    ".end array-data" to "Ends an array data block.",
    ".packed-switch" to "Starts a packed-switch block.",
    ".end packed-switch" to "Ends a packed-switch block.",
    ".sparse-switch" to "Starts a sparse-switch block.",
    ".end sparse-switch" to "Ends a sparse-switch block."
)

val accessList = listOf(
    "public" to "Accessible from anywhere.",
    "private" to "Accessible only within the defining class.",
    "protected" to "Accessible within the same package or subclasses.",
    "static" to "Belongs to the class, not instance.",
    "final" to "Cannot be overridden or changed.",
    "synchronized" to "Method is synchronized for thread safety.",
    "bridge" to "Compiler-generated method for generics (bridge method).",
    "varargs" to "Accepts a variable number of arguments.",
    "native" to "Implemented in native code (JNI).",
    "abstract" to "Method or class has no implementation.",
    "strictfp" to "Strict floating-point calculations.",
    "synthetic" to "Compiler-generated element (not in source).",
    "constructor" to "Marks a method as a constructor.",
    "declared-synchronized" to "Synchronized (explicitly declared).",
    "interface" to "Declares an interface.",
    "enum" to "Declares an enum type.",
    "annotation" to "Declares an annotation type.",
    "volatile" to "Field is volatile (thread-visible).",
    "transient" to "Field should not be serialized."
)

val dalvikOpcodes = mapOf(
    "goto" to "Unconditionally jumps to a new instruction offset.",
    "return-void" to "Returns from a void method.",
    "nop" to "Performs no operation.",
    "return-void-barrier" to "Returns from a void method with a memory barrier.",
    "return-void-no-barrier" to "Returns from a void method without a memory barrier.",
    "const/4" to "Moves a 4-bit literal value to a register.",
    "move-result" to "Moves the result of the previous invoke or filled-new-array instruction to a register.",
    "move-result-wide" to "Moves the wide result of the previous invoke or filled-new-array instruction to a pair of registers.",
    "move-result-object" to "Moves the object result of the previous invoke or filled-new-array instruction to a register.",
    "move-exception" to "Moves the exception object to a register.",
    "return" to "Returns from a method with an integer result.",
    "return-wide" to "Returns from a method with a long or double result.",
    "return-object" to "Returns from a method with an object result.",
    "monitor-enter" to "Enters a monitor for an object.",
    "monitor-exit" to "Exits a monitor for an object.",
    "throw" to "Throws an exception object.",
    "move" to "Moves a register's content to another register.",
    "move-wide" to "Moves a wide register's content to another pair of registers.",
    "move-object" to "Moves an object register's content to another object register.",
    "array-length" to "Gets the length of an array.",
    "neg-int" to "Negates an integer.",
    "not-int" to "Performs a bitwise NOT on an integer.",
    "neg-long" to "Negates a long.",
    "not-long" to "Performs a bitwise NOT on a long.",
    "neg-float" to "Negates a float.",
    "neg-double" to "Negates a double.",
    "int-to-long" to "Converts an integer to a long.",
    "int-to-float" to "Converts an integer to a float.",
    "int-to-double" to "Converts an integer to a double.",
    "long-to-int" to "Converts a long to an integer.",
    "long-to-float" to "Converts a long to a float.",
    "long-to-double" to "Converts a long to a double.",
    "float-to-int" to "Converts a float to an integer.",
    "float-to-long" to "Converts a float to a long.",
    "float-to-double" to "Converts a float to a double.",
    "double-to-int" to "Converts a double to an integer.",
    "double-to-long" to "Converts a double to a long.",
    "double-to-float" to "Converts a double to a float.",
    "int-to-byte" to "Converts an integer to a byte.",
    "int-to-char" to "Converts an integer to a char.",
    "int-to-short" to "Converts an integer to a short.",
    "add-int/2addr" to "Adds two integers, storing the result in the first operand.",
    "sub-int/2addr" to "Subtracts two integers, storing the result in the first operand.",
    "mul-int/2addr" to "Multiplies two integers, storing the result in the first operand.",
    "div-int/2addr" to "Divides two integers, storing the result in the first operand.",
    "rem-int/2addr" to "Calculates the remainder of integer division, storing the result in the first operand.",
    "and-int/2addr" to "Performs a bitwise AND on two integers, storing the result in the first operand.",
    "or-int/2addr" to "Performs a bitwise OR on two integers, storing the result in the first operand.",
    "xor-int/2addr" to "Performs a bitwise XOR on two integers, storing the result in the first operand.",
    "shl-int/2addr" to "Performs a left bit shift on an integer, storing the result in the first operand.",
    "shr-int/2addr" to "Performs a right bit shift on an integer, storing the result in the first operand.",
    "ushr-int/2addr" to "Performs an unsigned right bit shift on an integer, storing the result in the first operand.",
    "add-long/2addr" to "Adds two longs, storing the result in the first operand.",
    "sub-long/2addr" to "Subtracts two longs, storing the result in the first operand.",
    "mul-long/2addr" to "Multiplies two longs, storing the result in the first operand.",
    "div-long/2addr" to "Divides two longs, storing the result in the first operand.",
    "rem-long/2addr" to "Calculates the remainder of long division, storing the result in the first operand.",
    "and-long/2addr" to "Performs a bitwise AND on two longs, storing the result in the first operand.",
    "or-long/2addr" to "Performs a bitwise OR on two longs, storing the result in the first operand.",
    "xor-long/2addr" to "Performs a bitwise XOR on two longs, storing the result in the first operand.",
    "shl-long/2addr" to "Performs a left bit shift on a long, storing the result in the first operand.",
    "shr-long/2addr" to "Performs a right bit shift on a long, storing the result in the first operand.",
    "ushr-long/2addr" to "Performs an unsigned right bit shift on a long, storing the result in the first operand.",
    "add-float/2addr" to "Adds two floats, storing the result in the first operand.",
    "sub-float/2addr" to "Subtracts two floats, storing the result in the first operand.",
    "mul-float/2addr" to "Multiplies two floats, storing the result in the first operand.",
    "div-float/2addr" to "Divides two floats, storing the result in the first operand.",
    "rem-float/2addr" to "Calculates the remainder of float division, storing the result in the first operand.",
    "add-double/2addr" to "Adds two doubles, storing the result in the first operand.",
    "sub-double/2addr" to "Subtracts two doubles, storing the result in the first operand.",
    "mul-double/2addr" to "Multiplies two doubles, storing the result in the first operand.",
    "div-double/2addr" to "Divides two doubles, storing the result in the first operand.",
    "rem-double/2addr" to "Calculates the remainder of double division, storing the result in the first operand.",
    "throw-verification-error" to "Throws a verification error.",
    "goto/16" to "Unconditionally jumps to a new 16-bit instruction offset.",
    "sget" to "Gets a static field's integer value.",
    "sget-wide" to "Gets a static field's wide value (long or double).",
    "sget-object" to "Gets a static field's object value.",
    "sget-boolean" to "Gets a static field's boolean value.",
    "sget-byte" to "Gets a static field's byte value.",
    "sget-char" to "Gets a static field's char value.",
    "sget-short" to "Gets a static field's short value.",
    "sput" to "Puts an integer value into a static field.",
    "sput-wide" to "Puts a wide value (long or double) into a static field.",
    "sput-object" to "Puts an object value into a static field.",
    "sput-boolean" to "Puts a boolean value into a static field.",
    "sput-byte" to "Puts a byte value into a static field.",
    "sput-char" to "Puts a char value into a static field.",
    "sput-short" to "Puts a short value into a static field.",
    "sget-volatile" to "Gets a static volatile field's integer value.",
    "sget-wide-volatile" to "Gets a static volatile field's wide value.",
    "sget-object-volatile" to "Gets a static volatile field's object value.",
    "sput-volatile" to "Puts an integer value into a static volatile field.",
    "sput-wide-volatile" to "Puts a wide value into a static volatile field.",
    "sput-object-volatile" to "Puts an object value into a static volatile field.",
    "const-string" to "Loads a string constant into a register.",
    "check-cast" to "Checks if an object can be cast to a specified type.",
    "new-instance" to "Creates a new instance of an object.",
    "const-class" to "Loads a class constant into a register.",
    "const-method-handle" to "Loads a method handle constant into a register.",
    "const-method-type" to "Loads a method type constant into a register.",
    "const/high16" to "Moves a 32-bit literal (high 16 bits) to a register.",
    "const-wide/high16" to "Moves a 64-bit literal (high 16 bits) to a pair of registers.",
    "const/16" to "Moves a 16-bit literal value to a register.",
    "const-wide/16" to "Moves a 16-bit wide literal value to a pair of registers.",
    "if-eqz" to "Branches if a register's value is equal to zero.",
    "if-nez" to "Branches if a register's value is not equal to zero.",
    "if-ltz" to "Branches if a register's value is less than zero.",
    "if-gez" to "Branches if a register's value is greater than or equal to zero.",
    "if-gtz" to "Branches if a register's value is greater than zero.",
    "if-lez" to "Branches if a register's value is less than or equal to zero.",
    "add-int/lit8" to "Adds an 8-bit literal to an integer register.",
    "rsub-int/lit8" to "Subtracts an integer register from an 8-bit literal.",
    "mul-int/lit8" to "Multiplies an integer register by an 8-bit literal.",
    "div-int/lit8" to "Divides an integer register by an 8-bit literal.",
    "rem-int/lit8" to "Calculates the remainder of integer division with an 8-bit literal.",
    "and-int/lit8" to "Performs a bitwise AND on an integer register with an 8-bit literal.",
    "or-int/lit8" to "Performs a bitwise OR on an integer register with an 8-bit literal.",
    "xor-int/lit8" to "Performs a bitwise XOR on an integer register with an 8-bit literal.",
    "shl-int/lit8" to "Performs a left bit shift on an integer register by an 8-bit literal.",
    "shr-int/lit8" to "Performs a right bit shift on an integer register by an 8-bit literal.",
    "ushr-int/lit8" to "Performs an unsigned right bit shift on an integer register by an 8-bit literal.",
    "iget" to "Gets an instance field's integer value.",
    "iget-wide" to "Gets an instance field's wide value (long or double).",
    "iget-object" to "Gets an instance field's object value.",
    "iget-boolean" to "Gets an instance field's boolean value.",
    "iget-byte" to "Gets an instance field's byte value.",
    "iget-char" to "Gets an instance field's char value.",
    "iget-short" to "Gets an instance field's short value.",
    "iput" to "Puts an integer value into an instance field.",
    "iput-wide" to "Puts a wide value (long or double) into an instance field.",
    "iput-object" to "Puts an object value into an instance field.",
    "iput-boolean" to "Puts a boolean value into an instance field.",
    "iput-byte" to "Puts a byte value into an instance field.",
    "iput-char" to "Puts a char value into an instance field.",
    "iput-short" to "Puts a short value into an instance field.",
    "iget-volatile" to "Gets an instance volatile field's integer value.",
    "iget-wide-volatile" to "Gets an instance volatile field's wide value.",
    "iget-object-volatile" to "Gets an instance volatile field's object value.",
    "iput-volatile" to "Puts an integer value into an instance volatile field.",
    "iput-wide-volatile" to "Puts a wide value into an instance volatile field.",
    "iput-object-volatile" to "Puts an object value into an instance volatile field.",
    "instance-of" to "Checks if an object is an instance of a specified type.",
    "new-array" to "Creates a new array of a specified type.",
    "iget-quick" to "Gets an instance field's integer value quickly.",
    "iget-wide-quick" to "Gets an instance field's wide value quickly.",
    "iget-object-quick" to "Gets an instance field's object value quickly.",
    "iput-quick" to "Puts an integer value into an instance field quickly.",
    "iput-wide-quick" to "Puts a wide value into an instance field quickly.",
    "iput-object-quick" to "Puts an object value into an instance field quickly.",
    "iput-boolean-quick" to "Puts a boolean value into an instance field quickly.",
    "iput-byte-quick" to "Puts a byte value into an instance field quickly.",
    "iput-char-quick" to "Puts a char value into an instance field quickly.",
    "iput-short-quick" to "Puts a short value into an instance field quickly.",
    "rsub-int" to "Subtracts an integer register from a literal value.",
    "add-int/lit16" to "Adds a 16-bit literal to an integer register.",
    "mul-int/lit16" to "Multiplies an integer register by a 16-bit literal.",
    "div-int/lit16" to "Divides an integer register by a 16-bit literal.",
    "rem-int/lit16" to "Calculates the remainder of integer division with a 16-bit literal.",
    "and-int/lit16" to "Performs a bitwise AND on an integer register with a 16-bit literal.",
    "or-int/lit16" to "Performs a bitwise OR on an integer register with a 16-bit literal.",
    "xor-int/lit16" to "Performs a bitwise XOR on an integer register with a 16-bit literal.",
    "if-eq" to "Branches if two registers' values are equal.",
    "if-ne" to "Branches if two registers' values are not equal.",
    "if-lt" to "Branches if the first register's value is less than the second.",
    "if-ge" to "Branches if the first register's value is greater than or equal to the second.",
    "if-gt" to "Branches if the first register's value is greater than the second.",
    "if-le" to "Branches if the first register's value is less than or equal to the second.",
    "move/from16" to "Moves a register's content from a 16-bit source to a destination.",
    "move-wide/from16" to "Moves a wide register's content from a 16-bit source to a destination.",
    "move-object/from16" to "Moves an object register's content from a 16-bit source to a destination.",
    "cmpl-float" to "Compares two floats, less than (-1), equal (0), or greater than (1), handling NaNs.",
    "cmpg-float" to "Compares two floats, less than (-1), equal (0), or greater than (1), handling NaNs.",
    "cmpl-double" to "Compares two doubles, less than (-1), equal (0), or greater than (1), handling NaNs.",
    "cmpg-double" to "Compares two doubles, less than (-1), equal (0), or greater than (1), handling NaNs.",
    "cmp-long" to "Compares two longs, returning -1, 0, or 1.",
    "aget" to "Gets an integer element from an array.",
    "aget-wide" to "Gets a wide element (long or double) from an array.",
    "aget-object" to "Gets an object element from an array.",
    "aget-boolean" to "Gets a boolean element from an array.",
    "aget-byte" to "Gets a byte element from an array.",
    "aget-char" to "Gets a char element from an array.",
    "aget-short" to "Gets a short element from an array.",
    "aput" to "Puts an integer element into an array.",
    "aput-wide" to "Puts a wide element (long or double) into an array.",
    "aput-object" to "Puts an object element into an array.",
    "aput-boolean" to "Puts a boolean element into an array.",
    "aput-byte" to "Puts a byte element into an array.",
    "aput-char" to "Puts a char element into an array.",
    "aput-short" to "Puts a short element into an array.",
    "add-int" to "Adds two integers.",
    "sub-int" to "Subtracts two integers.",
    "mul-int" to "Multiplies two integers.",
    "div-int" to "Divides two integers.",
    "rem-int" to "Calculates the remainder of integer division.",
    "and-int" to "Performs a bitwise AND on two integers.",
    "or-int" to "Performs a bitwise OR on two integers.",
    "xor-int" to "Performs a bitwise XOR on two integers.",
    "shl-int" to "Performs a left bit shift on an integer.",
    "shr-int" to "Performs a right bit shift on an integer.",
    "ushr-int" to "Performs an unsigned right bit shift on an integer.",
    "add-long" to "Adds two longs.",
    "sub-long" to "Subtracts two longs.",
    "mul-long" to "Multiplies two longs.",
    "div-long" to "Divides two longs.",
    "rem-long" to "Calculates the remainder of long division.",
    "and-long" to "Performs a bitwise AND on two longs.",
    "or-long" to "Performs a bitwise OR on two longs.",
    "xor-long" to "Performs a bitwise XOR on two longs.",
    "shl-long" to "Performs a left bit shift on a long.",
    "shr-long" to "Performs a right bit shift on a long.",
    "ushr-long" to "Performs an unsigned right bit shift on a long.",
    "add-float" to "Adds two floats.",
    "sub-float" to "Subtracts two floats.",
    "mul-float" to "Multiplies two floats.",
    "div-float" to "Divides two floats.",
    "rem-float" to "Calculates the remainder of float division.",
    "add-double" to "Adds two doubles.",
    "sub-double" to "Subtracts two doubles.",
    "mul-double" to "Multiplies two doubles.",
    "div-double" to "Divides two doubles.",
    "rem-double" to "Calculates the remainder of double division.",
    "goto/32" to "Unconditionally jumps to a new 32-bit instruction offset.",
    "const-string/jumbo" to "Loads a jumbo string constant into a register.",
    "const" to "Moves a 32-bit literal value to a register.",
    "const-wide/32" to "Moves a 32-bit wide literal value to a pair of registers.",
    "fill-array-data" to "Fills an array with data from a data table.",
    "packed-switch" to "Performs a jump based on a packed switch statement.",
    "sparse-switch" to "Performs a jump based on a sparse switch statement.",
    "move/16" to "Moves a register's content with a 16-bit register operand.",
    "move-wide/16" to "Moves a wide register's content with a 16-bit register operand.",
    "move-object/16" to "Moves an object register's content with a 16-bit register operand.",
    "invoke-custom" to "Invokes a dynamically linked call site.",
    "invoke-virtual" to "Invokes a virtual method.",
    "invoke-super" to "Invokes a superclass method.",
    "invoke-direct" to "Invokes a direct method.",
    "invoke-static" to "Invokes a static method.",
    "invoke-interface" to "Invokes an interface method.",
    "invoke-direct-empty" to "Invokes a direct method (empty args optimization).",
    "filled-new-array" to "Creates a new array and fills it with elements.",
    "execute-inline" to "Executes an inline method.",
    "invoke-virtual-quick" to "Invokes a virtual method quickly.",
    "invoke-super-quick" to "Invokes a superclass method quickly.",
    "invoke-custom/range" to "Invokes a dynamically linked call site with a range of registers.",
    "invoke-virtual/range" to "Invokes a virtual method with a range of registers.",
    "invoke-super/range" to "Invokes a superclass method with a range of registers.",
    "invoke-direct/range" to "Invokes a direct method with a range of registers.",
    "invoke-static/range" to "Invokes a static method with a range of registers.",
    "invoke-interface/range" to "Invokes an interface method with a range of registers.",
    "invoke-object-init/range" to "Invokes an object initializer method with a range of registers.",
    "filled-new-array/range" to "Creates and fills a new array with elements from a range of registers.",
    "execute-inline/range" to "Executes an inline method with a range of registers.",
    "invoke-virtual-quick/range" to "Invokes a virtual method quickly with a range of registers.",
    "invoke-super-quick/range" to "Invokes a superclass method quickly with a range of registers.",
    "invoke-polymorphic" to "Invokes a polymorphic method.",
    "invoke-polymorphic/range" to "Invokes a polymorphic method with a range of registers.",
    "const-wide" to "Moves a 64-bit literal value to a pair of registers."
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
    }

    fun suggestNext(): List<AssistSuggestion> {
        if (currentToken.text.isEmpty()) return emptyList()
        val suggestions = mutableListOf<AssistSuggestion>()

        when {
            currentToken.text.startsWith(".") -> {
                val spanStyle = getSmaliTokenStyle(
                    smaliParser.METHOD_DIRECTIVE, highlightColors
                )
                // directive suggestions
                val directives = if (estimatedMethodRange.isEmpty()) outsideMethodDirectives else insideMethodDirectives
                directives.forEach { (directive, description) ->
                    if (startsWithNotEquals(directive)) {
                        suggestions.add(AssistSuggestion(directive, description, SuggestionType.Directive, spanStyle))
                    }
                }
            }

            !currentToken.text.isEmpty() -> {
                findAllTokensOfType(
                    smaliParser.SIMPLE_NAME,
                    smaliParser.CLASS_DESCRIPTOR,
                    smaliParser.ARRAY_TYPE_PREFIX,
                    smaliParser.PRIMITIVE_TYPE
                ).distinctBy { it.text }.forEach { token ->
                    if(token == currentToken) return@forEach
                    if (startsWithNotEquals(token.text)) {
                        val spanStyle = getSmaliTokenStyle(
                            token.type, highlightColors
                        )
                        suggestions.add(AssistSuggestion(token.text, "Re-use suggestion", SuggestionType.LabelOrType, spanStyle))
                    }
                }
            }
        }

        if (!estimatedMethodRange.isEmpty()) {
            if (currentToken.text.startsWith("v") || currentToken.text.startsWith("p")) {
                val spanStyle = getSmaliTokenStyle(
                    smaliParser.REGISTER, highlightColors
                )
                val registerCount = getRegisterCount()
                if (registerCount > 0) {
                    for (i in 0 until registerCount) {
                        val registerName = currentToken.text.substring(0, 1) + i
                        if (startsWithNotEquals(registerName)) {
                            suggestions.add(
                                AssistSuggestion(
                                    registerName,
                                    "Load register $registerName",
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
                if (startsWithNotEquals(opcode)) {
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
                    if (startsWithNotEquals(access)) {
                        suggestions.add(AssistSuggestion(access, description, SuggestionType.Access, spanStyle))
                    }
                }
            }
        }

        return suggestions
    }

    private fun startsWithNotEquals(directive: String): Boolean =
        directive.startsWith(currentToken.text) && !directive.equals(currentToken.text, ignoreCase = true)

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