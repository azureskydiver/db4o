namespace com.db4o.nativequery.expr.cmp
{
	public interface ComparisonOperandVisitor
	{
		void visit(com.db4o.nativequery.expr.cmp.ArithmeticExpression operand);

		void visit(com.db4o.nativequery.expr.cmp.ConstValue operand);

		void visit(com.db4o.nativequery.expr.cmp.FieldValue operand);
	}
}
