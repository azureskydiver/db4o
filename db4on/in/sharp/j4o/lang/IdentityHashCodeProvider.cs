/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;

namespace j4o.lang
{
	public class IdentityHashCodeProvider
	{
#if !CF_1_0 && !CF_2_0
		public static int identityHashCode(object obj)
		{
			return System.Runtime.CompilerServices.RuntimeHelpers.GetHashCode(obj);
		}
#else
		public static int identityHashCode(object obj)
		{
			if (obj == null) return 0;
			return (int) _hashMethod.Invoke(null, new object[] { obj });
		}

		private static MethodInfo _hashMethod = getIdentityHashCodeMethod();

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
			catch (Exception e)
			{
			}

			// We may be running the CF app on .NET Framework 1.1
			// for profiling, let's give that a chance
			try
			{
				Type t = assembly.GetType(
					"System.Runtime.CompilerServices.RuntimeHelpers");
				return t.GetMethod(
					"GetHashCode",
					BindingFlags.Public |
					BindingFlags.Static);
			}
			catch (Exception e)
			{
			}

			// and for completeness sake, let's provide .NET Framework 1.0
			// compliance also so we can debug the CF app there too
			return (typeof(object)).GetMethod("GetHashCode");

			return null;
		}
#endif
	}
}