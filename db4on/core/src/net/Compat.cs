/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Reflection.Emit;
using System.Threading;
using com.db4o.query;
using com.db4o.reflect;
using com.db4o.reflect.net;
using j4o.lang;
using Thread=System.Threading.Thread;

namespace com.db4o
{
	/// <exclude />
	public class Compat
	{
		public static void addShutDownHook(EventHandler handler)
		{
			AppDomain.CurrentDomain.ProcessExit += handler;
			AppDomain.CurrentDomain.DomainUnload += handler;
		}

		public static Stream buffer(Stream stream)
		{
			return new BufferedStream(stream);
		}

		public static Stream buffer(Stream stream, int bufferSize)
		{
			return new BufferedStream(stream, bufferSize);
		}

		public static bool compact()
		{
			return false;
		}

#if !MONO && !NET_2_0
		/// <summary>
		/// Emits a HashCodeFunction which calls System.Object.GetHashCode
		/// non virtually thus yielding an identity based hash code value.
		/// </summary>
		public static IdentityHashCodeProvider.HashCodeFunction getIdentityHashCodeFunction()
		{
			// for compatibility with .net 1.0
			return EmitHashCodeFunction();
		}

		private static IdentityHashCodeProvider.HashCodeFunction EmitHashCodeFunction()
		{
			/*
			 class HashCodeHelper {
			 	public static int GetHashCode(object o) {
			 		return o.GetHashCode(); // non virtual call
			 	}
			 
			 	public static HashCodeFunction NewHashCodeFunction() {
			 		return new HashCodeFunction(GetHashCode);
			 	}
			 }
			 */
			AssemblyName name = new AssemblyName();
			name.Name = "db4o-runtime-helper";
			AssemblyBuilder assembly = AppDomain.CurrentDomain.DefineDynamicAssembly(name, AssemblyBuilderAccess.Run);
			ModuleBuilder module = assembly.DefineDynamicModule("db4o-runtime-helpers");
			TypeBuilder type = module.DefineType("HashCodeHelper");
			MethodBuilder getHashCodeMethod = type.DefineMethod("GetHashCode",
			                                                    MethodAttributes.Public | MethodAttributes.Static,
			                                                    typeof(int),
			                                                    new Type[] {typeof(object)});
			ILGenerator il = getHashCodeMethod.GetILGenerator();
			il.Emit(OpCodes.Ldarg_0);
			il.Emit(OpCodes.Call, typeof(Object).GetMethod("GetHashCode"));
			il.Emit(OpCodes.Ret);

			MethodBuilder newHashCodeFunctionMethod = type.DefineMethod("NewHashCodeFunction",
			                                                            MethodAttributes.Public | MethodAttributes.Static,
			                                                            typeof(IdentityHashCodeProvider.HashCodeFunction),
			                                                            new Type[0]);
			il = newHashCodeFunctionMethod.GetILGenerator();
			il.Emit(OpCodes.Ldnull);
			il.Emit(OpCodes.Ldftn, getHashCodeMethod);
			il.Emit(OpCodes.Newobj,
			        typeof(IdentityHashCodeProvider.HashCodeFunction).GetConstructor(new Type[] {typeof(object), typeof(IntPtr)}));
			il.Emit(OpCodes.Ret);
			return
				(IdentityHashCodeProvider.HashCodeFunction) type.CreateType().GetMethod("NewHashCodeFunction").Invoke(null, null);
		}
#endif

		public static long doubleToLong(double a_double)
		{
			return BitConverter.DoubleToInt64Bits(a_double);
		}

		public static int getArrayRank(Type type)
		{
			return type.GetArrayRank();
		}

		public static bool isDirectory(string path)
		{
			return (File.GetAttributes(path) & FileAttributes.Directory) != 0;
		}

		public static void lockFileStream(FileStream fs)
		{
			fs.Lock(0, 1);
		}

		public static double longToDouble(long a_long)
		{
			return BitConverter.Int64BitsToDouble(a_long);
		}

		public static void notify(object obj)
		{
			Monitor.Pulse(obj);
		}

		public static void notifyAll(object obj)
		{
			Monitor.PulseAll(obj);
		}

		public static ReflectConstructor serializationConstructor(Type type)
		{
			return new SerializationConstructor(type);
		}

		public static string stackTrace()
		{
			return new StackTrace(true).ToString();
		}

		public static void threadSetName(Thread thread, string name)
		{
			thread.Name = name;
		}

		public static string threadGetName(Thread thread)
		{
			return thread.Name;
		}

		public static void wait(object obj, long timeout)
		{
			Monitor.Wait(obj, (int) timeout);
		}

		internal static object wrapEvaluation(object evaluation)
		{
			return (evaluation is EvaluationDelegate)
			       	? new EvaluationDelegateWrapper((EvaluationDelegate) evaluation)
			       	: evaluation;
		}
	}
}