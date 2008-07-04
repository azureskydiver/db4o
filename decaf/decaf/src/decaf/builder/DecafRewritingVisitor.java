package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;


@SuppressWarnings("unchecked")
public final class DecafRewritingVisitor extends ASTVisitor {
	private final ASTRewrite rewrite;
	private final DecafASTNodeBuilder builder;

	public DecafRewritingVisitor(AST ast, ASTRewrite rewrite, DecafConfiguration decafConfig) {
		builder = new DecafASTNodeBuilder(ast, decafConfig);
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
		ITypeBinding binding = node.getType().resolveBinding();
		replace(node, builder.mappedType(binding, builder.newType(binding.getErasure())));
		return false;
	}
	
	@Override
	public boolean visit(SimpleType node) {
		ITypeBinding binding = node.resolveBinding();
		if (binding.isTypeVariable()) {
			binding = binding.getErasure();
			Type mapped = 
				builder.mappedType(binding, builder.newType(binding));
			replace(node, mapped);
			return false;
		}
		return true;
	}
	
	@Override
	public void postVisit(ASTNode node) {
		if (node instanceof Expression) {
			final Expression expression = (Expression)node;
			if (expression.resolveUnboxing()) {
				if(!isMethodName(expression)) {
					replace(expression, unboxedMethodInvocation(expression));
				}
			} 
			else {
				if (expression.resolveBoxing()) {
					if(!isMethodName(expression)) {
						replace(expression, box(expression));
					}
				}
			}
		}
	}

	private MethodInvocation unboxedMethodInvocation(final Expression expression) {
		Expression modified = expression;
		if(expression.getNodeType() == ASTNode.METHOD_INVOCATION) {
			ASTNode parent = expression.getParent();
			StructuralPropertyDescriptor parentLocation = expression.getLocationInParent();
			if(parentLocation.isChildListProperty()) {
				// FIXME This assumes that original and rewritten list are of the same size.
				ListRewrite listRewrite = rewrite.getListRewrite(parent, (ChildListPropertyDescriptor) parentLocation);
				List originalList = listRewrite.getOriginalList();
				int originalIdx = originalList.indexOf(expression);
				modified = (Expression) listRewrite.getRewrittenList().get(originalIdx);
			}
			else {
				modified = (Expression) rewrite.get(parent, parentLocation);
			}
		}
		return (expression == modified ? unbox(modified) : unboxModified(expression, modified));
	}

	private boolean isMethodName(Expression exp) {
		return (exp.getParent().getNodeType() == ASTNode.METHOD_INVOCATION) && (exp.getLocationInParent() == MethodInvocation.NAME_PROPERTY);
	}
	
	@Override
	public void endVisit(ClassInstanceCreation node) {
		final IMethodBinding ctor = node.resolveConstructorBinding();
		final List arguments = node.arguments();
		if (builder.requiresVarArgsTranslation(ctor, arguments)) {
			rewriteVarArgsArguments(ctor, arguments, rewrite.getListRewrite(node, ClassInstanceCreation.ARGUMENTS_PROPERTY));
		}
	}
	
	@Override
	public void endVisit(ArrayAccess node) {
		final Expression erasure = erasureFor(node.getArray());
		if (null != erasure) {
			replace(node.getArray(), erasure);
		}
	}
	
	public boolean visit(MethodInvocation node) {
		return true;
	}
	
	@Override
	public void endVisit(MethodInvocation node) {
		final IMethodBinding method = node.resolveMethodBinding();
		final List arguments = node.arguments();
		if (builder.requiresVarArgsTranslation(method, arguments)) {
			rewriteVarArgsArguments(method, arguments, rewrite.getListRewrite(node, MethodInvocation.ARGUMENTS_PROPERTY));
		}

		if (!builder.isExpressionStatement(node.getParent())) {
			if (builder.hasGenericReturnType(method)) {
				replaceWithCast(node, method.getReturnType());
			}
		}
	}

