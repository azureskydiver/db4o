namespace com.db4o.nativequery.expr.cmp
{
	public class ArrayAccessValue : com.db4o.nativequery.expr.cmp.ComparisonOperandDescendant
	{
		private com.db4o.nativequery.expr.cmp.ComparisonOperand _index;

		public ArrayAccessValue(com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor parent
			, com.db4o.nativequery.expr.cmp.ComparisonOperand index) : base(parent)
		{
			_index = index;
		}

		public override void Accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.Visit(this);
		}

		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperand Index()
		{
			return _index;
		}

		public override bool Equals(object obj)
		{
			if (!base.Equals(obj))
			{
				return false;
			}
			com.db4o.nativequery.expr.cmp.ArrayAccessValue casted = (com.db4o.nativequery.expr.cmp.ArrayAccessValue
				)obj;
			return _index.Equals(casted._index);
		}

		public override int GetHashCode()
		{
			return base.GetHashCode() * 29 + _index.GetHashCode();
		}

		public override string ToString()
		{
			return base.ToString() + "[" + _index + "]";
		}
	}
}
