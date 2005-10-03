package com.db4o.nativequery.expr.cmp;

public interface ComparisonOperand {
	void accept(ComparisonOperandVisitor visitor);

}
