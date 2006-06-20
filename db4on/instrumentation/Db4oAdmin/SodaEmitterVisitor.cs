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
		private CilWorker _worker;
		private InstrumentationContext _context;
		private MethodReference _Query_Descend;
		private MethodReference _Query_Constrain;
		private int _depth = 0;
		private MethodReference _Constraint_Or;
		private MethodReference _Constraint_And;

		public SodaEmitterVisitor(InstrumentationContext context, MethodDefinition method)
		{
			_context = context;
			_worker = method.Body.CilWorker;

			_Query_Descend = ImportQueryMethod("Descend", typeof(string));
			_Query_Constrain = ImportQueryMethod("Constrain", typeof(object));
			_Constraint_Or = _context.Import(typeof(Constraint).GetMethod("Or"));
			_Constraint_And = _context.Import(typeof(Constraint).GetMethod("And"));
		}

		private MethodReference ImportQueryMethod(string methodName, params Type[] signature)
		{
			return _context.Import(typeof(Query).GetMethod(methodName, signature));
		}

		public void Visit(AndExpression expression)
		{
			EmitBinaryExpression(expression, _Constraint_And);
		}

		public void Visit(OrExpression expression)
		{
			EmitBinaryExpression(expression, _Constraint_Or);
		}

		private void EmitBinaryExpression(BinaryExpression expression, MethodReference op)
		{
			EnterExpression();
			expression.Left().Accept(this);
			expression.Right().Accept(this);
			_worker.Emit(OpCodes.Callvirt, op);
			LeaveExpression();
		}

		public void Visit(NotExpression expression)
		{
			throw new NotImplementedException();
		}

		public void Visit(ComparisonExpression expression)
		{
			EnterExpression();
			expression.Left().Accept(this);
			expression.Right().Accept(this);
			_worker.Emit(OpCodes.Callvirt, _Query_Constrain);
			LeaveExpression();
		}

		private void EnterExpression()
		{
			_depth++;
		}

		private void LeaveExpression()
		{
			_depth--;
			if (0 == _depth) _worker.Emit(OpCodes.Pop);
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
		    operand.Parent().Accept(this);
		    
            ComparisonOperandAnchor root = operand.Root();
		    if (root is CandidateFieldRoot)
			{
				// query.Descend(operand.FieldName());
			    _worker.Emit(OpCodes.Ldstr, operand.FieldName());
			    _worker.Emit(OpCodes.Callvirt, _Query_Descend);
			}
			else if (root is PredicateFieldRoot)
			{
				FieldReference field = GetFieldReference(operand);
				_worker.Emit(OpCodes.Ldfld, field);
				if (field.FieldType.IsValueType)
				{
					_worker.Emit(OpCodes.Box, field.FieldType);
				}
			}
		}

		private static FieldReference GetFieldReference(FieldValue operand)
		{
			return (FieldReference)((IFieldReferenceExpression) operand.Tag()).Field;
		}

		public void Visit(CandidateFieldRoot root)
		{
			// being visited as part of a FieldValue
            _worker.Emit(OpCodes.Ldarg_1);
		}

		public void Visit(PredicateFieldRoot root)
		{
            _worker.Emit(OpCodes.Ldarg_0);
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