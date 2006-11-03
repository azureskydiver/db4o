namespace com.db4o
{
	/// <exclude></exclude>
	public class DTrace
	{
		public const bool enabled = false;

		private static void BreakPoint()
		{
		}

		private static void Configure()
		{
		}

		private static object Init()
		{
			return null;
		}

		private static void TrackEventsWithoutRange()
		{
			_trackEventsWithoutRange = true;
		}

		private DTrace(bool enabled_, bool break_, string tag_, bool log_)
		{
		}

		private bool _enabled;

		private bool _break;

		private bool _log;

		private string _tag;

		private static long[] _rangeStart;

		private static long[] _rangeEnd;

		private static int _rangeCount;

		public static long _eventNr;

		private static long[] _breakEventNrs;

		private static int _breakEventCount;

		private static bool _trackEventsWithoutRange;

		public static com.db4o.DTrace ADD_TO_CLASS_INDEX;

		public static com.db4o.DTrace BIND;

		public static com.db4o.DTrace BTREE_NODE_COMMIT_OR_ROLLBACK;

		public static com.db4o.DTrace BTREE_NODE_REMOVE;

		public static com.db4o.DTrace CANDIDATE_READ;

		public static com.db4o.DTrace CLOSE;

		public static com.db4o.DTrace COLLECT_CHILDREN;

		public static com.db4o.DTrace COMMIT;

		public static com.db4o.DTrace CONTINUESET;

		public static com.db4o.DTrace CREATE_CANDIDATE;

		public static com.db4o.DTrace DELETE;

		public static com.db4o.DTrace DONOTINCLUDE;

		public static com.db4o.DTrace EVALUATE_SELF;

		public static com.db4o.DTrace FILE_FREE;

		public static com.db4o.DTrace FREE;

		public static com.db4o.DTrace FREE_RAM;

		public static com.db4o.DTrace FREE_ON_COMMIT;

		public static com.db4o.DTrace FREE_ON_ROLLBACK;

		public static com.db4o.DTrace GET_SLOT;

		public static com.db4o.DTrace GET_FREESPACE;

		public static com.db4o.DTrace GET_FREESPACE_RAM;

		public static com.db4o.DTrace GET_YAPOBJECT;

		public static com.db4o.DTrace ID_TREE_ADD;

		public static com.db4o.DTrace ID_TREE_REMOVE;

		public static com.db4o.DTrace IO_COPY;

		public static com.db4o.DTrace JUST_SET;

		public static com.db4o.DTrace NEW_INSTANCE;

		public static com.db4o.DTrace PRODUCE_SLOT_CHANGE;

		public static com.db4o.DTrace QUERY_PROCESS;

		public static com.db4o.DTrace READ_ARRAY_WRAPPER;

		public static com.db4o.DTrace READ_BYTES;

		public static com.db4o.DTrace READ_ID;

		public static com.db4o.DTrace READ_SLOT;

		public static com.db4o.DTrace REFERENCE_REMOVED;

		public static com.db4o.DTrace REGULAR_SEEK;

		public static com.db4o.DTrace REMOVE_FROM_CLASS_INDEX;

		public static com.db4o.DTrace REREAD_OLD_UUID;

		public static com.db4o.DTrace SLOT_SET_POINTER;

		public static com.db4o.DTrace SLOT_DELETE;

		public static com.db4o.DTrace SLOT_FREE_ON_COMMIT;

		public static com.db4o.DTrace SLOT_FREE_ON_ROLLBACK_ID;

		public static com.db4o.DTrace SLOT_FREE_ON_ROLLBACK_ADDRESS;

		public static com.db4o.DTrace TRANS_COMMIT;

		public static com.db4o.DTrace TRANS_DONT_DELETE;

		public static com.db4o.DTrace TRANS_DELETE;

		public static com.db4o.DTrace TRANS_FLUSH;

		public static com.db4o.DTrace YAPCLASS_BY_ID;

		public static com.db4o.DTrace YAPCLASS_INIT;

		public static com.db4o.DTrace YAPMETA_SET_ID;

		public static com.db4o.DTrace YAPMETA_WRITE;

		public static com.db4o.DTrace WRITE_BYTES;

		public static com.db4o.DTrace WRITE_POINTER;

		public static com.db4o.DTrace WRITE_XBYTES;

		public static com.db4o.DTrace WRITE_UPDATE_DELETE_MEMBERS;

		public static readonly object forInit = Init();

		private static com.db4o.DTrace all;

		private static int current;

		public virtual void Log()
		{
		}

		public virtual void Log(long p)
		{
		}

		public virtual void LogInfo(string info)
		{
		}

		public virtual void Log(long p, string info)
		{
		}

		public virtual void LogLength(long start, long length)
		{
		}

		public virtual void LogEnd(long start, long end)
		{
		}

		public virtual void LogEnd(long start, long end, string info)
		{
		}

		public static void AddRange(long pos)
		{
		}

		public static void AddRangeWithLength(long start, long length)
		{
		}

		public static void AddRangeWithEnd(long start, long end)
		{
		}

		private static void BreakOnEvent(long eventNr)
		{
		}

		private string FormatInt(long i, int len)
		{
			return null;
		}

		private string FormatInt(long i)
		{
			return null;
		}

		private static void TurnAllOffExceptFor(com.db4o.DTrace[] these)
		{
		}
	}
}
