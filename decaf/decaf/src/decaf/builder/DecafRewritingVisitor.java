package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import sharpen.core.framework.*;
import decaf.core.*;
import decaf.rewrite.*;

@SuppressWarnings("unchecked")
public final class DecafRewritingVisitor extends ASTVisitor {
	
	private final DecafRewritingContext _context;
	
	public DecafRewritingVisitor(DecafRewritingContext context) {
		_context = context;
	}
	
	@Override
	public void endVisit(CompilationUnit node) {
		if (allTopLevelTypesHaveBeenRemoved(node)) {
			// imports are removed to avoid possible compilation errors
			removeImports(node);
		}
		super.endVisit(node);
	}

	private void removeImports(CompilationUnit node) {
		removeAll(node.imports());
	}

	private void removeAll(final List nodes) {
	    for (Object importNode : nodes) {
			rewrite().remove((ASTNode) importNode);
		}
    }

	private boolean allTopLevelTypesHaveBeenRemoved(CompilationUnit node) {
		return rewrittenTypeListFor(node).isEmpty();
	}

	private List rewrittenTypeListFor(CompilationUnit node) {
		return getListRewrite(node, CompilationUnit.TYPES_PROPERTY).getRewrittenList();
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		if (handledAsIgnored(node, node.resolveBinding()) || handledAsRemoved(node)) {
			return false;
		}
		return true;
	}

	private boolean handledAsRemoved(TypeDeclaration node) {
	    if (isMarkedForRemoval(node.resolveBinding())) {
	    	rewrite().remove(node);
	    	return true;
	    }
	    return false;
    }

	private boolean isMarkedForRemoval(final IBinding binding) {
	    return containsAnnotation(binding, decaf.Remove.class);
    }

	private boolean containsAnnotation(final IBinding binding, String annotation) {
		final IAnnotationBinding annotationBinding = findAnnotation(binding, annotation);
		if (annotationBinding == null)
			return false;
		
		return isApplicableToTargetPlatform(annotationBinding);
	}

	private boolean isApplicableToTargetPlatform(final IAnnotationBinding annotationBinding) {
		if (targetPlatform().isNone())
			return true; // annotation is considered to be valid for all platforms
		
		final String platform = applicablePlatformFor(annotationBinding);
		if (platform == null)
			return false; // not a decaf annotation
		
		return platform.equals("ALL")
        	|| platform.equals(targetPlatform().toString());
	}

	private IAnnotationBinding findAnnotation(IBinding binding, String annotationQualifiedName) {
	    for (IAnnotationBinding annotationBinding : binding.getAnnotations()) {
			final ITypeBinding annotationtype = annotationBinding.getAnnotationType();
			if (typeHasQualifiedName(annotationtype, annotationQualifiedName))
				return annotationBinding;
		}
		return null;
    }

	private boolean typeHasQualifiedName(final ITypeBinding type, String qualifiedName) {
	    return Bindings.qualifiedName(type).equals(qualifiedName);
    }

	private String applicablePlatformFor(IAnnotationBinding annotationBinding) {
		for (IMemberValuePairBinding valuePair : annotationBinding.getAllMemberValuePairs()) {
			final Object value = valuePair.getValue();
            if (!(value instanceof IVariableBinding))
            	continue;
            
            final IVariableBinding variable = (IVariableBinding)value;
            if (isDecafPlatform(variable.getType()))
            	return variable.getName();
        }
		return null;
	}

	private boolean isDecafPlatform(ITypeBinding type) {
		return typeHasSameQualifiedNameAs(type, decaf.Platform.class);
    }

	@Override
	public boolean visit(EnumDeclaration node) {
		if (isIgnored(node.resolveBinding())) {
			rewrite().remove(node);
			return false;
		}
		
		final TypeDeclaration enumType = new EnumProcessor(_context).run(node);		
		rewrite().replace(node, enumType);
		
		return false;
	}
	
	@Override
	public boolean visit(SwitchStatement node) {
		final ITypeBinding binding = node.getExpression().resolveTypeBinding();
		if (!binding.isEnum()) {
			return true;
		}
		rewrite().replace(node, new EnumProcessor(_context).transformEnumSwitchStatement(node));
		return false;
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		processIgnoreExtends(node);
		processIgnoreImplements(node);
		processMixins(node);
	}
	
	private void processMixins(TypeDeclaration node) {
		for (TypeDeclaration mixin : node.getTypes()) {
			if (!isMixin(mixin))
				continue;
			processMixin(node, mixin);
		}
	}

	private boolean isMixin(TypeDeclaration type) {
		return containsAnnotation(type.resolveBinding(), decaf.Mixin.class);
    }

