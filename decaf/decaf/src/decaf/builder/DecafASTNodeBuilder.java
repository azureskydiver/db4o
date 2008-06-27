package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;
import org.eclipse.jdt.core.dom.Modifier.*;
import org.eclipse.jdt.core.dom.PrimitiveType.*;

import sharpen.core.framework.*;

@SuppressWarnings("unchecked")
class DecafASTNodeBuilder {
	private final AST ast;
	private final DecafConfiguration config;
	
	public DecafASTNodeBuilder(AST ast, DecafConfiguration config) {
		this.ast = ast;
		this.config = config;
	}

	public <T extends ASTNode> T clone(T node) {
		return (T) ASTNode.copySubtree(ast, node);
	}
	
	public SimpleName newSimpleName(String name) {
		return ast.newSimpleName(name);
	}

	public VariableDeclarationExpression newVariableDeclaration(Type variableType, String variableName, Expression initializer) {
		VariableDeclarationFragment indexFragment = newVariableFragment(variableName, initializer);

		VariableDeclarationExpression index = ast.newVariableDeclarationExpression(indexFragment);
		index.setType(variableType);
		return index;
	}

	private VariableDeclarationFragment newVariableFragment(String variableName, Expression initializer) {
		final SimpleName name = newSimpleName(variableName);
		return newVariableFragment(name, initializer);
	}

	private VariableDeclarationFragment newVariableFragment(
			final SimpleName name, Expression initializer) {
		VariableDeclarationFragment indexFragment = ast.newVariableDeclarationFragment();
		indexFragment.setName(name);
		indexFragment.setInitializer(initializer);
		return indexFragment;
	}

	public ForStatement newForStatement(
			Expression initializer,
			Expression comparison,
			Expression updater,
			Statement body) {
		ForStatement stmt = ast.newForStatement();
		stmt.initializers().add(initializer);
		stmt.setExpression(comparison);
		stmt.updaters().add(updater);
		stmt.setBody(body);
		return stmt;
	}

	public Block newBlock(Statement... stmts) {
		Block block = ast.newBlock();
		for (Statement stmt : stmts) {
			block.statements().add(stmt);
		}
		return block;
	}

	public Type newType(ITypeBinding type) {
		if (type.isArray()) {
			return ast.newArrayType(newType(type.getComponentType()));
		}
		if (type.isPrimitive()) {
			return newPrimitiveType(type.getName());
		}
		return newSimpleType(type.getName());
	}

	public SimpleType newSimpleType(final String typeName) {
		return ast.newSimpleType(ast.newName(typeName));
	}

	private PrimitiveType newPrimitiveType(final String primitiveTypeName) {
		return ast.newPrimitiveType(PrimitiveType.toCode(primitiveTypeName));
	}

	public Expression newFieldAccess(Expression e, SimpleName fieldName) {
		FieldAccess field = ast.newFieldAccess();
		field.setExpression(e);
		field.setName(fieldName);
		return field;
	}

	
	public ArrayAccess newArrayAccess(Expression array, Expression index) {
		ArrayAccess access = ast.newArrayAccess();
		access.setArray(array);
		access.setIndex(index);
		return access;
	}

	public PrefixExpression newPrefixExpression(
			PrefixExpression.Operator operator,
			SimpleName operand) {
		PrefixExpression increment = ast.newPrefixExpression();
		increment.setOperator(operator);
		increment.setOperand(operand);
		return increment;
	}

	public InfixExpression newInfixExpression(Operator operator,
			Expression left, Expression right) {
		InfixExpression e = ast.newInfixExpression();
		e.setOperator(operator);
		e.setLeftOperand(left);
		e.setRightOperand(right);
		return e;
	}

	public VariableDeclarationStatement newVariableDeclarationStatement(
			String variableName, Type variableType, Expression initializer) {
		VariableDeclarationStatement variable = ast.newVariableDeclarationStatement(newVariableFragment(variableName, initializer));
		variable.setType(variableType);
		variable.modifiers().add(newFinalModifier());
		return variable;
	}
	
