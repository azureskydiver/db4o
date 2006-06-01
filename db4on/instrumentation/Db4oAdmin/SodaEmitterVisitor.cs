using System;
using com.db4o.nativequery.expr;
using com.db4o.nativequery.expr.cmp;
using com.db4o.nativequery.expr.cmp.field;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Db4oAdmin
{
	class SodaEmitterVisitor : ExpressionVisitor, ComparisonOperandVisitor
	{
		private MethodDefinition _method;
		private CilWorker _worker;

		public SodaEmitterVisitor(MethodDefinition method)
		{
			_method = method;
			_worker = method.Body.CilWorker;
		}
		
		public void Visit(AndExpression expression)
		{
			throw new NotImplementedException();
		}

		public void Visit(OrExpression expression)
		{
			throw new NotImplementedException();
		}

		public void Visit(NotExpression expression)
		{
			throw new NotImplementedException();
		}

		public void Visit(ComparisonExpression expression)
		{
			expression.Left().Accept(this);
		}

		public void Visit(BoolConstExpression expression)
		{
			throw new NotImplementedException();
		}

		public void Visit(ArithmeticExpression operand)
		{
			throw new NotImplementedException();
		}

		public void Visit(ConstValue operand)
		{
			throw new NotImplementedException();
		}

		public void Visit(FieldValue operand)
		{
			if (operand.Parent() is CandidateFieldRoot)
			{
				
			}
		}

		public void Visit(CandidateFieldRoot root)
		{
			throw new NotImplementedException();
		}

		public void Visit(PredicateFieldRoot root)
		{
			throw new NotImplementedException();
		}

		public void Visit(StaticFieldRoot root)
		{
			throw new NotImplementedException();
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
}