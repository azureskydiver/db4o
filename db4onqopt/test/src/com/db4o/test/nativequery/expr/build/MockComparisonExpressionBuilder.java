/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery.expr.build;

import com.db4o.nativequery.expr.ComparisonExpression;
import com.db4o.nativequery.expr.cmp.ComparisonOperator;
import com.db4o.nativequery.expr.cmp.ConstValue;
import com.db4o.nativequery.expr.cmp.FieldValue;
import com.db4o.nativequery.expr.cmp.field.CandidateFieldRoot;

public class MockComparisonExpressionBuilder {
	private int id=0;
	
	public ComparisonExpression build() {
		id++;
		return new ComparisonExpression(new FieldValue(CandidateFieldRoot.INSTANCE,"a"+id),new ConstValue(String.valueOf(id)),ComparisonOperator.EQUALS);
	}
}
