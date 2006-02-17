namespace com.db4o.nativequery.expr.cmp
{
	public interface ComparisonOperandVisitor
	{
		void visit(com.db4o.nativequery.expr.cmp.ArithmeticExpression operand);

		void visit(com.db4o.nativequery.expr.cmp.ConstValue operand);

		void visit(com.db4o.nativequery.expr.cmp.FieldValue operand);

		void visit(com.db4o.nativequery.expr.cmp.field.CandidateFieldRoot root);

		void visit(com.db4o.nativequery.expr.cmp.field.PredicateFieldRoot root);

		void visit(com.db4o.nativequery.expr.cmp.field.StaticFieldRoot root);

		void visit(com.db4o.nativequery.expr.cmp.ArrayAccessValue operand);

		void visit(com.db4o.nativequery.expr.cmp.MethodCallValue value);
	}
}
