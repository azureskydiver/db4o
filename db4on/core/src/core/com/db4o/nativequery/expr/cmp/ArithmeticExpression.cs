namespace com.db4o.nativequery.expr.cmp
{
	public class ArithmeticExpression : com.db4o.nativequery.expr.cmp.ComparisonOperand
	{
		private com.db4o.nativequery.expr.cmp.ArithmeticOperator _op;

		private com.db4o.nativequery.expr.cmp.ComparisonOperand _left;

		private com.db4o.nativequery.expr.cmp.ComparisonOperand _right;

		public ArithmeticExpression(com.db4o.nativequery.expr.cmp.ComparisonOperand left, 
			com.db4o.nativequery.expr.cmp.ComparisonOperand right, com.db4o.nativequery.expr.cmp.ArithmeticOperator
			 op)
		{
			this._op = op;
			this._left = left;
			this._right = right;
		}

		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperand Left()
		{
			return _left;
		}

		public virtual com.db4o.nativequery.expr.cmp.ComparisonOperand Right()
		{
			return _right;
		}

		public virtual com.db4o.nativequery.expr.cmp.ArithmeticOperator Op()
		{
			return _op;
		}

		public override string ToString()
		{
			return "(" + _left + _op + _right + ")";
		}

		public override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null || j4o.lang.JavaSystem.GetClassForObject(obj) != j4o.lang.JavaSystem.GetClassForObject
				(this))
			{
				return false;
			}
			com.db4o.nativequery.expr.cmp.ArithmeticExpression casted = (com.db4o.nativequery.expr.cmp.ArithmeticExpression
				)obj;
			return _left.Equals(casted._left) && _right.Equals(casted._right) && _op.Equals(casted
				._op);
		}

		public override int GetHashCode()
		{
			int hc = _left.GetHashCode();
			hc *= 29 + _right.GetHashCode();
			hc *= 29 + _op.GetHashCode();
			return hc;
		}

		public virtual void Accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.Visit(this);
		}
	}
}
