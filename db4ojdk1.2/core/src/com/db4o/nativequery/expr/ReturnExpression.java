package com.db4o.nativequery.expr;

public class ReturnExpression {
	private Expression expr;

	public ReturnExpression(Expression expr) {
		this.expr = expr;
	}
	
	public String toString() {
		return expr.toString();
	}
}
