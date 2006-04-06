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
        private TypeReference _System_Reflection_MethodBase;
        private TypeReference _System_Object;
        private TypeReference _System_Void;
        private MethodReference _System_Reflection_MethodBase_GetMethodFromHandle;

        private TypeReference _MetaDelegate;
        private TypeReference _YapStream;
        private MethodReference _YapStream_GetNativeQueryHandler;
        private MethodReference _NativeQueryHandler_ExecuteMeta;
        
        private Dictionary<TypeReference, MethodReference> _cachedMetaPredicateConstructors = new Dictionary<TypeReference, MethodReference>();
            
        public QueryInvocationProcessor(AssemblyDefinition assembly)
        {
            _assembly = assembly;
            _YapStream = Import(typeof(YapStream));
            _System_Predicate = Import(typeof(System.Predicate<object>).GetGenericTypeDefinition());
            _MetaDelegate = Import(typeof(MetaDelegate<object>).GetGenericTypeDefinition());
            _System_Reflection_MethodBase = Import(typeof(System.Reflection.MethodBase));
            _System_Object = Import(typeof(object));
            _System_Void = Import(typeof(void));
            _YapStream_GetNativeQueryHandler = Import(typeof(YapStream).GetMethod("GetNativeQueryHandler", BindingFlags.Public | BindingFlags.Instance | BindingFlags.IgnoreCase));
            _System_Reflection_MethodBase_GetMethodFromHandle = Import(typeof(MethodBase).GetMethod("GetMethodFromHandle", new Type[] { typeof(System.RuntimeMethodHandle) }));
            _NativeQueryHandler_ExecuteMeta = Import(typeof(com.db4o.inside.query.NativeQueryHandler).GetMethod("ExecuteMeta"));
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
            Instruction queryPatternStart = GetNthPrevious(queryInvocation, 3);
            InsertGetNativeQueryHandlerBefore(worker, queryPatternStart);

            worker.InsertAfter(queryPatternStart, worker.Create(OpCodes.Dup)); // target object

            ReplaceByExecuteMeta(worker, queryInvocation, GetMethodReferenceFromInlinePredicatePattern(queryInvocation));
        }

        private void ProcessCachedStaticFieldPattern(CilWorker worker, Instruction queryInvocation)
        {
            Instruction queryPatternStart = GetNthPrevious(queryInvocation, 8);
            InsertGetNativeQueryHandlerBefore(worker, queryPatternStart);

            worker.InsertBefore(queryPatternStart, worker.Create(OpCodes.Ldnull)); // target object

            ReplaceByExecuteMeta(worker, queryInvocation, GetMethodReferenceFromStaticFieldPattern(queryInvocation));
        }

        private void ReplaceByExecuteMeta(CilWorker worker, Instruction queryInvocation, MethodReference targetMethod)
        {
            TypeReference extent = GetQueryCallExtent(queryInvocation);

            worker.InsertBefore(queryInvocation, worker.Create(OpCodes.Ldtoken, targetMethod));
            worker.InsertBefore(queryInvocation, worker.Create(OpCodes.Call, _System_Reflection_MethodBase_GetMethodFromHandle));

            worker.InsertBefore(queryInvocation, worker.Create(OpCodes.Newobj,
                                                                    GetMetaPredicateConstructor(extent)));
            worker.InsertBefore(queryInvocation, worker.Create(OpCodes.Ldnull)); // comparator

            worker.Replace(queryInvocation, worker.Create(OpCodes.Callvirt,
                                                               InstantiateGenericMethod(
                                                                   _NativeQueryHandler_ExecuteMeta,
                                                                   extent)));
        }

        private void InsertGetNativeQueryHandlerBefore(CilWorker worker, Instruction instruction)
        {
            worker.InsertBefore(instruction, worker.Create(OpCodes.Castclass, _YapStream));
            worker.InsertBefore(instruction,
                                worker.Create(OpCodes.Callvirt,
                                              _YapStream_GetNativeQueryHandler));
        }

        MethodReference GetMetaPredicateConstructor(TypeReference extent)
        {
            MethodReference ctor = null;
            if (!_cachedMetaPredicateConstructors.TryGetValue(extent, out ctor))
            {
                ctor = CreateMetaPredicateConstructor(extent);
                _cachedMetaPredicateConstructors.Add(extent, ctor);
            }
            return ctor;
        }

        private MethodReference CreateMetaPredicateConstructor(TypeReference extent)
        {
            // MetaPredicate<System.Predicate<extent> >
            GenericInstanceType concretePredicate = InstantiateGenericType(_System_Predicate, extent);
            GenericInstanceType concreteMetaDelegate = InstantiateGenericType(_MetaDelegate, concretePredicate);
            MethodReference ctor = new MethodReference(MethodDefinition.Ctor, concreteMetaDelegate, _System_Void, true, false, MethodCallingConvention.Default);
            ctor.Parameters.Add(new ParameterDefinition(_System_Object));
            ctor.Parameters.Add(new ParameterDefinition(_MetaDelegate.GenericParameters[0]));
            ctor.Parameters.Add(new ParameterDefinition(_System_Reflection_MethodBase));
            _assembly.MainModule.MemberReferences.Add(ctor);
            return ctor;
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
    }
}