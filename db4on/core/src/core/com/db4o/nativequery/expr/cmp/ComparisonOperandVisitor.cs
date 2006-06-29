namespace com.db4o.nativequery.expr.cmp
{
	public interface ComparisonOperandVisitor
	{
		void Visit(com.db4o.nativequery.expr.cmp.ArithmeticExpression operand);

		void Visit(com.db4o.nativequery.expr.cmp.ConstValue operand);

		void Visit(com.db4o.nativequery.expr.cmp.FieldValue operand);

		void Visit(com.db4o.nativequery.expr.cmp.field.CandidateFieldRoot root);

		void Visit(com.db4o.nativequery.expr.cmp.field.PredicateFieldRoot root);

		void Visit(com.db4o.nativequery.expr.cmp.field.StaticFieldRoot root);

		void Visit(com.db4o.nativequery.expr.cmp.ArrayAccessValue operand);

		void Visit(com.db4o.nativequery.expr.cmp.MethodCallValue value);
	}
}
