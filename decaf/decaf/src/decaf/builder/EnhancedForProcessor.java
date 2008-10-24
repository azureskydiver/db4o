package decaf.builder;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import sharpen.core.framework.*;
import decaf.core.*;
import decaf.rewrite.*;

public class EnhancedForProcessor {

	private final DecafRewritingContext _context;

	public EnhancedForProcessor(DecafRewritingContext context) {
		_context = context;
	}

	public void run(EnhancedForStatement node) {
		final SingleVariableDeclaration variable = node.getParameter();
		final Expression origExpr = node.getExpression();
		final Expression erasure = erasureFor(origExpr);
		final Expression sequenceExpr = erasure != null ? erasure : origExpr;

		if(origExpr.resolveTypeBinding().isArray()) {
			buildArrayEnhancedFor(node, variable, sequenceExpr);
		} else {
			buildIterableEnhancedFor(node, variable, sequenceExpr);
		}
	}
	
	private final DecafRewritingServices.CastProvider _platformCoercion = new DecafRewritingServices.CastProvider() {
		public Expression provide(Expression e, ITypeBinding type) {
			if (exposesIteratorMethod(type))
				return builder().createParenthesizedCast(rewrite().safeMove(e), type);
			return iterablePlatformMapping().coerceIterableExpression(e);
        }
	};

	private Expression erasureFor(final Expression origExpr) {
		DynamicVariableCapture context = rewrite().forceCastOnErasure().using(true);
		if (isJDK12()) {
			context = context.combine(rewrite().castForErasureProvider().using(_platformCoercion));
		}
		return context.run(new Producer<Expression>() {
			public Expression produce() {
				return rewrite().erasureFor(origExpr);
			}
		});
    }

	private boolean isJDK12() {
		return TargetPlatform.JDK12 == targetPlatform();
    }

	private IterablePlatformMapping iterablePlatformMapping() {
	    return targetPlatform().iterablePlatformMapping();
    }

	private TargetPlatform targetPlatform() {
	    return _context.targetPlatform();
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
			tempArrayVariable = builder().newVariableDeclarationStatement(name, builder().newType(node.getExpression().resolveTypeBinding()), rewrite().safeMove(array));
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

		final ListRewrite statementsRewrite = rewrite().getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
		statementsRewrite.insertFirst(
				builder().newVariableDeclarationStatement(
						variable.getName().toString(),
						builder().clone(builder().mapType(variable.getType())),
						builder().newArrayAccess(
								clone(arrayReference),
								builder().newSimpleName(indexVariableName))), null);

		final PrefixExpression updater = builder().newPrefixExpression(
				PrefixExpression.Operator.INCREMENT,
				builder().newSimpleName(indexVariableName));

		replaceEnhancedForStatement(node, tempArrayVariable, index, cmp,updater);
	}
	
	private void buildIterableEnhancedFor(EnhancedForStatement node, final SingleVariableDeclaration variable, final Expression iterable) {
		IterablePlatformMapping iterableMapping = iterablePlatformMapping();
		buildIterableEnhancedFor(node, variable, iterable, iterableMapping.iteratorClassName(), iterableMapping.iteratorNextCheckName(), iterableMapping.iteratorNextElementName());	
	}

	private void buildIterableEnhancedFor(EnhancedForStatement node,
			final SingleVariableDeclaration variable,
			final Expression iterable, String iteratorClassName,
			String nextCheckMethodName, String nextElementMethodName) {
		
		final String iterVariableName = variable.getName() + "Iter";
		
		final VariableDeclarationExpression iter = builder().newVariableDeclaration(
				builder().newSimpleType(iteratorClassName),
				iterVariableName,
				builder().newMethodInvocation(rewrite().safeMove(iterable), "iterator"));

		final Expression cmp = builder().newMethodInvocation(builder().newSimpleName(iterVariableName), nextCheckMethodName);

		final ListRewrite statementsRewrite = rewrite().getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
		Expression initializerExpr = newInitializerExpression(variable, nextElementMethodName, iterVariableName);
		statementsRewrite.insertFirst(
				builder().newVariableDeclarationStatement(
						variable.getName().toString(),
						builder().clone(builder().mapType(variable.getType())),
						initializerExpr), null);

		replaceEnhancedForStatement(node, null, iter, cmp, null);
	}

	private Expression newInitializerExpression(final SingleVariableDeclaration elementVariable, String nextElementMethodName, final String iterVariableName) {
		final MethodInvocation iterNextInvocation = builder().newMethodInvocation(builder().newSimpleName(iterVariableName), nextElementMethodName);
		final ITypeBinding elementType = elementVariable.getType().resolveBinding();
		if (elementVariable.getType().isPrimitiveType()) {
			final String wrapperTypeName = "java.lang." + builder().boxedTypeFor(elementType);
			final ITypeBinding wrapperType = builder().resolveWellKnownType(wrapperTypeName);
			final Expression castExpr = builder().newCast(iterNextInvocation, wrapperType);
			return rewrite().unboxModified(castExpr, wrapperType);
		} else {
			if (Bindings.qualifiedName(elementType.getErasure()).equals("java.lang.Object"))
				return iterNextInvocation;
			return builder().createParenthesizedCast(iterNextInvocation, elementType);
		}
	}
	
	private DecafASTNodeBuilder builder() {
		return _context.builder();
	}
	
	private void replace(ASTNode node, ASTNode replacement) {
		rewrite().replace(node, replacement);
	}

	private DecafRewritingServices rewrite() {
		return _context.rewrite();
	}

	private void replaceEnhancedForStatement(EnhancedForStatement node, Statement tempVariable, Expression loopVar, Expression cmp, final Expression updater) {
		final ForStatement stmt = builder().newForStatement(loopVar, cmp, updater, rewrite().move(node.getBody()));
		ASTNode replacement = (null == tempVariable) ? stmt : builder().newBlock(tempVariable, stmt);
		replace(node, replacement);
	}	
	
	private <T extends ASTNode> T clone(T node) {
		return builder().clone(node);
	}

	private boolean exposesIteratorMethod(ITypeBinding type) {
		return null != findMethod(type, "iterator");
    }

	private IMethodBinding findMethod(ITypeBinding type, String methodName) {
		while (type != null) {
			final IMethodBinding found = declaredMethod(type, methodName);
			if (null != found)
				return found;
			type = type.getSuperclass();
		}
		return null;
    }

	private IMethodBinding declaredMethod(ITypeBinding type, String methodName) {
		for (IMethodBinding method : type.getDeclaredMethods())
	        if (method.getName().equals(methodName))
	        	return method;
	    return null;
    }
}
