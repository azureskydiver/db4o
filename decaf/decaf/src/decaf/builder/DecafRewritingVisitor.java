package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import sharpen.core.framework.*;


@SuppressWarnings("unchecked")
public final class DecafRewritingVisitor extends ASTVisitor {
	
	private final DecafRewritingContext _context;

	public DecafRewritingVisitor(CompilationUnit unit, ASTRewrite rewrite, DecafConfiguration decafConfig) {
		_context = new DecafRewritingContext(unit, rewrite, decafConfig);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		if (handledAsIgnored(node)) {
			return false;
		}
		return true;
	}

	
	@Override
	public void endVisit(TypeDeclaration node) {
		processIgnoreExtends(node);
		processIgnoreImplements(node);
		processMixins(node);
	}
	
	private void processMixins(TypeDeclaration node) {
		final List<TagElement> mixins = JavadocUtility.getJavadocTags(node, DecafAnnotations.MIXIN);
		for (String mixin : textFragments(mixins)) {
			processMixin(node, mixin);
		}
	}

	private void processMixin(TypeDeclaration node, String mixinTypeName) {
		
		new MixinProcessor(_context, node, mixinTypeName).run();
	}
		
	private DecafASTNodeBuilder builder() {
		return _context.builder();
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		remove(node);
		return false;	
	}
	
	@Override
	public boolean visit(MarkerAnnotation node) {
		remove(node);
		return false;	
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		remove(node);
		return false;	
	}
	
	@Override
	public boolean visit(TypeParameter node) {
		remove(node);
		return false;
	}
	
	@Override
	public boolean visit(ParameterizedType node) {
		final ITypeBinding binding = node.getType().resolveBinding().getErasure();
		final Type mappedType = builder().mappedType(binding);
		replace(node, mappedType == null ? newType(binding) : mappedType);
		return false;
	}
	
