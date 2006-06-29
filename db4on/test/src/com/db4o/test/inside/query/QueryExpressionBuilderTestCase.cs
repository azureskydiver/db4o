using Db4oTools.NativeQueries;
using System;
using System.Reflection;
using com.db4o.test.nativequeries;

using com.db4o.nativequery.expr;
using com.db4o.nativequery.expr.cmp;
using com.db4o.nativequery.expr.cmp.field;

namespace com.db4o.test.inside.query
{	
	public class QueryExpressionBuilderTestCase
	{
		public void TestNameEqualsTo()
		{
			Expression expression = ExpressionFromPredicate(typeof(NameEqualsTo));
			Tester.EnsureEquals(
				new ComparisonExpression(
				new FieldValue(CandidateFieldRoot.INSTANCE, "name"),
				new FieldValue(PredicateFieldRoot.INSTANCE, "_name"),
				ComparisonOperator.EQUALS),
				NullifyTags(expression));
		}
	
		public void TestHasPreviousWithPrevious()
		{
			// candidate.HasPrevious && candidate.Previous.HasPrevious
			Expression expression = ExpressionFromPredicate(typeof(HasPreviousWithPrevious));
			Expression expected = 
				new AndExpression(
				new NotExpression(
				new ComparisonExpression(
				new FieldValue(CandidateFieldRoot.INSTANCE, "previous"), 
				new ConstValue(null),
				ComparisonOperator.EQUALS)),
				new NotExpression(
				new ComparisonExpression(
				new FieldValue(
					new FieldValue(CandidateFieldRoot.INSTANCE, "previous"),
					"previous"),
				new ConstValue(null),
				ComparisonOperator.EQUALS)));

			Tester.EnsureEquals(expected, NullifyTags(expression));
		}
		
		enum MessagePriority
		{
			None,
			Low,
			Normal,
			High
		}
		
		class Message
		{
			private MessagePriority _priority;

			public MessagePriority Priority
			{
				get { return _priority;  }
				set { _priority = value;  }
			}
		}
		
		private bool MatchEnumConstrain(Message message)
		{
			return message.Priority == MessagePriority.High;
		}
		
		public void TestQueryWithEnumConstrain()
		{
			Expression expression = ExpressionFromMethod("MatchEnumConstrain");
			Expression expected = new ComparisonExpression(
				new FieldValue(CandidateFieldRoot.INSTANCE, "_priority"),
				new ConstValue(MessagePriority.High),
				ComparisonOperator.EQUALS);
			Tester.EnsureEquals(expected, NullifyTags(expression));
		}
		
		class TagNullifier : TraversingExpressionVisitor 
		{
			override public void Visit(FieldValue operand)
			{
				base.Visit(operand);
				operand.Tag(null);
			}
		}

		/// <summary>
		/// Set FieldValue.Tag to null so that Equals ignores it.
		/// </summary>
		/// <param name="expression"></param>
		/// <returns></returns>
		private Expression NullifyTags(Expression expression)
		{
			expression.Accept(new TagNullifier());
			return expression;
		}

		private Expression ExpressionFromMethod(string methodName)
		{
			return new QueryExpressionBuilder().FromMethod(GetType().GetMethod(methodName, BindingFlags.Public|BindingFlags.NonPublic|BindingFlags.Instance|BindingFlags.Static));
		}

		private Expression ExpressionFromPredicate(Type type)
		{
			// XXX: move knowledge about IMethodDefinition to QueryExpressionBuilder
			return (new QueryExpressionBuilder()).FromMethod(type.GetMethod("Match"));
		}
	}
}
