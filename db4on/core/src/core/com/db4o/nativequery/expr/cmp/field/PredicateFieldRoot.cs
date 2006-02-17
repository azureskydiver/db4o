namespace com.db4o.nativequery.expr.cmp.field
{
	public class PredicateFieldRoot : com.db4o.nativequery.expr.cmp.ComparisonOperandRoot
	{
		public static readonly com.db4o.nativequery.expr.cmp.field.PredicateFieldRoot INSTANCE
			 = new com.db4o.nativequery.expr.cmp.field.PredicateFieldRoot();

		private PredicateFieldRoot()
		{
		}

		public override string ToString()
		{
			return "PREDICATE";
		}

		public override void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.visit(this);
		}
	}
}
