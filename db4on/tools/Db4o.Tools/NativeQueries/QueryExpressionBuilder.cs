﻿/* Copyright (C) 2004-2006   db4objects Inc.   http://www.db4o.com */

namespace Db4o.Tools.NativeQueries
{
	using System;
	using System.Collections;
	using System.Reflection;

	using Mono.Cecil;

	using Cecil.FlowAnalysis;
	using Cecil.FlowAnalysis.ActionFlow;
	using Cecil.FlowAnalysis.CodeStructure;

	using com.db4o.nativequery.expr;
	using com.db4o.nativequery.expr.cmp;
	using com.db4o.nativequery.expr.cmp.field;
	using com.db4o.inside.query;

	/// <summary>
	/// Build a com.db4o.nativequery.expr tree out of
	/// a predicate method definition.
	/// </summary>
	public class QueryExpressionBuilder : ExpressionBuilder
	{
		public override Expression FromMethod(System.Reflection.MethodBase method)
		{
			if (method == null)
				throw new ArgumentNullException("method");
			Expression e = (Expression)_expressionCachingStrategy.Get(method);
			if (e != null)
				return e;

			string location = GetAssemblyLocation(method);
			IAssemblyDefinition assembly = GetAssembly(location);
			ITypeDefinition type = FindTypeDefinition(assembly.MainModule, method.DeclaringType);
			if (null == type)
				UnsupportedPredicate(string.Format("Unable to load type '{0}' from assembly '{1}'", method.DeclaringType.FullName, location));
			IMethodDefinition methodDef = type.Methods.GetMethod(method.Name, GetParameterTypes(method));
			if (null == methodDef)
				UnsupportedPredicate(string.Format("Unable to load the definition of '{0}' from assembly '{1}'", method, location));

			e = FromMethodDefinition(methodDef);
			_expressionCachingStrategy.Add(method, e);
			return e;
		}

		private static IAssemblyDefinition GetAssembly(string location)
		{
			IAssemblyDefinition assembly = (IAssemblyDefinition)_assemblyCachingStrategy.Get(location);
			if (null == assembly)
			{
				assembly = AssemblyFactory.GetAssembly(location);
				_assemblyCachingStrategy.Add(location, assembly);
			}
			return assembly;
		}

		private static Type[] GetParameterTypes(MethodBase method)
		{
			ParameterInfo[] parameters = method.GetParameters();
			Type[] types = new Type[parameters.Length];
			for (int i = 0; i < parameters.Length; ++i)
			{
				types[i] = parameters[i].ParameterType;
			}
			return types;
		}

		private static ITypeDefinition FindTypeDefinition(IModuleDefinition module, Type type)
		{
			return IsNested(type)
				? FindNestedTypeDefinition(module, type)
				: FindTypeDefinition(module, type.FullName);
		}

		private static bool IsNested(Type type)
		{
			return type.IsNestedPublic || type.IsNestedPrivate || type.IsNestedAssembly;
		}

		private static ITypeDefinition FindNestedTypeDefinition(IModuleDefinition module, Type type)
		{
			foreach (ITypeDefinition td in FindTypeDefinition(module, type.DeclaringType).NestedTypes)
			{
				if (td.Name == type.Name)
					return td;
			}
			return null;
		}

		private static ITypeDefinition FindTypeDefinition(IModuleDefinition module, string fullName)
		{
			return module.Types[fullName];
		}

		private static string GetAssemblyLocation(MethodBase method)
		{
			return method.DeclaringType.Module.FullyQualifiedName;
		}

		private static Expression FromMethodDefinition(IMethodDefinition method)
		{
			if (method == null)
				throw new ArgumentNullException("method");
			if (1 != method.Parameters.Count)
				UnsupportedPredicate("A predicate must take a single argument.");
			if (0 != method.Body.ExceptionHandlers.Count)
				UnsupportedPredicate("A predicate can not contain exception handlers.");
			if (method.DeclaringType.Module.Import(typeof(bool)) != method.ReturnType.ReturnType)
				UnsupportedPredicate("A predicate must have a boolean return type.");

			IExpression expression = GetQueryExpression(method);
			if (null == expression)
				UnsupportedPredicate("No expression found.");

			Visitor visitor = new Visitor(method);
			expression.Accept(visitor);
			return visitor.Expression;
		}

