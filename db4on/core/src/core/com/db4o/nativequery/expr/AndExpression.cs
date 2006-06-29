namespace com.db4o.nativequery.expr
{
	public class AndExpression : com.db4o.nativequery.expr.BinaryExpression
	{
		public AndExpression(com.db4o.nativequery.expr.Expression left, com.db4o.nativequery.expr.Expression
			 right) : base(left, right)
		{
		}

		public override string ToString()
		{
			return "(" + _left + ")&&(" + _right + ")";
		}

		public override void Accept(com.db4o.nativequery.expr.ExpressionVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
