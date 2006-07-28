/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp;

// TODO: switch to individual classes and visitor dispatch?
public final class ComparisonOperator {
	public final static int EQUALS_ID=0;
	public final static int SMALLER_ID=1;
	public final static int GREATER_ID=2;
	public final static int CONTAINS_ID=3;
	public final static int STARTSWITH_ID=4;
	public final static int ENDSWITH_ID=5;
	
	public final static ComparisonOperator EQUALS=new ComparisonOperator(EQUALS_ID,"==", true);
	public final static ComparisonOperator SMALLER=new ComparisonOperator(SMALLER_ID,"<", false);
	public final static ComparisonOperator GREATER=new ComparisonOperator(GREATER_ID,">", false);
	public final static ComparisonOperator CONTAINS=new ComparisonOperator(CONTAINS_ID,"<CONTAINS>", false);
	public final static ComparisonOperator STARTSWITH=new ComparisonOperator(STARTSWITH_ID,"<STARTSWITH>", false);
	public final static ComparisonOperator ENDSWITH=new ComparisonOperator(ENDSWITH_ID,"<ENDSWITH>", false);
	
	private int _id;
	private String _op;
	private boolean _symmetric;
	
	private ComparisonOperator(int id, String op, boolean symmetric) {
		_id=id;
		_op=op;
		_symmetric=symmetric;
	}
	
	public int id() {
		return _id;
	}
	
	public String toString() {
		return _op;
	}
	
	public boolean isSymmetric() {
		return _symmetric;
	}
}
