package com.db4o.nativequery.expr.cmp;

public class ThreeWayComparison {
	FieldValue left;
	ComparisonOperand right;

	public ThreeWayComparison(FieldValue left, ComparisonOperand right) {
		this.left = left;
		this.right = right;
	}

	public FieldValue left() {
		return left;
	}

	public ComparisonOperand right() {
		return right;
	}
	
	
}
