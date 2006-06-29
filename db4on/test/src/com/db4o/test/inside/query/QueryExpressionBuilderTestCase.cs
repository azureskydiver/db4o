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
		
		class TagNullifier : ExpressionVisitor, ComparisonOperandVisitor
		{
			public void Visit(AndExpression expression)
			{
				VisitBinaryExpression(expression);
			}

			public void Visit(OrExpression expression)
			{
				VisitBinaryExpression(expression);
			}
			
			void VisitBinaryExpression(BinaryExpression e)
			{
				e.Left().Accept(this);
				e.Right().Accept(this);
			}

			public void Visit(NotExpression expression)
			{
				expression.Expr().Accept(this);
			}

			public void Visit(ComparisonExpression expression)
			{
				expression.Left().Accept(this);
				expression.Right().Accept(this);
			}

			public void Visit(BoolConstExpression expression)
			{	
			}

			public void Visit(ArithmeticExpression operand)
			{
				throw new NotImplementedException();
			}

			public void Visit(ConstValue operand)
			{
			}

			public void Visit(FieldValue operand)
			{
				operand.Parent().Accept(this);
				operand.Tag(null);
			}

			public void Visit(CandidateFieldRoot root)
			{	
			}

			public void Visit(PredicateFieldRoot root)
			{
			}

			public void Visit(StaticFieldRoot root)
			{
			}

			public void Visit(ArrayAccessValue operand)
			{
				throw new NotImplementedException();
			}

			public void Visit(MethodCallValue value)
			{
				throw new NotImplementedException();
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