	@Override
	public void endVisit(SimpleName node) {
		if(mapStaticInvocationClassName(node)) {
			return;
		}
		
		if (node.isDeclaration()) {
			return;
		}
		
		if (node.getLocationInParent() == QualifiedName.NAME_PROPERTY) {
			return ;
		}
		
		if (node.getLocationInParent() == FieldAccess.NAME_PROPERTY) {
			return;
		}

		processNameErasure(node);
	}

	private boolean mapStaticInvocationClassName(Name node) {
		// FIXME overcomplicated and fragile, too many unjustified assumptions here - find better way to handle static method invocation type mappings
		if(node.getLocationInParent() != MethodInvocation.EXPRESSION_PROPERTY) {
			return false;
		}
		MethodInvocation invocation = (MethodInvocation) node.getParent();
		boolean isStatic = (invocation.resolveMethodBinding().getModifiers() & Modifier.STATIC) != 0;
		ITypeBinding binding = node.resolveTypeBinding();
		if(!isStatic || binding == null) {
			return false;
		}
		SimpleType mapped = (SimpleType)builder.mappedType(binding, null);
		if(mapped == null) {
			return false;
		}
		replace(node, mapped.getName());
		return true;
	}
	
	@Override
	public void endVisit(QualifiedName node) {
		if(mapStaticInvocationClassName(node)) {
			return;
		}
		processNameErasure(node);
	}

	@Override
	public void endVisit(FieldAccess node) {
		final Expression erasure = erasureForField(node, node.resolveFieldBinding());
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
		
		final IMethodBinding definition = builder.originalMethodDefinitionFor(node);
		if (definition == null) {
			return;
		}
		
		final IMethodBinding originalMethodDeclaration = definition.getMethodDeclaration();
		if (originalMethodDeclaration != definition) {
			eraseMethodDeclaration(node, originalMethodDeclaration);
		}	
	}

	public void endVisit(EnhancedForStatement node) {

		final SingleVariableDeclaration variable = node.getParameter();
		Expression origExpr = node.getExpression();
		final Expression erasure = erasureFor(origExpr);
		final Expression sequenceExpr = erasure != null ? erasure : origExpr;

		if(origExpr.resolveTypeBinding().isArray()) {
			buildArrayEnhancedFor(node, variable, sequenceExpr);
		}
		else {
			buildIterableEnhancedFor(node, variable, sequenceExpr);
		}
	}

	private void buildArrayEnhancedFor(EnhancedForStatement node,
			final SingleVariableDeclaration variable, final Expression array) {
		String name = variable.getName() + "Array";
		Expression arrayReference = null;

		VariableDeclarationStatement tempArrayVariable = null;
		if (builder.isName(array)) {
			arrayReference = array;
		} 
		else {
			tempArrayVariable = builder.newVariableDeclarationStatement(name, builder.newType(node.getExpression().resolveTypeBinding()), safeMove(array));
			arrayReference = builder.newSimpleName(name);
		}

		final String indexVariableName = variable.getName() + "Index";
		VariableDeclarationExpression index = builder.newVariableDeclaration(
				builder.newPrimitiveType(PrimitiveType.INT),
				indexVariableName,
				builder.newNumberLiteral("0"));

		Expression cmp = builder.newInfixExpression(
								InfixExpression.Operator.LESS,
								builder.newSimpleName(indexVariableName),
								builder.newFieldAccess(builder.clone(arrayReference), builder.newSimpleName("length")));

		final ListRewrite statementsRewrite = rewrite.getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
		statementsRewrite.insertFirst(
				builder.newVariableDeclarationStatement(
						variable.getName().toString(),
						builder.clone(variable.getType()),
						builder.newArrayAccess(
								builder.clone(arrayReference),
								builder.newSimpleName(indexVariableName))), null);

		final PrefixExpression updater = builder.newPrefixExpression(
				PrefixExpression.Operator.INCREMENT,
				builder.newSimpleName(indexVariableName));

		replaceEnhancedForStatement(node, tempArrayVariable, index, cmp,updater);
	}

