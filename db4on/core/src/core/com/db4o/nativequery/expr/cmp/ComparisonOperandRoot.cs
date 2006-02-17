namespace com.db4o.nativequery.expr.cmp
{
	public abstract class ComparisonOperandRoot : com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor
	{
		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor parent()
		{
			return null;
		}

		public com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor root()
		{
			return this;
		}

		public abstract void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 arg1);
	}
}
