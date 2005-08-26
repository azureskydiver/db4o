/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;
using System.Collections;
using System.Reflection;
using System.Threading;

namespace System.Runtime.CompilerServices 
{
	class IsVolatile 
	{
	}
}

namespace com.db4o 
{

	/// <exclude />
	public class Compat
	{		
		static MethodInfo _hashMethod = getIdentityHashCodeMethod();
		
		private static MethodInfo getIdentityHashCodeMethod() 
		{
			Assembly assembly = typeof(object).Assembly;

			// CompactFramework
			try 
			{
				Type t = assembly.GetType("System.PInvoke.EE");
				return t.GetMethod(
					"Object_GetHashCode",
					BindingFlags.Public |
					BindingFlags.NonPublic |
					BindingFlags.Static);
			} 
			catch(Exception e) 
			{
			}
			
			return null;
		}
		
		private static int _identityHashCode(object o) 
		{
			return (int)_hashMethod.Invoke(null, new object[] { o });
		}
		
		public static j4o.lang.IdentityHashCodeProvider.HashCodeFunction getIdentityHashCodeFunction() 
		{
			return new j4o.lang.IdentityHashCodeProvider.HashCodeFunction(_identityHashCode);
		}

		public static void addShutDownHook(EventHandler handler)
		{
		}

		public static Stream buffer(Stream stream)
		{
			return stream;
		}

		public static Stream buffer(Stream stream, int bufferSize)
		{
			return stream;
		}

		public static bool compact()
		{
			return true;
		}

		public static long doubleToLong(double a_double)
		{
			return 0;
		}

		public static int getArrayRank(Type type)
		{
			if(type.Name.EndsWith(",]"))
			{
				return 2;
			}
			return 1;
		}

		public static bool isDirectory(string path)
		{
			return false;
		}

		public static void lockFileStream(FileStream fs)
		{
		}

		public static double longToDouble(long a_long)
		{
			return 0;
		}
    
		public static void notify(object obj) 
		{
		}

		public static void notifyAll(object obj)
		{
		}

		public static com.db4o.reflect.ReflectConstructor serializationConstructor(Type type)
		{
			return null;
		}

		public static string stackTrace()
		{
			return "";
		}

		public static void threadSetName(Thread thread, string name)
		{
		}

		public static string threadGetName(Thread thread)
		{
			return "";
		}

		public static void wait(object obj, long timeout) 
		{
		}
        
		static internal object wrapEvaluation(object evaluation) 
		{
			// FIXME: How to better support EvaluationDelegate on the CompactFramework?
			return evaluation;
		}
        
	}
}
