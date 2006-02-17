namespace com.db4o.nativequery.expr.cmp
{
	public class FieldValue : com.db4o.nativequery.expr.cmp.ComparisonOperandDescendant
	{
		private string _fieldName;

		public FieldValue(com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor root, string
			 name) : base(root)
		{
			_fieldName = name;
		}

		public virtual string fieldName()
		{
			return _fieldName;
		}

		public override bool Equals(object other)
		{
			if (!base.Equals(other))
			{
				return false;
			}
			com.db4o.nativequery.expr.cmp.FieldValue casted = (com.db4o.nativequery.expr.cmp.FieldValue
				)other;
			return _fieldName.Equals(casted._fieldName);
		}

		public override int GetHashCode()
		{
			return base.GetHashCode() * 29 + _fieldName.GetHashCode();
		}

		public override string ToString()
		{
			return base.ToString() + "." + _fieldName;
		}

		public override void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.visit(this);
		}
	}
}
