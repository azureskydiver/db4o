namespace com.db4o.nativequery.expr
{
	public class BoolConstExpression : com.db4o.nativequery.expr.Expression
	{
		public static readonly com.db4o.nativequery.expr.BoolConstExpression TRUE = new com.db4o.nativequery.expr.BoolConstExpression
			(true);

		public static readonly com.db4o.nativequery.expr.BoolConstExpression FALSE = new 
			com.db4o.nativequery.expr.BoolConstExpression(false);

		private bool _value;

		private BoolConstExpression(bool value)
		{
			this._value = value;
		}

		public virtual bool value()
		{
			return _value;
		}

		public override string ToString()
		{
			return j4o.lang.JavaSystem.getStringValueOf(_value);
		}

		public static com.db4o.nativequery.expr.BoolConstExpression expr(bool value)
		{
			return (value ? TRUE : FALSE);
		}

		public virtual void accept(com.db4o.nativequery.expr.ExpressionVisitor visitor)
		{
			visitor.visit(this);
		}

		public virtual com.db4o.nativequery.expr.Expression negate()
		{
			return (_value ? FALSE : TRUE);
		}
	}
}
