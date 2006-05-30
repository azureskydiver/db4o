namespace com.db4o
{
	internal class LongJumpOutException : j4o.lang.RuntimeException
	{
		public override System.Exception FillInStackTrace()
		{
			return null;
		}
	}
}
