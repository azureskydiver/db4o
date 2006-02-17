namespace com.db4o.nativequery.expr.cmp.field
{
	public class CandidateFieldRoot : com.db4o.nativequery.expr.cmp.ComparisonOperandRoot
	{
		public static readonly com.db4o.nativequery.expr.cmp.field.CandidateFieldRoot INSTANCE
			 = new com.db4o.nativequery.expr.cmp.field.CandidateFieldRoot();

		private CandidateFieldRoot()
		{
		}

		public override string ToString()
		{
			return "CANDIDATE";
		}

		public override void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.visit(this);
		}
	}
}
