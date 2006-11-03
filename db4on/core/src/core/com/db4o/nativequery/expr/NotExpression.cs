namespace com.db4o.nativequery.expr
{
	public class NotExpression : com.db4o.nativequery.expr.Expression
	{
		private com.db4o.nativequery.expr.Expression _expr;

		public NotExpression(com.db4o.nativequery.expr.Expression expr)
		{
			this._expr = expr;
		}

		public override string ToString()
		{
			return "!(" + _expr + ")";
		}

		public virtual com.db4o.nativequery.expr.Expression Expr()
		{
			return _expr;
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
			com.db4o.nativequery.expr.NotExpression casted = (com.db4o.nativequery.expr.NotExpression
				)other;
			return _expr.Equals(casted._expr);
		}

		public override int GetHashCode()
		{
			return -_expr.GetHashCode();
		}

		public virtual void Accept(com.db4o.nativequery.expr.ExpressionVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
