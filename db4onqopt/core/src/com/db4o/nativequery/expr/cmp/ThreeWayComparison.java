package com.db4o.nativequery.expr.cmp;

public class ThreeWayComparison {
	FieldValue _left;
	ComparisonOperand _right;

	public ThreeWayComparison(FieldValue left, ComparisonOperand right) {
		this._left = left;
		this._right = right;
	}

	public FieldValue left() {
		return _left;
	}

	public ComparisonOperand right() {
		return _right;
	}
	
	
}
