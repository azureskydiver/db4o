/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp;

public abstract class ComparisonOperandRoot implements ComparisonOperandAnchor {
	public ComparisonOperandAnchor parent() {
		return null;
	}
	
	public final ComparisonOperandAnchor root() {
		return this;
	}
}
