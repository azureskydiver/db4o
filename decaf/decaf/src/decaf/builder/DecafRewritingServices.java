package decaf.builder;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.text.edits.TextEditGroup;

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
