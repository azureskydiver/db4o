package com.db4o.nativequery.expr.cmp;

public final class ComparisonOperator {
	public final static int EQUALS_ID=0;
	public final static int SMALLER_ID=1;
	public final static int GREATER_ID=2;
	
	public final static ComparisonOperator EQUALS=new ComparisonOperator(EQUALS_ID,"==", true);
	public final static ComparisonOperator SMALLER=new ComparisonOperator(SMALLER_ID,"<", false);
	public final static ComparisonOperator GREATER=new ComparisonOperator(GREATER_ID,">", false);
	
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
