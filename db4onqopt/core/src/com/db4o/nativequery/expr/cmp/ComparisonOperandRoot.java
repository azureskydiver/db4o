package com.db4o.nativequery.expr.cmp;

public abstract class ComparisonOperandRoot implements ComparisonOperandAnchor {
	public ComparisonOperandAnchor parent() {
		return null;
	}
	
	public final ComparisonOperandAnchor root() {
		return this;
	}
}
