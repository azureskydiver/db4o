namespace com.db4o.nativequery.expr.cmp
{
	public class ThreeWayComparison
	{
		private com.db4o.nativequery.expr.cmp.FieldValue _left;

		private com.db4o.nativequery.expr.cmp.ComparisonOperand _right;

		private bool _swapped;

		public ThreeWayComparison(com.db4o.nativequery.expr.cmp.FieldValue left, com.db4o.nativequery.expr.cmp.ComparisonOperand
			 right, bool swapped)
		{
			this._left = left;
			this._right = right;
			_swapped = swapped;
		}

		public virtual com.db4o.nativequery.expr.cmp.FieldValue Left()
		{
			return _left;
		}

		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperand Right()
		{
			return _right;
		}

		public virtual bool Swapped()
		{
			return _swapped;
		}
	}
}