	private void buildIterableEnhancedFor(EnhancedForStatement node, final SingleVariableDeclaration variable, final Expression iterable) {
		final String iterVariableName = variable.getName() + "Iter";
		VariableDeclarationExpression iter = builder.newVariableDeclaration(
				builder.newSimpleType(Iterator.class.getName()),
				iterVariableName,
				builder.newMethodInvocation(safeMove(iterable), "iterator"));

		Expression cmp = builder.newMethodInvocation(builder.newSimpleName(iterVariableName), "hasNext");

		final ListRewrite statementsRewrite = rewrite.getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
		statementsRewrite.insertFirst(
				builder.newVariableDeclarationStatement(
						variable.getName().toString(),
						builder.clone(variable.getType()),
						builder.createParenthesizedCast(builder.newMethodInvocation(builder.newSimpleName(iterVariableName), "next"), variable.getType().resolveBinding())), null);

		replaceEnhancedForStatement(node, null, iter, cmp, null);
	}

	private void replaceEnhancedForStatement(EnhancedForStatement node, Statement tempVariable, Expression loopVar, Expression cmp, final Expression updater) {
		final ForStatement stmt = builder.newForStatement(loopVar, cmp, updater, move(node.getBody()));
		ASTNode replacement = (null == tempVariable) ? stmt : builder.newBlock(tempVariable, stmt);
		replace(node, replacement);
	}


	@Override
	public boolean visit(PackageDeclaration node) {
		return false;
	}

	private void eraseMethodDeclaration(MethodDeclaration node, IMethodBinding originalMethodDeclaration) {
		eraseReturnType(node, originalMethodDeclaration);
		final HashSet<IVariableBinding> erasedParameters = eraseParametersWhereNeeded(node, originalMethodDeclaration);
		eraseParameterReferences(node, erasedParameters);
	}

	private void eraseReturnType(MethodDeclaration node, IMethodBinding originalMethodDeclaration) {
		eraseIfNeeded(node.getReturnType2(), originalMethodDeclaration.getReturnType());
	}

