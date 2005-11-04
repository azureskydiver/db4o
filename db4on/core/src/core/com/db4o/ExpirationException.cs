namespace com.db4o
{
	internal class ExpirationException : j4o.lang.RuntimeException
	{
		public override System.Exception fillInStackTrace()
		{
			return null;
		}

		public override string ToString()
		{
			return "";
		}
	}
}
