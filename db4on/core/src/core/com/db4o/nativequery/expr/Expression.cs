namespace com.db4o.nativequery.expr
{
	public interface Expression
	{
		/// <param name="visitor">
		/// must implement the visitor interface required
		/// by the concrete Expression implementation.
		/// </param>
		void Accept(com.db4o.nativequery.expr.ExpressionVisitor visitor);
	}
}
