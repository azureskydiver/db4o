package decaf.builder;

import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

public class EnhancedForProcessor {

	private final DecafRewritingContext _context;

	public EnhancedForProcessor(DecafRewritingContext context) {
		_context = context;
	}

	public void run(EnhancedForStatement node) {
		final SingleVariableDeclaration variable = node.getParameter();
		final Expression origExpr = node.getExpression();
		final Expression erasure = rewrite().erasureFor(origExpr);
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
		if (_context.targetPlatform().isJDK11()) {
			buildIterableEnhancedFor(node, variable, iterable, "com.db4o.foundation.Iterator4", "moveNext", "current");	
		}
		else {
			buildIterableEnhancedFor(node, variable, iterable, Iterator.class.getName(), "hasNext", "next");
		}
	}

	private void buildIterableEnhancedFor(EnhancedForStatement node,
			final SingleVariableDeclaration variable,
			final Expression iterable, String iteratorClassName,
			String nextCheckMethodName, String nextElementMethodName) {
		final String iterVariableName = variable.getName() + "Iter";
		
		VariableDeclarationExpression iter = builder().newVariableDeclaration(
				builder().newSimpleType(iteratorClassName),
				iterVariableName,
				builder().newMethodInvocation(rewrite().safeMove(iterable), "iterator"));

		Expression cmp = builder().newMethodInvocation(builder().newSimpleName(iterVariableName), nextCheckMethodName);

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
		MethodInvocation iterNextInvocation = builder().newMethodInvocation(builder().newSimpleName(iterVariableName), nextElementMethodName);
		ITypeBinding elementType = elementVariable.getType().resolveBinding();
		if(elementVariable.getType().isPrimitiveType()) {
			String wrapperTypeName = "java.lang." + builder().boxedTypeFor(elementType);
			ITypeBinding wrapperType = builder().resolveWellKnownType(wrapperTypeName);
			Expression castExpr = builder().newCast(iterNextInvocation, wrapperType);
			return rewrite().unboxModified(castExpr, wrapperType);
		}
		else {
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
}
