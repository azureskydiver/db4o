namespace com.db4o.nativequery.expr
{
	public class TraversingExpressionVisitor : com.db4o.nativequery.expr.ExpressionVisitor
		, com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
	{
		public virtual void Visit(com.db4o.nativequery.expr.AndExpression expression)
		{
			expression.Left().Accept(this);
			expression.Right().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.BoolConstExpression expression
			)
		{
		}

		public virtual void Visit(com.db4o.nativequery.expr.OrExpression expression)
		{
			expression.Left().Accept(this);
			expression.Right().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.ComparisonExpression expression
			)
		{
			expression.Left().Accept(this);
			expression.Right().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.NotExpression expression)
		{
			expression.Expr().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.cmp.ArithmeticExpression operand
			)
		{
			operand.Left().Accept(this);
			operand.Right().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.cmp.ConstValue operand)
		{
		}

		public virtual void Visit(com.db4o.nativequery.expr.cmp.FieldValue operand)
		{
			operand.Parent().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.cmp.field.CandidateFieldRoot 
			root)
		{
		}

		public virtual void Visit(com.db4o.nativequery.expr.cmp.field.PredicateFieldRoot 
			root)
		{
		}

		public virtual void Visit(com.db4o.nativequery.expr.cmp.field.StaticFieldRoot root
			)
		{
		}

		public virtual void Visit(com.db4o.nativequery.expr.cmp.ArrayAccessValue operand)
		{
			operand.Parent().Accept(this);
			operand.Index().Accept(this);
		}

		public virtual void Visit(com.db4o.nativequery.expr.cmp.MethodCallValue value)
		{
			value.Parent().Accept(this);
			VisitArgs(value);
		}

		protected virtual void VisitArgs(com.db4o.nativequery.expr.cmp.MethodCallValue value
			)
		{
			com.db4o.nativequery.expr.cmp.ComparisonOperand[] args = value.Args();
			for (int i = 0; i < args.Length; ++i)
			{
				args[i].Accept(this);
			}
		}
	}
}
