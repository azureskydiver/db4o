/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using com.db4o;

namespace j4o.lang
{
	public class IdentityHashCodeProvider
	{
#if NET_2_0 || MONO
		public static int identityHashCode(object obj)
		{
			return System.Runtime.CompilerServices.RuntimeHelpers.GetHashCode(obj);
		}
#else
		public delegate int HashCodeFunction(object o);

		private static HashCodeFunction _hashCode = Compat.getIdentityHashCodeFunction();

		public static int identityHashCode(object obj)
		{
			if (obj == null)
			{
				return 0;
			}
			return _hashCode(obj);
		}
#endif
	}
}