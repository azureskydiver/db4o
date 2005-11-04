namespace com.db4o.nativequery.expr.cmp
{
	public class ConstValue : com.db4o.nativequery.expr.cmp.ComparisonOperand
	{
		public interface Visitor : com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
		{
			void visit(com.db4o.nativequery.expr.cmp.ConstValue operand);
		}

		private object _value;

		public ConstValue(object value)
		{
			this._value = value;
		}

		public virtual object value()
		{
			return _value;
		}

		public override string ToString()
		{
			return (_value == null ? "[null]" : _value.ToString());
		}

		public override bool Equals(object other)
		{
			if (this == other)
			{
				return true;
			}
			if (other == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject
				(other))
			{
				return false;
			}
			object otherValue = ((com.db4o.nativequery.expr.cmp.ConstValue)other)._value;
			if (otherValue == _value)
			{
				return true;
			}
			if (otherValue == null || _value == null)
			{
				return false;
			}
			return _value.Equals(otherValue);
		}

		public override int GetHashCode()
		{
			return _value.GetHashCode();
		}

		public virtual void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			((com.db4o.nativequery.expr.cmp.ConstValue.Visitor)visitor).visit(this);
		}
	}
}
