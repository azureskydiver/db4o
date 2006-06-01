using System.Collections;
using System.Collections.Generic;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Db4oAdmin
{
	public abstract class AbstractNQInstrumentation
	{
		protected AssemblyDefinition _assembly;
		protected string _assemblyLocation;
		
		protected AbstractNQInstrumentation(string location)
		{
			_assemblyLocation = location;
			_assembly = AssemblyFactory.GetAssembly(location);
		}

		protected abstract void ProcessQueryInvocation(MethodDefinition parent, Instruction queryInvocation);

		public void Run()
		{
			ProcessAssembly();
			SaveAssembly();
		}

		private void SaveAssembly()
		{
			AssemblyFactory.SaveAssembly(_assembly, _assemblyLocation);
		}

		private void InstrumentMethod(MethodDefinition method)
		{
			if (null == method.Body) return;
			
			List<Instruction> instructions = CollectQueryInvocations(method);
			foreach (Instruction instruction in instructions)
			{
				ProcessQueryInvocation(method, instruction);
			}
		}

		private List<Instruction> CollectQueryInvocations(MethodDefinition method)
		{
			return new List<Instruction>(EnumerateQueryInvocations(method));
		}

		private IEnumerable<Instruction> EnumerateQueryInvocations(MethodDefinition method)
		{
			foreach (Instruction instruction in method.Body.Instructions)
			{
				if (IsObjectContainerQueryOnPredicateInvocation(instruction))
				{
					yield return instruction;
				}
			}
		}

		private bool IsObjectContainerQueryOnPredicateInvocation(Instruction instruction)
		{
			if (instruction.OpCode.Value != OpCodes.Callvirt.Value) return false;
			GenericInstanceMethod methodRef = instruction.Operand as GenericInstanceMethod;
			if (null == methodRef) return false;
			if (1 == string.Compare("query", methodRef.Name, true)) return false;
			if (1 != methodRef.Parameters.Count) return false;
			return IsSystemPredicateInstance(methodRef.Parameters[0].ParameterType);
		}

		private bool IsSystemPredicateInstance(TypeReference type)
		{
			GenericInstanceType genericType = type as GenericInstanceType;
			if (null == genericType) return false;
			return genericType.FullName.StartsWith("System.Predicate");
		}

		private void ProcessAssembly()
		{
			foreach (ModuleDefinition module in _assembly.Modules)
			{
				ProcessModule(module);
			}
		}

		protected virtual void ProcessModule(ModuleDefinition module)
		{
			foreach (TypeDefinition typedef in module.Types)
			{
				ProcessType(typedef);
			}
		}

		protected virtual void ProcessType(TypeDefinition type)
		{
			InstrumentMethods(type.Methods);
			InstrumentMethods(type.Constructors);
		}

		private void InstrumentMethods(IEnumerable methods)
		{
			foreach (MethodDefinition methodef in methods)
			{
				InstrumentMethod(methodef);
			}
		}
	}
}