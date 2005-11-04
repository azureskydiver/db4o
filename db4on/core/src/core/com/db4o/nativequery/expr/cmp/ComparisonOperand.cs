namespace com.db4o.nativequery.expr.cmp
{
	public interface ComparisonOperand
	{
		void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor visitor);
	}
}
