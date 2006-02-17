namespace com.db4o.nativequery.expr.cmp
{
	public abstract class ComparisonOperandDescendant : com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor
	{
		private com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor _parent;

		protected ComparisonOperandDescendant(com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor
			 _parent)
		{
			this._parent = _parent;
		}

		public com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor parent()
		{
			return _parent;
		}

		public com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor root()
		{
			return _parent.root();
		}

		public override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject
				(obj))
			{
				return false;
			}
			com.db4o.nativequery.expr.cmp.ComparisonOperandDescendant casted = (com.db4o.nativequery.expr.cmp.ComparisonOperandDescendant
				)obj;
			return _parent.Equals(casted._parent);
		}

		public override int GetHashCode()
		{
			return _parent.GetHashCode();
		}

		public override string ToString()
		{
			return _parent.ToString();
		}

		public abstract void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 arg1);
	}
}