		private static IExpression GetQueryExpression(IMethodDefinition method)
		{
			IActionFlowGraph afg = FlowGraphFactory.CreateActionFlowGraph(FlowGraphFactory.CreateControlFlowGraph(method));
			return GetQueryExpression(afg);
		}

		private static void UnsupportedPredicate(string reason)
		{
			throw new UnsupportedPredicateException(reason);
		}

		private static void UnsupportedExpression(IExpression node)
		{
			UnsupportedPredicate("Unsupported expression: " + ExpressionPrinter.ToString(node));
		}

		private static IExpression GetQueryExpression(IActionFlowGraph afg)
		{
			Hashtable variables = new Hashtable();
			IActionBlock block = afg.Blocks[0];
			while (block != null)
			{
				switch (block.ActionType)
				{
					case ActionType.Invoke:
						UnsupportedExpression(((IInvokeActionBlock)block).Expression);
						break;

					case ActionType.ConditionalBranch:
						UnsupportedPredicate("Conditional blocks are not supported.");
						break;

					case ActionType.Branch:
						block = ((IBranchActionBlock)block).Target;
						break;

					case ActionType.Assign:
						{
							IAssignActionBlock assignBlock = (IAssignActionBlock)block;
							IAssignExpression assign = assignBlock.AssignExpression;
							IVariableReferenceExpression variable = assign.Target as IVariableReferenceExpression;
							if (null == variable)
							{
								UnsupportedExpression(assign);
							}
							else
							{
								if (variables.Contains(variable.Variable.Index))
									UnsupportedExpression(assign.Expression);
								variables.Add(variable.Variable.Index, assign.Expression);
								block = assignBlock.Next;
							}
							break;
						}

					case ActionType.Return:
						{
							IExpression expression = ((IReturnActionBlock)block).Expression;
							IVariableReferenceExpression variable = expression as IVariableReferenceExpression;
							return null == variable
								? expression
								: (IExpression)variables[variable.Variable.Index];
						}
				}
			}
			return null;
		}

		class Visitor : AbstractCodeStructureVisitor
		{
			object _current;
			private int _insideCandidate = 0;
			Hashtable _assemblies = new Hashtable();
			IList _methodDefinitionStack = new ArrayList();

			public Visitor(IMethodDefinition topLevelMethod)
			{
				EnterMethodDefinition(topLevelMethod);
				RegisterAssembly(topLevelMethod.DeclaringType.Module.Assembly);
			}

			private void EnterMethodDefinition(IMethodDefinition method)
			{
				_methodDefinitionStack.Add(method);
			}

			private void LeaveMethodDefinition(IMethodDefinition method)
			{
				int lastIndex = _methodDefinitionStack.Count - 1;
				object popped = _methodDefinitionStack[lastIndex];
				System.Diagnostics.Debug.Assert(method == popped);
				_methodDefinitionStack.RemoveAt(lastIndex);
			}

			/// <summary>
			/// Registers an assembly so it can be looked up by its assembly name
			/// string later.
			/// </summary>
			/// <param name="assembly"></param>
			private void RegisterAssembly(IAssemblyDefinition assembly)
			{
				_assemblies.Add(assembly.Name.FullName, assembly);
			}

			private IAssemblyDefinition LookupAssembly(string fullName)
			{
				return (IAssemblyDefinition)_assemblies[fullName];
			}

			public Expression Expression
			{
				get { return (Expression)_current; }
			}

			private bool InsideCandidate
			{
				get { return _insideCandidate > 0; }
			}

			public override void Visit(IAssignExpression node)
			{
				UnsupportedExpression(node);
			}

			public override void Visit(IVariableReferenceExpression node)
			{
				UnsupportedExpression(node);
			}

