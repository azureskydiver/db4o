namespace com.db4o.nativequery.expr.cmp
{
	public interface ComparisonOperand
	{
		void Accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor visitor);
	}
}
