package com.db4o.nativequery.expr;

public interface ExpressionVisitor {

	void visit(AndExpression expression);

	void visit(BoolConstExpression expression);

	void visit(OrExpression expression);

	void visit(ComparisonExpression expression);

	void visit(NotExpression expression);

}
