package com.db4o.nativequery.expr;

import com.db4o.nativequery.expr.NotExpression.Visitor;

public class BoolConstExpression implements Expression {	
	public interface Visitor extends ExpressionVisitor  {

		void visit(BoolConstExpression expression);

	}

	public static final BoolConstExpression TRUE=new BoolConstExpression(true);
	public static final BoolConstExpression FALSE=new BoolConstExpression(false);

	private boolean _value;
	
	private BoolConstExpression(boolean value) {
		this._value=value;
	}
	
	public boolean value() {
		return _value;
	}
	
	public String toString() {
		return String.valueOf(_value);
	}
	
	public static BoolConstExpression expr(boolean value) {
		return (value ? TRUE : FALSE);
	}

	public void accept(ExpressionVisitor visitor) {
		((Visitor)visitor).visit(this);
	}

	public Expression negate() {
		return (_value ? FALSE : TRUE);
	}
}
