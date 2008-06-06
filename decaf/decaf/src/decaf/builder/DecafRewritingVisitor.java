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
		return false;
	}
	
	@Override
	public boolean visit(ParameterizedType node) {
		replace(node, node.getType());
		return false;
	}
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		final Expression initializer = node.getInitializer();
		final Expression erasure = getErasure(initializer);
		if (erasure != null) {
			replace(initializer, erasure);
		}
	}

	private Expression getErasure(final Expression expression) {
		if (expression instanceof Name) {
			return getErasureForName((Name)expression);
		}
		if (expression instanceof FieldAccess) {
			return getErasureForField(expression, ((FieldAccess)expression).resolveFieldBinding());
		}
		return null;
	}

	private Expression getErasureForName(final Name name) {
		final IVariableBinding field = fieldBinding(name);
		if (null == field) {
			return null;
		}
		return getErasureForField(name, field);
	}

	private Expression getErasureForField(final Expression expression,
			final IVariableBinding field) {
		if (isErasedFieldAccess(field)) {
			return createCastForErasure(expression, field.getType());
		}
		return null;
	}
	
	private boolean isErasedFieldAccess(IVariableBinding binding) {
		final ITypeBinding originalType = binding.getVariableDeclaration().getType();
		return originalType != binding.getType();
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
		cast.setType(newType(type.getErasure()));
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
			return false;
		}
		return true;
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
	public void endVisit(ArrayAccess node) {
		final Expression erasure = getErasure(node.getArray());
		if (null != erasure) {
			replace(node.getArray(), erasure);
		}
	}
	
	@Override
	public void endVisit(MethodInvocation node) {
	
		
		final IMethodBinding method = node.resolveMethodBinding();
		final List arguments = node.arguments();
		if (requiresVarArgsTranslation(method, arguments)) {
			rewriteVarArgsArguments(method, arguments, rewrite.getListRewrite(node, MethodInvocation.ARGUMENTS_PROPERTY));
		}
		
		final Expression erasure = getErasure(node.getExpression());
		if (erasure != null) {
			replace(node.getExpression(), erasure);
		}

		if (!isExpressionStatement(node.getParent())) {
			if (hasGenericReturnType(method)) {
				replaceWithCast(node, method.getReturnType());
			}
		}
	}

	private boolean isExpressionStatement(final ASTNode parent) {
		return parent.getNodeType() == ASTNode.EXPRESSION_STATEMENT;
	}

	private boolean hasGenericReturnType(final IMethodBinding method) {
		return method.getMethodDeclaration().getReturnType() != method.getReturnType();
	}

	private void rewriteVarArgsArguments(final IMethodBinding method,
			final List arguments, final ListRewrite argumentListRewrite) {
		
		final ITypeBinding[] parameters = method.getParameterTypes();
		final List arguments1 = argumentListRewrite.getRewrittenList();
		final List originalList = argumentListRewrite.getOriginalList();
		for (int i = parameters.length-1; i < originalList.size(); ++i) {
			argumentListRewrite.remove((ASTNode)originalList.get(i), null);
		}
		
		ArrayInitializer arrayInitializer = ast.newArrayInitializer();
		for (int i = parameters.length-1; i < arguments1.size(); ++i) {
			final Expression arg = (Expression) arguments1.get(i);
			arrayInitializer.expressions().add(safeMove(arg));
		}
		
		argumentListRewrite.insertLast(
				newArrayCreation(parameters[parameters.length-1], arrayInitializer),
				null);
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
	
	private <T extends ASTNode> T safeCopy(final T arg) {
		return isExistingNode(arg) ? copy(arg) : arg;
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
	
	@Override
	public void endVisit(QualifiedName node) {
		
		final Expression erasure = getErasureForName(node);
		if (null != erasure) {
			replace(node, erasure);
		}
	}
	
	@Override
	public void endVisit(FieldAccess node) {
		final Expression erasure = getErasureForField(node, node.resolveFieldBinding());
		if (null != erasure) {
			replace(node, erasure);
		}
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

		final SingleVariableDeclaration variable = node.getParameter();
		final Expression erasure = getErasure(node.getExpression());
		final Expression array = erasure != null ? erasure : node.getExpression();
		
		Expression arrayReference = null;
		
		VariableDeclarationStatement tempArrayVariable = null;
		if (isName(array)) {
			arrayReference = array;
		} else {
			String tempArrayName = variable.getName() + "Array";
			final ITypeBinding type = node.getExpression().resolveTypeBinding();
			tempArrayVariable = newVariableDeclarationStatement(
				tempArrayName,
				newType(type),
				safeMove(array));
			arrayReference = newSimpleName(tempArrayName);
		}

		final String indexVariableName = variable.getName() + "Index";
		VariableDeclarationExpression index = newVariableDeclaration(
				ast.newPrimitiveType(PrimitiveType.INT),
				indexVariableName,
				ast.newNumberLiteral("0"));

		InfixExpression cmp = newInfixExpression(
								InfixExpression.Operator.LESS,
								newSimpleName(indexVariableName),
								newFieldAccess(clone(arrayReference), "length"));

		final ListRewrite statementsRewrite = rewrite.getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
		statementsRewrite.insertFirst(
				newVariableDeclarationStatement(
						variable.getName().toString(),
						clone(variable.getType()),
						newArrayAccess(
								clone(arrayReference),
								newSimpleName(indexVariableName))), null);

		final PrefixExpression updater = newPrefixExpression(
				PrefixExpression.Operator.INCREMENT,
				newSimpleName(indexVariableName));

		final ForStatement stmt = newForStatement(index, cmp, updater, move(node.getBody()));
		if (null == tempArrayVariable) {
			replace(node, stmt);
		} else {
			replace(node, newBlock(tempArrayVariable, stmt));
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
			Statement body) {
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
}