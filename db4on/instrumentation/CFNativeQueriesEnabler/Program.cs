using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using Cecil.FlowAnalysis.CecilUtilities;
using com.db4o;
using com.db4o.inside.query;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace CFNativeQueriesEnabler
{
    public class Program
    {
        private AssemblyDefinition _assembly;
        private string _assemblyLocation;

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
            foreach (MethodDefinition methodDefinition in EnumerateMethods(_assembly))
            {
                //Console.WriteLine(CecilFormatter.FormatMethodBody(methodDefinition));
                ProcessQueryCalls(methodDefinition);
            }
            AssemblyFactory.SaveAssembly(_assembly, _assemblyLocation);
        }

        void ProcessQueryCalls(MethodDefinition method)
        {
            foreach (Instruction instr in method.Body.Instructions)
            {
                if (IsObjectContainerQueryOnPredicateCall(instr))
                {
                    ProcessQueryCall(method, instr);
                    break;
                }
            }
        }

        void ProcessQueryCall(MethodDefinition parent, Instruction queryCallInstruction)
        {
            //Console.WriteLine(CecilFormatter.FormatMethodBody(parent));

            CilWorker worker = parent.Body.CilWorker;
            if (IsCachedStaticFieldPattern(queryCallInstruction))
            {
                InstrumentCachedStaticFieldPattern(worker, queryCallInstruction);
            }
            else if (IsPredicateCreationPattern(queryCallInstruction))
            {
                InstrumentPredicateCreationPattern(worker, queryCallInstruction);
            }

            // Console.WriteLine(CecilFormatter.FormatMethodBody(parent));
        }

        private void InstrumentPredicateCreationPattern(CilWorker worker, Instruction queryCallInstruction)
        {
            MethodReference predicateMethod = GetMethodReferenceFromInlinePredicatePattern(queryCallInstruction);
            Instruction queryCallPatternStart = GetNthPrevious(queryCallInstruction, 3);
            InsertGetNativeQueryHandlerBefore(worker, queryCallPatternStart);
            
            worker.InsertAfter(queryCallPatternStart, worker.Create(OpCodes.Dup)); // target object
            
            ReplaceByExecuteMeta(worker, queryCallInstruction, GetMethodReferenceFromInlinePredicatePattern(queryCallInstruction));
        }

        private void InstrumentCachedStaticFieldPattern(CilWorker worker, Instruction queryCallInstruction)
        {
            Instruction queryCallPatternStart = GetNthPrevious(queryCallInstruction, 8);
            InsertGetNativeQueryHandlerBefore(worker, queryCallPatternStart);

            worker.InsertBefore(queryCallPatternStart, worker.Create(OpCodes.Ldnull)); // target object

            ReplaceByExecuteMeta(worker, queryCallInstruction, GetMethodReferenceFromStaticFieldPattern(queryCallInstruction));
        }

        private void ReplaceByExecuteMeta(CilWorker worker, Instruction queryCallInstruction, MethodReference targetMethod)
        {
            TypeReference extent = GetQueryCallExtent(queryCallInstruction);

            worker.InsertBefore(queryCallInstruction, worker.Create(OpCodes.Ldtoken, targetMethod));
            
            worker.InsertBefore(queryCallInstruction, worker.Create(OpCodes.Newobj,
                                                                    GetMetaPredicateConstructor(extent)));
            worker.InsertBefore(queryCallInstruction, worker.Create(OpCodes.Ldnull)); // comparator
            
            worker.Replace(queryCallInstruction, worker.Create(OpCodes.Callvirt,
                                                               InstantiateGenericMethod(
                                                                   NativeQueryHandler_ExecuteMeta,
                                                                   extent)));
        }

        private void InsertGetNativeQueryHandlerBefore(CilWorker worker, Instruction instruction)
        {
            worker.InsertBefore(instruction,
                                worker.Create(OpCodes.Castclass, Import(typeof(YapStream))));

            worker.InsertBefore(instruction,
                                worker.Create(OpCodes.Callvirt,
                                              YapStream_GetNativeQueryHandler));
        }

        static System.Type MetaDelegateType = typeof(MetaDelegate<object>).GetGenericTypeDefinition();
        
        static System.Type PredicateType = typeof(System.Predicate<object>).GetGenericTypeDefinition();

        static System.Reflection.ConstructorInfo MetaDelegate_ConstructorInfo = MetaDelegateType.GetConstructors()[0];

        private MethodReference GetMetaPredicateConstructor(TypeReference extent)
        {
            GenericInstanceType concretePredicate = InstantiateGenericType(Import(PredicateType), extent);
            GenericInstanceType concreteMetaDelegate = InstantiateGenericType(Import(MetaDelegateType), concretePredicate);
            
            MethodReference ctor = Import(MetaDelegate_ConstructorInfo);

            // TODO: CECIL needs something like a
            // GenericParameterRef or to make TypeReference implement IGenericParameterProvider
            TypeReference typeRef = concreteMetaDelegate;
            return InstantiateGenericMethod(ctor, typeRef);
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

        private MethodReference YapStream_GetNativeQueryHandler
        {
            get
            {
                return Import(typeof(YapStream).GetMethod("GetNativeQueryHandler", BindingFlags.Public | BindingFlags.Instance | BindingFlags.IgnoreCase));
            }
        }

        private MethodReference GetMethodReferenceFromInlinePredicatePattern(Instruction queryCallInstruction)
        {
            return (MethodReference)GetNthPrevious(queryCallInstruction, 2).Operand;
        }

        private bool IsPredicateCreationPattern(Instruction queryCallInstruction)
        {
            return ComparePrevious(queryCallInstruction, OpCodes.Newobj, OpCodes.Ldftn);
        }

        private MethodReference InstantiateGenericMethod(MethodReference methodReference, TypeReference extent)
        {
            GenericInstanceMethod instance = new GenericInstanceMethod(methodReference);
            instance.GenericArguments.Add(extent);
            return instance;
        }

        private TypeReference GetQueryCallExtent(Instruction queryCallInstruction)
        {
            GenericInstanceMethod method = (GenericInstanceMethod)queryCallInstruction.Operand;
            return method.GenericArguments[0];
        }

        private MethodReference NativeQueryHandler_ExecuteMeta
        {
            get
            {
                return Import(typeof(com.db4o.inside.query.NativeQueryHandler).GetMethod("ExecuteMeta"));
            }
        }

        private TypeReference Import(Type type)
        {
            return _assembly.MainModule.Import(type);
        }

        private MethodReference Import(MethodBase method)
        {
            return _assembly.MainModule.Import(method);
        }

        private Instruction RemoveNPrevious(CilWorker worker, Instruction instr, int n)
        {
            Instruction previous = instr;
            for (int i = 0; i < n; ++i)
            {
                previous = previous.Previous;
                worker.Remove(previous);
            }
            return previous;
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

        bool IsObjectContainerQueryOnPredicateCall(Instruction instr)
        {
            if (instr.OpCode.Value != OpCodes.Callvirt.Value) return false;
            GenericInstanceMethod methodRef = instr.Operand as GenericInstanceMethod;
            if (null == methodRef) return false;
            if ("query" != methodRef.Name) return false;
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