namespace com.db4o.nativequery.expr.cmp
{
	public interface DiscriminatingComparisonOperandVisitor : com.db4o.nativequery.expr.cmp.ConstValue.Visitor
		, com.db4o.nativequery.expr.cmp.FieldValue.Visitor, com.db4o.nativequery.expr.cmp.ArithmeticExpression.Visitor
	{
	}
}
