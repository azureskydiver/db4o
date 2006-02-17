namespace com.db4o.nativequery.expr.cmp.field
{
	public class StaticFieldRoot : com.db4o.nativequery.expr.cmp.ComparisonOperandRoot
	{
		private string _className;

		public StaticFieldRoot(string className)
		{
			this._className = className;
		}

		public virtual string className()
		{
			return _className;
		}

		public override bool Equals(object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (obj == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject
				(obj))
			{
				return false;
			}
			com.db4o.nativequery.expr.cmp.field.StaticFieldRoot casted = (com.db4o.nativequery.expr.cmp.field.StaticFieldRoot
				)obj;
			return _className.Equals(casted._className);
		}

		public override int GetHashCode()
		{
			return _className.GetHashCode();
		}

		public override string ToString()
		{
			return _className;
		}

		public override void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.visit(this);
		}
	}
}