			public override void Visit(IArgumentReferenceExpression node)
			{
				UnsupportedExpression(node);
			}

			public override void Visit(IUnaryExpression node)
			{
				switch (node.Operator)
				{
					case UnaryOperator.Not:
						Visit(node.Operand);
						Negate();
						break;

					default:
						UnsupportedExpression(node);
						break;
				}
			}

			public override void Visit(IBinaryExpression node)
			{
				switch (node.Operator)
				{
					case BinaryOperator.ValueEquality:
						PushComparison(node.Left, node.Right, ComparisonOperator.EQUALS);
						break;

					case BinaryOperator.ValueInequality:
						PushComparison(node.Left, node.Right, ComparisonOperator.EQUALS);
						Negate();
						break;

					case BinaryOperator.LessThan:
						PushComparison(node.Left, node.Right, ComparisonOperator.SMALLER);
						break;

					case BinaryOperator.GreaterThan:
						PushComparison(node.Left, node.Right, ComparisonOperator.GREATER);
						break;

					case BinaryOperator.GreaterThanOrEqual:
						PushComparison(node.Left, node.Right, ComparisonOperator.SMALLER);
						Negate();
						break;

					case BinaryOperator.LessThanOrEqual:
						PushComparison(node.Left, node.Right, ComparisonOperator.GREATER);
						Negate();
						break;

					case BinaryOperator.LogicalOr:
						Push(new OrExpression(Convert(node.Left), Convert(node.Right)));
						break;

					case BinaryOperator.LogicalAnd:
						Push(new AndExpression(Convert(node.Left), Convert(node.Right)));
						break;

					default:
						UnsupportedExpression(node);
						break;
				}
			}

			private void Negate()
			{
				Expression top = (Expression)Pop();
				NotExpression topNot = top as NotExpression;
				if (topNot != null)
				{
					Push(topNot.Expr());
					return;
				}
				Push(new NotExpression(top));
			}

			private void PushComparison(IExpression lhs, IExpression rhs, ComparisonOperator op)
			{
				Visit(lhs);
				object left = Pop();
				Visit(rhs);
				object right = Pop();

				bool areOperandsSwapped = IsCandidateFieldValue(right);
				if (areOperandsSwapped)
				{
					object temp = left;
					left = right;
					right = temp;
				}

				AssertType(left, typeof(FieldValue), lhs);
				AssertType(right, typeof(ComparisonOperand), rhs);
				Push(
					new ComparisonExpression(
						(FieldValue)left,
						(ComparisonOperand)right,
						op));

				if (areOperandsSwapped && !op.IsSymmetric())
				{
					Negate();
				}
			}

			private bool IsCandidateFieldValue(object o)
			{
				FieldValue value = o as FieldValue;
				if (value == null)
					return false;
				return value.Root() is CandidateFieldRoot;
			}

			public override void Visit(IMethodInvocationExpression node)
			{
				IMethodReferenceExpression methodRef = node.Target as IMethodReferenceExpression;
				if (null == methodRef)
					UnsupportedExpression(node);

				IMethodReference method = methodRef.Method;
				if (IsOperator(method))
				{
					ProcessOperatorMethodInvocation(node, method);
					return;
				}

				if (IsSystemString(method.DeclaringType))
				{
					ProcessStringMethod(node, methodRef);
					return;
				}

				ProcessRegularMethodInvocation(node, methodRef);
			}

			private static bool IsSystemString(TypeReference type)
			{
				return type.FullName == "System.String";
			}

			private void ProcessStringMethod(IMethodInvocationExpression node, IMethodReferenceExpression methodRef)
			{
				IMethodReference method = methodRef.Method;

				if (method.Parameters.Count != 1
					|| !IsSystemString(method.Parameters[0].ParameterType))
				{
					UnsupportedExpression(methodRef);
				}

				switch (method.Name)
				{
					case "Contains":
						PushComparison(methodRef.Target, node.Arguments[0], ComparisonOperator.CONTAINS);
						break;

					case "StartsWith":
						PushComparison(methodRef.Target, node.Arguments[0], ComparisonOperator.STARTSWITH);
						break;

					case "EndsWith":
						PushComparison(methodRef.Target, node.Arguments[0], ComparisonOperator.ENDSWITH);
						break;

					case "Equals":
						PushComparison(methodRef.Target, node.Arguments[0], ComparisonOperator.EQUALS);
						break;

					default:
						UnsupportedExpression(methodRef);
						break;
				}
			}

