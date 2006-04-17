using System;
using System.Collections.Generic;
using System.Reflection;
using com.db4o;
using com.db4o.inside.query;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace CFNativeQueriesEnabler
{
	class QueryInvocationProcessor
	{
		private AssemblyDefinition _assembly;

		private TypeReference _System_Predicate;
		private TypeReference _System_Object;
		private TypeReference _System_Void;

		private TypeReference _YapStream;
		private MethodReference _NativeQueryHandler_ExecuteInstrumentedDelegateQuery;
		private MethodReference _NativeQueryHandler_ExecuteInstrumentedStaticDelegateQuery;

		public QueryInvocationProcessor(AssemblyDefinition assembly)
		{
			_assembly = assembly;
			_YapStream = Import(typeof(YapStream));
			_System_Predicate = Import(typeof(System.Predicate<object>).GetGenericTypeDefinition());
			_System_Object = Import(typeof(object));
			_System_Void = Import(typeof(void));
			_NativeQueryHandler_ExecuteInstrumentedDelegateQuery = Import(typeof(com.db4o.inside.query.NativeQueryHandler).GetMethod("ExecuteInstrumentedDelegateQuery", BindingFlags.Public | BindingFlags.Static));
			_NativeQueryHandler_ExecuteInstrumentedStaticDelegateQuery = Import(typeof(com.db4o.inside.query.NativeQueryHandler).GetMethod("ExecuteInstrumentedStaticDelegateQuery", BindingFlags.Public | BindingFlags.Static));
		}

		public void Process(MethodDefinition parent, Instruction queryInvocation)
		{
			//Console.WriteLine(CecilFormatter.FormatMethodBody(parent));

			CilWorker worker = parent.Body.CilWorker;
			if (IsCachedStaticFieldPattern(queryInvocation))
			{
				ProcessCachedStaticFieldPattern(worker, queryInvocation);
			}
			else if (IsPredicateCreationPattern(queryInvocation))
			{
				ProcessPredicateCreationPattern(worker, queryInvocation);
			}
			else
			{
				throw new ArgumentException("Unknown query invocation pattern!");
			}

			// Console.WriteLine(CecilFormatter.FormatMethodBody(parent));
		}

		private void ProcessPredicateCreationPattern(CilWorker worker, Instruction queryInvocation)
		{
			MethodReference predicateMethod = GetMethodReferenceFromInlinePredicatePattern(queryInvocation);

			Instruction ldftn = GetNthPrevious(queryInvocation, 2);
			worker.InsertBefore(ldftn, worker.Create(OpCodes.Dup));

			worker.InsertBefore(queryInvocation, worker.Create(OpCodes.Ldtoken, predicateMethod));

			// At this point the stack is like this:
			//     runtime method handle, delegate reference, target object, ObjectContainer
			worker.Replace(queryInvocation,
						   worker.Create(OpCodes.Call,
										 InstantiateGenericMethod(
											 _NativeQueryHandler_ExecuteInstrumentedDelegateQuery,
											 GetQueryCallExtent(queryInvocation))));
		}

		private void ProcessCachedStaticFieldPattern(CilWorker worker, Instruction queryInvocation)
		{
			MethodReference predicateMethod = GetMethodReferenceFromStaticFieldPattern(queryInvocation);
			worker.InsertBefore(queryInvocation, worker.Create(OpCodes.Ldtoken, predicateMethod));

			// At this point the stack is like this:
			//     runtime method handle, delegate reference, ObjectContainer
			worker.Replace(queryInvocation,
						   worker.Create(OpCodes.Call,
										 InstantiateGenericMethod(
											 _NativeQueryHandler_ExecuteInstrumentedStaticDelegateQuery,
											 GetQueryCallExtent(queryInvocation))));
		}

		private GenericInstanceType InstantiateGenericType(TypeReference genericTypeDefinition, params TypeReference[] arguments)
		{
			GenericInstanceType type = new GenericInstanceType(genericTypeDefinition);
			foreach (TypeReference argument in arguments)
			{
				type.GenericArguments.Add(argument);
			}
			return type;
		}

		private MethodReference GetMethodReferenceFromInlinePredicatePattern(Instruction queryInvocation)
		{
			return (MethodReference)GetNthPrevious(queryInvocation, 2).Operand;
		}

		private bool IsPredicateCreationPattern(Instruction queryInvocation)
		{
			return ComparePrevious(queryInvocation, OpCodes.Newobj, OpCodes.Ldftn);
		}

		private MethodReference InstantiateGenericMethod(MethodReference methodReference, TypeReference extent)
		{
			GenericInstanceMethod instance = new GenericInstanceMethod(methodReference);
			instance.GenericArguments.Add(extent);
			return instance;
		}

		private TypeReference GetQueryCallExtent(Instruction queryInvocation)
		{
			GenericInstanceMethod method = (GenericInstanceMethod)queryInvocation.Operand;
			return method.GenericArguments[0];
		}

		private TypeReference Import(Type type)
		{
			return _assembly.MainModule.Import(type);
		}

		private MethodReference Import(MethodBase method)
		{
			return _assembly.MainModule.Import(method);
		}

		private MethodReference GetMethodReferenceFromStaticFieldPattern(Instruction instr)
		{
			return (MethodReference)GetNthPrevious(instr, 5).Operand;
		}

		private Instruction GetNthPrevious(Instruction instr, int n)
		{
			Instruction previous = instr;
			for (int i = 0; i < n; ++i)
			{
				previous = previous.Previous;
			}
			return previous;
		}

		private bool IsCachedStaticFieldPattern(Instruction instr)
		{
			return
				ComparePrevious(instr, OpCodes.Ldsfld, OpCodes.Br_S, OpCodes.Stsfld, OpCodes.Newobj, OpCodes.Ldftn,
								OpCodes.Ldnull, OpCodes.Brtrue_S, OpCodes.Ldsfld);

		}

		private bool ComparePrevious(IInstruction instr, params OpCode[] opcodes)
		{
			IInstruction previous = instr.Previous;
			foreach (OpCode opcode in opcodes)
			{
				if (previous == null) return false;
				if (previous.OpCode.Value != opcode.Value) return false;

				previous = previous.Previous;
			}
			return true;
		}
	}
}