namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class Debug : com.db4o.foundation.Debug4
	{
		public const bool useNIxPaths = true;

		public const bool ixTrees = false;

		public const bool freespace = com.db4o.Deploy.debug ? true : false;

		public const bool xbytes = com.db4o.Debug.freespace ? true : false;

		public const bool freespaceChecker = false;

		public const bool checkSychronization = false;

		public const bool atHome = false;

		public const bool indexAllFields = false;

		public const bool configureAllClasses = indexAllFields;

		public const bool configureAllFields = indexAllFields;

		public const bool weakReferences = true;

		public const bool arrayTypes = true;

		public const bool verbose = false;

		public const bool fakeServer = false;

		internal const bool messages = false;

		public const bool nio = true;

		internal const bool lockFile = true;

		internal const bool longTimeOuts = false;

		internal static com.db4o.YapFile serverStream;

		internal static com.db4o.YapClient clientStream;

		internal static com.db4o.foundation.Queue4 clientMessageQueue;

		internal static com.db4o.foundation.Lock4 clientMessageQueueLock;

		public static void expect(bool cond)
		{
			if (!cond)
			{
				throw new j4o.lang.RuntimeException("Should never happen");
			}
		}

		public static void ensureLock(object obj)
		{
		}

		public static bool exceedsMaximumBlockSize(int a_length)
		{
			if (a_length > com.db4o.YapConst.MAXIMUM_BLOCK_SIZE)
			{
				return true;
			}
			return false;
		}

		public static bool exceedsMaximumArrayEntries(int a_entries, bool a_primitive)
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
