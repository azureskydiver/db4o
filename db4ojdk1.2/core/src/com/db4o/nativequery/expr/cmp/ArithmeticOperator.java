package com.db4o.nativequery.expr.cmp;

public final class ArithmeticOperator {
	public final static int ADD_ID=0;
	public final static int SUBTRACT_ID=1;
	public final static int MULTIPLY_ID=2;
	public final static int DIVIDE_ID=3;
	
	public final static ArithmeticOperator ADD=new ArithmeticOperator(ADD_ID,"+");
	public final static ArithmeticOperator SUBTRACT=new ArithmeticOperator(SUBTRACT_ID,"-");
	public final static ArithmeticOperator MULTIPLY=new ArithmeticOperator(MULTIPLY_ID,"*");
	public final static ArithmeticOperator DIVIDE=new ArithmeticOperator(DIVIDE_ID,"/");
	
	private String _op;
	private int _id;
	
	private ArithmeticOperator(int id,String op) {
		_id=id;
		_op=op;
	}
	
	public int id() {
		return _id;
	}
	
	public String toString() {
		return _op;
	}
}
