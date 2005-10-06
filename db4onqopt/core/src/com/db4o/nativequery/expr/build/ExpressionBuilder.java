package com.db4o.nativequery.expr.build;

import com.db4o.nativequery.expr.AndExpression;
import com.db4o.nativequery.expr.BoolConstExpression;
import com.db4o.nativequery.expr.Expression;
import com.db4o.nativequery.expr.NotExpression;
import com.db4o.nativequery.expr.OrExpression;

public class ExpressionBuilder {
	/**
	 * Optimizations: !(Bool)->(!Bool), !!X->X
	 */
	public Expression not(Expression expr) {
		if(expr.equals(BoolConstExpression.TRUE)) {
			return BoolConstExpression.FALSE;
		}
		if(expr.equals(BoolConstExpression.FALSE)) {
			return BoolConstExpression.TRUE;
		}
		if(expr instanceof NotExpression) {
			return ((NotExpression)expr).expr();
		}
		return new NotExpression(expr);
	}

	/**
	 * Optimizations: f&&X->f, t&&X->X, X&&X->X, X&&!X->f
	 */
	public Expression and(Expression left, Expression right) {
		if(left.equals(BoolConstExpression.FALSE)||right.equals(BoolConstExpression.FALSE)) {
			return BoolConstExpression.FALSE;
		}
		if(left.equals(BoolConstExpression.TRUE)) {
			return right;
		}
		if(right.equals(BoolConstExpression.TRUE)) {
			return left;
		}
		if(left.equals(right)) {
			return left;
		}
		if(negatives(left,right)) {
			return BoolConstExpression.FALSE;
		}
		return new AndExpression(left,right);
	}

	/**
	 * Optimizations: X||t->t, f||X->X, X||X->X, X||!X->t
	 */
	public Expression or(Expression left, Expression right) {
		if(left.equals(BoolConstExpression.TRUE)||right.equals(BoolConstExpression.TRUE)) {
			return BoolConstExpression.TRUE;
		}
		if(left.equals(BoolConstExpression.FALSE)) {
			return right;
		}
		if(right.equals(BoolConstExpression.FALSE)) {
			return left;
		}
		if(left.equals(right)) {
			return left;
		}
		if(negatives(left,right)) {
			return BoolConstExpression.TRUE;
		}
		return new OrExpression(left,right);
	}

	/**
	 * Optimizations: static bool roots
	 */
	public BoolConstExpression constant(Boolean value) {
		return BoolConstExpression.expr(value.booleanValue());
	}

	public Expression ifThenElse(Expression cond, Expression truePath, Expression falsePath) {
		return or(and(cond,truePath),and(not(cond),falsePath));
	}

	private boolean negatives(Expression left,Expression right) {
		return negativeOf(left,right)||negativeOf(right,left);
	}
	
	private boolean negativeOf(Expression right, Expression left) {
		return (right instanceof NotExpression)&&((NotExpression)right).expr().equals(left);
	}
}
