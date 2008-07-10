package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;

public class MixinProcessor {
	
	private final ListRewrite _nodeDeclarationsRewrite;
	private final TypeDeclaration _mixin;
	private final FieldDeclaration _mixinField;
	private final TypeDeclaration _node;
	private final DecafRewritingContext _context;

	public MixinProcessor(DecafRewritingContext context, TypeDeclaration node, String mixinTypeName) {
		_context = context;
		_node = node;
		_nodeDeclarationsRewrite = getListRewrite(node, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		_mixin = resolveMixin(mixinTypeName);
		_mixinField = newMixinFieldDeclaration();
		
	}

	public void run() {
		_nodeDeclarationsRewrite.insertFirst(_mixinField, null);
		
		generateMixinDelegators();
		
		introduceMixinInstantiations();
	}
	
	private void introduceMixinInstantiations() {
		
		_node.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {
				if (!node.isConstructor()) {
					return false;
				}
				
				final ClassInstanceCreation mixinInstantiation = newClassInstanceCreation(_mixin);
				mixinInstantiation.arguments().add(builder().newThisExpression());
				addParametersToArgumentList(node, mixinInstantiation.arguments());
				bodyListRewriteFor(node).insertLast(
					builder().newExpressionStatement(
						builder().newAssignment(
							newMixinFieldAccess(),
							mixinInstantiation)),
					null);
				
				return false;
			}			
		});
	}
	
	private void generateMixinDelegators() {
		_mixin.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {
				
				if (node.isConstructor()) {
					return false;
				}
				
				final MethodInvocation delegation = builder().newMethodInvocation(
					newMixinFieldAccess(),
					node.getName().toString());
				addParametersToArgumentList(node, delegation.arguments());
								
				final Statement stmt = returnsValue(node)
					? builder().newReturnStatement(delegation)
					: builder().newExpressionStatement(delegation);
				
				final MethodDeclaration delegator = builder().clone(node);
				delegator.setBody(builder().newBlock(stmt));
				
				_nodeDeclarationsRewrite.insertLast(delegator, null);
				return false;
			}
		});
	}
	
	private boolean returnsValue(MethodDeclaration node) {
		final Type returnType = node.getReturnType2();		
		if (!returnType.isPrimitiveType()) {
			return true;
		}
		return PrimitiveType.VOID != ((PrimitiveType)returnType).getPrimitiveTypeCode();
	}
	
	private FieldDeclaration newMixinFieldDeclaration() {
		final FieldDeclaration mixinField = newField(newType(_mixin.resolveBinding()), "_mixin", null);
		mixinField.modifiers().add(builder().newPrivateModifier());
		mixinField.modifiers().add(builder().newFinalModifier());
		return mixinField;
	}

	private ClassInstanceCreation newClassInstanceCreation(
			final TypeDeclaration type) {
		return builder().newClassInstanceCreation(newType(type.resolveBinding()));
	}

	private FieldDeclaration newField(Type fieldType, String fieldName, Expression initializer) {
		return builder().newField(fieldType, fieldName, initializer);
	}

	private TypeDeclaration resolveMixin(String mixinTypeName) {
		final CompilationUnit unit = (CompilationUnit) _node.getParent();
		for (Object o : unit.types()) {
			final TypeDeclaration typeDeclaration = (TypeDeclaration)o;
			if (typeDeclaration.getName().toString().equals(mixinTypeName)) {
				return typeDeclaration;
			}
		}
		throw new IllegalArgumentException("Type '" + mixinTypeName + "' must be defined in the same file as '" + _node.getName() + "'.");
	}
	
	private void addParametersToArgumentList(MethodDeclaration node,
			final List argumentList) {
		for (Object o : node.parameters()) {
			final SingleVariableDeclaration parameter = (SingleVariableDeclaration)o;
			argumentList.add(builder().clone(parameter.getName()));
		}
	}

	private Expression newMixinFieldAccess() {
		return builder().newFieldAccess(
			builder().newThisExpression(),
			"_mixin");
	}
	
	private DecafASTNodeBuilder builder() {
		return _context.builder();
	}
	
	private ListRewrite bodyListRewriteFor(MethodDeclaration node) {
		return getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
	}

	private ListRewrite getListRewrite(final ASTNode node,
			final ChildListPropertyDescriptor property) {
		return rewrite().getListRewrite(node, property);
	}

	private ASTRewrite rewrite() {
		return _context.rewrite();
	}
	
	private Type newType(final ITypeBinding typeBinding) {
		return builder().newType(typeBinding);
	}
}
