package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import sharpen.core.framework.*;

@SuppressWarnings("unchecked")
public final class DecafRewritingVisitor extends ASTVisitor {
	
	private final DecafRewritingContext _context;

	public DecafRewritingVisitor(DecafRewritingContext context) {
		_context = context;
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
		final List<TagElement> mixins = getJavadocTags(node, DecafAnnotations.MIXIN);
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
		rewrite().remove(node);
		return false;	
	}
	
	@Override
	public boolean visit(MarkerAnnotation node) {
		rewrite().remove(node);
		return false;	
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		rewrite().remove(node);
		return false;	
	}
	
	@Override
	public boolean visit(TypeParameter node) {
		rewrite().remove(node);
		return false;
	}
	
	@Override
	public boolean visit(ParameterizedType node) {
		rewrite().replace(node, builder().mapType(node.getType()));
		return false;		
	}
	
	@Override
	public boolean visit(SimpleType node) {
		final ITypeBinding binding = node.resolveBinding();
		if (binding.isTypeVariable()) {
			rewrite().replace(node, builder().safeMapTypeBinding(binding.getErasure()));
			return false;
		}
		
		final Type mappedType = builder().mappedType(binding);
		if (null != mappedType) {
			rewrite().replace(node, mappedType);
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
					rewrite().replace(expression, rewrite().unboxedMethodInvocation(expression));
				}
			} else {
				if (expression.resolveBoxing()) {
					if(!isMethodName(expression)) {
						rewrite().replace(expression, rewrite().box(expression));
					}
				}
			}
		}
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
		final Expression erasure = rewrite().erasureFor(node.getArray());
		if (null != erasure) {
			rewrite().replace(node.getArray(), erasure);
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
				rewrite().replaceWithCast(node, method.getReturnType());
			}
		}
		coerceIterableMethodArguments(node, method);
	}

	private void coerceIterableMethodArguments(MethodInvocation node,
			final IMethodBinding method) {
		ListRewrite rewrittenArgs = getListRewrite(node, MethodInvocation.ARGUMENTS_PROPERTY);
		List<Expression> rewrittenArgList = rewrittenArgs.getRewrittenList();
		int paramIdx = 0;
		for (ITypeBinding paramType : method.getParameterTypes()) {
			if(Iterable.class.getName().equals(paramType.getErasure().getQualifiedName())) {
				Expression iterableArg = rewrittenArgList.get(paramIdx);
				Expression coerced = coerceIterableExpression(iterableArg);
				if(coerced != iterableArg) {
					rewrite().replace(iterableArg, coerced);
				}
			}
			paramIdx++;
		}
	}

	private Expression coerceIterableExpression(Expression iterableExpr) {
		return _context.targetPlatform().iterablePlatformMapping().coerceIterableExpression(iterableExpr, builder(), rewrite());
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
		rewrite().replace(node, mapped.getName());
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
		final Expression erasure = rewrite().erasureForField(node, node.resolveFieldBinding());
		if (null != erasure) {
			rewrite().replace(node, erasure);
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
		new EnhancedForProcessor(_context).run(node);
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

	private void eraseParameterReferences(MethodDeclaration node, final HashSet<IVariableBinding> erasedParameters) {		
		node.accept(new ASTVisitor() {
			@Override
			public void endVisit(SimpleName node) {
				if (node.isDeclaration()) {
					return;
				}
				if (erasedParameters.contains(node.resolveBinding())) {
					rewrite().replaceWithCast(node, node.resolveTypeBinding());
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
		
		Type mappedType = builder().mappedType(expectedErasure);
		rewrite().replace(actualType, mappedType == null ? newType(expectedErasure) : mappedType);
		return true;
	}

	private Type newType(final ITypeBinding typeBinding) {
		return builder().newType(typeBinding);
	}

	private void handleVarArgsMethod(MethodDeclaration method) {
		SingleVariableDeclaration varArgsParameter = builder().lastParameter(method.parameters());

		set(varArgsParameter, SingleVariableDeclaration.VARARGS_PROPERTY, Boolean.FALSE);
		rewrite().replace(varArgsParameter.getType(), builder().newType(varArgsParameter.resolveBinding().getType().getErasure()));
	}

	private void set(final ASTNode node, final SimplePropertyDescriptor property, final Object value) {
		rewrite().set(node, property, value, null);
	}

	private boolean handledAsIgnored(BodyDeclaration node) {
		if (isIgnored(node)) {
			rewrite().remove(node);
			return true;
		}
		return false;
	}

	private void processIgnoreExtends(TypeDeclaration node) {
		if (ignoreExtends(node)) {
			rewrite().remove(node.getSuperclassType());
		}
	}
	

	public boolean ignoreExtends(TypeDeclaration node) {
		return containsJavadoc(node, DecafAnnotations.IGNORE_EXTENDS);
	}

	public boolean isIgnored(BodyDeclaration node) {
		return containsJavadoc(node, DecafAnnotations.IGNORE);
	}

	private boolean containsJavadoc(BodyDeclaration node, String tag) {
		if (JavadocUtility.containsJavadoc(node, tag)) {
			return true;
		}
		return JavadocUtility.containsJavadoc(node, platformSpecificTag(tag));
	}

	private String platformSpecificTag(String tag) {
		return _context.targetPlatform().appendPlatformId(tag, ".");
	}

	private void processIgnoreImplements(TypeDeclaration node) {
		final Set<String> ignoredImplements = ignoredImplements(node);
		if (ignoredImplements.isEmpty()) {
			return;
		}
		
		for (Object o : node.superInterfaceTypes()) {
			final Type type = (Type)o;
			if (ignoredImplements.contains(type.toString())) {
				rewrite().remove(type);
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
		
		final List<TagElement> tags = getJavadocTags(node, DecafAnnotations.IGNORE_IMPLEMENTS);
		if (tags.isEmpty()) {
			return Collections.emptySet();
		}
		
		if (singleTagWithNoFragments(tags)) {
			return allSuperInterfaceTypeNames(node);
		}
		
		return textFragments(tags);
	}

	private List<TagElement> getJavadocTags(BodyDeclaration node, String tagName) {
		final Javadoc javadoc = node.getJavadoc();
		if (null == javadoc) {
			return Collections.emptyList();
		}
		final ArrayList<TagElement> found = new ArrayList<TagElement>();
		JavadocUtility.collectTags(javadoc.tags(), tagName, found);
		if (_context.targetPlatform().isNone()) {
			return found;
		}
		return JavadocUtility.collectTags(javadoc.tags(), platformSpecificTag(tagName), found);
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
			arrayInitializer.expressions().add(rewrite().safeMove(arg));
		}
		
		argumentListRewrite.insertLast(
				builder().newArrayCreation(parameters[parameters.length-1], arrayInitializer),
				null);
	}

	private void processNameErasure(Name node) {
		final Expression erasure = rewrite().erasureForName(node);
		if (null != erasure) {
			rewrite().replace(node, erasure);
		}
	}	

	private ListRewrite getListRewrite(ASTNode node, ChildListPropertyDescriptor property) {
		return rewrite().getListRewrite(node, property);
	}
	
	private DecafRewritingServices rewrite() {
		return _context.rewrite();
	}
}