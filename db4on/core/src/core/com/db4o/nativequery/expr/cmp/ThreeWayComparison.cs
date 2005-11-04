namespace com.db4o.nativequery.expr.cmp
{
	public class ThreeWayComparison
	{
		internal com.db4o.nativequery.expr.cmp.FieldValue _left;

		internal com.db4o.nativequery.expr.cmp.ComparisonOperand _right;

		public ThreeWayComparison(com.db4o.nativequery.expr.cmp.FieldValue left, com.db4o.nativequery.expr.cmp.ComparisonOperand
			 right)
		{
			this._left = left;
			this._right = right;
		}

		public virtual com.db4o.nativequery.expr.cmp.FieldValue left()
		{
			return _left;
		}

		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperand right()
		{
			return _right;
		}
	}
}
