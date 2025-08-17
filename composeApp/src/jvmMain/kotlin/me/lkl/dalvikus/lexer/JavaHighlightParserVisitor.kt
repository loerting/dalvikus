package me.lkl.dalvikus.lexer

import me.lkl.dalvikus.lexer.java.JavaParser
import me.lkl.dalvikus.lexer.java.JavaParserBaseVisitor

class JavaHighlightParserVisitor : JavaParserBaseVisitor<Unit>() {
    val methodNames = mutableSetOf<Pair<Int, Int>>()
    val classNames = mutableSetOf<Pair<Int, Int>>()
    val fieldNames = mutableSetOf<Pair<Int, Int>>()
    val variableNames = mutableSetOf<Pair<Int, Int>>()

    override fun visitVariableModifier(ctx: JavaParser.VariableModifierContext) {
        val start = ctx.start.startIndex
        val end = ctx.stop.stopIndex + 1
        classNames.add(Pair(start, end))
        super.visitVariableModifier(ctx)
    }

    override fun visitTypeType(ctx: JavaParser.TypeTypeContext) {
        val start = ctx.start.startIndex
        val end = ctx.stop.stopIndex + 1
        classNames.add(Pair(start, end))
        return super.visitTypeType(ctx)
    }

    override fun visitMemberReferenceExpression(ctx: JavaParser.MemberReferenceExpressionContext?) {
        ctx?.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            methodNames.add(Pair(start, end))
        }
        super.visitMemberReferenceExpression(ctx)
    }

    override fun visitMethodDeclaration(ctx: JavaParser.MethodDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            methodNames.add(Pair(start, end))
        }
        super.visitMethodDeclaration(ctx)
    }

    override fun visitConstructorDeclaration(ctx: JavaParser.ConstructorDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            methodNames.add(Pair(start, end))
        }
        super.visitConstructorDeclaration(ctx)
    }

    override fun visitClassDeclaration(ctx: JavaParser.ClassDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            classNames.add(Pair(start, end))
        }
        super.visitClassDeclaration(ctx)
    }

    override fun visitInterfaceDeclaration(ctx: JavaParser.InterfaceDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            classNames.add(Pair(start, end))
        }
        super.visitInterfaceDeclaration(ctx)
    }

    override fun visitEnumDeclaration(ctx: JavaParser.EnumDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            classNames.add(Pair(start, end))
        }
        super.visitEnumDeclaration(ctx)
    }

    override fun visitRecordDeclaration(ctx: JavaParser.RecordDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            classNames.add(Pair(start, end))
        }
        super.visitRecordDeclaration(ctx)
    }

    override fun visitFieldDeclaration(ctx: JavaParser.FieldDeclarationContext) {
        ctx.variableDeclarators()?.variableDeclarator()?.forEach { varDecl ->
            varDecl.variableDeclaratorId()?.identifier()?.let { identifier ->
                val start = identifier.start.startIndex
                val end = identifier.stop.stopIndex + 1
                fieldNames.add(Pair(start, end))
            }
        }
        super.visitFieldDeclaration(ctx)
    }

    override fun visitLocalVariableDeclaration(ctx: JavaParser.LocalVariableDeclarationContext) {
        ctx.variableModifier()
        ctx.variableDeclarators()?.variableDeclarator()?.forEach { varDecl ->
            varDecl.variableDeclaratorId()?.identifier()?.let { identifier ->
                val start = identifier.start.startIndex
                val end = identifier.stop.stopIndex + 1
                variableNames.add(Pair(start, end))
            }
        }


        super.visitLocalVariableDeclaration(ctx)
    }

    override fun visitFormalParameter(ctx: JavaParser.FormalParameterContext) {
        ctx.variableDeclaratorId()?.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            variableNames.add(Pair(start, end))
        }
        super.visitFormalParameter(ctx)
    }

    override fun visitMethodCall(ctx: JavaParser.MethodCallContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            methodNames.add(Pair(start, end))
        }
        super.visitMethodCall(ctx)
    }

    // New methods to detect additional cases
    override fun visitAnnotationTypeDeclaration(ctx: JavaParser.AnnotationTypeDeclarationContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            classNames.add(Pair(start, end))
        }
        super.visitAnnotationTypeDeclaration(ctx)
    }

    override fun visitLambdaLVTIParameter(ctx: JavaParser.LambdaLVTIParameterContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            variableNames.add(Pair(start, end))
        }
        super.visitLambdaLVTIParameter(ctx)
    }

    override fun visitResource(ctx: JavaParser.ResourceContext) {
        ctx.variableDeclaratorId()?.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            variableNames.add(Pair(start, end))
        }
        super.visitResource(ctx)
    }

    override fun visitForControl(ctx: JavaParser.ForControlContext) {
        ctx.forInit()?.let { forInit ->
            forInit.localVariableDeclaration()?.let { localVarDecl ->
                visitLocalVariableDeclaration(localVarDecl)
            }
        }
        super.visitForControl(ctx)
    }


    override fun visitEnhancedForControl(ctx: JavaParser.EnhancedForControlContext) {
        ctx.variableDeclaratorId()?.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            variableNames.add(Pair(start, end))
        }
        super.visitEnhancedForControl(ctx)
    }

    override fun visitCatchClause(ctx: JavaParser.CatchClauseContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            variableNames.add(Pair(start, end))
        }
        super.visitCatchClause(ctx)
    }

    override fun visitRecordComponent(ctx: JavaParser.RecordComponentContext) {
        ctx.identifier()?.let { identifier ->
            val start = identifier.start.startIndex
            val end = identifier.stop.stopIndex + 1
            variableNames.add(Pair(start, end))
        }
        super.visitRecordComponent(ctx)
    }
}