	@Override
	public boolean visit(SimpleType node) {
		final ITypeBinding binding = node.resolveBinding();
		if (binding.isTypeVariable()) {
			final ITypeBinding erasure = binding.getErasure();
			final Type mapped = builder().mappedType(erasure);
			replace(node, mapped == null ? newType(erasure) : mapped);
			return false;
		}
		
		final Type mappedType = builder().mappedType(binding);
		if (null != mappedType) {
			replace(node, mappedType);
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
			} else {
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
				ListRewrite listRewrite = getListRewrite(parent, (ChildListPropertyDescriptor) parentLocation);
				List originalList = listRewrite.getOriginalList();
				int originalIdx = originalList.indexOf(expression);
				modified = (Expression) listRewrite.getRewrittenList().get(originalIdx);
			}
			else {
				modified = (Expression) rewrite().get(parent, parentLocation);
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
		if (builder().requiresVarArgsTranslation(ctor, arguments)) {
			rewriteVarArgsArguments(ctor, arguments, getListRewrite(node, ClassInstanceCreation.ARGUMENTS_PROPERTY));
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
		if (builder().requiresVarArgsTranslation(method, arguments)) {
			rewriteVarArgsArguments(method, arguments, getListRewrite(node, MethodInvocation.ARGUMENTS_PROPERTY));
		}

		if (!builder().isExpressionStatement(node.getParent())) {
			if (builder().hasGenericReturnType(method)) {
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
		SimpleType mapped = (SimpleType)builder().mappedType(binding);
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
		
		processRewritingAnnotations(node);
		processMethodDeclarationErasure(node);	
	}

	private void processMethodDeclarationErasure(MethodDeclaration node) {		
		if(node.isConstructor()) {
			return;
		}
		
		final IMethodBinding definition = builder().originalMethodDefinitionFor(node);
		if (definition == null) {
			return;
		}
		
		final IMethodBinding originalMethodDeclaration = definition.getMethodDeclaration();
		if (originalMethodDeclaration != definition) {
			eraseMethodDeclaration(node, originalMethodDeclaration);
		}
	}

	private void processRewritingAnnotations(MethodDeclaration node) {
		
		if (node.getBody() == null) {
			return;
		}
		
		final ListRewrite bodyRewrite = bodyListRewriteFor(node);
		for (TagElement tag : javadocTags(node)) {
			final String tagName = tag.getTagName();
			if (null == tagName) {
				continue;
			}
			if (tagName.equals(DecafAnnotations.INSERT_FIRST)) {
				bodyRewrite.insertFirst(statementFromTagText(tag), null);
			} else if (tagName.equals(DecafAnnotations.REPLACE_FIRST)) {
				bodyRewrite.replace(firstNode(bodyRewrite), statementFromTagText(tag), null);
			} else if (tagName.equals(DecafAnnotations.REMOVE_FIRST)) {
				bodyRewrite.remove(firstNode(bodyRewrite), null);
			}
		}
		
	}

	private ASTNode firstNode(final ListRewrite bodyRewrite) {
		return originalNodeAt(bodyRewrite, 0);
	}

	private ASTNode originalNodeAt(final ListRewrite bodyRewrite,
			final int index) {
		return (ASTNode) bodyRewrite.getOriginalList().get(index);
	}

	private ASTNode statementFromTagText(TagElement tag) {
		final String code = textFragment(tag, 0);
		final ASTNode stmt = rewrite().createStringPlaceholder(code, ASTNode.EXPRESSION_STATEMENT);
		return stmt;
	}

	private ListRewrite bodyListRewriteFor(MethodDeclaration node) {
		return getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
	}

	private List<TagElement> javadocTags(MethodDeclaration node) {
		final Javadoc javadoc = node.getJavadoc();
		return javadoc == null
			? Collections.emptyList()
			: javadoc.tags();
	}

	public void endVisit(EnhancedForStatement node) {

		final SingleVariableDeclaration variable = node.getParameter();
		final Expression origExpr = node.getExpression();
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
		if (builder().isName(array)) {
			arrayReference = array;
		} 
		else {
			tempArrayVariable = builder().newVariableDeclarationStatement(name, builder().newType(node.getExpression().resolveTypeBinding()), safeMove(array));
			arrayReference = builder().newSimpleName(name);
		}

		final String indexVariableName = variable.getName() + "Index";
		VariableDeclarationExpression index = builder().newVariableDeclaration(
				builder().newPrimitiveType(PrimitiveType.INT),
				indexVariableName,
				builder().newNumberLiteral("0"));

		Expression cmp = builder().newInfixExpression(
								InfixExpression.Operator.LESS,
								builder().newSimpleName(indexVariableName),
								builder().newFieldAccess(clone(arrayReference), builder().newSimpleName("length")));

		final ListRewrite statementsRewrite = getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
		statementsRewrite.insertFirst(
				builder().newVariableDeclarationStatement(
						variable.getName().toString(),
						builder().clone(variable.getType()),
						builder().newArrayAccess(
								clone(arrayReference),
								builder().newSimpleName(indexVariableName))), null);

		final PrefixExpression updater = builder().newPrefixExpression(
				PrefixExpression.Operator.INCREMENT,
				builder().newSimpleName(indexVariableName));

		replaceEnhancedForStatement(node, tempArrayVariable, index, cmp,updater);
	}

	private <T extends ASTNode> T clone(T node) {
		return builder().clone(node);
	}

	private void buildIterableEnhancedFor(EnhancedForStatement node, final SingleVariableDeclaration variable, final Expression iterable) {
		final String iterVariableName = variable.getName() + "Iter";
		VariableDeclarationExpression iter = builder().newVariableDeclaration(
				builder().newSimpleType(Iterator.class.getName()),
				iterVariableName,
				builder().newMethodInvocation(safeMove(iterable), "iterator"));

		Expression cmp = builder().newMethodInvocation(builder().newSimpleName(iterVariableName), "hasNext");

		final ListRewrite statementsRewrite = getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
		statementsRewrite.insertFirst(
				builder().newVariableDeclarationStatement(
						variable.getName().toString(),
						builder().clone(variable.getType()),
						builder().createParenthesizedCast(builder().newMethodInvocation(builder().newSimpleName(iterVariableName), "next"), variable.getType().resolveBinding())), null);

		replaceEnhancedForStatement(node, null, iter, cmp, null);
	}

	private void replaceEnhancedForStatement(EnhancedForStatement node, Statement tempVariable, Expression loopVar, Expression cmp, final Expression updater) {
		final ForStatement stmt = builder().newForStatement(loopVar, cmp, updater, move(node.getBody()));
		ASTNode replacement = (null == tempVariable) ? stmt : builder().newBlock(tempVariable, stmt);
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
		replace(actualType, newType(expectedErasure));
		return true;
	}

	private Type newType(final ITypeBinding typeBinding) {
		return builder().newType(typeBinding);
	}

	private void handleVarArgsMethod(MethodDeclaration method) {
		SingleVariableDeclaration varArgsParameter = builder().lastParameter(method.parameters());

		set(varArgsParameter, SingleVariableDeclaration.VARARGS_PROPERTY, Boolean.FALSE);
		replace(varArgsParameter.getType(), builder().newType(varArgsParameter.resolveBinding().getType().getErasure()));
	}

	private void set(final ASTNode node, final SimplePropertyDescriptor property, final Object value) {
		rewrite().set(node, property, value, null);
	}

	private boolean handledAsIgnored(BodyDeclaration node) {
		if (builder().isIgnored(node)) {
			remove(node);
			return true;
		}
		return false;
	}

	private MethodInvocation unbox(final Expression expression) {
		return builder().newMethodInvocation(
			parenthesizedMove(expression),
			builder().unboxingMethodFor(expression.resolveTypeBinding()));
	}

	private MethodInvocation unboxModified(final Expression expression, final Expression modified) {
		return builder().newMethodInvocation(
			builder().parenthesize(modified),
			builder().unboxingMethodFor(expression.resolveTypeBinding()));
	}

	private ClassInstanceCreation box(final Expression expression) {
		SimpleType type = builder().newSimpleType(builder().boxedTypeFor(expression.resolveTypeBinding()));
		final ClassInstanceCreation creation = builder().newClassInstanceCreation(type);
		creation.arguments().add(safeMove(expression));
		return creation;
	}

	private void replaceWithCast(Expression node, final ITypeBinding type) {
		replace(node, createCastForErasure(node, type));
	}

	private Expression createCastForErasure(Expression node, final ITypeBinding type) {
		return builder().createParenthesizedCast(move(node), type);
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
		if (builder().hasGenericReturnType(method)) {
			return createCastForErasure(node, method.getReturnType());
		}
		return null;
	}

	private Expression erasureForName(final Name name) {
		final IVariableBinding field = builder().fieldBinding(name);
		if (null == field) {
			return null;
		}
		return erasureForField(name, field);
	}

	private Expression erasureForField(final Expression expression,
			final IVariableBinding field) {
		if (builder().isErasedFieldAccess(field)) {
			return createCastForErasure(expression, field.getType());
		}
		return null;
	}
	
	private Expression parenthesizedMove(final Expression expression) {
		Expression moved = move(expression);
		final Expression target = expression instanceof Name
			? moved
			: builder().parenthesize(moved);
		return target;
	}
	
	
	private void processIgnoreExtends(TypeDeclaration node) {
		if (builder().ignoreExtends(node)) {
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
	
	private Set<String> allSuperInterfaceTypeNames(TypeDeclaration node) {
		final List superInterfaces = node.superInterfaceTypes();
		final HashSet<String> set = new HashSet<String>(superInterfaces.size());
		for (Object o : superInterfaces) {
			set.add(o.toString());
		}
		return set;
	}
	
	private boolean singleTagWithNoFragments(final List<TagElement> tags) {
		return tags.size() == 1 && tags.get(0).fragments().isEmpty();
	}

	public Set<String> ignoredImplements(TypeDeclaration node) {
		
		final List<TagElement> tags = JavadocUtility.getJavadocTags(node, DecafAnnotations.IGNORE_IMPLEMENTS);
		if (tags.isEmpty()) {
			return Collections.emptySet();
		}
		
		if (singleTagWithNoFragments(tags)) {
			return allSuperInterfaceTypeNames(node);
		}
		
		return textFragments(tags);
	}

	private Set<String> textFragments(final List<TagElement> tags) {
		final HashSet<String> ignored = new HashSet<String>(tags.size());
		for (TagElement tag : tags) {
			ignored.add(textFragment(tag, 0));
		}
		return ignored;
	}

	private String textFragment(TagElement tag, int index) {
		return JavadocUtility.textFragment(tag.fragments(), index);
	}

	private void rewriteVarArgsArguments(final IMethodBinding method,
			final List arguments, final ListRewrite argumentListRewrite) {
		
		final ITypeBinding[] parameters = method.getParameterTypes();
		final List rewrittenArguments = argumentListRewrite.getRewrittenList();
		final List originalList = argumentListRewrite.getOriginalList();
		for (int i = parameters.length-1; i < originalList.size(); ++i) {
			argumentListRewrite.remove((ASTNode)originalList.get(i), null);
		}
		
		ArrayInitializer arrayInitializer = builder().newArrayInitializer();
		for (int i = parameters.length-1; i < rewrittenArguments.size(); ++i) {
			final Expression arg = (Expression) rewrittenArguments.get(i);
			arrayInitializer.expressions().add(safeMove(arg));
		}
		
		argumentListRewrite.insertLast(
				builder().newArrayCreation(parameters[parameters.length-1], arrayInitializer),
				null);
	}

	private <T extends ASTNode> T safeMove(final T arg) {
		return builder().isExistingNode(arg) ? move(arg) : arg;
	}

	private void processNameErasure(Name node) {
		final Expression erasure = erasureForName(node);
		if (null != erasure) {
			replace(node, erasure);
		}
	}	

	private void remove(ASTNode node) {
		rewrite().remove(node, null);
	}

	private void replace(ASTNode node, ASTNode replacement) {
		rewrite().replace(node, replacement, null);
	}
	
	private ListRewrite getListRewrite(ASTNode node, ChildListPropertyDescriptor property) {
		return rewrite().getListRewrite(node, property);
	}
	
	private ASTRewrite rewrite() {
		return _context.rewrite();
	}

	private <T extends ASTNode> T move(T node) {
		return (T)rewrite().createMoveTarget(node);
	}

}