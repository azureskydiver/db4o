using Db4objects.Db4o.Nativequery.Expr;
using Db4objects.Db4o.Nativequery.Expr.Cmp;
using Db4objects.Db4o.Nativequery.Expr.Cmp.Field;

namespace Db4objects.Db4o.Nativequery.Expr
{
	public class TraversingExpressionVisitor : IExpressionVisitor, IComparisonOperandVisitor
	{
		public virtual void Visit(AndExpression expression)
		{
			expression.Left().Accept(this);
			expression.Right().Accept(this);
		}

		public virtual void Visit(BoolConstExpression expression)
		{
		}

		public virtual void Visit(OrExpression expression)
		{
			expression.Left().Accept(this);
			expression.Right().Accept(this);
		}

		public virtual void Visit(ComparisonExpression expression)
		{
			expression.Left().Accept(this);
			expression.Right().Accept(this);
		}

		public virtual void Visit(NotExpression expression)
		{
			expression.Expr().Accept(this);
		}

		public virtual void Visit(ArithmeticExpression operand)
		{
			operand.Left().Accept(this);
			operand.Right().Accept(this);
		}

		public virtual void Visit(ConstValue operand)
		{
		}

		public virtual void Visit(FieldValue operand)
		{
			operand.Parent().Accept(this);
		}

		public virtual void Visit(CandidateFieldRoot root)
		{
		}

		public virtual void Visit(PredicateFieldRoot root)
		{
		}

		public virtual void Visit(StaticFieldRoot root)
		{
		}

		public virtual void Visit(ArrayAccessValue operand)
		{
			operand.Parent().Accept(this);
			operand.Index().Accept(this);
		}

		public virtual void Visit(MethodCallValue value)
		{
			value.Parent().Accept(this);
			VisitArgs(value);
		}

		protected virtual void VisitArgs(MethodCallValue value)
		{
			IComparisonOperand[] args = value.Args();
			for (int i = 0; i < args.Length; ++i)
			{
				args[i].Accept(this);
			}
		}
	}
}
