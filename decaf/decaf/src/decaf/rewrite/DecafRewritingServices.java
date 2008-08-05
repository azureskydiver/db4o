package decaf.rewrite;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

public class DecafRewritingServices {
	private final ASTRewrite _rewrite;
	private final DecafASTNodeBuilder _builder;
	
	public DecafRewritingServices(ASTRewrite rewrite, DecafASTNodeBuilder builder) {
		_rewrite = rewrite;
		_builder = builder;
	}
	
	public <T extends ASTNode> T move(T node) {
		return (T) rewrite().createMoveTarget(node);
	}

	public <T extends ASTNode> T safeMove(final T arg) {
		return builder().isExistingNode(arg) ? move(arg) : arg;
	}
	
	public void replace(ASTNode node, ASTNode replacement) {
		rewrite().replace(node, replacement, null);
	}

	public void remove(ASTNode node) {
		rewrite().remove(node, null);
	}
	
	public void replaceWithCast(Expression node, final ITypeBinding type) {
		replace(node, createCastForErasure(node, type));
	}
	
	public Expression erasureFor(final Expression expression) {
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
	
	public Expression erasureForName(final Name name) {
		final IVariableBinding field = builder().fieldBinding(name);
		if (null == field) {
			return null;
		}
		return erasureForField(name, field);
	}

	public Expression erasureForField(final Expression expression,
			final IVariableBinding field) {
		if (builder().isErasedFieldAccess(field)) {
			return createCastForErasure(expression, field.getType());
		}
		return null;
	}
	
	public Object get(ASTNode node, StructuralPropertyDescriptor parentLocation) {
		return rewrite().get(node, parentLocation);
	}
	
	public void set(ASTNode node, SimplePropertyDescriptor property, Object value, TextEditGroup editGroup) {
		rewrite().set(node, property, value, editGroup);		
	}	
	
	public ListRewrite getListRewrite(ASTNode node, ChildListPropertyDescriptor property) {
		return rewrite().getListRewrite(node, property);
	}
	
	public ASTNode createStringPlaceholder(String code, int nodeType) {
		return rewrite().createStringPlaceholder(code, nodeType);
	}

	public MethodInvocation unboxedMethodInvocation(final Expression expression) {
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

	public ClassInstanceCreation box(final Expression expression) {
		SimpleType type = builder().newSimpleType(builder().boxedTypeFor(expression.resolveTypeBinding()));
		final ClassInstanceCreation creation = builder().newClassInstanceCreation(type);
		creation.arguments().add(safeMove(expression));
		return creation;
	}

	public MethodInvocation unbox(final Expression expression) {
		return builder().newMethodInvocation(
			parenthesizedMove(expression),
			builder().unboxingMethodFor(expression.resolveTypeBinding()));
	}

	public MethodInvocation unboxModified(final Expression expression, final Expression modified) {
		return unboxModified(modified, expression.resolveTypeBinding());
	}

	public MethodInvocation unboxModified(final Expression modified, final ITypeBinding typeBinding) {
		return unboxModified(modified, typeBinding.getQualifiedName());
	}

	public MethodInvocation unboxModified(final Expression modified,
			String name) {
		return builder().newMethodInvocation(
			builder().parenthesize(modified),
			builder().unboxingMethodFor(name));
	}

	private Expression parenthesizedMove(final Expression expression) {
		Expression moved = move(expression);
		final Expression target = expression instanceof Name
			? moved
			: builder().parenthesize(moved);
		return target;
	}
	
	private Expression createCastForErasure(Expression node, final ITypeBinding type) {
		return builder().createParenthesizedCast(move(node), type);
	}
	
	private ASTRewrite rewrite() {
		return _rewrite;
	}

	private DecafASTNodeBuilder builder() {
		return _builder;
	}
}
