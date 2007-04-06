using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4o.Internal
{
	/// <exclude></exclude>
	public class Exceptions4
	{
		public static void ThrowRuntimeException(int code)
		{
			ThrowRuntimeException(code, null, null);
		}

		public static void ThrowRuntimeException(int code, Exception cause)
		{
			ThrowRuntimeException(code, null, cause);
		}

		public static void ThrowRuntimeException(int code, string msg)
		{
			ThrowRuntimeException(code, msg, null);
		}

		public static void ThrowRuntimeException(int code, string msg, Exception cause)
		{
			ThrowRuntimeException(code, msg, cause, true);
		}

		public static void ThrowRuntimeException(int code, string msg, Exception cause, bool
			 doLog)
		{
			if (doLog)
			{
				Db4objects.Db4o.Internal.Messages.LogErr(Db4oFactory.Configure(), code, msg, cause
					);
			}
			throw new Db4oException(Db4objects.Db4o.Internal.Messages.Get(code, msg));
		}

		/// <deprecated>Use com.db4o.foundation.NotSupportedException instead</deprecated>
		public static void NotSupported()
		{
			ThrowRuntimeException(53);
		}

		public static void CatchAllExceptDb4oException(Exception exc)
		{
			if (exc is Db4oException)
			{
				throw (Db4oException)exc;
			}
		}

		public static Exception ShouldNeverBeCalled()
		{
			throw new Exception();
		}

		public static void ShouldNeverHappen()
		{
			throw new Exception();
		}

		public static Exception VirtualException()
		{
			throw new Exception();
		}
	}
}
