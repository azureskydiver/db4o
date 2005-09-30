package com.db4o.nativequery.expr.cmp;

// TODO urgently: introduce visitor pattern

public interface ComparisonOperand {
	interface ComparisonOperandVisitor {
	}
	void accept(ComparisonOperandVisitor visitor);

}
