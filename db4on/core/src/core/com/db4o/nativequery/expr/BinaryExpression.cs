namespace com.db4o.nativequery.expr
{
	public abstract class BinaryExpression : com.db4o.nativequery.expr.Expression
	{
		protected com.db4o.nativequery.expr.Expression _left;

		protected com.db4o.nativequery.expr.Expression _right;

		public BinaryExpression(com.db4o.nativequery.expr.Expression left, com.db4o.nativequery.expr.Expression
			 right)
		{
			this._left = left;
			this._right = right;
		}

		public virtual com.db4o.nativequery.expr.Expression Left()
		{
			return _left;
		}

		public virtual com.db4o.nativequery.expr.Expression Right()
		{
			return _right;
		}

		public override bool Equals(object other)
		{
			if (this == other)
			{
				return true;
			}
			if (other == null || j4o.lang.JavaSystem.GetClassForObject(this) != j4o.lang.JavaSystem.GetClassForObject
				(other))
			{
				return false;
			}
			com.db4o.nativequery.expr.BinaryExpression casted = (com.db4o.nativequery.expr.BinaryExpression
				)other;
			return _left.Equals(casted._left) && (_right.Equals(casted._right)) || _left.Equals
				(casted._right) && (_right.Equals(casted._left));
		}

		public override int GetHashCode()
		{
			return _left.GetHashCode() + _right.GetHashCode();
		}

		public abstract void Accept(com.db4o.nativequery.expr.ExpressionVisitor arg1);
	}
}
