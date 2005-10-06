package com.db4o.nativequery.expr;

public interface DiscriminatingExpressionVisitor
	extends AndExpression.Visitor,
		BoolConstExpression.Visitor,
		OrExpression.Visitor,
		ComparisonExpression.Visitor,
		NotExpression.Visitor
{

}
