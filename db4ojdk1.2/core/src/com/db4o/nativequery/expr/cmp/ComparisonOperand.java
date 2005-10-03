package com.db4o.nativequery.expr.cmp;

public interface ComparisonOperand {
	interface ComparisonOperandVisitor {
	}
	void accept(ComparisonOperandVisitor visitor);

}
