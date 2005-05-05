/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
using System;
using System.IO;
using System.Diagnostics;
using System.Reflection;
using System.Reflection.Emit;
using System.Threading;

namespace com.db4o {

	/// <exclude />
    public class Compat {
		
		public static void addShutDownHook(EventHandler handler) {
            AppDomain.CurrentDomain.ProcessExit += handler;
            AppDomain.CurrentDomain.DomainUnload += handler;
        }

        public static Stream buffer(Stream stream){
            return new BufferedStream(stream);
        }

        public static Stream buffer(Stream stream, int bufferSize){
            return new BufferedStream(stream, bufferSize);
        }

        public static bool compact(){
            return false;
        }
		
		/// <summary>
		/// Emits a HashCodeFunction which calls System.Object.GetHashCode
		/// non virtually thus yielding an identity based hash code value.
		/// </summary>
		public static j4o.lang.IdentityHashCodeProvider.HashCodeFunction getIdentityHashCodeFunction() {
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
								 MethodAttributes.Public|MethodAttributes.Static,
								 typeof(int),
								 new Type[] { typeof(object) });
			ILGenerator il = getHashCodeMethod.GetILGenerator();
			il.Emit(OpCodes.Ldarg_0);
			il.Emit(OpCodes.Call, typeof(Object).GetMethod("GetHashCode"));
			il.Emit(OpCodes.Ret);
			
			MethodBuilder newHashCodeFunctionMethod = type.DefineMethod("NewHashCodeFunction",
										MethodAttributes.Public|MethodAttributes.Static,
										typeof(j4o.lang.IdentityHashCodeProvider.HashCodeFunction),
										new Type[0]);
			il = newHashCodeFunctionMethod.GetILGenerator();
			il.Emit(OpCodes.Ldnull);
			il.Emit(OpCodes.Ldftn, getHashCodeMethod);
			il.Emit(OpCodes.Newobj, typeof(j4o.lang.IdentityHashCodeProvider.HashCodeFunction).GetConstructor(new Type[] { typeof(object), typeof(System.IntPtr) }));
			il.Emit(OpCodes.Ret);
			return (j4o.lang.IdentityHashCodeProvider.HashCodeFunction)type.CreateType().GetMethod("NewHashCodeFunction").Invoke(null, null);
		}
		
		
        public static long doubleToLong(double a_double) {
            return BitConverter.DoubleToInt64Bits(a_double);
        }

        public static int getArrayRank(Type type) {
            return type.GetArrayRank();
        }

        public static bool isDirectory(string path) {
            return (File.GetAttributes(path) &  FileAttributes.Directory) != 0;
        }

        public static void lockFileStream(FileStream fs) {
            fs.Lock(0, 1);
        }

        public static double longToDouble(long a_long) {
            return BitConverter.Int64BitsToDouble(a_long);
        }
    
        public static void notify(object obj) {
            Monitor.Pulse(obj);
        }

        public static void notifyAll(object obj){
            Monitor.PulseAll(obj);
        }

        public static com.db4o.reflect.ReflectConstructor serializationConstructor(Type type){
            return new com.db4o.reflect.net.SerializationConstructor(type);
        }

        public static string stackTrace() {
            return new StackTrace(true).ToString();
        }

        public static void threadSetName(Thread thread, string name) {
            thread.Name = name;
        }

        public static string threadGetName(Thread thread) {
            return thread.Name;
        }

        public static void wait(object obj, long timeout) {
            Monitor.Wait(obj, (int)timeout);
        }
        
        static internal object wrapEvaluation(object evaluation) {
        	return (evaluation is com.db4o.query.EvaluationDelegate)
	        	? new EvaluationDelegateWrapper((com.db4o.query.EvaluationDelegate)evaluation)
	        	: evaluation;
        }
        
    }
}