	private  Modifier newFinalModifier() {
		return ast.newModifier(ModifierKeyword.FINAL_KEYWORD);
	}
	
	public CastExpression newCast(final ITypeBinding type,
			final Expression expression) {
		final CastExpression cast = ast.newCastExpression();
		cast.setType(newType(type.getErasure()));
		cast.setExpression(expression);
		return cast;
	}
	
	public MethodInvocation newMethodInvocation(final Expression target,
			final String name) {
		final MethodInvocation invocation = ast.newMethodInvocation();
		invocation.setExpression(target);
		invocation.setName(newSimpleName(name));
		return invocation;
	}

	public ParenthesizedExpression parenthesize(final Expression expression) {
		final ParenthesizedExpression pe = ast.newParenthesizedExpression();
		pe.setExpression(expression);
		return pe;
	}

	public ArrayCreation newArrayCreation(final ITypeBinding arrayType,
			ArrayInitializer arrayInitializer) {
		ArrayCreation varArgsArray = ast.newArrayCreation();
		varArgsArray.setInitializer(arrayInitializer);

		varArgsArray.setType((ArrayType) newType(arrayType));
		return varArgsArray;
	}

	public ArrayInitializer newArrayInitializer() {
		return ast.newArrayInitializer();
	}

	private IMethodBinding originalMethodDefinitionFor(
			final IMethodBinding method) {
		return Bindings.findMethodDefininition(method, ast);
	}
	
	public ClassInstanceCreation newClassInstanceCreation() {
		return ast.newClassInstanceCreation();
	}
	
	public PrimitiveType newPrimitiveType(Code typeCode) {
		return ast.newPrimitiveType(typeCode);
	}
	
	public NumberLiteral newNumberLiteral(String literal) {
		return ast.newNumberLiteral(literal);
	}

	public IMethodBinding originalMethodDefinitionFor(MethodDeclaration node) {
		return originalMethodDefinitionFor(node.resolveBinding());
	}
	
	public ParenthesizedExpression createParenthesizedCast(Expression node, final ITypeBinding type) {
		return parenthesize(newCast(type, node));
	}

	public boolean isIgnored(BodyDeclaration node) {
		return containsJavadoc(node, DecafAnnotations.IGNORE);
	}

	private boolean containsJavadoc(BodyDeclaration node, String tag) {
		return JavadocUtility.containsJavadoc(node, tag);
	}

	public boolean isName(Expression array) {
		return array instanceof Name;
	}

	public SingleVariableDeclaration lastParameter(List parameters) {
		return (SingleVariableDeclaration) parameters.get(parameters.size()-1);
	}

	public boolean isErasedFieldAccess(IVariableBinding binding) {
		final ITypeBinding originalType = binding.getVariableDeclaration().getType();
		return originalType != binding.getType();
	}

	public IVariableBinding fieldBinding(Name node) {
		final IBinding binding = node.resolveBinding();
		if(binding == null){
			return null;
		}	
		if (binding.getKind() != IBinding.VARIABLE) {
			return null;
		}
		IVariableBinding variable = (IVariableBinding)binding;
		if (!variable.isField()) {
			return null;
		}
		return variable;
	}

	public String boxedTypeFor(ITypeBinding type) {
		final String typeName = type.getName();
		if ("byte".equals(typeName)) {
			return "Byte";
		}
		if ("boolean".equals(typeName)) {
			return "Boolean";
		}
		if ("short".equals(typeName)) {
			return "Short";
		}
		if ("int".equals(typeName)) {
			return "Integer";
		}
		if ("long".equals(typeName)) {
			return "Long";
		}
		if ("float".equals(typeName)) {
			return "Float";
		}
		if ("double".equals(typeName)) {
			return "Double";
		}
		if ("char".equals(typeName)) {
			return "Character";
		}
		throw new IllegalArgumentException(typeName);
	}

