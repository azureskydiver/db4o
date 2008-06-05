package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import sharpen.core.framework.*;

@SuppressWarnings("unchecked")
public final class DecafRewritingVisitor extends ASTVisitor {
	private final AST ast;
	private final ASTRewrite rewrite;

	public DecafRewritingVisitor(AST ast, ASTRewrite rewrite) {
		this.ast = ast;
		this.rewrite = rewrite;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if (handledAsIgnored(node)) {
			return false;
		}
		
		processIgnoreExtends(node);
		processIgnoreImplements(node);
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeParameter node) {
		remove(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ParameterizedType node) {
		replace(node, node.getType());
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldAccess node) {
		replaceGenericFieldAccessWithCast(node, node.resolveFieldBinding());
		return super.visit(node);
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		IVariableBinding binding = fieldBinding(node);
		if (null != binding) {
			replaceGenericFieldAccessWithCast(node, binding);
		}
		return super.visit(node);
	}

	private void replaceGenericFieldAccessWithCast(Expression node, IVariableBinding binding) {
		final ITypeBinding originalType = binding.getVariableDeclaration().getType();
		final ITypeBinding actualType = binding.getType();
		if (originalType != actualType) {
			replaceWithCast(node, actualType);
		}
	}

	private void replaceWithCast(Expression node, final ITypeBinding type) {
		replace(node, createCastForErasure(node, type));
	}

	private ParenthesizedExpression createCastForErasure(Expression node,
			final ITypeBinding type) {
		return parenthesize(
			newCast(type, move(node)));
	}

	private CastExpression newCast(final ITypeBinding type,
			final Expression expression) {
		final CastExpression cast = ast.newCastExpression();
		cast.setType(newType(type));
		cast.setExpression(expression);
		return cast;
	}
	
	private IVariableBinding fieldBinding(Name node) {
		final IBinding binding = node.resolveBinding();
		if (binding.getKind() != IBinding.VARIABLE) {
			return null;
		}
		IVariableBinding variable = (IVariableBinding)binding;
		if (!variable.isField()) {
			return null;
		}
		return variable;
	}

	@Override
	public boolean visit(SimpleType node) {
		final ITypeBinding binding = node.resolveBinding();
		if (binding.isTypeVariable()) {
			replace(node, newType(binding.getErasure()));
		}
		return super.visit(node);
	}
	
	@Override
	public boolean visit(IfStatement node) {
		
		final Expression expression = node.getExpression();
		if (expression.resolveUnboxing()) {
			replace(expression,
					newMethodInvocation(
						parenthesize(copy(expression)), "booleanValue"));
		}
		return super.visit(node);
	}

	private ParenthesizedExpression parenthesize(final Expression expression) {
		final ParenthesizedExpression pe = ast.newParenthesizedExpression();
		pe.setExpression(expression);
		return pe;
	}

	private MethodInvocation newMethodInvocation(final Expression target,
			final String name) {
		final MethodInvocation invocation = ast.newMethodInvocation();
		invocation.setExpression(target);
		invocation.setName(newSimpleName(name));
		return invocation;
	}

	private void processIgnoreExtends(TypeDeclaration node) {
		if (ignoreExtends(node)) {
			remove(node.getSuperclassType());
		}
	}

	private void processIgnoreImplements(TypeDeclaration node) {
		final Set<String> ignoredImplements = ignoredImplements(node);
		if (ignoredImplements.isEmpty()) {
			return;
		}
		
		for (Object o : node.superInterfaceTypes()) {
			final Type type = (Type)o;
			if (ignoredImplements.contains(type.toString())) {
				remove(type);
			}
		}
	}	

	private Set<String> ignoredImplements(TypeDeclaration node) {
		
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

	private boolean ignoreExtends(TypeDeclaration node) {
		return containsJavadoc(node, DecafAnnotations.IGNORE_EXTENDS);
	}
	
	@Override
	public void endVisit(ClassInstanceCreation node) {
		final IMethodBinding ctor = node.resolveConstructorBinding();
		final List arguments = node.arguments();
		if (requiresVarArgsTranslation(ctor, arguments)) {
			rewriteVarArgsArguments(ctor, arguments, rewrite.getListRewrite(node, ClassInstanceCreation.ARGUMENTS_PROPERTY));
		}
	}

	@Override
	public boolean visit(MethodInvocation node) {
		return true;
	}
	
	@Override
	public void endVisit(MethodInvocation node) {
	
		
		final IMethodBinding method = node.resolveMethodBinding();
		final List arguments = node.arguments();
		if (requiresVarArgsTranslation(method, arguments)) {
			rewriteVarArgsArguments(method, arguments, rewrite.getListRewrite(node, MethodInvocation.ARGUMENTS_PROPERTY));
		}
		
		if (hasGenericReturnType(method)) {
			replaceWithCast(node, method.getReturnType());
		}
	}

	private boolean hasGenericReturnType(final IMethodBinding method) {
		return method.getMethodDeclaration().getReturnType() != method.getReturnType();
	}

	private void rewriteVarArgsArguments(final IMethodBinding method,
			final List arguments, final ListRewrite argumentListRewrite) {
		
		final ITypeBinding[] parameters = method.getParameterTypes();
		argumentListRewrite.insertLast(varArgsToArray(argumentListRewrite, parameters), null);
	}

	private boolean requiresVarArgsTranslation(final IMethodBinding method,
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

	private boolean argumentCountDoesNotMatchParameterCount(final IMethodBinding method, final List arguments) {
		return method.getParameterTypes().length != arguments.size();
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

	private ArrayCreation varArgsToArray(ListRewrite argumentListRewrite, ITypeBinding[] parameterTypes) {
		
		final List arguments = argumentListRewrite.getRewrittenList();
		final List originalList = argumentListRewrite.getOriginalList();
		for (int i = parameterTypes.length-1; i < originalList.size(); ++i) {
			argumentListRewrite.remove((ASTNode)originalList.get(i), null);
		}
		
		ArrayInitializer arrayInitializer = ast.newArrayInitializer();
		for (int i = parameterTypes.length-1; i < arguments.size(); ++i) {
			final Expression arg = (Expression) arguments.get(i);
			arrayInitializer.expressions().add(safeMove(arg));
		}

		return newArrayCreation(parameterTypes[parameterTypes.length-1], arrayInitializer);
	}

	private <T extends ASTNode> T safeMove(final T arg) {
		return isExistingNode(arg) ? move(arg) : arg;
	}

	private ArrayCreation newArrayCreation(final ITypeBinding arrayType,
			ArrayInitializer arrayInitializer) {
		ArrayCreation varArgsArray = ast.newArrayCreation();
		varArgsArray.setInitializer(arrayInitializer);

		varArgsArray.setType((ArrayType) newType(arrayType));
		return varArgsArray;
	}

	private boolean isExistingNode(ASTNode arg) {
		return arg.getStartPosition() != -1;
	}

	public boolean visit(MethodDeclaration node) {
		if (handledAsIgnored(node)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		if (node.isVarargs()) {
			handleVarArgsMethod(node);
		}
	}

	private void handleVarArgsMethod(MethodDeclaration method) {
		SingleVariableDeclaration varArgsParameter = lastParameter(method.parameters());

		set(varArgsParameter, SingleVariableDeclaration.VARARGS_PROPERTY, Boolean.FALSE);
		replace(varArgsParameter.getType(), newType(varArgsParameter.resolveBinding().getType().getErasure()));
	}

	private void set(final ASTNode node, final SimplePropertyDescriptor property, final Object value) {
		rewrite.set(node, property, value, null);
	}

	private SingleVariableDeclaration lastParameter(List parameters) {
		return (SingleVariableDeclaration) parameters.get(parameters.size()-1);
	}

	public void endVisit(EnhancedForStatement node) {

		SingleVariableDeclaration variable = node.getParameter();
		Expression array = node.getExpression();

		VariableDeclarationStatement tempArrayVariable = null;
		if (!isName(array)) {
			String tempArrayName = variable.getName() + "Array";
			final ITypeBinding type = node.getExpression().resolveTypeBinding();
			tempArrayVariable = newVariableDeclarationStatement(
				tempArrayName,
				newType(type),
				move(array));
			array = newSimpleName(tempArrayName);
		}

		String indexVariableName = variable.getName() + "Index";
		VariableDeclarationExpression index = newVariableDeclaration(
				ast.newPrimitiveType(PrimitiveType.INT),
				indexVariableName,
				ast.newNumberLiteral("0"));

		InfixExpression cmp = newInfixExpression(
								InfixExpression.Operator.LESS,
								newSimpleName(indexVariableName),
								newFieldAccess(clone(array), "length"));

		Block newBody = ast.newBlock();
		newBody.statements().add(
				newVariableDeclarationStatement(
						variable.getName().toString(),
						move(variable.getType()),
						newArrayAccess(
								clone(array),
								newSimpleName(indexVariableName))));

		moveTo(node.getBody(), newBody);

		PrefixExpression updater = newPrefixExpression(
												PrefixExpression.Operator.INCREMENT,
												newSimpleName(indexVariableName));

		ForStatement stmt = newForStatement(index, cmp, updater, newBody);
		if (null == tempArrayVariable) {
			replace(node, stmt);
		} else {
			replace(node, newBlock(tempArrayVariable, stmt));
		}
	}

	private void moveTo(Statement statement, Block body) {
		if (statement instanceof Block) {
			final List statements = ((Block)statement).statements();
			for (Object stmt : statements) {
				moveTo((Statement) stmt, body);
			}
		} else {
			body.statements().add(move(statement));
		}
	}

	private Expression newFieldAccess(Expression e, String fieldName) {
		return newFieldAccess(e, newSimpleName(fieldName));
	}

	private boolean isName(Expression array) {
		return array instanceof Name;
	}

	private ForStatement newForStatement(
			Expression initializer,
			Expression comparison,
			Expression updater,
			Block body) {
		ForStatement stmt = ast.newForStatement();
		stmt.initializers().add(initializer);
		stmt.setExpression(comparison);
		stmt.updaters().add(updater);
		stmt.setBody(body);
		return stmt;
	}

	private Block newBlock(Statement... stmts) {
		Block block = ast.newBlock();
		for (Statement stmt : stmts) {
			block.statements().add(stmt);
		}
		return block;
	}

	private Type newType(ITypeBinding type) {
		if (type.isArray()) {
			return ast.newArrayType(newType(type.getComponentType()));
		}
		if (type.isPrimitive()) {
			return ast.newPrimitiveType(PrimitiveType.toCode(type.getName()));
		}
		return ast.newSimpleType(ast.newName(type.getName()));
	}

	private Expression newFieldAccess(Expression e, SimpleName fieldName) {
		FieldAccess field = ast.newFieldAccess();
		field.setExpression(e);
		field.setName(fieldName);
		return field;
	}

	private ArrayAccess newArrayAccess(Expression array, Expression index) {
		ArrayAccess access = ast.newArrayAccess();
		access.setArray(array);
		access.setIndex(index);
		return access;
	}

	private PrefixExpression newPrefixExpression(
			PrefixExpression.Operator operator,
			SimpleName operand) {
		PrefixExpression increment = ast.newPrefixExpression();
		increment.setOperator(operator);
		increment.setOperand(operand);
		return increment;
	}

	private InfixExpression newInfixExpression(Operator operator,
			Expression left, Expression right) {
		InfixExpression e = ast.newInfixExpression();
		e.setOperator(operator);
		e.setLeftOperand(left);
		e.setRightOperand(right);
		return e;
	}

	private VariableDeclarationStatement newVariableDeclarationStatement(
			String variableName, Type variableType, Expression initializer) {
		VariableDeclarationStatement variable = ast.newVariableDeclarationStatement(newVariableFragment(variableName, initializer));
		variable.setType(variableType);
		return variable;
	}
	
	private boolean handledAsIgnored(BodyDeclaration node) {
		if (isIgnored(node)) {
			remove(node);
			return true;
		}
		return false;
	}

	private void remove(ASTNode node) {
		rewrite.remove(node, null);
	}

	private boolean isIgnored(BodyDeclaration node) {
		return containsJavadoc(node, DecafAnnotations.IGNORE);
	}

	private boolean containsJavadoc(BodyDeclaration node, String tag) {
		return JavadocUtility.containsJavadoc(node, tag);
	}

	private void replace(ASTNode node, ASTNode replacement) {
		rewrite.replace(node, replacement, null);
	}

	private <T extends ASTNode> T copy(T node) {
		return (T)rewrite.createCopyTarget(node);
	}
	
	private <T extends ASTNode> T move(T node) {
		return (T)rewrite.createMoveTarget(node);
	}
	
	private <T extends ASTNode> T clone(T node) {
		return (T) ASTNode.copySubtree(ast, node);
	}
	
	private SimpleName newSimpleName(String name) {
		return ast.newSimpleName(name);
	}

	VariableDeclarationExpression newVariableDeclaration(Type variableType, String variableName, Expression initializer) {
		VariableDeclarationFragment indexFragment = newVariableFragment(variableName, initializer);

		VariableDeclarationExpression index = ast.newVariableDeclarationExpression(indexFragment);
		index.setType(variableType);
		return index;
	}

	VariableDeclarationFragment newVariableFragment(String variableName, Expression initializer) {
		VariableDeclarationFragment indexFragment = ast.newVariableDeclarationFragment();
		indexFragment.setName(newSimpleName(variableName));
		indexFragment.setInitializer(initializer);
		return indexFragment;
	}
}