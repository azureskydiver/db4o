using System;
using System.Collections.Generic;
using System.IO;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace CFNativeQueriesEnabler
{
    public class Program
    {
        private AssemblyDefinition _assembly;
        private string _assemblyLocation;
        private QueryInvocationProcessor _processor;

        static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine("Usage:\n\tCFNativeQueriesEnabler <assembly location>");
                return;
            }
            string assemblyLocation = args[0];
            new Program(assemblyLocation).Run();
        }

        public Program(string location)
        {
            _assemblyLocation = location;
            _assembly = AssemblyFactory.GetAssembly(location);
        }

        public void Run()
        {
            InstrumentAssembly();
            SaveAssembly();
        }

        private void SaveAssembly()
        {
            AssemblyFactory.SaveAssembly(_assembly, _assemblyLocation);
        }

        private void InstrumentAssembly()
        {   
            foreach (MethodDefinition method in EnumerateMethods(_assembly))
            {
                if (null == method.Body) continue;
                InstrumentMethod(method);
            }
        }

        private void InstrumentMethod(MethodDefinition method)
        {
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

        void ProcessQueryInvocation(MethodDefinition parent, Instruction queryInvocation)
        {
            if (null == _processor) _processor = new QueryInvocationProcessor(_assembly);
            _processor.Process(parent, queryInvocation);
        }

        bool IsObjectContainerQueryOnPredicateInvocation(Instruction instruction)
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

        IEnumerable<MethodDefinition> EnumerateMethods(AssemblyDefinition assembly)
        {
            foreach (ModuleDefinition module in assembly.Modules)
            {
                foreach (TypeDefinition typedef in module.Types)
                {
                    foreach (MethodDefinition methodef in typedef.Methods)
                    {
                        yield return methodef;
                    }
                }
            }
        }

        static string AssemblyPath
        {
            get
            {
                return Path.GetDirectoryName(typeof(Program).Module.FullyQualifiedName);
            }
        }
    }
}