	private void eraseParameterReferences(MethodDeclaration node,
			final HashSet<IVariableBinding> erasedParameters) {
		node.accept(new ASTVisitor() {
			@Override
			public void endVisit(SimpleName node) {
				if (node.isDeclaration()) {
					return;
				}
				if (erasedParameters.contains(node.resolveBinding())) {
					replaceWithCast(node, node.resolveTypeBinding());
				}
			}
		});
	}

	
	private HashSet<IVariableBinding> eraseParametersWhereNeeded(
			MethodDeclaration node, IMethodBinding originalMethodDeclaration) {
		final HashSet<IVariableBinding> erasedParameters = new HashSet<IVariableBinding>();
		final ITypeBinding[] parameterTypes = originalMethodDeclaration.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			final SingleVariableDeclaration actualParameter = (SingleVariableDeclaration) node.parameters().get(i);
			if (eraseIfNeeded(actualParameter.getType(), parameterTypes[i])) {
				erasedParameters.add(actualParameter.resolveBinding());
			}
		}
		return erasedParameters;
	}

	private boolean eraseIfNeeded(final Type actualType, final ITypeBinding expectedType) {
		final ITypeBinding expectedErasure = expectedType.getErasure();
		if (actualType.resolveBinding() == expectedErasure) {
			return false;
		}
		replace(actualType, builder.newType(expectedErasure));
		return true;
	}

	private void handleVarArgsMethod(MethodDeclaration method) {
		SingleVariableDeclaration varArgsParameter = builder.lastParameter(method.parameters());

		set(varArgsParameter, SingleVariableDeclaration.VARARGS_PROPERTY, Boolean.FALSE);
		replace(varArgsParameter.getType(), builder.newType(varArgsParameter.resolveBinding().getType().getErasure()));
	}

	private void set(final ASTNode node, final SimplePropertyDescriptor property, final Object value) {
		rewrite.set(node, property, value, null);
	}

	private boolean handledAsIgnored(BodyDeclaration node) {
		if (builder.isIgnored(node)) {
			remove(node);
			return true;
		}
		return false;
	}

	private MethodInvocation unbox(final Expression expression) {
		return builder.newMethodInvocation(
			parenthesizedMove(expression),
			builder.unboxingMethodFor(expression.resolveTypeBinding()));
	}

	private MethodInvocation unboxModified(final Expression expression, final Expression modified) {
		return builder.newMethodInvocation(
			builder.parenthesize(modified),
			builder.unboxingMethodFor(expression.resolveTypeBinding()));
	}

	private ClassInstanceCreation box(final Expression expression) {
		final ClassInstanceCreation creation = builder.newClassInstanceCreation();
		creation.setType(builder.newSimpleType(builder.boxedTypeFor(expression.resolveTypeBinding())));
		creation.arguments().add(safeMove(expression));
		return creation;
	}

	private void replaceWithCast(Expression node, final ITypeBinding type) {
		replace(node, createCastForErasure(node, type));
	}

	private Expression createCastForErasure(Expression node, final ITypeBinding type) {
		return builder.createParenthesizedCast(move(node), type);
	}

	private Expression erasureFor(final Expression expression) {
		if (expression instanceof Name) {
			return erasureForName((Name)expression);
		}
		if (expression instanceof FieldAccess) {
			return erasureForField(expression, ((FieldAccess)expression).resolveFieldBinding());
		}
		if(expression instanceof MethodInvocation) {
			return erasureForMethodInvocation(((MethodInvocation)expression));
		}
		return null;
	}

	private Expression erasureForMethodInvocation(MethodInvocation node) {
		final IMethodBinding method = node.resolveMethodBinding();
		if (builder.hasGenericReturnType(method)) {
			return createCastForErasure(node, method.getReturnType());
		}
		return null;
	}

	private Expression erasureForName(final Name name) {
		final IVariableBinding field = builder.fieldBinding(name);
		if (null == field) {
			return null;
		}
		return erasureForField(name, field);
	}

	private Expression erasureForField(final Expression expression,
			final IVariableBinding field) {
		if (builder.isErasedFieldAccess(field)) {
			return createCastForErasure(expression, field.getType());
		}
		return null;
	}
	
	private Expression parenthesizedMove(final Expression expression) {
		Expression moved = move(expression);
		final Expression target = expression instanceof Name
			? moved
			: builder.parenthesize(moved);
		return target;
	}
	
	
	private void processIgnoreExtends(TypeDeclaration node) {
		if (builder.ignoreExtends(node)) {
			remove(node.getSuperclassType());
		}
	}

	private void processIgnoreImplements(TypeDeclaration node) {
		final Set<String> ignoredImplements = builder.ignoredImplements(node);
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

	private void rewriteVarArgsArguments(final IMethodBinding method,
			final List arguments, final ListRewrite argumentListRewrite) {
		
		final ITypeBinding[] parameters = method.getParameterTypes();
		final List rewrittenArguments = argumentListRewrite.getRewrittenList();
		final List originalList = argumentListRewrite.getOriginalList();
		for (int i = parameters.length-1; i < originalList.size(); ++i) {
			argumentListRewrite.remove((ASTNode)originalList.get(i), null);
		}
		
		ArrayInitializer arrayInitializer = builder.newArrayInitializer();
		for (int i = parameters.length-1; i < rewrittenArguments.size(); ++i) {
			final Expression arg = (Expression) rewrittenArguments.get(i);
			arrayInitializer.expressions().add(safeMove(arg));
		}
		
		argumentListRewrite.insertLast(
				builder.newArrayCreation(parameters[parameters.length-1], arrayInitializer),
				null);
	}

	private <T extends ASTNode> T safeMove(final T arg) {
		return builder.isExistingNode(arg) ? move(arg) : arg;
	}

	private void processNameErasure(Name node) {
		final Expression erasure = erasureForName(node);
		if (null != erasure) {
			replace(node, erasure);
		}
	}	

	private void remove(ASTNode node) {
		rewrite.remove(node, null);
	}

	private void replace(ASTNode node, ASTNode replacement) {
		rewrite.replace(node, replacement, null);
	}
	
	private <T extends ASTNode> T move(T node) {
		return (T)rewrite.createMoveTarget(node);
	}

}