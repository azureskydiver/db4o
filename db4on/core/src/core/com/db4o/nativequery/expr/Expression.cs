namespace com.db4o.nativequery.expr
{
	public interface Expression
	{
		/// <summary><a href='http://c2.com/cgi/wiki?AcyclicVisitor'>Acyclic Visitor</a></summary>
		/// <param name="visitor">
		/// must implement the visitor interface required
		/// by the concrete Expression implementation.
		/// </param>
		void accept(com.db4o.nativequery.expr.ExpressionVisitor visitor);
	}
}
