/**
 * 
 */
package com.db4o.nativequery.expr.cmp;

public interface ComparisonOperandVisitor {
	void visit(ArithmeticExpression operand);
	void visit(ConstValue operand);
	void visit(FieldValue operand);
}