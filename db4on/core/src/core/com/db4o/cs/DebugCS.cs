namespace com.db4o.cs
{
	/// <exclude></exclude>
	public class DebugCS
	{
		public static com.db4o.cs.YapClient clientStream;

		public static com.db4o.YapFile serverStream;

		public static com.db4o.foundation.Queue4 clientMessageQueue;

		public static com.db4o.foundation.Lock4 clientMessageQueueLock;
	}
}
