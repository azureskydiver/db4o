/**
 * 
 */
package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;
import org.eclipse.jdt.core.dom.rewrite.*;

public final class DecafRewritingVisitor extends ASTVisitor {
	private final AST ast;
	private final ASTRewrite rewrite;

	public DecafRewritingVisitor(AST ast, ASTRewrite rewrite) {
		this.ast = ast;
		this.rewrite = rewrite;
	}

	public boolean visit(EnhancedForStatement node) {
		
		SingleVariableDeclaration variable = node.getParameter();
		Expression array = node.getExpression();

		VariableDeclarationStatement tempArrayVariable = null;
		if (!isName(array)) {
			String tempArrayName = variable.getName() + "Array";
			tempArrayVariable = newVariableDeclarationStatement(
				tempArrayName,
				newType(array.resolveTypeBinding()),
				copy(array));
			array = ast.newSimpleName(tempArrayName);
		}
		
		String indexVariableName = variable.getName() + "Index";
		VariableDeclarationExpression index = newVariableDeclaration(
				ast.newPrimitiveType(PrimitiveType.INT),
				indexVariableName,
				ast.newNumberLiteral("0"));
		
		InfixExpression cmp = newInfixExpression(InfixExpression.Operator.LESS, ast.newSimpleName(indexVariableName), newFieldAccess(copy(array), ast.newSimpleName("length")));
			
		Block body = ast.newBlock();
		body.statements().add(newVariableDeclarationStatement(variable.getName().toString(), copy(variable.getType()), newArrayAccess(copy(array), ast.newSimpleName(indexVariableName))));
		if (node.getBody() instanceof Block) {
			body.statements().addAll(copyAll(((Block)node.getBody()).statements()));
		} else {
			body.statements().add(copy(node.getBody()));
		}
		
		PrefixExpression updater = newPrefixExpression(PrefixExpression.Operator.INCREMENT, ast.newSimpleName(indexVariableName));
		
		ForStatement stmt = newForStatement(index, cmp, updater, body);
		if (null == tempArrayVariable) {
			replace(node, stmt);	
		} else {
			replace(node, newBlock(tempArrayVariable, stmt));
		}
		return false;
	}

	private boolean isName(Expression array) {
		return array instanceof Name;
	}

	private ForStatement newForStatement(
			Expression initializer,
			Expression comparison,
			Expression updater,
			Block body) {
		ForStatement stmt = ast.newForStatement();
		stmt.initializers().add(initializer);
		stmt.setExpression(comparison);
		stmt.updaters().add(updater);
		stmt.setBody(body);
		return stmt;
	}

	private Block newBlock(Statement... stmts) {
		Block block = ast.newBlock();
		for (Statement stmt : stmts) {
			block.statements().add(stmt);
		}
		return block;
	}

	private Type newType(ITypeBinding type) {
		if (type.isArray()) {
			return ast.newArrayType(newType(type.getComponentType()));
		}
		return ast.newSimpleType(ast.newName(type.getName()));
	}

	private Expression newFieldAccess(Expression e, SimpleName fieldName) {
		FieldAccess field = ast.newFieldAccess();
		field.setExpression(e);
		field.setName(fieldName);
		return field;
	}

	private ArrayAccess newArrayAccess(Expression array, Expression index) {
		ArrayAccess access = ast.newArrayAccess();
		access.setArray(array);
		access.setIndex(index);
		return access;
	}

	private PrefixExpression newPrefixExpression(
			PrefixExpression.Operator operator,
			SimpleName operand) {
		PrefixExpression increment = ast.newPrefixExpression();
		increment.setOperator(operator);
		increment.setOperand(operand);
		return increment;
	}

	private InfixExpression newInfixExpression(Operator operator,
			Expression left, Expression right) {
		InfixExpression e = ast.newInfixExpression();
		e.setOperator(operator);
		e.setLeftOperand(left);
		e.setRightOperand(right);
		return e;
	}

	private VariableDeclarationStatement newVariableDeclarationStatement(
			String variableName, Type variableType, Expression initializer) {
		VariableDeclarationStatement variable = ast.newVariableDeclarationStatement(newVariableFragment(variableName, initializer));
		variable.setType(variableType);
		return variable;
	}

	private void replace(ASTNode node, ASTNode replacement) {
		rewrite.replace(node, replacement, null);
	}

	private <T extends ASTNode> T copy(T array) {
		return (T) ASTNode.copySubtree(ast, array);
	}
	
	private List copyAll(List nodes) {
		return ASTNode.copySubtrees(ast, nodes);
	}
	

	VariableDeclarationExpression newVariableDeclaration(Type variableType, String variableName, Expression initializer) {
		VariableDeclarationFragment indexFragment = newVariableFragment(variableName, initializer);
		
		VariableDeclarationExpression index = ast.newVariableDeclarationExpression(indexFragment);
		index.setType(variableType);
		return index;
	}

	VariableDeclarationFragment newVariableFragment(String variableName, Expression initializer) {
		VariableDeclarationFragment indexFragment = ast.newVariableDeclarationFragment();
		indexFragment.setName(ast.newSimpleName(variableName));
		indexFragment.setInitializer(initializer);
		return indexFragment;
	}


}