namespace com.db4o.nativequery.expr.cmp
{
	public abstract class ComparisonOperandRoot : com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor
	{
		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor Parent()
		{
			return null;
		}

		public com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor Root()
		{
			return this;
		}

		public abstract void Accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 arg1);
	}
}
