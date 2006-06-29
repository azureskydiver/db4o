namespace com.db4o.nativequery.expr
{
	public class TraversingExpressionVisitor : com.db4o.nativequery.expr.ExpressionVisitor
	{
		public virtual void Visit(com.db4o.nativequery.expr.AndExpression expression)
		{
			expression.Left().Accept(this);
			expression.Right().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.BoolConstExpression expression
			)
		{
		}

		public virtual void Visit(com.db4o.nativequery.expr.OrExpression expression)
		{
			expression.Left().Accept(this);
			expression.Right().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.ComparisonExpression expression
			)
		{
		}

		public virtual void Visit(com.db4o.nativequery.expr.NotExpression expression)
		{
			expression.Expr().Accept(this);
		}
	}
}
