package com.db4o.nativequery.expr.cmp;

public interface DiscriminatingComparisonOperandVisitor 
	extends ConstValue.Visitor,
		FieldValue.Visitor,
		ArithmeticExpression.Visitor {
}
