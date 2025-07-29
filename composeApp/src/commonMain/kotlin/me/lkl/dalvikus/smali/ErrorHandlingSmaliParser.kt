package me.lkl.dalvikus.smali

import com.android.tools.smali.smali.smaliParser
import org.antlr.runtime.*

class ErrorHandlingSmaliParser(
    tokens: CommonTokenStream
) : smaliParser(tokens) {

    val errorTokens = mutableMapOf<CommonToken, String>()
    var errorLines: List<Int> = mutableListOf()

    override fun reportError(e: RecognitionException?) {
        try {
            // due to a bug in smali we have to try catch here. https://github.com/google/smali/issues/98
            super.reportError(e)
        } catch (ex: Exception) {
            // ignore.
        }
        val offending = e?.token
        if (offending is CommonToken) {
            errorTokens[offending] = getShortErrorMessage(e)
            if (offending.line !in errorLines) {
                errorLines += offending.line
            }
        }
    }

    fun getShortErrorMessage(e: RecognitionException): String {
        fun tokenName(id: Int): String = if (id == Token.EOF) "end of input" else tokenNames[id] ?: "<unknown>"
        val tokenText = e.token?.text ?: "<unknown>"

        return when (e) {
            is UnwantedTokenException -> {
                val expected = tokenName(e.expecting)
                "Unexpected '$tokenText', expected $expected"
            }

            is MissingTokenException -> {
                val expected = tokenName(e.expecting)
                "Missing $expected before '$tokenText'"
            }

            is MismatchedTokenException -> {
                val expected = tokenName(e.expecting)
                "Expected $expected but found '$tokenText'"
            }

            is MismatchedTreeNodeException -> {
                val expected = tokenName(e.expecting)
                "Wrong tree node '${e.node}', expected $expected"
            }

            is NoViableAltException -> {
                "Cannot parse '$tokenText'"
            }

            is EarlyExitException -> {
                "Incomplete input after '$tokenText'"
            }

            is MismatchedSetException -> {
                "Unexpected '$tokenText'"
            }

            is FailedPredicateException -> {
                "Rule '${e.ruleName}' failed: ${e.predicateText}"
            }

            else -> e.message ?: "Unknown error at '$tokenText'"
        }
    }
}
