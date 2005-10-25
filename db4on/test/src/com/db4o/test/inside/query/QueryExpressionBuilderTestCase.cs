using System;
using com.db4o.inside.query;
using com.db4o.test.nativequeries;

namespace com.db4o.test.inside.query
{
	using System;
	using com.db4o.nativequery.expr;
	using com.db4o.nativequery.expr.cmp;
	
	public class QueryExpressionBuilderTestCase
	{
		public void TestNameEqualsTo()
		{
			Expression expression = ExpressionFromPredicate(typeof(NameEqualsTo));
			Tester.ensureEquals(
				new ComparisonExpression(
				new FieldValue(1, "name"),
				new FieldValue(0, "_name"),
				ComparisonOperator.EQUALS),
				expression);
		}
	
		public void TestHasPreviousWithPrevious()
		{
			// candidate.HasPrevious && candidate.Previous.HasPrevious
			Expression expression = ExpressionFromPredicate(typeof(HasPreviousWithPrevious));
			Expression expected = 
				new AndExpression(
				new NotExpression(
				new ComparisonExpression(
				new FieldValue(1, "previous"), 
				new ConstValue(null),
				ComparisonOperator.EQUALS)),
				new NotExpression(
				new ComparisonExpression(
				new FieldValue(1, new string[] { "previous", "previous" }),
				new ConstValue(null),
				ComparisonOperator.EQUALS)));

			Tester.ensureEquals(expected, expression);
		}

		private Expression ExpressionFromPredicate(Type type)
		{
			// XXX: move knowledge about IMethodDefinition to QueryExpressionBuilder
			return QueryExpressionBuilder.FromMethod(type.GetMethod("Match"));
		}
	}
}
