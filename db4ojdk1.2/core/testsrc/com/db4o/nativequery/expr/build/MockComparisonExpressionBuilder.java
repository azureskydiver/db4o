package com.db4o.nativequery.expr.build;

import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;

public class MockComparisonExpressionBuilder {
	private int id=0;
	
	public ComparisonExpression build() {
		id++;
		return new ComparisonExpression(new FieldValue(1,"a"+id),new ConstValue(String.valueOf(id)),ComparisonOperator.EQUALS);
	}
}
