/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp.operand;


public class ThreeWayComparison {
	private FieldValue _left;
	private ComparisonOperand _right;
	private boolean _swapped;

	public ThreeWayComparison(FieldValue left, ComparisonOperand right,boolean swapped) {
		this._left = left;
		this._right = right;
		_swapped=swapped;
	}

	public FieldValue left() {
		return _left;
	}

	public ComparisonOperand right() {
		return _right;
	}
	
	public boolean swapped() {
		return _swapped;
	}
}
