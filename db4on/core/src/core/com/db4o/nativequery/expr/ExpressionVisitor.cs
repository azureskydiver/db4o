namespace com.db4o.nativequery.expr
{
	public interface ExpressionVisitor
	{
		void visit(com.db4o.nativequery.expr.AndExpression expression);

		void visit(com.db4o.nativequery.expr.OrExpression expression);

		void visit(com.db4o.nativequery.expr.NotExpression expression);

		void visit(com.db4o.nativequery.expr.ComparisonExpression expression);

		void visit(com.db4o.nativequery.expr.BoolConstExpression expression);
	}
}
