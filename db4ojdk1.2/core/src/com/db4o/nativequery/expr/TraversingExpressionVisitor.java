package com.db4o.nativequery.expr;

public class TraversingExpressionVisitor implements DiscriminatingExpressionVisitor {
	public void visit(AndExpression expression) {
		expression.left().accept(this);
		expression.right().accept(this);
	}

	public void visit(BoolConstExpression expression) {
	}

	public void visit(OrExpression expression) {
		expression.left().accept(this);
		expression.right().accept(this);
	}

	public void visit(ComparisonExpression expression) {
	}

	public void visit(NotExpression expression) {
		expression.expr().accept(this);
	}
}
