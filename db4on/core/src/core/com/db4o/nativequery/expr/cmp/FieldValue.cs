namespace com.db4o.nativequery.expr.cmp
{
	public class FieldValue : com.db4o.nativequery.expr.cmp.ComparisonOperandDescendant
	{
		private string _fieldName;

		private object _tag;

		public FieldValue(com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor root, string
			 name) : this(root, name, null)
		{
		}

		public FieldValue(com.db4o.nativequery.expr.cmp.ComparisonOperandAnchor root, string
			 name, object tag) : base(root)
		{
			_fieldName = name;
			_tag = tag;
		}

		public virtual string FieldName()
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
			if (_tag == null)
			{
				if (casted._tag != null)
				{
					return false;
				}
			}
			else
			{
				if (!_tag.Equals(casted._tag))
				{
					return false;
				}
			}
			return _fieldName.Equals(casted._fieldName);
		}

		public override int GetHashCode()
		{
			int hash = base.GetHashCode() * 29 + _fieldName.GetHashCode();
			if (_tag != null)
			{
				hash *= 29 + _tag.GetHashCode();
			}
			return hash;
		}

		public override string ToString()
		{
			return base.ToString() + "." + _fieldName;
		}

		public override void Accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.Visit(this);
		}

		/// <summary>Code analysis specific information.</summary>
		/// <remarks>
		/// Code analysis specific information.
		/// This is used in the .net side to preserve Mono.Cecil references
		/// for instance.
		/// </remarks>
		public virtual object Tag()
		{
			return _tag;
		}

		public virtual void Tag(object value)
		{
			_tag = value;
		}
	}
}
