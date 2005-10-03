package com.db4o.nativequery.expr;


public class NotExpression implements Expression {
	public interface Visitor extends ExpressionVisitor  {

		void visit(NotExpression expression);

	}

	private Expression _expr;

	public NotExpression(Expression expr) {
		this._expr = expr;
	}
	
	public String toString() {
		return "!("+_expr+")";
	}

	public Expression expr() {
		return _expr;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		NotExpression casted = (NotExpression) other;
		return _expr.equals(casted._expr);
	}
	
	public int hashCode() {
		return -_expr.hashCode();
	}

	public void accept(ExpressionVisitor visitor) {
		((Visitor)visitor).visit(this);
	}
}
