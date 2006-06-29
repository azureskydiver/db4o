namespace com.db4o.nativequery.expr
{
	public interface ExpressionVisitor
	{
		void Visit(com.db4o.nativequery.expr.AndExpression expression);

		void Visit(com.db4o.nativequery.expr.OrExpression expression);

		void Visit(com.db4o.nativequery.expr.NotExpression expression);

		void Visit(com.db4o.nativequery.expr.ComparisonExpression expression);

		void Visit(com.db4o.nativequery.expr.BoolConstExpression expression);
	}
}
