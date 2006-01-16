namespace com.db4o.nativequery.expr
{
	public class TraversingExpressionVisitor : com.db4o.nativequery.expr.ExpressionVisitor
	{
		public virtual void visit(com.db4o.nativequery.expr.AndExpression expression)
		{
			expression.left().accept(this);
			expression.right().accept(this);
		}

		public virtual void visit(com.db4o.nativequery.expr.BoolConstExpression expression
			)
		{
		}

		public virtual void visit(com.db4o.nativequery.expr.OrExpression expression)
		{
			expression.left().accept(this);
			expression.right().accept(this);
		}

		public virtual void visit(com.db4o.nativequery.expr.ComparisonExpression expression
			)
		{
		}

		public virtual void visit(com.db4o.nativequery.expr.NotExpression expression)
		{
			expression.expr().accept(this);
		}
	}
}