			private void ProcessRegularMethodInvocation(IMethodInvocationExpression node, IMethodReferenceExpression methodRef)
			{
				if (node.Arguments.Count != 0)
					UnsupportedExpression(node);

				IExpression target = methodRef.Target;
				switch (target.CodeElementType)
				{
					case CodeElementType.ThisReferenceExpression:
						if (!InsideCandidate)
							UnsupportedExpression(node);
						ProcessCandidateMethodInvocation(node, methodRef);
						break;

					case CodeElementType.ArgumentReferenceExpression:
						ProcessCandidateMethodInvocation(node, methodRef);
						break;

					default:
						Push(ToFieldValue(target));
						ProcessCandidateMethodInvocation(node, methodRef);
						break;
				}
			}

			private void ProcessOperatorMethodInvocation(IMethodInvocationExpression node, IMethodReference method)
			{
				switch (method.Name)
				{
					case "op_Equality":
						PushComparison(node.Arguments[0], node.Arguments[1], ComparisonOperator.EQUALS);
						break;

					case "op_Inequality":
						PushComparison(node.Arguments[0], node.Arguments[1], ComparisonOperator.EQUALS);
						Negate();
						break;

					// XXX: check if the operations below are really supported for the
					// data types in question
					case "op_GreaterThanOrEqual":
						PushComparison(node.Arguments[0], node.Arguments[1], ComparisonOperator.SMALLER);
						Negate();
						break;

					case "op_LessThanOrEqual":
						PushComparison(node.Arguments[0], node.Arguments[1], ComparisonOperator.GREATER);
						Negate();
						break;

					case "op_LessThan":
						PushComparison(node.Arguments[0], node.Arguments[1], ComparisonOperator.SMALLER);
						break;

					case "op_GreaterThan":
						PushComparison(node.Arguments[0], node.Arguments[1], ComparisonOperator.GREATER);
						break;

					default:
						UnsupportedExpression(node);
						break;
				}
			}

			private void ProcessCandidateMethodInvocation(IMethodInvocationExpression node, IMethodReferenceExpression methodRef)
			{
				IMethodDefinition method = GetMethodDefinition(methodRef);
				if (null == method)
					UnsupportedExpression(node);

				AssertMethodCanBeVisited(node, method);

				IExpression expression = GetQueryExpression(method);
				if (null == expression)
					UnsupportedExpression(node);

				EnterCandidateMethod(method);
				try
				{
					Visit(expression);
				}
				finally
				{
					LeaveCandidateMethod(method);
				}
			}

			private void AssertMethodCanBeVisited(IMethodInvocationExpression node, IMethodDefinition method)
			{
				if (_methodDefinitionStack.Contains(method))
					UnsupportedExpression(node);
			}

			private IMethodDefinition GetMethodDefinition(IMethodReferenceExpression methodRef)
			{
				IMethodDefinition definition = methodRef.Method as IMethodDefinition;
				return definition != null
					? definition
					: LoadExternalMethodDefinition(methodRef);
			}

			private IMethodDefinition LoadExternalMethodDefinition(IMethodReferenceExpression methodRef)
			{
				IMethodReference method = methodRef.Method;
				IAssemblyDefinition assemblyDef = GetContainingAssembly(method.DeclaringType);
				ITypeDefinition type = assemblyDef.MainModule.Types[method.DeclaringType.FullName];
				return type.Methods.GetMethod(method.Name, method.Parameters);
			}

