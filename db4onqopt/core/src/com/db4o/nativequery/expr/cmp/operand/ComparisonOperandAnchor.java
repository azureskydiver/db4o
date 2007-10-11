/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp.operand;


public interface ComparisonOperandAnchor extends ComparisonOperand {
	ComparisonOperandAnchor parent();
	ComparisonOperandAnchor root();
}