	private boolean containsAnnotation(IBinding binding, Class<?> annotationType) {
		return containsAnnotation(binding, annotationType.getName());
    }

	private void processMixin(TypeDeclaration node, TypeDeclaration mixinType) {
		
		new MixinProcessor(_context, node, mixinType).run();
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
	public boolean visit(NormalAnnotation node) {
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
		if (!(node instanceof Expression)) {
			return;
		}
		
		try {
			postVisitExpression((Expression)node);
		} catch (RuntimeException e) {
			unsupportedConstruct(node, e);
		}
	}
	
	private void unsupportedConstruct(ASTNode node, Exception cause) {
		unsupportedConstruct(node, "failed to map: '" + node + "'", cause);
	}
	
	protected String sourceInformation(ASTNode node) {
		return builder().sourceInformationFor(node);
	}

	private void unsupportedConstruct(ASTNode node, final String message, Exception cause) {
		throw new IllegalArgumentException(sourceInformation(node) + ": " + message, cause);
	}

	private void postVisitExpression(final Expression expression) {
		
		if (!isAutoBoxingTarget(expression)) {
			return;
			
		}
	    if (expression.resolveUnboxing()) {
			final Expression erasure = rewrite().erasureFor(expression);
			rewrite().replace(expression, rewrite().unboxedMethodInvocation(erasure != null ? erasure : expression, expression.resolveTypeBinding()));
			return;
		}
	    
	    if (expression.resolveBoxing()) {
			rewrite().replace(expression, rewrite().box(expression));
		}
    }

	private boolean isAutoBoxingTarget(Expression exp) {
		if (!(exp instanceof Name)) {
			return true;
		}
		return exp.getLocationInParent() != MethodInvocation.NAME_PROPERTY
			&& !isPartOfFieldAccess(exp)
			&& !isPartOfQualifiedName(exp);
	}

	private boolean isPartOfFieldAccess(Expression exp) {
		if (exp.getLocationInParent() == FieldAccess.NAME_PROPERTY) {
			return true;
		}
	    return exp.getLocationInParent() == FieldAccess.EXPRESSION_PROPERTY;
    }

	private boolean isPartOfQualifiedName(Expression exp) {
		if (exp.getLocationInParent() == QualifiedName.NAME_PROPERTY) {
			return true;
		}
		return exp.getLocationInParent() == QualifiedName.QUALIFIER_PROPERTY;
    }
	
	@Override
	public void endVisit(ClassInstanceCreation node) {
		try {
			final IMethodBinding ctor = node.resolveConstructorBinding();
			final List arguments = node.arguments();
			rewriteVarArgsArguments(ctor, arguments, getListRewrite(node, ClassInstanceCreation.ARGUMENTS_PROPERTY));
		} catch (RuntimeException e) {
			System.err.println("Error processing node '" + node + "': " + e);
			throw e;
		}
	}
	
	@Override
	public void endVisit(ArrayAccess node) {
		final Expression erasure = rewrite().erasureFor(node.getArray());
		if (null != erasure) {
			rewrite().replace(node.getArray(), erasure);
		}
	}
	
	@Override
	public void endVisit(Assignment node) {
		if (isIterableInterface(node.getLeftHandSide().resolveTypeBinding())) {
			replaceCoercedIterable(node.getRightHandSide());
		}
	}
	
	public boolean visit(MethodInvocation node) {
		return true;
	}
	
	@Override
	public void endVisit(MethodInvocation node) {
		
		if (mapClassCastIdiom(node))
			return;
		
		removeAll(node.typeArguments());
		
		final IMethodBinding method = node.resolveMethodBinding();
		final List arguments = node.arguments();
		rewriteVarArgsArguments(method, arguments, getListRewrite(node, MethodInvocation.ARGUMENTS_PROPERTY));

		if (!builder().isExpressionStatement(node.getParent())) {
			if (builder().hasGenericReturnType(method)) {
				rewrite().replaceWithCast(node, method.getReturnType());
			}
		}
		coerceIterableMethodArguments(node, method);
		IdiomProcessor.processMethodInvocation(_context, node);
	}
	
	private boolean mapClassCastIdiom(MethodInvocation node) {
		if (!Bindings.qualifiedName(node.resolveMethodBinding()).equals("java.lang.Class.cast"))
			return false;
		
		rewrite().replace(
					node,
					builder().newCast(
							rewrite().move((Expression)node.arguments().get(0)),
							node.resolveMethodBinding().getReturnType()));
		return true;
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		replaceCoercedIterableFieldInitializer(node.fragments(), node.getType());
	}
	
	@Override
	public void endVisit(VariableDeclarationStatement node) {
		replaceCoercedIterableFieldInitializer(node.fragments(), node.getType());
	}

	private void replaceCoercedIterableFieldInitializer(List<VariableDeclarationFragment> fragments, Type type) {
		if(isIterableInterface(type.resolveBinding())) {
			for (VariableDeclarationFragment fragment : fragments) {
				replaceCoercedIterable(fragment.getInitializer());
			}
		}
	}

	@Override
	public void endVisit(ReturnStatement node) {
		if(node.getExpression() == null) {
			return;
		}
		MethodDeclaration methodDeclaration = builder().findMethodDeclarationParent(node);
		if(!isIterableInterface(methodDeclaration.getReturnType2().resolveBinding())) {
			return;
		}
		replaceCoercedIterable(node.getExpression());
	}
	
	@Override
	public void endVisit(CastExpression node) {
		if(!isIterableInterface(node.getExpression().resolveTypeBinding())) {
			return;
		}
		Expression rewrittenExpr = (Expression) rewrite().get(node, CastExpression.EXPRESSION_PROPERTY);
		replaceUnwrappedIterable(rewrittenExpr);
	}
	
	@Override
	public boolean visit(TypeLiteral node) {
	    if (isMarkedForRemoval(node.getType().resolveBinding())) {
			rewrite().remove(node);
			return false;
		}
	    return super.visit(node);
	}

	private void coerceIterableMethodArguments(MethodInvocation node,
			final IMethodBinding method) {
		ListRewrite rewrittenArgs = getListRewrite(node, MethodInvocation.ARGUMENTS_PROPERTY);
		List<Expression> rewrittenArgList = rewrittenArgs.getRewrittenList();
		int paramIdx = 0;
		for (ITypeBinding paramType : method.getParameterTypes()) {
			if(isIterableInterface(paramType)) {
				Expression iterableArg = rewrittenArgList.get(paramIdx);
				replaceCoercedIterable(iterableArg);
			}
			paramIdx++;
		}
	}

	private void replaceCoercedIterable(Expression iterableArg) {
		Expression coerced = iterablePlatformMapping().coerceIterableExpression(iterableArg);
		if(coerced != iterableArg) {
			rewrite().replace(iterableArg, coerced);
		}
	}

	private IterablePlatformMapping iterablePlatformMapping() {
	    return targetPlatform().iterablePlatformMapping();
    }

	private TargetPlatform targetPlatform() {
	    return _context.targetPlatform();
    }

	private void replaceUnwrappedIterable(Expression iterableArg) {
		Expression unwrapped = iterablePlatformMapping().unwrapIterableExpression(iterableArg);
		if(unwrapped != iterableArg) {
			rewrite().replace(iterableArg, unwrapped);
		}
	}

	private boolean isIterableInterface(ITypeBinding paramType) {
		return Iterable.class.getName().equals(paramType.getErasure().getQualifiedName());
	}

	@Override
	public void endVisit(SimpleName node) {
		
		if (node.isDeclaration()) {
			return;
		}
		
		if (mapNameOfStaticMethodInvocation(node)) {
			return;
		}
		
		if (mapStaticInvocationClassName(node)) {
			return;
		}
		
		
		if (node.getLocationInParent() == QualifiedName.NAME_PROPERTY) {
			return;
		}
		
		if (node.getLocationInParent() == FieldAccess.NAME_PROPERTY) {
			return;
		}

		processNameErasure(node);
	}

	private boolean mapNameOfStaticMethodInvocation(SimpleName node) {
		if (node.getLocationInParent() != MethodInvocation.NAME_PROPERTY)
			return false;
		
		final MethodInvocation invocation = parentMethodInvocation(node);
		if (invocation.getExpression() != null)
			return false;
		
		final IMethodBinding method = invocation.resolveMethodBinding();
		if (!isStaticImport(method))
			return false;
		
		rewrite().replace(node, 
				builder().newQualifiedName(Bindings.qualifiedName(method)));
			
		return true;
	}

	private boolean isStaticImport(IMethodBinding method) {
		if (!isStatic(method))
			return false;
		
		for (Object imp : builder().compilationUnit().imports())
			if (isStaticMethodImport((ImportDeclaration) imp, method))
				return true;
		
		return false;
	}

	private boolean isStaticMethodImport(ImportDeclaration imp, IMethodBinding method) {
		final IBinding binding = imp.resolveBinding();
		switch (binding.getKind()) {
		case IBinding.TYPE:
			return imp.isOnDemand() && method.getDeclaringClass() == binding;
		}
		return false;
	}

	private MethodInvocation parentMethodInvocation(SimpleName node) {
		return ((MethodInvocation)node.getParent());
	}

	private boolean mapStaticInvocationClassName(Name node) {
		// FIXME overcomplicated and fragile, too many unjustified assumptions here - find better way to handle static method invocation type mappings
		if(!isExpressionOfMethodInvocation(node)) {
			return false;
		}
		final MethodInvocation invocation = (MethodInvocation) node.getParent();
		final ITypeBinding binding = node.resolveTypeBinding();
		if(!isStatic(invocation) || binding == null) {
			return false;
		}
		SimpleType mapped = (SimpleType)builder().mappedType(binding);
		if(mapped == null) {
			return false;
		}
		rewrite().replace(node, mapped.getName());
		return true;
	}

	private boolean isStatic(MethodInvocation invocation) {
		return isStatic(invocation.resolveMethodBinding());
	}

	private boolean isStatic(final IMethodBinding method) {
		return Modifier.isStatic(method.getModifiers());
	}

	private boolean isExpressionOfMethodInvocation(Name node) {
		return node.getLocationInParent() == MethodInvocation.EXPRESSION_PROPERTY;
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
		if (isLeftHandSideOfAssignment(node)) {
			return;
		}
		final Expression erasure = rewrite().erasureForField(node, node.resolveFieldBinding());
		if (null != erasure) {
			rewrite().replace(node, erasure);
		}
	}

	public boolean visit(MethodDeclaration node) {
		if (handledAsIgnored(node, node.resolveBinding())) {
			return false;
		}
		
		rewrite().erasingParameters(!isPredicateMatchMethod(node));
		return true;
	}
	
	private boolean isPredicateMatchMethod(MethodDeclaration node) {
		return builder().isPredicateMatchOverride(node);
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
		if (!rewrite().erasingParameters())
			return;
		
		if (node.isConstructor())
			return;
		
		final IMethodBinding definition = builder().originalMethodDefinitionFor(node);
		if (definition == null)
			return;
		
		final IMethodBinding declaration = definition.getMethodDeclaration();
		if (declaration == definition)
			return;
		
		eraseMethodDeclaration(node, declaration);
	}

	private void processRewritingAnnotations(MethodDeclaration node) {
		
		if (node.getBody() == null) {
			return;
		}
		
		final ListRewrite bodyRewrite = bodyListRewriteFor(node);
		for (IAnnotationBinding annotation : node.resolveBinding().getAnnotations()) {
			
			if (!isApplicableToTargetPlatform(annotation))
				continue;
			
			if (isAnnotation(annotation, decaf.InsertFirst.class)) {
				bodyRewrite.insertFirst(statementFrom(annotation), null);
			} else if (isAnnotation(annotation, decaf.ReplaceFirst.class)) {
				bodyRewrite.replace(firstNode(bodyRewrite), statementFrom(annotation), null);
			} else if (isAnnotation(annotation, decaf.RemoveFirst.class)) {
				bodyRewrite.remove(firstNode(bodyRewrite), null);
			}
		}
	}

	private ASTNode statementFrom(IAnnotationBinding annotation) {
	    return statementFrom((String)valueFrom(annotation));
    }

	private <T> T valueFrom(IAnnotationBinding annotation) {
	    return (T)memberValueFrom(annotation, "value");
    }

	private <T> T memberValueFrom(IAnnotationBinding annotation, String memberName) {
		for (IMemberValuePairBinding valuePair : annotation.getAllMemberValuePairs()) {
	        if (valuePair.getName().equals(memberName))
	        	return (T)valuePair.getValue();
        }
		throw new IllegalArgumentException("No '" + memberName + "' member in annotation '" + annotation + "'.");
    }

	private boolean isAnnotation(IAnnotationBinding annotation, Class<?> annotationClass) {
	    return typeHasSameQualifiedNameAs(annotation.getAnnotationType(), annotationClass);
    }

	private boolean typeHasSameQualifiedNameAs(final ITypeBinding type, Class<?> classToCompare) {
	    return typeHasQualifiedName(type, classToCompare.getName());
    }

	private ASTNode firstNode(final ListRewrite bodyRewrite) {
		return originalNodeAt(bodyRewrite, 0);
	}

	private ASTNode originalNodeAt(final ListRewrite bodyRewrite,
			final int index) {
		return (ASTNode) bodyRewrite.getOriginalList().get(index);
	}

	private ASTNode statementFrom(final String code) {
	    return rewrite().createStringPlaceholder(code, ASTNode.EXPRESSION_STATEMENT);
    }

	private ListRewrite bodyListRewriteFor(MethodDeclaration node) {
		return getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
	}

	public void endVisit(final EnhancedForStatement node) {
		new EnhancedForProcessor(_context).run(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		return false;
	}
	
	@Override
	public void endVisit(ImportDeclaration node) {
		if (node.isStatic()) {
			rewrite().remove(node);
		}
	}

	private void eraseMethodDeclaration(MethodDeclaration node, IMethodBinding originalMethodDeclaration) {
		eraseReturnType(node, originalMethodDeclaration);
		eraseParametersWhereNeeded(node, originalMethodDeclaration);
	}

	private void eraseReturnType(MethodDeclaration node, IMethodBinding originalMethodDeclaration) {
		eraseIfNeeded(node.getReturnType2(), originalMethodDeclaration.getReturnType());
	}
	
	private void eraseParametersWhereNeeded(
			MethodDeclaration node, IMethodBinding originalMethodDeclaration) {
		final ITypeBinding[] parameterTypes = originalMethodDeclaration.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			final SingleVariableDeclaration actualParameter = parameterAt(node, i);
			eraseIfNeeded(actualParameter.getType(), parameterTypes[i]);
		}
	}

	private SingleVariableDeclaration parameterAt(MethodDeclaration node, int i) {
	    return (SingleVariableDeclaration) node.parameters().get(i);
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

	private boolean handledAsIgnored(BodyDeclaration node, IBinding binding) {
		if (isIgnored(binding)) {
			rewrite().remove(node);
			return true;
		}
		return false;
	}

	private boolean isIgnored(IBinding binding) {
		return containsAnnotation(binding, decaf.Ignore.class);
	}

	private void processIgnoreExtends(TypeDeclaration node) {
		if (ignoreExtends(node)) {
			rewrite().remove(node.getSuperclassType());
		}
	}

	public boolean ignoreExtends(TypeDeclaration node) {
		return containsAnnotation(node, decaf.IgnoreExtends.class);
	}

	private boolean containsAnnotation(TypeDeclaration node, Class<?> annotationType) {
	   return containsAnnotation(node.resolveBinding(), annotationType);
    }

	private void processIgnoreImplements(TypeDeclaration node) {
		final Set<ITypeBinding> ignoredImplements = ignoredImplements(node);
		if (ignoredImplements.isEmpty()) {
			return;
		}
		
		for (Object o : node.superInterfaceTypes()) {
			final Type type = (Type)o;
			if (ignoredImplements.contains(type.resolveBinding())) {
				rewrite().remove(type);
			}
		}
	}	
	
	private Set<ITypeBinding> allSuperInterfaceBindings(TypeDeclaration node) {
		final List superInterfaces = node.superInterfaceTypes();
		final HashSet<ITypeBinding> set = new HashSet<ITypeBinding>(superInterfaces.size());
		for (Object o : superInterfaces) {
			set.add(((Type)o).resolveBinding());
		}
		return set;
	}
	
	public Set<ITypeBinding> ignoredImplements(TypeDeclaration node) {
		final HashSet<ITypeBinding> ignored = new HashSet<ITypeBinding>();
		for (IAnnotationBinding annotation : node.resolveBinding().getAnnotations()) {
			if (!isAnnotation(annotation, decaf.IgnoreImplements.class))
				continue;
			if (!isApplicableToTargetPlatform(annotation))
				continue;
			
			final Object[] interfaces = memberValuesFrom(annotation, "interfaces");
			if (interfaces.length == 0)
				return allSuperInterfaceBindings(node);

			for (Object itf : interfaces)
				ignored.add((ITypeBinding)itf);
        }
		return ignored;
	}
	
	private Object[] memberValuesFrom(IAnnotationBinding annotation, String memberName) {
		final Object value = memberValueFrom(annotation, memberName);
		return value instanceof Object[] ? (Object[])value : new Object[] { value };
    }

	private void rewriteVarArgsArguments(final IMethodBinding method,
			final List arguments, final ListRewrite argumentListRewrite) {
		
		if (!builder().requiresVarArgsTranslation(method, arguments))
			return;
		
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
		if (isLeftHandSideOfAssignment(node)) {
			return;
		}
		
		final Expression erasure = rewrite().erasureForName(node);
		if (null != erasure) {
			rewrite().replace(node, erasure);
		}
	}	

	private boolean isLeftHandSideOfAssignment(ASTNode node) {
		return node.getLocationInParent() == Assignment.LEFT_HAND_SIDE_PROPERTY;
    }

	private ListRewrite getListRewrite(ASTNode node, ChildListPropertyDescriptor property) {
		return rewrite().getListRewrite(node, property);
	}
	
	private DecafRewritingServices rewrite() {
		return _context.rewrite();
	}
}