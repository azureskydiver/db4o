namespace com.db4o.inside
{
	/// <exclude></exclude>
	public class Exceptions4
	{
		public static void ThrowRuntimeException(int code)
		{
			ThrowRuntimeException(code, null, null);
		}

		public static void ThrowRuntimeException(int code, System.Exception cause)
		{
			ThrowRuntimeException(code, null, cause);
		}

		public static void ThrowRuntimeException(int code, string msg)
		{
			ThrowRuntimeException(code, msg, null);
		}

		public static void ThrowRuntimeException(int code, string msg, System.Exception cause
			)
		{
			com.db4o.Messages.LogErr(com.db4o.Db4o.Configure(), code, msg, cause);
			throw new com.db4o.ext.Db4oException(com.db4o.Messages.Get(code, msg));
		}

		public static void NotSupported()
		{
			ThrowRuntimeException(53);
		}

		public static void CatchAll(System.Exception exc)
		{
			if (exc is com.db4o.ext.Db4oException)
			{
				throw (com.db4o.ext.Db4oException)exc;
			}
		}

		public static j4o.lang.RuntimeException ShouldNeverBeCalled()
		{
			throw new j4o.lang.RuntimeException();
		}

		public static j4o.lang.RuntimeException ShouldNeverHappen()
		{
			throw new j4o.lang.RuntimeException();
		}

		public static j4o.lang.RuntimeException VirtualException()
		{
			throw new j4o.lang.RuntimeException();
		}
	}
}
