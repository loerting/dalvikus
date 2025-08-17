package me.lkl.dalvikus.lexer.java;// Generated from Java20Parser.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Java20Parser}.
 */
public interface Java20ParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Java20Parser#start_}.
	 * @param ctx the parse tree
	 */
	void enterStart_(Java20Parser.Start_Context ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#start_}.
	 * @param ctx the parse tree
	 */
	void exitStart_(Java20Parser.Start_Context ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(Java20Parser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(Java20Parser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeIdentifier(Java20Parser.TypeIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeIdentifier(Java20Parser.TypeIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unqualifiedMethodIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterUnqualifiedMethodIdentifier(Java20Parser.UnqualifiedMethodIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unqualifiedMethodIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitUnqualifiedMethodIdentifier(Java20Parser.UnqualifiedMethodIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#contextualKeyword}.
	 * @param ctx the parse tree
	 */
	void enterContextualKeyword(Java20Parser.ContextualKeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#contextualKeyword}.
	 * @param ctx the parse tree
	 */
	void exitContextualKeyword(Java20Parser.ContextualKeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#contextualKeywordMinusForTypeIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterContextualKeywordMinusForTypeIdentifier(Java20Parser.ContextualKeywordMinusForTypeIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#contextualKeywordMinusForTypeIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitContextualKeywordMinusForTypeIdentifier(Java20Parser.ContextualKeywordMinusForTypeIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#contextualKeywordMinusForUnqualifiedMethodIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterContextualKeywordMinusForUnqualifiedMethodIdentifier(Java20Parser.ContextualKeywordMinusForUnqualifiedMethodIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#contextualKeywordMinusForUnqualifiedMethodIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitContextualKeywordMinusForUnqualifiedMethodIdentifier(Java20Parser.ContextualKeywordMinusForUnqualifiedMethodIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(Java20Parser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(Java20Parser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(Java20Parser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(Java20Parser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#numericType}.
	 * @param ctx the parse tree
	 */
	void enterNumericType(Java20Parser.NumericTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#numericType}.
	 * @param ctx the parse tree
	 */
	void exitNumericType(Java20Parser.NumericTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#integralType}.
	 * @param ctx the parse tree
	 */
	void enterIntegralType(Java20Parser.IntegralTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#integralType}.
	 * @param ctx the parse tree
	 */
	void exitIntegralType(Java20Parser.IntegralTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#floatingPointType}.
	 * @param ctx the parse tree
	 */
	void enterFloatingPointType(Java20Parser.FloatingPointTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#floatingPointType}.
	 * @param ctx the parse tree
	 */
	void exitFloatingPointType(Java20Parser.FloatingPointTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#referenceType}.
	 * @param ctx the parse tree
	 */
	void enterReferenceType(Java20Parser.ReferenceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#referenceType}.
	 * @param ctx the parse tree
	 */
	void exitReferenceType(Java20Parser.ReferenceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#coit}.
	 * @param ctx the parse tree
	 */
	void enterCoit(Java20Parser.CoitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#coit}.
	 * @param ctx the parse tree
	 */
	void exitCoit(Java20Parser.CoitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceType(Java20Parser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceType(Java20Parser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(Java20Parser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(Java20Parser.ClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceType}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceType(Java20Parser.InterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceType}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceType(Java20Parser.InterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeVariable}.
	 * @param ctx the parse tree
	 */
	void enterTypeVariable(Java20Parser.TypeVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeVariable}.
	 * @param ctx the parse tree
	 */
	void exitTypeVariable(Java20Parser.TypeVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(Java20Parser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(Java20Parser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#dims}.
	 * @param ctx the parse tree
	 */
	void enterDims(Java20Parser.DimsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#dims}.
	 * @param ctx the parse tree
	 */
	void exitDims(Java20Parser.DimsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameter(Java20Parser.TypeParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameter(Java20Parser.TypeParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeParameterModifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameterModifier(Java20Parser.TypeParameterModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeParameterModifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameterModifier(Java20Parser.TypeParameterModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeBound}.
	 * @param ctx the parse tree
	 */
	void enterTypeBound(Java20Parser.TypeBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeBound}.
	 * @param ctx the parse tree
	 */
	void exitTypeBound(Java20Parser.TypeBoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#additionalBound}.
	 * @param ctx the parse tree
	 */
	void enterAdditionalBound(Java20Parser.AdditionalBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#additionalBound}.
	 * @param ctx the parse tree
	 */
	void exitAdditionalBound(Java20Parser.AdditionalBoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void enterTypeArguments(Java20Parser.TypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void exitTypeArguments(Java20Parser.TypeArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeArgumentList}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgumentList(Java20Parser.TypeArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeArgumentList}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgumentList(Java20Parser.TypeArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgument(Java20Parser.TypeArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgument(Java20Parser.TypeArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#wildcard}.
	 * @param ctx the parse tree
	 */
	void enterWildcard(Java20Parser.WildcardContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#wildcard}.
	 * @param ctx the parse tree
	 */
	void exitWildcard(Java20Parser.WildcardContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#wildcardBounds}.
	 * @param ctx the parse tree
	 */
	void enterWildcardBounds(Java20Parser.WildcardBoundsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#wildcardBounds}.
	 * @param ctx the parse tree
	 */
	void exitWildcardBounds(Java20Parser.WildcardBoundsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#moduleName}.
	 * @param ctx the parse tree
	 */
	void enterModuleName(Java20Parser.ModuleNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#moduleName}.
	 * @param ctx the parse tree
	 */
	void exitModuleName(Java20Parser.ModuleNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#packageName}.
	 * @param ctx the parse tree
	 */
	void enterPackageName(Java20Parser.PackageNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#packageName}.
	 * @param ctx the parse tree
	 */
	void exitPackageName(Java20Parser.PackageNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterTypeName(Java20Parser.TypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitTypeName(Java20Parser.TypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#packageOrTypeName}.
	 * @param ctx the parse tree
	 */
	void enterPackageOrTypeName(Java20Parser.PackageOrTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#packageOrTypeName}.
	 * @param ctx the parse tree
	 */
	void exitPackageOrTypeName(Java20Parser.PackageOrTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#expressionName}.
	 * @param ctx the parse tree
	 */
	void enterExpressionName(Java20Parser.ExpressionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#expressionName}.
	 * @param ctx the parse tree
	 */
	void exitExpressionName(Java20Parser.ExpressionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#methodName}.
	 * @param ctx the parse tree
	 */
	void enterMethodName(Java20Parser.MethodNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#methodName}.
	 * @param ctx the parse tree
	 */
	void exitMethodName(Java20Parser.MethodNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#ambiguousName}.
	 * @param ctx the parse tree
	 */
	void enterAmbiguousName(Java20Parser.AmbiguousNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#ambiguousName}.
	 * @param ctx the parse tree
	 */
	void exitAmbiguousName(Java20Parser.AmbiguousNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(Java20Parser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(Java20Parser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#ordinaryCompilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterOrdinaryCompilationUnit(Java20Parser.OrdinaryCompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#ordinaryCompilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitOrdinaryCompilationUnit(Java20Parser.OrdinaryCompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#modularCompilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterModularCompilationUnit(Java20Parser.ModularCompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#modularCompilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitModularCompilationUnit(Java20Parser.ModularCompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPackageDeclaration(Java20Parser.PackageDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPackageDeclaration(Java20Parser.PackageDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#packageModifier}.
	 * @param ctx the parse tree
	 */
	void enterPackageModifier(Java20Parser.PackageModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#packageModifier}.
	 * @param ctx the parse tree
	 */
	void exitPackageModifier(Java20Parser.PackageModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(Java20Parser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(Java20Parser.ImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#singleTypeImportDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterSingleTypeImportDeclaration(Java20Parser.SingleTypeImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#singleTypeImportDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitSingleTypeImportDeclaration(Java20Parser.SingleTypeImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeImportOnDemandDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeImportOnDemandDeclaration(Java20Parser.TypeImportOnDemandDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeImportOnDemandDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeImportOnDemandDeclaration(Java20Parser.TypeImportOnDemandDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#singleStaticImportDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterSingleStaticImportDeclaration(Java20Parser.SingleStaticImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#singleStaticImportDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitSingleStaticImportDeclaration(Java20Parser.SingleStaticImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#staticImportOnDemandDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStaticImportOnDemandDeclaration(Java20Parser.StaticImportOnDemandDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#staticImportOnDemandDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStaticImportOnDemandDeclaration(Java20Parser.StaticImportOnDemandDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#topLevelClassOrInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTopLevelClassOrInterfaceDeclaration(Java20Parser.TopLevelClassOrInterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#topLevelClassOrInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTopLevelClassOrInterfaceDeclaration(Java20Parser.TopLevelClassOrInterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#moduleDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterModuleDeclaration(Java20Parser.ModuleDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#moduleDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitModuleDeclaration(Java20Parser.ModuleDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#moduleDirective}.
	 * @param ctx the parse tree
	 */
	void enterModuleDirective(Java20Parser.ModuleDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#moduleDirective}.
	 * @param ctx the parse tree
	 */
	void exitModuleDirective(Java20Parser.ModuleDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#requiresModifier}.
	 * @param ctx the parse tree
	 */
	void enterRequiresModifier(Java20Parser.RequiresModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#requiresModifier}.
	 * @param ctx the parse tree
	 */
	void exitRequiresModifier(Java20Parser.RequiresModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(Java20Parser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(Java20Parser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#normalClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterNormalClassDeclaration(Java20Parser.NormalClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#normalClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitNormalClassDeclaration(Java20Parser.NormalClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classModifier}.
	 * @param ctx the parse tree
	 */
	void enterClassModifier(Java20Parser.ClassModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classModifier}.
	 * @param ctx the parse tree
	 */
	void exitClassModifier(Java20Parser.ClassModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameters(Java20Parser.TypeParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameters(Java20Parser.TypeParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeParameterList}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameterList(Java20Parser.TypeParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeParameterList}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameterList(Java20Parser.TypeParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classExtends}.
	 * @param ctx the parse tree
	 */
	void enterClassExtends(Java20Parser.ClassExtendsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classExtends}.
	 * @param ctx the parse tree
	 */
	void exitClassExtends(Java20Parser.ClassExtendsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classImplements}.
	 * @param ctx the parse tree
	 */
	void enterClassImplements(Java20Parser.ClassImplementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classImplements}.
	 * @param ctx the parse tree
	 */
	void exitClassImplements(Java20Parser.ClassImplementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceTypeList}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceTypeList(Java20Parser.InterfaceTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceTypeList}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceTypeList(Java20Parser.InterfaceTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classPermits}.
	 * @param ctx the parse tree
	 */
	void enterClassPermits(Java20Parser.ClassPermitsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classPermits}.
	 * @param ctx the parse tree
	 */
	void exitClassPermits(Java20Parser.ClassPermitsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(Java20Parser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(Java20Parser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(Java20Parser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(Java20Parser.ClassBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassMemberDeclaration(Java20Parser.ClassMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassMemberDeclaration(Java20Parser.ClassMemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(Java20Parser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(Java20Parser.FieldDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#fieldModifier}.
	 * @param ctx the parse tree
	 */
	void enterFieldModifier(Java20Parser.FieldModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#fieldModifier}.
	 * @param ctx the parse tree
	 */
	void exitFieldModifier(Java20Parser.FieldModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorList(Java20Parser.VariableDeclaratorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorList(Java20Parser.VariableDeclaratorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(Java20Parser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(Java20Parser.VariableDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorId(Java20Parser.VariableDeclaratorIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorId(Java20Parser.VariableDeclaratorIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(Java20Parser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(Java20Parser.VariableInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unannType}.
	 * @param ctx the parse tree
	 */
	void enterUnannType(Java20Parser.UnannTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unannType}.
	 * @param ctx the parse tree
	 */
	void exitUnannType(Java20Parser.UnannTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unannPrimitiveType}.
	 * @param ctx the parse tree
	 */
	void enterUnannPrimitiveType(Java20Parser.UnannPrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unannPrimitiveType}.
	 * @param ctx the parse tree
	 */
	void exitUnannPrimitiveType(Java20Parser.UnannPrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unannReferenceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannReferenceType(Java20Parser.UnannReferenceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unannReferenceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannReferenceType(Java20Parser.UnannReferenceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannClassOrInterfaceType(Java20Parser.UnannClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannClassOrInterfaceType(Java20Parser.UnannClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#uCOIT}.
	 * @param ctx the parse tree
	 */
	void enterUCOIT(Java20Parser.UCOITContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#uCOIT}.
	 * @param ctx the parse tree
	 */
	void exitUCOIT(Java20Parser.UCOITContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unannClassType}.
	 * @param ctx the parse tree
	 */
	void enterUnannClassType(Java20Parser.UnannClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unannClassType}.
	 * @param ctx the parse tree
	 */
	void exitUnannClassType(Java20Parser.UnannClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unannInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannInterfaceType(Java20Parser.UnannInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unannInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannInterfaceType(Java20Parser.UnannInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unannTypeVariable}.
	 * @param ctx the parse tree
	 */
	void enterUnannTypeVariable(Java20Parser.UnannTypeVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unannTypeVariable}.
	 * @param ctx the parse tree
	 */
	void exitUnannTypeVariable(Java20Parser.UnannTypeVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unannArrayType}.
	 * @param ctx the parse tree
	 */
	void enterUnannArrayType(Java20Parser.UnannArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unannArrayType}.
	 * @param ctx the parse tree
	 */
	void exitUnannArrayType(Java20Parser.UnannArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(Java20Parser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(Java20Parser.MethodDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#methodModifier}.
	 * @param ctx the parse tree
	 */
	void enterMethodModifier(Java20Parser.MethodModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#methodModifier}.
	 * @param ctx the parse tree
	 */
	void exitMethodModifier(Java20Parser.MethodModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#methodHeader}.
	 * @param ctx the parse tree
	 */
	void enterMethodHeader(Java20Parser.MethodHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#methodHeader}.
	 * @param ctx the parse tree
	 */
	void exitMethodHeader(Java20Parser.MethodHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#result}.
	 * @param ctx the parse tree
	 */
	void enterResult(Java20Parser.ResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#result}.
	 * @param ctx the parse tree
	 */
	void exitResult(Java20Parser.ResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#methodDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarator(Java20Parser.MethodDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#methodDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarator(Java20Parser.MethodDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#receiverParameter}.
	 * @param ctx the parse tree
	 */
	void enterReceiverParameter(Java20Parser.ReceiverParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#receiverParameter}.
	 * @param ctx the parse tree
	 */
	void exitReceiverParameter(Java20Parser.ReceiverParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(Java20Parser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(Java20Parser.FormalParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(Java20Parser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(Java20Parser.FormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableArityParameter}.
	 * @param ctx the parse tree
	 */
	void enterVariableArityParameter(Java20Parser.VariableArityParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableArityParameter}.
	 * @param ctx the parse tree
	 */
	void exitVariableArityParameter(Java20Parser.VariableArityParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void enterVariableModifier(Java20Parser.VariableModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void exitVariableModifier(Java20Parser.VariableModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#throwsT}.
	 * @param ctx the parse tree
	 */
	void enterThrowsT(Java20Parser.ThrowsTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#throwsT}.
	 * @param ctx the parse tree
	 */
	void exitThrowsT(Java20Parser.ThrowsTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#exceptionTypeList}.
	 * @param ctx the parse tree
	 */
	void enterExceptionTypeList(Java20Parser.ExceptionTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#exceptionTypeList}.
	 * @param ctx the parse tree
	 */
	void exitExceptionTypeList(Java20Parser.ExceptionTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#exceptionType}.
	 * @param ctx the parse tree
	 */
	void enterExceptionType(Java20Parser.ExceptionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#exceptionType}.
	 * @param ctx the parse tree
	 */
	void exitExceptionType(Java20Parser.ExceptionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody(Java20Parser.MethodBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody(Java20Parser.MethodBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#instanceInitializer}.
	 * @param ctx the parse tree
	 */
	void enterInstanceInitializer(Java20Parser.InstanceInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#instanceInitializer}.
	 * @param ctx the parse tree
	 */
	void exitInstanceInitializer(Java20Parser.InstanceInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#staticInitializer}.
	 * @param ctx the parse tree
	 */
	void enterStaticInitializer(Java20Parser.StaticInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#staticInitializer}.
	 * @param ctx the parse tree
	 */
	void exitStaticInitializer(Java20Parser.StaticInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(Java20Parser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(Java20Parser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#constructorModifier}.
	 * @param ctx the parse tree
	 */
	void enterConstructorModifier(Java20Parser.ConstructorModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#constructorModifier}.
	 * @param ctx the parse tree
	 */
	void exitConstructorModifier(Java20Parser.ConstructorModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#constructorDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclarator(Java20Parser.ConstructorDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#constructorDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclarator(Java20Parser.ConstructorDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#simpleTypeName}.
	 * @param ctx the parse tree
	 */
	void enterSimpleTypeName(Java20Parser.SimpleTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#simpleTypeName}.
	 * @param ctx the parse tree
	 */
	void exitSimpleTypeName(Java20Parser.SimpleTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBody(Java20Parser.ConstructorBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBody(Java20Parser.ConstructorBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitConstructorInvocation(Java20Parser.ExplicitConstructorInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitConstructorInvocation(Java20Parser.ExplicitConstructorInvocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterEnumDeclaration(Java20Parser.EnumDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitEnumDeclaration(Java20Parser.EnumDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#enumBody}.
	 * @param ctx the parse tree
	 */
	void enterEnumBody(Java20Parser.EnumBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#enumBody}.
	 * @param ctx the parse tree
	 */
	void exitEnumBody(Java20Parser.EnumBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#enumConstantList}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstantList(Java20Parser.EnumConstantListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#enumConstantList}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstantList(Java20Parser.EnumConstantListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#enumConstant}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstant(Java20Parser.EnumConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#enumConstant}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstant(Java20Parser.EnumConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#enumConstantModifier}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstantModifier(Java20Parser.EnumConstantModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#enumConstantModifier}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstantModifier(Java20Parser.EnumConstantModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 */
	void enterEnumBodyDeclarations(Java20Parser.EnumBodyDeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 */
	void exitEnumBodyDeclarations(Java20Parser.EnumBodyDeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#recordDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterRecordDeclaration(Java20Parser.RecordDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#recordDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitRecordDeclaration(Java20Parser.RecordDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#recordHeader}.
	 * @param ctx the parse tree
	 */
	void enterRecordHeader(Java20Parser.RecordHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#recordHeader}.
	 * @param ctx the parse tree
	 */
	void exitRecordHeader(Java20Parser.RecordHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#recordComponentList}.
	 * @param ctx the parse tree
	 */
	void enterRecordComponentList(Java20Parser.RecordComponentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#recordComponentList}.
	 * @param ctx the parse tree
	 */
	void exitRecordComponentList(Java20Parser.RecordComponentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#recordComponent}.
	 * @param ctx the parse tree
	 */
	void enterRecordComponent(Java20Parser.RecordComponentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#recordComponent}.
	 * @param ctx the parse tree
	 */
	void exitRecordComponent(Java20Parser.RecordComponentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableArityRecordComponent}.
	 * @param ctx the parse tree
	 */
	void enterVariableArityRecordComponent(Java20Parser.VariableArityRecordComponentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableArityRecordComponent}.
	 * @param ctx the parse tree
	 */
	void exitVariableArityRecordComponent(Java20Parser.VariableArityRecordComponentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#recordComponentModifier}.
	 * @param ctx the parse tree
	 */
	void enterRecordComponentModifier(Java20Parser.RecordComponentModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#recordComponentModifier}.
	 * @param ctx the parse tree
	 */
	void exitRecordComponentModifier(Java20Parser.RecordComponentModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#recordBody}.
	 * @param ctx the parse tree
	 */
	void enterRecordBody(Java20Parser.RecordBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#recordBody}.
	 * @param ctx the parse tree
	 */
	void exitRecordBody(Java20Parser.RecordBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#recordBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterRecordBodyDeclaration(Java20Parser.RecordBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#recordBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitRecordBodyDeclaration(Java20Parser.RecordBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#compactConstructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterCompactConstructorDeclaration(Java20Parser.CompactConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#compactConstructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitCompactConstructorDeclaration(Java20Parser.CompactConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceDeclaration(Java20Parser.InterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceDeclaration(Java20Parser.InterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#normalInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterNormalInterfaceDeclaration(Java20Parser.NormalInterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#normalInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitNormalInterfaceDeclaration(Java20Parser.NormalInterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceModifier}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceModifier(Java20Parser.InterfaceModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceModifier}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceModifier(Java20Parser.InterfaceModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceExtends}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceExtends(Java20Parser.InterfaceExtendsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceExtends}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceExtends(Java20Parser.InterfaceExtendsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfacePermits}.
	 * @param ctx the parse tree
	 */
	void enterInterfacePermits(Java20Parser.InterfacePermitsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfacePermits}.
	 * @param ctx the parse tree
	 */
	void exitInterfacePermits(Java20Parser.InterfacePermitsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBody(Java20Parser.InterfaceBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBody(Java20Parser.InterfaceBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclaration(Java20Parser.InterfaceMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclaration(Java20Parser.InterfaceMemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#constantDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstantDeclaration(Java20Parser.ConstantDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#constantDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstantDeclaration(Java20Parser.ConstantDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#constantModifier}.
	 * @param ctx the parse tree
	 */
	void enterConstantModifier(Java20Parser.ConstantModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#constantModifier}.
	 * @param ctx the parse tree
	 */
	void exitConstantModifier(Java20Parser.ConstantModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodDeclaration(Java20Parser.InterfaceMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodDeclaration(Java20Parser.InterfaceMethodDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#interfaceMethodModifier}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodModifier(Java20Parser.InterfaceMethodModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#interfaceMethodModifier}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodModifier(Java20Parser.InterfaceMethodModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#annotationInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationInterfaceDeclaration(Java20Parser.AnnotationInterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#annotationInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationInterfaceDeclaration(Java20Parser.AnnotationInterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#annotationInterfaceBody}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationInterfaceBody(Java20Parser.AnnotationInterfaceBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#annotationInterfaceBody}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationInterfaceBody(Java20Parser.AnnotationInterfaceBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#annotationInterfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationInterfaceMemberDeclaration(Java20Parser.AnnotationInterfaceMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#annotationInterfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationInterfaceMemberDeclaration(Java20Parser.AnnotationInterfaceMemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#annotationInterfaceElementDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationInterfaceElementDeclaration(Java20Parser.AnnotationInterfaceElementDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#annotationInterfaceElementDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationInterfaceElementDeclaration(Java20Parser.AnnotationInterfaceElementDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#annotationInterfaceElementModifier}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationInterfaceElementModifier(Java20Parser.AnnotationInterfaceElementModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#annotationInterfaceElementModifier}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationInterfaceElementModifier(Java20Parser.AnnotationInterfaceElementModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValue(Java20Parser.DefaultValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValue(Java20Parser.DefaultValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(Java20Parser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(Java20Parser.AnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#normalAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterNormalAnnotation(Java20Parser.NormalAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#normalAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitNormalAnnotation(Java20Parser.NormalAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#elementValuePairList}.
	 * @param ctx the parse tree
	 */
	void enterElementValuePairList(Java20Parser.ElementValuePairListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#elementValuePairList}.
	 * @param ctx the parse tree
	 */
	void exitElementValuePairList(Java20Parser.ElementValuePairListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#elementValuePair}.
	 * @param ctx the parse tree
	 */
	void enterElementValuePair(Java20Parser.ElementValuePairContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#elementValuePair}.
	 * @param ctx the parse tree
	 */
	void exitElementValuePair(Java20Parser.ElementValuePairContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#elementValue}.
	 * @param ctx the parse tree
	 */
	void enterElementValue(Java20Parser.ElementValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#elementValue}.
	 * @param ctx the parse tree
	 */
	void exitElementValue(Java20Parser.ElementValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterElementValueArrayInitializer(Java20Parser.ElementValueArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitElementValueArrayInitializer(Java20Parser.ElementValueArrayInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#elementValueList}.
	 * @param ctx the parse tree
	 */
	void enterElementValueList(Java20Parser.ElementValueListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#elementValueList}.
	 * @param ctx the parse tree
	 */
	void exitElementValueList(Java20Parser.ElementValueListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#markerAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterMarkerAnnotation(Java20Parser.MarkerAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#markerAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitMarkerAnnotation(Java20Parser.MarkerAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#singleElementAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterSingleElementAnnotation(Java20Parser.SingleElementAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#singleElementAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitSingleElementAnnotation(Java20Parser.SingleElementAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayInitializer(Java20Parser.ArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayInitializer(Java20Parser.ArrayInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableInitializerList}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializerList(Java20Parser.VariableInitializerListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableInitializerList}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializerList(Java20Parser.VariableInitializerListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(Java20Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(Java20Parser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatements(Java20Parser.BlockStatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatements(Java20Parser.BlockStatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(Java20Parser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(Java20Parser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#localClassOrInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalClassOrInterfaceDeclaration(Java20Parser.LocalClassOrInterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#localClassOrInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalClassOrInterfaceDeclaration(Java20Parser.LocalClassOrInterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclaration(Java20Parser.LocalVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclaration(Java20Parser.LocalVariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#localVariableType}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableType(Java20Parser.LocalVariableTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#localVariableType}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableType(Java20Parser.LocalVariableTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclarationStatement(Java20Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclarationStatement(Java20Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(Java20Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(Java20Parser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#statementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterStatementNoShortIf(Java20Parser.StatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#statementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitStatementNoShortIf(Java20Parser.StatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#statementWithoutTrailingSubstatement}.
	 * @param ctx the parse tree
	 */
	void enterStatementWithoutTrailingSubstatement(Java20Parser.StatementWithoutTrailingSubstatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#statementWithoutTrailingSubstatement}.
	 * @param ctx the parse tree
	 */
	void exitStatementWithoutTrailingSubstatement(Java20Parser.StatementWithoutTrailingSubstatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#emptyStatement_}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStatement_(Java20Parser.EmptyStatement_Context ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#emptyStatement_}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStatement_(Java20Parser.EmptyStatement_Context ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#labeledStatement}.
	 * @param ctx the parse tree
	 */
	void enterLabeledStatement(Java20Parser.LabeledStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#labeledStatement}.
	 * @param ctx the parse tree
	 */
	void exitLabeledStatement(Java20Parser.LabeledStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#labeledStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterLabeledStatementNoShortIf(Java20Parser.LabeledStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#labeledStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitLabeledStatementNoShortIf(Java20Parser.LabeledStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(Java20Parser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(Java20Parser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpression(Java20Parser.StatementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpression(Java20Parser.StatementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#ifThenStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfThenStatement(Java20Parser.IfThenStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#ifThenStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfThenStatement(Java20Parser.IfThenStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#ifThenElseStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfThenElseStatement(Java20Parser.IfThenElseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#ifThenElseStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfThenElseStatement(Java20Parser.IfThenElseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#ifThenElseStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterIfThenElseStatementNoShortIf(Java20Parser.IfThenElseStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#ifThenElseStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitIfThenElseStatementNoShortIf(Java20Parser.IfThenElseStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssertStatement(Java20Parser.AssertStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssertStatement(Java20Parser.AssertStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void enterSwitchStatement(Java20Parser.SwitchStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void exitSwitchStatement(Java20Parser.SwitchStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#switchBlock}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlock(Java20Parser.SwitchBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#switchBlock}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlock(Java20Parser.SwitchBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#switchRule}.
	 * @param ctx the parse tree
	 */
	void enterSwitchRule(Java20Parser.SwitchRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#switchRule}.
	 * @param ctx the parse tree
	 */
	void exitSwitchRule(Java20Parser.SwitchRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroup(Java20Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroup(Java20Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel(Java20Parser.SwitchLabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel(Java20Parser.SwitchLabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#caseConstant}.
	 * @param ctx the parse tree
	 */
	void enterCaseConstant(Java20Parser.CaseConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#caseConstant}.
	 * @param ctx the parse tree
	 */
	void exitCaseConstant(Java20Parser.CaseConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(Java20Parser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(Java20Parser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#whileStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatementNoShortIf(Java20Parser.WhileStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#whileStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatementNoShortIf(Java20Parser.WhileStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#doStatement}.
	 * @param ctx the parse tree
	 */
	void enterDoStatement(Java20Parser.DoStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#doStatement}.
	 * @param ctx the parse tree
	 */
	void exitDoStatement(Java20Parser.DoStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(Java20Parser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(Java20Parser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#forStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterForStatementNoShortIf(Java20Parser.ForStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#forStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitForStatementNoShortIf(Java20Parser.ForStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#basicForStatement}.
	 * @param ctx the parse tree
	 */
	void enterBasicForStatement(Java20Parser.BasicForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#basicForStatement}.
	 * @param ctx the parse tree
	 */
	void exitBasicForStatement(Java20Parser.BasicForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#basicForStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterBasicForStatementNoShortIf(Java20Parser.BasicForStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#basicForStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitBasicForStatementNoShortIf(Java20Parser.BasicForStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(Java20Parser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(Java20Parser.ForInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(Java20Parser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(Java20Parser.ForUpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#statementExpressionList}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpressionList(Java20Parser.StatementExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#statementExpressionList}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpressionList(Java20Parser.StatementExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#enhancedForStatement}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForStatement(Java20Parser.EnhancedForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#enhancedForStatement}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForStatement(Java20Parser.EnhancedForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#enhancedForStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForStatementNoShortIf(Java20Parser.EnhancedForStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#enhancedForStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForStatementNoShortIf(Java20Parser.EnhancedForStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(Java20Parser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(Java20Parser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(Java20Parser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(Java20Parser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(Java20Parser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(Java20Parser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void enterThrowStatement(Java20Parser.ThrowStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void exitThrowStatement(Java20Parser.ThrowStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#synchronizedStatement}.
	 * @param ctx the parse tree
	 */
	void enterSynchronizedStatement(Java20Parser.SynchronizedStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#synchronizedStatement}.
	 * @param ctx the parse tree
	 */
	void exitSynchronizedStatement(Java20Parser.SynchronizedStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryStatement(Java20Parser.TryStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryStatement(Java20Parser.TryStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#catches}.
	 * @param ctx the parse tree
	 */
	void enterCatches(Java20Parser.CatchesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#catches}.
	 * @param ctx the parse tree
	 */
	void exitCatches(Java20Parser.CatchesContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void enterCatchClause(Java20Parser.CatchClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void exitCatchClause(Java20Parser.CatchClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#catchFormalParameter}.
	 * @param ctx the parse tree
	 */
	void enterCatchFormalParameter(Java20Parser.CatchFormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#catchFormalParameter}.
	 * @param ctx the parse tree
	 */
	void exitCatchFormalParameter(Java20Parser.CatchFormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#catchType}.
	 * @param ctx the parse tree
	 */
	void enterCatchType(Java20Parser.CatchTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#catchType}.
	 * @param ctx the parse tree
	 */
	void exitCatchType(Java20Parser.CatchTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void enterFinallyBlock(Java20Parser.FinallyBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void exitFinallyBlock(Java20Parser.FinallyBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#tryWithResourcesStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryWithResourcesStatement(Java20Parser.TryWithResourcesStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#tryWithResourcesStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryWithResourcesStatement(Java20Parser.TryWithResourcesStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#resourceSpecification}.
	 * @param ctx the parse tree
	 */
	void enterResourceSpecification(Java20Parser.ResourceSpecificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#resourceSpecification}.
	 * @param ctx the parse tree
	 */
	void exitResourceSpecification(Java20Parser.ResourceSpecificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#resourceList}.
	 * @param ctx the parse tree
	 */
	void enterResourceList(Java20Parser.ResourceListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#resourceList}.
	 * @param ctx the parse tree
	 */
	void exitResourceList(Java20Parser.ResourceListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#resource}.
	 * @param ctx the parse tree
	 */
	void enterResource(Java20Parser.ResourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#resource}.
	 * @param ctx the parse tree
	 */
	void exitResource(Java20Parser.ResourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#variableAccess}.
	 * @param ctx the parse tree
	 */
	void enterVariableAccess(Java20Parser.VariableAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#variableAccess}.
	 * @param ctx the parse tree
	 */
	void exitVariableAccess(Java20Parser.VariableAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#yieldStatement}.
	 * @param ctx the parse tree
	 */
	void enterYieldStatement(Java20Parser.YieldStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#yieldStatement}.
	 * @param ctx the parse tree
	 */
	void exitYieldStatement(Java20Parser.YieldStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#pattern}.
	 * @param ctx the parse tree
	 */
	void enterPattern(Java20Parser.PatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#pattern}.
	 * @param ctx the parse tree
	 */
	void exitPattern(Java20Parser.PatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typePattern}.
	 * @param ctx the parse tree
	 */
	void enterTypePattern(Java20Parser.TypePatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typePattern}.
	 * @param ctx the parse tree
	 */
	void exitTypePattern(Java20Parser.TypePatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(Java20Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(Java20Parser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(Java20Parser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(Java20Parser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#primaryNoNewArray}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray(Java20Parser.PrimaryNoNewArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#primaryNoNewArray}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray(Java20Parser.PrimaryNoNewArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#pNNA}.
	 * @param ctx the parse tree
	 */
	void enterPNNA(Java20Parser.PNNAContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#pNNA}.
	 * @param ctx the parse tree
	 */
	void exitPNNA(Java20Parser.PNNAContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classLiteral}.
	 * @param ctx the parse tree
	 */
	void enterClassLiteral(Java20Parser.ClassLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classLiteral}.
	 * @param ctx the parse tree
	 */
	void exitClassLiteral(Java20Parser.ClassLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classInstanceCreationExpression}.
	 * @param ctx the parse tree
	 */
	void enterClassInstanceCreationExpression(Java20Parser.ClassInstanceCreationExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classInstanceCreationExpression}.
	 * @param ctx the parse tree
	 */
	void exitClassInstanceCreationExpression(Java20Parser.ClassInstanceCreationExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unqualifiedClassInstanceCreationExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnqualifiedClassInstanceCreationExpression(Java20Parser.UnqualifiedClassInstanceCreationExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unqualifiedClassInstanceCreationExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnqualifiedClassInstanceCreationExpression(Java20Parser.UnqualifiedClassInstanceCreationExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#classOrInterfaceTypeToInstantiate}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceTypeToInstantiate(Java20Parser.ClassOrInterfaceTypeToInstantiateContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#classOrInterfaceTypeToInstantiate}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceTypeToInstantiate(Java20Parser.ClassOrInterfaceTypeToInstantiateContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgumentsOrDiamond(Java20Parser.TypeArgumentsOrDiamondContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgumentsOrDiamond(Java20Parser.TypeArgumentsOrDiamondContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#arrayCreationExpression}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreationExpression(Java20Parser.ArrayCreationExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#arrayCreationExpression}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreationExpression(Java20Parser.ArrayCreationExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#arrayCreationExpressionWithoutInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreationExpressionWithoutInitializer(Java20Parser.ArrayCreationExpressionWithoutInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#arrayCreationExpressionWithoutInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreationExpressionWithoutInitializer(Java20Parser.ArrayCreationExpressionWithoutInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#arrayCreationExpressionWithInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreationExpressionWithInitializer(Java20Parser.ArrayCreationExpressionWithInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#arrayCreationExpressionWithInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreationExpressionWithInitializer(Java20Parser.ArrayCreationExpressionWithInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#dimExprs}.
	 * @param ctx the parse tree
	 */
	void enterDimExprs(Java20Parser.DimExprsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#dimExprs}.
	 * @param ctx the parse tree
	 */
	void exitDimExprs(Java20Parser.DimExprsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#dimExpr}.
	 * @param ctx the parse tree
	 */
	void enterDimExpr(Java20Parser.DimExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#dimExpr}.
	 * @param ctx the parse tree
	 */
	void exitDimExpr(Java20Parser.DimExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#arrayAccess}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess(Java20Parser.ArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#arrayAccess}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess(Java20Parser.ArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess(Java20Parser.FieldAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess(Java20Parser.FieldAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#methodInvocation}.
	 * @param ctx the parse tree
	 */
	void enterMethodInvocation(Java20Parser.MethodInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#methodInvocation}.
	 * @param ctx the parse tree
	 */
	void exitMethodInvocation(Java20Parser.MethodInvocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(Java20Parser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(Java20Parser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#methodReference}.
	 * @param ctx the parse tree
	 */
	void enterMethodReference(Java20Parser.MethodReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#methodReference}.
	 * @param ctx the parse tree
	 */
	void exitMethodReference(Java20Parser.MethodReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression(Java20Parser.PostfixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression(Java20Parser.PostfixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#pfE}.
	 * @param ctx the parse tree
	 */
	void enterPfE(Java20Parser.PfEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#pfE}.
	 * @param ctx the parse tree
	 */
	void exitPfE(Java20Parser.PfEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#postIncrementExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostIncrementExpression(Java20Parser.PostIncrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#postIncrementExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostIncrementExpression(Java20Parser.PostIncrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#postDecrementExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostDecrementExpression(Java20Parser.PostDecrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#postDecrementExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostDecrementExpression(Java20Parser.PostDecrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(Java20Parser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(Java20Parser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#preIncrementExpression}.
	 * @param ctx the parse tree
	 */
	void enterPreIncrementExpression(Java20Parser.PreIncrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#preIncrementExpression}.
	 * @param ctx the parse tree
	 */
	void exitPreIncrementExpression(Java20Parser.PreIncrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#preDecrementExpression}.
	 * @param ctx the parse tree
	 */
	void enterPreDecrementExpression(Java20Parser.PreDecrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#preDecrementExpression}.
	 * @param ctx the parse tree
	 */
	void exitPreDecrementExpression(Java20Parser.PreDecrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpressionNotPlusMinus(Java20Parser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpressionNotPlusMinus(Java20Parser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void enterCastExpression(Java20Parser.CastExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void exitCastExpression(Java20Parser.CastExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(Java20Parser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(Java20Parser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(Java20Parser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(Java20Parser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void enterShiftExpression(Java20Parser.ShiftExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void exitShiftExpression(Java20Parser.ShiftExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(Java20Parser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(Java20Parser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(Java20Parser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(Java20Parser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#andExpression}.
	 * @param ctx the parse tree
	 */
	void enterAndExpression(Java20Parser.AndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#andExpression}.
	 * @param ctx the parse tree
	 */
	void exitAndExpression(Java20Parser.AndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterExclusiveOrExpression(Java20Parser.ExclusiveOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitExclusiveOrExpression(Java20Parser.ExclusiveOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterInclusiveOrExpression(Java20Parser.InclusiveOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitInclusiveOrExpression(Java20Parser.InclusiveOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalAndExpression(Java20Parser.ConditionalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalAndExpression(Java20Parser.ConditionalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalOrExpression(Java20Parser.ConditionalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalOrExpression(Java20Parser.ConditionalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression(Java20Parser.ConditionalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression(Java20Parser.ConditionalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(Java20Parser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(Java20Parser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(Java20Parser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(Java20Parser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#leftHandSide}.
	 * @param ctx the parse tree
	 */
	void enterLeftHandSide(Java20Parser.LeftHandSideContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#leftHandSide}.
	 * @param ctx the parse tree
	 */
	void exitLeftHandSide(Java20Parser.LeftHandSideContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator(Java20Parser.AssignmentOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator(Java20Parser.AssignmentOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#lambdaExpression}.
	 * @param ctx the parse tree
	 */
	void enterLambdaExpression(Java20Parser.LambdaExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#lambdaExpression}.
	 * @param ctx the parse tree
	 */
	void exitLambdaExpression(Java20Parser.LambdaExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#lambdaParameters}.
	 * @param ctx the parse tree
	 */
	void enterLambdaParameters(Java20Parser.LambdaParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#lambdaParameters}.
	 * @param ctx the parse tree
	 */
	void exitLambdaParameters(Java20Parser.LambdaParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#lambdaParameterList}.
	 * @param ctx the parse tree
	 */
	void enterLambdaParameterList(Java20Parser.LambdaParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#lambdaParameterList}.
	 * @param ctx the parse tree
	 */
	void exitLambdaParameterList(Java20Parser.LambdaParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#lambdaParameter}.
	 * @param ctx the parse tree
	 */
	void enterLambdaParameter(Java20Parser.LambdaParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#lambdaParameter}.
	 * @param ctx the parse tree
	 */
	void exitLambdaParameter(Java20Parser.LambdaParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#lambdaParameterType}.
	 * @param ctx the parse tree
	 */
	void enterLambdaParameterType(Java20Parser.LambdaParameterTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#lambdaParameterType}.
	 * @param ctx the parse tree
	 */
	void exitLambdaParameterType(Java20Parser.LambdaParameterTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#lambdaBody}.
	 * @param ctx the parse tree
	 */
	void enterLambdaBody(Java20Parser.LambdaBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#lambdaBody}.
	 * @param ctx the parse tree
	 */
	void exitLambdaBody(Java20Parser.LambdaBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#switchExpression}.
	 * @param ctx the parse tree
	 */
	void enterSwitchExpression(Java20Parser.SwitchExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#switchExpression}.
	 * @param ctx the parse tree
	 */
	void exitSwitchExpression(Java20Parser.SwitchExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java20Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(Java20Parser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java20Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(Java20Parser.ConstantExpressionContext ctx);
}