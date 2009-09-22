/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import sharpen.core.framework.*;
import decaf.core.*;
import decaf.rewrite.*;

public abstract class DecafVisitorBase extends ASTVisitor {

	protected final DecafRewritingContext _context;

	public DecafVisitorBase(DecafRewritingContext context) {
		super(false);
		_context = context;
	}

	protected boolean isApplicableToTargetPlatform(final IAnnotationBinding annotationBinding) {
		if (targetPlatform().isNone())
			return true; // annotation is considered to be valid for all platforms
		
		final Set<String> platforms = applicablePlatformsFor(annotationBinding);
		
		return platforms.contains("ALL")
	    	|| platforms.contains(targetPlatform().toString());
	}

	protected boolean typeHasQualifiedName(final ITypeBinding type, String qualifiedName) {
	    return BindingUtils.qualifiedName(type).equals(qualifiedName);
	}

	private Set<String> applicablePlatformsFor(IAnnotationBinding annotationBinding) {
		Set<String> declaredPlatforms = applicablePlatformsFor(annotationBinding.getDeclaredMemberValuePairs());
		if (null != declaredPlatforms && declaredPlatforms.size() > 0) 
			return declaredPlatforms;
		
		return applicablePlatformsFor(annotationBinding.getAllMemberValuePairs());
	}

	private Set<String> applicablePlatformsFor(IMemberValuePairBinding[] pairs) {
		Set<String> platforms = new HashSet<String>();
		for (IMemberValuePairBinding valuePair : pairs) {
			final Object value = valuePair.getValue();
			if (value instanceof Object[]) {
				tryAddToPlatforms(platforms, (Object[]) value);
			} else {
				tryAddToPlatform(platforms, value);
			}
	    }
		return platforms;
	}

	private void tryAddToPlatforms(Set<String> platforms, final Object[] values) {
		for (Object value : values) {
			tryAddToPlatform(platforms, value);
		}
	}
	
	private void tryAddToPlatform(Set<String> platforms, final Object value) {
		if (!(value instanceof IVariableBinding)) {
			return;
		}
			
		final IVariableBinding variable = (IVariableBinding)value;
		if (isDecafPlatform(variable.getType())) {
			platforms.add(variable.getName());
		}
	}

	private boolean isDecafPlatform(ITypeBinding type) {
		return typeHasSameQualifiedNameAs(type, decaf.Platform.class);
	}

	protected DecafASTNodeBuilder builder() {
		return _context.builder();
	}

	protected TargetPlatform targetPlatform() {
	    return _context.targetPlatform();
	}

	protected boolean typeHasSameQualifiedNameAs(final ITypeBinding type, Class<?> classToCompare) {
	    return typeHasQualifiedName(type, classToCompare.getName());
	}

	protected DecafRewritingServices rewrite() {
		return _context.rewrite();
	}

	protected Object[] memberValuesFrom(IAnnotationBinding annotation, String memberName) {
		final Object value = memberValueFrom(annotation, memberName);
		return value instanceof Object[] ? (Object[])value : new Object[] { value };
	}

	protected <T> T memberValueFrom(IAnnotationBinding annotation, String memberName) {
		for (IMemberValuePairBinding valuePair : annotation.getAllMemberValuePairs()) {
	        if (valuePair.getName().equals(memberName))
	        	return (T)valuePair.getValue();
	    }
		throw new IllegalArgumentException("No '" + memberName + "' member in annotation '" + annotation + "'.");
	}

	protected boolean isAnnotation(IAnnotationBinding annotation, Class<?> annotationClass) {
	    return typeHasSameQualifiedNameAs(annotation.getAnnotationType(), annotationClass);
	}

	protected ListRewrite getListRewrite(ASTNode node, ChildListPropertyDescriptor property) {
		return rewrite().getListRewrite(node, property);
	}
	
	protected boolean isIgnored(IBinding binding) {
		return containsAnnotation(binding, decaf.Ignore.class);
	}

	private boolean containsAnnotation(final IBinding binding, String annotation) {
		final IAnnotationBinding annotationBinding = findAnnotation(binding, annotation);
		if (annotationBinding == null)
			return false;
		return isApplicableToTargetPlatform(annotationBinding);
	}

	private IAnnotationBinding findAnnotation(IBinding binding, String annotationQualifiedName) {
	    for (IAnnotationBinding annotationBinding : binding.getAnnotations()) {
			final ITypeBinding annotationtype = annotationBinding.getAnnotationType();
			if (typeHasQualifiedName(annotationtype, annotationQualifiedName))
				return annotationBinding;
		}
		return null;
	}

	protected boolean containsAnnotation(IBinding binding, Class<?> annotationType) {
		return containsAnnotation(binding, annotationType.getName());
	}

	protected boolean isMarkedForRemoval(final IBinding binding) {
	    return containsAnnotation(binding, decaf.Remove.class);
	}

	protected void removeAll(final List nodes) {
	    for (Object importNode : nodes) {
			rewrite().remove((ASTNode) importNode);
		}
	}
}