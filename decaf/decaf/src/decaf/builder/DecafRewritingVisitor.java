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
		if (originalType.isTypeVariable()) {
			replace(node,
					parenthesize(
						newCast(binding.getType(), copy(node))));
		}
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

	public boolean visit(MethodInvocation node) {
		if (requiresVarArgsTranslation(node)) {
			MethodInvocation explicityArrayInCall = copy(node);

			List arguments = explicityArrayInCall.arguments();

			List newArguments = mapVarArgsToArray(arguments, node.resolveMethodBinding().getParameterTypes());

			arguments.clear();
			arguments.addAll(newArguments);

			replace(node, explicityArrayInCall);
		}

		return true;
	}

	private boolean requiresVarArgsTranslation(MethodInvocation node) {
		if (!isVarArgsMethodInvocation(node)) {
			return false;
		}
		if (argumentCountDoesNotMatchParameterCount(node)) {
			return true;
		}
		if (lastArgumentIsAssignmentCompatibleWithLastParameter(node)) {
			return false;
		}
		return true;
	}

	private boolean lastArgumentIsAssignmentCompatibleWithLastParameter(
			MethodInvocation node) {
		final IMethodBinding binding = node.resolveMethodBinding();
		final ITypeBinding[] parameters = binding.getParameterTypes();
		final int lastIndex = parameters.length - 1;
		final ITypeBinding lastArgumentType = expressionType(node.arguments().get(lastIndex));
		final ITypeBinding lastParameterType = parameters[lastIndex];
		return lastArgumentType.isAssignmentCompatible(lastParameterType);
	}

	private boolean argumentCountDoesNotMatchParameterCount(
			MethodInvocation node) {
		return node.resolveMethodBinding().getParameterTypes().length != node.arguments().size();
	}

	private boolean isVarArgsMethodInvocation(MethodInvocation node) {
		return node.resolveMethodBinding().isVarargs();
	}

	private ITypeBinding expressionType(final Object expression) {
		return ((Expression)expression).resolveTypeBinding();
	}

	private List mapVarArgsToArray(List arguments, ITypeBinding[] parameterTypes) {
		List newArguments = copyAllArgumentsButVarArgs(arguments, parameterTypes);
		ArrayCreation varArgsArray = varArgsToArray(arguments, parameterTypes);
		newArguments.add(varArgsArray);
		return newArguments;
	}

	private ArrayCreation varArgsToArray(List arguments, ITypeBinding[] parameterTypes) {
		ArrayInitializer arrayInitializer = ast.newArrayInitializer();
		for (int i = parameterTypes.length-1; i < arguments.size(); ++i) {
			arrayInitializer.expressions().add(copy((ASTNode) arguments.get(i)));
		}

		ArrayCreation varArgsArray = ast.newArrayCreation();
		varArgsArray.setInitializer(arrayInitializer);

		varArgsArray.setType((ArrayType) newType(parameterTypes[parameterTypes.length-1]));
		return varArgsArray;
	}

	private List copyAllArgumentsButVarArgs(List arguments, ITypeBinding[] parameterTypes) {
		List newArguments = new ArrayList();
		for (int i = 0; i < parameterTypes.length - 1; i++) {
			ASTNode arg = (ASTNode) arguments.get(i);
			newArguments.add(copy( (ASTNode) arg));
		}
		return newArguments;
	}

	public boolean visit(MethodDeclaration method) {
		if (handledAsIgnored(method)) {
			return false;
		}
		if (method.isVarargs()) {
			handleVarArgsMethod(method);
		}
		return true;
	}

	private void handleVarArgsMethod(MethodDeclaration method) {
		MethodDeclaration decafMethod = copy(method);

		List parameters = decafMethod.parameters();
		SingleVariableDeclaration varArgsParameter = lastParameter(parameters);

		SingleVariableDeclaration expandedVarArgsParam = newSingleVariableDeclaration(
															copy(varArgsParameter.getName()),
															ast.newArrayType(copy(varArgsParameter.getType())));

		parameters.remove(varArgsParameter);
		parameters.add(expandedVarArgsParam);

		replace(method, decafMethod);
	}

	private SingleVariableDeclaration newSingleVariableDeclaration(SimpleName paramName, Type paramType) {
		SingleVariableDeclaration expandedVarArgsParam = ast.newSingleVariableDeclaration();
		expandedVarArgsParam.setName(paramName);
		expandedVarArgsParam.setType(paramType);
		return expandedVarArgsParam;
	}

	private SingleVariableDeclaration lastParameter(List parameters) {
		return (SingleVariableDeclaration) parameters.get(parameters.size()-1);
	}

	public boolean visit(EnhancedForStatement node) {

		SingleVariableDeclaration variable = node.getParameter();
		Expression array = node.getExpression();

		VariableDeclarationStatement tempArrayVariable = null;
		if (!isName(array)) {
			String tempArrayName = variable.getName() + "Array";
			tempArrayVariable = newVariableDeclarationStatement(
				tempArrayName,
				newType(array.resolveTypeBinding()),
				copy(array));
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
								newFieldAccess(copy(array), "length"));

		Block newBody = ast.newBlock();
		newBody.statements().add(
				newVariableDeclarationStatement(
						variable.getName().toString(),
						copy(variable.getType()),
						newArrayAccess(
								copy(array),
								newSimpleName(indexVariableName))));

		copyTo(node.getBody(), newBody);

		PrefixExpression updater = newPrefixExpression(
												PrefixExpression.Operator.INCREMENT,
												newSimpleName(indexVariableName));

		ForStatement stmt = newForStatement(index, cmp, updater, newBody);
		if (null == tempArrayVariable) {
			replace(node, stmt);
		} else {
			replace(node, newBlock(tempArrayVariable, stmt));
		}
		return false;
	}

	private void copyTo(Statement statement, Block body) {
		if (statement instanceof Block) {
			body.statements().addAll(copyAll(((Block)statement).statements()));
		} else {
			body.statements().add(copy(statement));
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

	private <T extends ASTNode> T copy(T array) {
		return (T) ASTNode.copySubtree(ast, array);
	}

	private List copyAll(List nodes) {
		return ASTNode.copySubtrees(ast, nodes);
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