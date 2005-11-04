namespace com.db4o.nativequery.expr
{
	public interface DiscriminatingExpressionVisitor : com.db4o.nativequery.expr.AndExpression.Visitor
		, com.db4o.nativequery.expr.BoolConstExpression.Visitor, com.db4o.nativequery.expr.OrExpression.Visitor
		, com.db4o.nativequery.expr.ComparisonExpression.Visitor, com.db4o.nativequery.expr.NotExpression.Visitor
	{
	}
}
