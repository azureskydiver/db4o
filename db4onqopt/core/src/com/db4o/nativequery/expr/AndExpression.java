package com.db4o.nativequery.expr;


public class AndExpression extends BinaryExpression {
	public AndExpression(Expression left, Expression right) {
		super(left, right);
	}

	public String toString() {
		return "("+_left+")&&("+_right+")";
	}

	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
