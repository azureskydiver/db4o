namespace com.db4o.nativequery.expr.cmp
{
	public interface ComparisonOperandAnchor : com.db4o.nativequery.expr.cmp.ComparisonOperand
	{
		com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor parent();

		com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor root();
	}
}
