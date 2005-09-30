package com.db4o.nativequery.expr.cmp;

public interface ComparisonOperandVisitor {
	void visit(ConstValue operand);
	void visit(FieldValue operand);
	void visit(ArithmeticExpression operand);
}
