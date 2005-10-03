package com.db4o.nativequery.expr.build;

import com.db4o.nativequery.expr.AndExpression;
import com.db4o.nativequery.expr.BoolConstExpression;
import com.db4o.nativequery.expr.ComparisonExpression;
import com.db4o.nativequery.expr.DiscriminatingExpressionVisitor;
import com.db4o.nativequery.expr.Expression;
import com.db4o.nativequery.expr.NotExpression;
import com.db4o.nativequery.expr.OrExpression;

public class CNFBuilder {
	public Expression cnf(Expression expr) {
		FirstStepVisitor firstVisitor=new FirstStepVisitor();
		expr.accept(firstVisitor);
		return firstVisitor.expression;
	}
	
	private static class FirstStepVisitor implements DiscriminatingExpressionVisitor {
		Expression expression;
		
		public void visit(AndExpression expression) {
			expression.left().accept(this);
			Expression left=this.expression;
			expression.right().accept(this);
			Expression right=this.expression;
			this.expression=new AndExpression(left,right);
		}

		public void visit(BoolConstExpression expression) {
			this.expression=expression;
		}

		public void visit(OrExpression expression) {
			expression.left().accept(this);
			Expression left=this.expression;
			expression.right().accept(this);
			Expression right=this.expression;
			this.expression=new OrExpression(left,right);
		}

		public void visit(ComparisonExpression expression) {
			this.expression=expression;
		}

		public void visit(NotExpression expression) {
			expression.expr().accept(this);
			if(this.expression instanceof BoolConstExpression) {
				this.expression=((BoolConstExpression)this.expression).negate();
				return;
			}
			if(this.expression instanceof NotExpression) {
				this.expression=((NotExpression)this.expression).expr();
				return;
			}
			if(this.expression instanceof OrExpression) {
				OrExpression orExpr=(OrExpression)this.expression;
				this.expression=new AndExpression(new NotExpression(orExpr.left()),new NotExpression(orExpr.right()));
				return;
			}
			if(this.expression instanceof AndExpression) {
				AndExpression andExpr=(AndExpression)this.expression;
				this.expression=new OrExpression(new NotExpression(andExpr.left()),new NotExpression(andExpr.right()));
				return;
			}
			this.expression=new NotExpression(this.expression);
		}
		
	}
}