	public boolean ignoreExtends(TypeDeclaration node) {
		return containsJavadoc(node, DecafAnnotations.IGNORE_EXTENDS);
	}
	
	private static final Map<String, String> _unboxing = new HashMap<String, String>();
	{
		unboxing("java.lang.Byte", "byteValue");
		unboxing("java.lang.Short", "shortValue");
		unboxing("java.lang.Integer", "intValue");
		unboxing("java.lang.Long", "longValue");
		unboxing("java.lang.Float", "floatValue");
		unboxing("java.lang.Double", "doubleValue");
		unboxing("java.lang.Boolean", "booleanValue");
		unboxing("java.lang.Character", "charValue");
	}

	private void unboxing(final String typeName, final String method) {
		_unboxing.put(typeName, method);
	}
	
	public String unboxingMethodFor(ITypeBinding type) {
		return _unboxing.get(type.getQualifiedName());
	}		

	private boolean singleTagWithNoFragments(final List<TagElement> tags) {
		return tags.size() == 1 && tags.get(0).fragments().isEmpty();
	}

	private Set<String> allSuperInterfaceTypeNames(TypeDeclaration node) {
		final List superInterfaces = node.superInterfaceTypes();
		final HashSet<String> set = new HashSet<String>(superInterfaces.size());
		for (Object o : superInterfaces) {
			set.add(o.toString());
		}
		return set;
	}

	public Set<String> ignoredImplements(TypeDeclaration node) {
		
		final List<TagElement> tags = JavadocUtility.getJavadocTags(node, DecafAnnotations.IGNORE_IMPLEMENTS);
		if (tags.isEmpty()) {
			return Collections.emptySet();
		}
		
		if (singleTagWithNoFragments(tags)) {
			return allSuperInterfaceTypeNames(node);
		}
		
		final HashSet<String> ignored = new HashSet<String>(tags.size());
		for (TagElement tag : tags) {
			ignored.add(JavadocUtility.textFragment(tag.fragments(), 0));
		}
		return ignored;
	}

	public boolean isExpressionStatement(final ASTNode parent) {
		return parent.getNodeType() == ASTNode.EXPRESSION_STATEMENT;
	}

	public boolean hasGenericReturnType(final IMethodBinding method) {
		return method.getMethodDeclaration().getReturnType().getErasure() != method.getReturnType().getErasure();
	}

	private boolean lastArgumentIsAssignmentCompatibleWithLastParameter(
			final IMethodBinding binding, final List arguments) {
		final ITypeBinding[] parameters = binding.getParameterTypes();
		final int lastIndex = parameters.length - 1;
		final ITypeBinding lastArgumentType = expressionType(arguments.get(lastIndex));
		final ITypeBinding lastParameterType = parameters[lastIndex];
		return lastArgumentType.isAssignmentCompatible(lastParameterType);
	}

	private ITypeBinding expressionType(final Object expression) {
		return ((Expression)expression).resolveTypeBinding();
	}
	
	private boolean argumentCountDoesNotMatchParameterCount(final IMethodBinding method, final List arguments) {
		return method.getParameterTypes().length != arguments.size();
	}

	public boolean requiresVarArgsTranslation(final IMethodBinding method,
			final List arguments) {
		if (!method.isVarargs()) {
			return false;
		}
		if (argumentCountDoesNotMatchParameterCount(method, arguments)) {
			return true;
		}
		if (lastArgumentIsAssignmentCompatibleWithLastParameter(method, arguments)) {
			return false;
		}
		return true;
	}

	public boolean isExistingNode(ASTNode arg) {
		return arg.getStartPosition() != -1;
	}	

	public String qualifiedName(ITypeBinding type) {
		return type.getTypeDeclaration().getQualifiedName();
	}
	
	public Type mappedType(ITypeBinding origBinding, Type origType) {
		String name = qualifiedName(origBinding);
		String mappedName = config.typeNameMapping(name);
		return mappedName == null ? origType : newSimpleType(mappedName);
	}
}