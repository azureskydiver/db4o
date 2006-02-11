package com.db4o.nativequery.expr.cmp;

public interface ComparisonOperandAnchor extends ComparisonOperand {
	ComparisonOperandAnchor parent();
	ComparisonOperandAnchor root();
}
