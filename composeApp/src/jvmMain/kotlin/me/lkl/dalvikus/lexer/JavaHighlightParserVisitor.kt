package me.lkl.dalvikus.lexer

import me.lkl.dalvikus.lexer.java.JavaParser
import me.lkl.dalvikus.lexer.java.JavaParserBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode

class JavaHighlightParserVisitor : JavaParserBaseVisitor<Unit>() {
    val methodNames = mutableSetOf<Pair<Int, Int>>()
    val classNames = mutableSetOf<Pair<Int, Int>>()
    val fieldNames = mutableSetOf<Pair<Int, Int>>()

    // Method declarations and invocations
    override fun visitMethodDeclaration(ctx: JavaParser.MethodDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            addRange(methodNames, identifier)
        }

        // Handle throws clauses
        ctx.qualifiedNameList()?.qualifiedName()?.forEach { name ->
            addRange(classNames, name)
        }

        super.visitMethodDeclaration(ctx)
    }

    override fun visitConstructorDeclaration(ctx: JavaParser.ConstructorDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            addRange(methodNames, identifier)
        }

        ctx.qualifiedNameList()?.qualifiedName()?.forEach { name ->
            addRange(classNames, name)
        }

        super.visitConstructorDeclaration(ctx)
    }

    override fun visitMethodCall(ctx: JavaParser.MethodCallContext) {
        when {
            ctx.identifier() != null -> {
                addRange(methodNames, ctx.identifier()!!)
            }
            ctx.THIS() != null -> addRange(methodNames, ctx.THIS()!!)
            ctx.SUPER() != null -> addRange(methodNames, ctx.SUPER()!!)
        }
        super.visitMethodCall(ctx)
    }

    override fun visitInterfaceCommonBodyDeclaration(ctx: JavaParser.InterfaceCommonBodyDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            addRange(methodNames, identifier)
        }
        ctx.qualifiedNameList()?.qualifiedName()?.forEach { name ->
            addRange(classNames, name)
        }
        return super.visitInterfaceCommonBodyDeclaration(ctx)
    }

    // Class/interface/record/enum declarations and references
    override fun visitClassDeclaration(ctx: JavaParser.ClassDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            addRange(classNames, identifier)
        }
        super.visitClassDeclaration(ctx)
    }

    override fun visitInterfaceDeclaration(ctx: JavaParser.InterfaceDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            addRange(classNames, identifier)
        }
        super.visitInterfaceDeclaration(ctx)
    }

    override fun visitRecordDeclaration(ctx: JavaParser.RecordDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            addRange(classNames, identifier)
        }
        super.visitRecordDeclaration(ctx)
    }

    override fun visitEnumDeclaration(ctx: JavaParser.EnumDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            addRange(classNames, identifier)
        }
        super.visitEnumDeclaration(ctx)
    }

    override fun visitClassOrInterfaceType(ctx: JavaParser.ClassOrInterfaceTypeContext) {
        ctx.typeIdentifier()?.let { identifier ->
            addRange(classNames, identifier)
        }
        super.visitClassOrInterfaceType(ctx)
    }

    override fun visitCreator(ctx: JavaParser.CreatorContext) {
        ctx.createdName()?.identifier()?.forEach { identifier ->
            addRange(classNames, identifier)
        }
        super.visitCreator(ctx)
    }

    // Field declarations and accesses
    override fun visitFieldDeclaration(ctx: JavaParser.FieldDeclarationContext) {
        ctx.variableDeclarators().variableDeclarator().forEach { declarator ->
            declarator.variableDeclaratorId().identifier()?.let { identifier ->
                addRange(fieldNames, identifier)
            }
        }
        super.visitFieldDeclaration(ctx)
    }

    override fun visitMemberReferenceExpression(ctx: JavaParser.MemberReferenceExpressionContext) {
        when {
            ctx.methodCall() != null -> {
                // This is a method call, handled elsewhere
            }
            ctx.identifier() != null -> {
                addRange(fieldNames, ctx.identifier()!!)
            }
        }
        super.visitMemberReferenceExpression(ctx)
    }

    // Helper functions
    private fun addRange(collection: MutableSet<Pair<Int, Int>>, node: TerminalNode) {
        val start = node.symbol.startIndex
        val end = node.symbol.stopIndex + 1
        collection.add(Pair(start, end))
    }

    private fun addRange(collection: MutableSet<Pair<Int, Int>>, ctx: ParserRuleContext) {
        val start = ctx.start.startIndex
        val end = ctx.stop.stopIndex + 1
        collection.add(Pair(start, end))
    }

    private fun isClassified(identifier: JavaParser.IdentifierContext): Boolean {
        val start = identifier.start.startIndex
        val end = identifier.stop.stopIndex + 1
        return methodNames.any { it.first == start && it.second == end } ||
                classNames.any { it.first == start && it.second == end } ||
                fieldNames.any { it.first == start && it.second == end }
    }
}