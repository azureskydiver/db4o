using System;
using System.Reflection;
using Cecil.FlowAnalysis.CodeStructure;
using com.db4o.nativequery.expr;
using com.db4o.nativequery.expr.cmp;
using com.db4o.nativequery.expr.cmp.field;
using com.db4o.query;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Db4oAdmin
{
	class SodaEmitterVisitor : ExpressionVisitor, ComparisonOperandVisitor
	{
		private MethodDefinition _method;
		private CilWorker _worker;
		private InstrumentationContext _context;
		private MethodReference _Query_Descend;
		private MethodReference _Query_Constrain;

		public SodaEmitterVisitor(InstrumentationContext context, MethodDefinition method)
		{
			_context = context;
			_method = method;
			_worker = method.Body.CilWorker;

			_Query_Descend = ImportQueryMethod("Descend", typeof(string));
			_Query_Constrain = ImportQueryMethod("Constrain", typeof(object));
		}

		private MethodReference ImportQueryMethod(string methodName, params Type[] signature)
		{
			return _context.Import(typeof(Query).GetMethod(methodName, signature));
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
			expression.Right().Accept(this);
			_worker.Emit(OpCodes.Callvirt, _Query_Constrain);
			_worker.Emit(OpCodes.Pop);
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
				// query.Descend(operand.FieldName());
				_worker.Emit(OpCodes.Ldarg_1);
				_worker.Emit(OpCodes.Ldstr, operand.FieldName());
				_worker.Emit(OpCodes.Callvirt, _Query_Descend);
			}
			else if (operand.Parent() is PredicateFieldRoot)
			{
				FieldReference field = (FieldReference)((IFieldReferenceExpression) operand.Tag()).Field;
				
				// this.<operand.FieldName()>
				_worker.Emit(OpCodes.Ldarg_0);
				_worker.Emit(OpCodes.Ldfld, field);
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