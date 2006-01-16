namespace com.db4o.nativequery.expr
{
	public class ComparisonExpression : com.db4o.nativequery.expr.Expression
	{
		private com.db4o.nativequery.expr.cmp.FieldValue _left;

		private com.db4o.nativequery.expr.cmp.ComparisonOperand _right;

		private com.db4o.nativequery.expr.cmp.ComparisonOperator _op;

		public ComparisonExpression(com.db4o.nativequery.expr.cmp.FieldValue left, com.db4o.nativequery.expr.cmp.ComparisonOperand
			 right, com.db4o.nativequery.expr.cmp.ComparisonOperator op)
		{
			this._left = left;
			this._right = right;
			this._op = op;
		}

		public virtual com.db4o.nativequery.expr.cmp.FieldValue left()
		{
			return _left;
		}

		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperand right()
		{
			return _right;
		}

		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperator op()
		{
			return _op;
		}

		public override string ToString()
		{
			return _left + " " + _op + " " + _right;
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
			com.db4o.nativequery.expr.ComparisonExpression casted = (com.db4o.nativequery.expr.ComparisonExpression
				)other;
			return _left.Equals(casted._left) && _right.Equals(casted._right) && _op.Equals(casted
				._op);
		}

		public override int GetHashCode()
		{
			return (_left.GetHashCode() * 29 + _right.GetHashCode()) * 29 + _op.GetHashCode();
		}

		public virtual void accept(com.db4o.nativequery.expr.ExpressionVisitor visitor)
		{
			visitor.visit(this);
		}
	}
}
