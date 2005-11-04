namespace com.db4o.nativequery.expr
{
	public class ReturnExpression
	{
		private com.db4o.nativequery.expr.Expression expr;

		public ReturnExpression(com.db4o.nativequery.expr.Expression expr)
		{
			this.expr = expr;
		}

		public override string ToString()
		{
			return expr.ToString();
		}
	}
}
