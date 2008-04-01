package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;
import org.eclipse.jdt.core.dom.rewrite.*;

@SuppressWarnings("unchecked")
public final class DecafRewritingVisitor extends ASTVisitor {
	private final AST ast;
	private final ASTRewrite rewrite;

	public DecafRewritingVisitor(AST ast, ASTRewrite rewrite) {
		this.ast = ast;
		this.rewrite = rewrite;
	}

	public boolean visit(MethodInvocation methodCall) {
		IMethodBinding methodBinding = methodCall.resolveMethodBinding();
		if (methodBinding.isVarargs()) {
			MethodInvocation explicityArrayInCall = copy(methodCall);			

			List arguments = explicityArrayInCall.arguments();
			
			List newArguments = mapVarArgsToArray(arguments, methodBinding.getParameterTypes());
			
			arguments.clear();
			arguments.addAll(newArguments);
			
			replace(methodCall, explicityArrayInCall);
		}
		
		return true;
	}

	private List mapVarArgsToArray(List arguments, ITypeBinding[] parameterTypes) {
		List newArguments = copyAllArgumentsButVarArgs(arguments, parameterTypes);					
		ArrayCreation varArgsArray = varArgsToArray(arguments, parameterTypes);
		newArguments.add(varArgsArray);
		return newArguments;
	}

	private ArrayCreation varArgsToArray(List arguments, ITypeBinding[] parameterTypes) {
		ArrayInitializer arrayInitializer = ast.newArrayInitializer();
		for (int i = parameterTypes.length-1; i < arguments.size(); ++i) {
			arrayInitializer.expressions().add(copy((ASTNode) arguments.get(i)));
		}

		ArrayCreation varArgsArray = ast.newArrayCreation();
		varArgsArray.setInitializer(arrayInitializer);
		
		varArgsArray.setType((ArrayType) newType(parameterTypes[parameterTypes.length-1]));
		return varArgsArray;
	}

	private List copyAllArgumentsButVarArgs(List arguments, ITypeBinding[] parameterTypes) {
		List newArguments = new ArrayList();			
		for (int i = 0; i < parameterTypes.length - 1; i++) {
			ASTNode arg = (ASTNode) arguments.get(i);
			newArguments.add(copy( (ASTNode) arg));
		}
		return newArguments;
	}
	
	public boolean visit(MethodDeclaration method) {
		if (method.isVarargs()) {
			MethodDeclaration decafMethod = copy(method);
			
			List parameters = decafMethod.parameters();
			SingleVariableDeclaration varArgsParameter = lastParameter(parameters);
			
			SingleVariableDeclaration expandedVarArgsParam = newSingleVariableDeclaration(
																copy(varArgsParameter.getName()), 
																ast.newArrayType(copy(varArgsParameter.getType())));
			
			parameters.remove(varArgsParameter);
			parameters.add(expandedVarArgsParam);
			
			replace(method, decafMethod);
		}
		
		return true;
	}

	private SingleVariableDeclaration newSingleVariableDeclaration(SimpleName paramName, Type paramType) {
		SingleVariableDeclaration expandedVarArgsParam = ast.newSingleVariableDeclaration();
		expandedVarArgsParam.setName(paramName);
		expandedVarArgsParam.setType(paramType);
		return expandedVarArgsParam;
	}

	private SingleVariableDeclaration lastParameter(List parameters) {
		return (SingleVariableDeclaration) parameters.get(parameters.size()-1);
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
			array = newSimpleName(tempArrayName);
		}
		
		String indexVariableName = variable.getName() + "Index";
		VariableDeclarationExpression index = newVariableDeclaration(
				ast.newPrimitiveType(PrimitiveType.INT),
				indexVariableName,
				ast.newNumberLiteral("0"));
		
		InfixExpression cmp = newInfixExpression(
								InfixExpression.Operator.LESS,
								newSimpleName(indexVariableName),
								newFieldAccess(copy(array), "length"));
			
		Block newBody = ast.newBlock();
		newBody.statements().add(
				newVariableDeclarationStatement(
						variable.getName().toString(),
						copy(variable.getType()),
						newArrayAccess(
								copy(array),
								newSimpleName(indexVariableName))));
		
		copyTo(node.getBody(), newBody);
		
		PrefixExpression updater = newPrefixExpression(
												PrefixExpression.Operator.INCREMENT,
												newSimpleName(indexVariableName));
		
		ForStatement stmt = newForStatement(index, cmp, updater, newBody);
		if (null == tempArrayVariable) {
			replace(node, stmt);	
		} else {
			replace(node, newBlock(tempArrayVariable, stmt));
		}
		return false;
	}

	private void copyTo(Statement statement, Block body) {
		if (statement instanceof Block) {
			body.statements().addAll(copyAll(((Block)statement).statements()));
		} else {
			body.statements().add(copy(statement));
		}
	}

	private Expression newFieldAccess(Expression e, String fieldName) {
		return newFieldAccess(e, newSimpleName(fieldName));
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
	
	private SimpleName newSimpleName(String name) {
		return ast.newSimpleName(name);
	}

	VariableDeclarationExpression newVariableDeclaration(Type variableType, String variableName, Expression initializer) {
		VariableDeclarationFragment indexFragment = newVariableFragment(variableName, initializer);
		
		VariableDeclarationExpression index = ast.newVariableDeclarationExpression(indexFragment);
		index.setType(variableType);
		return index;
	}

	VariableDeclarationFragment newVariableFragment(String variableName, Expression initializer) {
		VariableDeclarationFragment indexFragment = ast.newVariableDeclarationFragment();
		indexFragment.setName(newSimpleName(variableName));
		indexFragment.setInitializer(initializer);
		return indexFragment;
	}
}