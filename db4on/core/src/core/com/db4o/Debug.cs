namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class Debug : com.db4o.foundation.Debug4
	{
		public const bool indexAllFields = false;

		public const bool queries = false;

		public const bool atHome = false;

		public const bool longTimeOuts = false;

		public const bool freespace = com.db4o.Deploy.debug;

		public const bool xbytes = freespace;

		public const bool freespaceChecker = false;

		public const bool checkSychronization = false;

		public const bool configureAllClasses = indexAllFields;

		public const bool configureAllFields = indexAllFields;

		public const bool weakReferences = true;

		public const bool fakeServer = false;

		public const bool messages = false;

		public const bool nio = true;

		public const bool lockFile = true;

		public static void Expect(bool cond)
		{
			if (!cond)
			{
				throw new System.Exception("Should never happen");
			}
		}

		public static void EnsureLock(object obj)
		{
		}

		public static bool ExceedsMaximumBlockSize(int a_length)
		{
			if (a_length > com.db4o.@internal.Const4.MAXIMUM_BLOCK_SIZE)
			{
				return true;
			}
			return false;
		}

		public static bool ExceedsMaximumArrayEntries(int a_entries, bool a_primitive)
		{
			if (a_entries > (a_primitive ? com.db4o.@internal.Const4.MAXIMUM_ARRAY_ENTRIES_PRIMITIVE
				 : com.db4o.@internal.Const4.MAXIMUM_ARRAY_ENTRIES))
			{
				return true;
			}
			return false;
		}
	}
}
