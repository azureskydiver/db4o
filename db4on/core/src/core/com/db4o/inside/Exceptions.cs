namespace com.db4o.inside
{
	public class Exceptions
	{
		public static void throwRuntimeException(int code)
		{
			throwRuntimeException(code, null, null);
		}

		public static void throwRuntimeException(int code, System.Exception cause)
		{
			throwRuntimeException(code, null, cause);
		}

		public static void throwRuntimeException(int code, string msg)
		{
			throwRuntimeException(code, msg, null);
		}

		public static void throwRuntimeException(int code, string msg, System.Exception cause
			)
		{
			com.db4o.Messages.logErr(com.db4o.Db4o.configure(), code, msg, cause);
			throw new j4o.lang.RuntimeException(com.db4o.Messages.get(code, msg));
		}
	}
}
