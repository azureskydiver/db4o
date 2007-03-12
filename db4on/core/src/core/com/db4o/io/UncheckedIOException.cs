namespace com.db4o.io
{
	[System.Serializable]
	public class UncheckedIOException : com.db4o.foundation.Db4oRuntimeException
	{
		public UncheckedIOException() : base()
		{
		}

		public UncheckedIOException(string msg, System.Exception cause) : base(msg, cause
			)
		{
		}

		public UncheckedIOException(string msg) : base(msg)
		{
		}

		public UncheckedIOException(System.Exception cause) : base(cause)
		{
		}
	}
}
