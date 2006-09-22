namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class Debug : com.db4o.foundation.Debug4
	{
		public const bool indexAllFields = false;

		public const bool queries = false;

		public const bool atHome = false;

		public static bool longTimeOuts = false;

		public const bool freespace = com.db4o.Deploy.debug ? true : false;

		public const bool xbytes = freespace ? true : false;

		public const bool freespaceChecker = false;

		public const bool checkSychronization = false;

		public const bool configureAllClasses = indexAllFields;

		public const bool configureAllFields = indexAllFields;

		public const bool weakReferences = true;

		public const bool fakeServer = false;

		internal const bool messages = false;

		public const bool nio = true;

		internal const bool lockFile = true;

		internal static com.db4o.YapFile serverStream;

		internal static com.db4o.YapClient clientStream;

		internal static com.db4o.foundation.Queue4 clientMessageQueue;

		internal static com.db4o.foundation.Lock4 clientMessageQueueLock;

		public static void Expect(bool cond)
		{
			if (!cond)
			{
				throw new j4o.lang.RuntimeException("Should never happen");
			}
		}

		public static void EnsureLock(object obj)
		{
		}

		public static bool ExceedsMaximumBlockSize(int a_length)
		{
			if (a_length > com.db4o.YapConst.MAXIMUM_BLOCK_SIZE)
			{
				return true;
			}
			return false;
		}

		public static bool ExceedsMaximumArrayEntries(int a_entries, bool a_primitive)
		{
			if (a_entries > (a_primitive ? com.db4o.YapConst.MAXIMUM_ARRAY_ENTRIES_PRIMITIVE : 
				com.db4o.YapConst.MAXIMUM_ARRAY_ENTRIES))
			{
				return true;
			}
			return false;
		}
	}
}