			private IAssemblyDefinition GetContainingAssembly(ITypeReference type)
			{
				AssemblyNameReference scope = (AssemblyNameReference)type.Scope;
				string assemblyName = scope.FullName;
				IAssemblyDefinition definition = LookupAssembly(assemblyName);
				if (null == definition)
				{
					Assembly assembly = Assembly.Load(assemblyName);
					string location = assembly.GetType(type.FullName).Module.FullyQualifiedName;
					definition = QueryExpressionBuilder.GetAssembly(location);
					RegisterAssembly(definition);
				}
				return definition;
			}

			private void EnterCandidateMethod(IMethodDefinition method)
			{
				EnterMethodDefinition(method);
				++_insideCandidate;
			}

			private void LeaveCandidateMethod(IMethodDefinition method)
			{
				--_insideCandidate;
				LeaveMethodDefinition(method);
			}

			private static bool IsOperator(IMethodReference method)
			{
				return !method.HasThis && method.Name.StartsWith("op_") && 2 == method.Parameters.Count;
			}

			public override void Visit(IFieldReferenceExpression node)
			{
				IExpression target = node.Target;
				switch (target.CodeElementType)
				{
					case CodeElementType.ArgumentReferenceExpression:
						//IArgumentReferenceExpression arg = (IArgumentReferenceExpression)target;
						Push(new FieldValue(CandidateFieldRoot.INSTANCE, node.Field.Name));
						break;

					case CodeElementType.ThisReferenceExpression:
						if (InsideCandidate)
						{
							if (_current != null)
							{
								FieldValue current = PopFieldValue(node);
								Push(new FieldValue(current, node.Field.Name));
							}
							else
							{
								Push(new FieldValue(CandidateFieldRoot.INSTANCE, node.Field.Name));
							}
						}
						else
						{
							Push(new FieldValue(PredicateFieldRoot.INSTANCE, node.Field.Name));
						}
						break;

					case CodeElementType.MethodInvocationExpression:
					case CodeElementType.FieldReferenceExpression:
						FieldValue value = ToFieldValue(target);
						Push(new FieldValue(value, node.Field.Name));
						break;

					default:
						UnsupportedExpression(node);
						break;
				}
			}

			public override void Visit(ILiteralExpression node)
			{
				Push(new ConstValue(node.Value));
			}

			Expression Convert(IExpression node)
			{
				return ReconstructNullComparisonIfNecessary(node);
			}

			private Expression ReconstructNullComparisonIfNecessary(IExpression node)
			{
				Visit(node);

				object top = Pop();
				FieldValue fieldValue = top as FieldValue;
				if (fieldValue == null)
				{
					AssertType(top, typeof(Expression), node);
					return (Expression)top;
				}

				return
					new NotExpression(
						new ComparisonExpression(
							fieldValue,
							new ConstValue(null),
							ComparisonOperator.EQUALS));
			}

			FieldValue ToFieldValue(IExpression node)
			{
				Visit(node);
				return PopFieldValue(node);
			}

			private FieldValue PopFieldValue(IExpression node)
			{
				return (FieldValue)Pop(node, typeof(FieldValue));
			}

			void Push(object value)
			{
				Assert(_current == null, "expression stack must be empty before Push");
				_current = value;
			}

			object Pop(IExpression node, System.Type expectedType)
			{
				object value = Pop();
				AssertType(value, expectedType, node);
				return value;
			}

			private static void AssertType(object value, Type expectedType, IExpression sourceExpression)
			{
				Type actualType = value.GetType();
				if (!expectedType.IsAssignableFrom(actualType))
				{
					UnsupportedPredicate(
						string.Format("Unsupported expression: {0}. Unexpected type on stack. Expected: {1}, Got: {2}.",
									  ExpressionPrinter.ToString(sourceExpression), expectedType, actualType));
				}
			}

			object Pop()
			{
				Assert(_current != null, "expression stack is empty");
				object value = _current;
				_current = null;
				return value;
			}

			private void Assert(bool condition, string message)
			{
				System.Diagnostics.Debug.Assert(condition, message);
			}
		}
	}
}