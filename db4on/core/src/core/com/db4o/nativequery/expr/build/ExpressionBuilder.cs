namespace com.db4o.nativequery.expr.build
{
	public class ExpressionBuilder
	{
		/// <summary>Optimizations: !(Bool)->(!Bool), !!X->X</summary>
		public virtual com.db4o.nativequery.expr.Expression Not(com.db4o.nativequery.expr.Expression
			 expr)
		{
			if (expr.Equals(com.db4o.nativequery.expr.BoolConstExpression.TRUE))
			{
				return com.db4o.nativequery.expr.BoolConstExpression.FALSE;
			}
			if (expr.Equals(com.db4o.nativequery.expr.BoolConstExpression.FALSE))
			{
				return com.db4o.nativequery.expr.BoolConstExpression.TRUE;
			}
			if (expr is com.db4o.nativequery.expr.NotExpression)
			{
				return ((com.db4o.nativequery.expr.NotExpression)expr).Expr();
			}
			return new com.db4o.nativequery.expr.NotExpression(expr);
		}

		/// <summary>Optimizations: f&&X->f, t&&X->X, X&&X->X, X&&!X->f</summary>
		public virtual com.db4o.nativequery.expr.Expression And(com.db4o.nativequery.expr.Expression
			 left, com.db4o.nativequery.expr.Expression right)
		{
			if (left.Equals(com.db4o.nativequery.expr.BoolConstExpression.FALSE) || right.Equals
				(com.db4o.nativequery.expr.BoolConstExpression.FALSE))
			{
				return com.db4o.nativequery.expr.BoolConstExpression.FALSE;
			}
			if (left.Equals(com.db4o.nativequery.expr.BoolConstExpression.TRUE))
			{
				return right;
			}
			if (right.Equals(com.db4o.nativequery.expr.BoolConstExpression.TRUE))
			{
				return left;
			}
			if (left.Equals(right))
			{
				return left;
			}
			if (Negatives(left, right))
			{
				return com.db4o.nativequery.expr.BoolConstExpression.FALSE;
			}
			return new com.db4o.nativequery.expr.AndExpression(left, right);
		}

		/// <summary>Optimizations: X||t->t, f||X->X, X||X->X, X||!X->t</summary>
		public virtual com.db4o.nativequery.expr.Expression Or(com.db4o.nativequery.expr.Expression
			 left, com.db4o.nativequery.expr.Expression right)
		{
			if (left.Equals(com.db4o.nativequery.expr.BoolConstExpression.TRUE) || right.Equals
				(com.db4o.nativequery.expr.BoolConstExpression.TRUE))
			{
				return com.db4o.nativequery.expr.BoolConstExpression.TRUE;
			}
			if (left.Equals(com.db4o.nativequery.expr.BoolConstExpression.FALSE))
			{
				return right;
			}
			if (right.Equals(com.db4o.nativequery.expr.BoolConstExpression.FALSE))
			{
				return left;
			}
			if (left.Equals(right))
			{
				return left;
			}
			if (Negatives(left, right))
			{
				return com.db4o.nativequery.expr.BoolConstExpression.TRUE;
			}
			return new com.db4o.nativequery.expr.OrExpression(left, right);
		}

		/// <summary>Optimizations: static bool roots</summary>
		public virtual com.db4o.nativequery.expr.BoolConstExpression Constant(bool value)
		{
			return com.db4o.nativequery.expr.BoolConstExpression.Expr(value);
		}

		public virtual com.db4o.nativequery.expr.Expression IfThenElse(com.db4o.nativequery.expr.Expression
			 cond, com.db4o.nativequery.expr.Expression truePath, com.db4o.nativequery.expr.Expression
			 falsePath)
		{
			com.db4o.nativequery.expr.Expression expr = CheckBoolean(cond, truePath, falsePath
				);
			if (expr != null)
			{
				return expr;
			}
			return Or(And(cond, truePath), And(Not(cond), falsePath));
		}

		private com.db4o.nativequery.expr.Expression CheckBoolean(com.db4o.nativequery.expr.Expression
			 cmp, com.db4o.nativequery.expr.Expression trueExpr, com.db4o.nativequery.expr.Expression
			 falseExpr)
		{
			if (cmp is com.db4o.nativequery.expr.BoolConstExpression)
			{
				return null;
			}
			if (trueExpr is com.db4o.nativequery.expr.BoolConstExpression)
			{
				bool leftNegative = trueExpr.Equals(com.db4o.nativequery.expr.BoolConstExpression
					.FALSE);
				if (!leftNegative)
				{
					return Or(cmp, falseExpr);
				}
				else
				{
					return And(Not(cmp), falseExpr);
				}
			}
			if (falseExpr is com.db4o.nativequery.expr.BoolConstExpression)
			{
				bool rightNegative = falseExpr.Equals(com.db4o.nativequery.expr.BoolConstExpression
					.FALSE);
				if (!rightNegative)
				{
					return And(cmp, trueExpr);
				}
				else
				{
					return Or(Not(cmp), falseExpr);
				}
			}
			if (cmp is com.db4o.nativequery.expr.NotExpression)
			{
				cmp = ((com.db4o.nativequery.expr.NotExpression)cmp).Expr();
				com.db4o.nativequery.expr.Expression swap = trueExpr;
				trueExpr = falseExpr;
				falseExpr = swap;
			}
			if (trueExpr is com.db4o.nativequery.expr.OrExpression)
			{
				com.db4o.nativequery.expr.OrExpression orExpr = (com.db4o.nativequery.expr.OrExpression
					)trueExpr;
				com.db4o.nativequery.expr.Expression orLeft = orExpr.Left();
				com.db4o.nativequery.expr.Expression orRight = orExpr.Right();
				if (falseExpr.Equals(orRight))
				{
					com.db4o.nativequery.expr.Expression swap = orRight;
					orRight = orLeft;
					orLeft = swap;
				}
				if (falseExpr.Equals(orLeft))
				{
					return Or(orLeft, And(cmp, orRight));
				}
			}
			if (falseExpr is com.db4o.nativequery.expr.AndExpression)
			{
				com.db4o.nativequery.expr.AndExpression andExpr = (com.db4o.nativequery.expr.AndExpression
					)falseExpr;
				com.db4o.nativequery.expr.Expression andLeft = andExpr.Left();
				com.db4o.nativequery.expr.Expression andRight = andExpr.Right();
				if (trueExpr.Equals(andRight))
				{
					com.db4o.nativequery.expr.Expression swap = andRight;
					andRight = andLeft;
					andLeft = swap;
				}
				if (trueExpr.Equals(andLeft))
				{
					return And(andLeft, Or(cmp, andRight));
				}
			}
			return null;
		}

		private bool Negatives(com.db4o.nativequery.expr.Expression left, com.db4o.nativequery.expr.Expression
			 right)
		{
			return NegativeOf(left, right) || NegativeOf(right, left);
		}

		private bool NegativeOf(com.db4o.nativequery.expr.Expression right, com.db4o.nativequery.expr.Expression
			 left)
		{
			return (right is com.db4o.nativequery.expr.NotExpression) && ((com.db4o.nativequery.expr.NotExpression
				)right).Expr().Equals(left);
		}
	}
}
