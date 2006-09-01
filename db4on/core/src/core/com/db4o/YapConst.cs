namespace com.db4o
{
	/// <exclude></exclude>
	public sealed class YapConst
	{
		internal static readonly object initMe = Init();

		public const byte YAPFILEVERSION = 4;

		public const byte YAPBEGIN = (byte)'{';

		public const byte YAPFILE = (byte)'Y';

		internal const byte YAPID = (byte)'#';

		internal const byte YAPPOINTER = (byte)'>';

		public const byte YAPCLASSCOLLECTION = (byte)'A';

		public const byte YAPCLASS = (byte)'C';

		internal const byte YAPFIELD = (byte)'F';

		public const byte YAPOBJECT = (byte)'O';

		internal const byte YAPARRAY = (byte)'N';

		internal const byte YAPARRAYN = (byte)'Z';

		public const byte YAPINDEX = (byte)'X';

		public const byte YAPSTRING = (byte)'S';

		internal const byte YAPLONG = (byte)'l';

		internal const byte YAPINTEGER = (byte)'i';

		internal const byte YAPBOOLEAN = (byte)'=';

		internal const byte YAPDOUBLE = (byte)'d';

		internal const byte YAPBYTE = (byte)'b';

		internal const byte YAPSHORT = (byte)'s';

		internal const byte YAPCHAR = (byte)'c';

		internal const byte YAPFLOAT = (byte)'f';

		internal const byte YAPEND = (byte)'}';

		internal const byte YAPNULL = (byte)'0';

		public const byte BTREE = (byte)'T';

		public const byte BTREE_NODE = (byte)'B';

		internal const int IDENTIFIER_LENGTH = (com.db4o.Deploy.debug && com.db4o.Deploy.
			identifiers) ? 1 : 0;

		public const int BRACKETS_BYTES = (com.db4o.Deploy.debug && com.db4o.Deploy.brackets
			) ? 1 : 0;

		internal const int BRACKETS_LENGTH = BRACKETS_BYTES * 2;

		public const int LEADING_LENGTH = IDENTIFIER_LENGTH + BRACKETS_BYTES;

		internal const int ADDED_LENGTH = IDENTIFIER_LENGTH + BRACKETS_LENGTH;

		internal const int SHORT_BYTES = 2;

		internal const int INTEGER_BYTES = (com.db4o.Deploy.debug && com.db4o.Deploy.debugLong
			) ? 11 : 4;

		internal const int LONG_BYTES = (com.db4o.Deploy.debug && com.db4o.Deploy.debugLong
			) ? 20 : 8;

		internal const int CHAR_BYTES = 2;

		internal const int UNSPECIFIED = int.MinValue + 100;

		public const int INT_LENGTH = INTEGER_BYTES + ADDED_LENGTH;

		public const int ID_LENGTH = INT_LENGTH;

		internal const int LONG_LENGTH = LONG_BYTES + ADDED_LENGTH;

		internal const int WRITE_LOOP = (INTEGER_BYTES - 1) * 8;

		public const int OBJECT_LENGTH = ADDED_LENGTH;

		public const int POINTER_LENGTH = (INT_LENGTH * 2) + ADDED_LENGTH;

		internal const int MESSAGE_LENGTH = INT_LENGTH * 2 + 1;

		internal const byte SYSTEM_TRANS = (byte)'s';

		internal const byte USER_TRANS = (byte)'u';

		internal const byte XBYTE = (byte)'X';

		public const int IGNORE_ID = -99999;

		internal const int PRIMITIVE = -2000000000;

		internal const int TYPE_SIMPLE = 1;

		internal const int TYPE_CLASS = 2;

		internal const int TYPE_ARRAY = 3;

		internal const int TYPE_NARRAY = 4;

		internal const int NONE = 0;

		internal const int STATE = 1;

		internal const int ACTIVATION = 2;

		internal const int TRANSIENT = -1;

		internal const int ADD_MEMBERS_TO_ID_TREE_ONLY = 0;

		internal const int ADD_TO_ID_TREE = 1;

		internal const byte ISO8859 = (byte)1;

		internal const byte UNICODE = (byte)2;

		internal const int LOCK_TIME_INTERVAL = 1000;

		internal static readonly int SERVER_SOCKET_TIMEOUT = com.db4o.Debug.longTimeOuts ? 
			1000000 : 5000;

		internal const int CLIENT_SOCKET_TIMEOUT = 300000;

		internal static readonly int CONNECTION_TIMEOUT = com.db4o.Debug.longTimeOuts ? 1000000
			 : 180000;

		internal const int PREFETCH_ID_COUNT = 10;

		internal const int PREFETCH_OBJECT_COUNT = 10;

		public const int MAXIMUM_BLOCK_SIZE = 70000000;

		internal const int MAXIMUM_ARRAY_ENTRIES = 7000000;

		internal const int MAXIMUM_ARRAY_ENTRIES_PRIMITIVE = MAXIMUM_ARRAY_ENTRIES * 100;

		internal static j4o.lang.Class CLASS_COMPARE;

		internal static j4o.lang.Class CLASS_DB4OTYPE;

		internal static j4o.lang.Class CLASS_DB4OTYPEIMPL;

		internal static j4o.lang.Class CLASS_INTERNAL;

		internal static j4o.lang.Class CLASS_UNVERSIONED;

		internal static j4o.lang.Class CLASS_METACLASS;

		internal static j4o.lang.Class CLASS_METAFIELD;

		internal static j4o.lang.Class CLASS_METAINDEX;

		public static j4o.lang.Class CLASS_OBJECT;

		internal static j4o.lang.Class CLASS_OBJECTCONTAINER;

		internal static j4o.lang.Class CLASS_REPLICATIONRECORD;

		internal static j4o.lang.Class CLASS_STATICFIELD;

		internal static j4o.lang.Class CLASS_STATICCLASS;

		internal static j4o.lang.Class CLASS_TRANSIENTCLASS;

		internal static readonly string EMBEDDED_CLIENT_USER = "embedded client";

		internal const int CLEAN = 0;

		internal const int ACTIVE = 1;

		internal const int PROCESSING = 2;

		internal const int CACHED_DIRTY = 3;

		internal const int CONTINUE = 4;

		internal const int STATIC_FIELDS_STORED = 5;

		internal const int CHECKED_CHANGES = 6;

		internal const int DEAD = 7;

		internal const int READING = 8;

		internal const int UNCHECKED = 0;

		public const int NO = -1;

		public const int YES = 1;

		public const int DEFAULT = 0;

		public const int UNKNOWN = 0;

		public const int OLD = -1;

		public const int NEW = 1;

		internal static readonly com.db4o.YapStringIOUnicode stringIO = new com.db4o.YapStringIOUnicode
			();

		private static object Init()
		{
			CLASS_OBJECT = j4o.lang.Class.GetClassForObject(new object());
			CLASS_COMPARE = Db4oClass("config.Compare");
			CLASS_DB4OTYPE = Db4oClass("types.Db4oType");
			CLASS_DB4OTYPEIMPL = Db4oClass("Db4oTypeImpl");
			CLASS_INTERNAL = Db4oClass("Internal4");
			CLASS_UNVERSIONED = Db4oClass("types.Unversioned");
			CLASS_METACLASS = j4o.lang.Class.GetClassForObject(new com.db4o.MetaClass());
			CLASS_METAFIELD = j4o.lang.Class.GetClassForObject(new com.db4o.MetaField());
			CLASS_METAINDEX = j4o.lang.Class.GetClassForObject(new com.db4o.MetaIndex());
			CLASS_OBJECTCONTAINER = Db4oClass("ObjectContainer");
			CLASS_REPLICATIONRECORD = j4o.lang.Class.GetClassForObject(new com.db4o.ReplicationRecord
				());
			CLASS_STATICFIELD = j4o.lang.Class.GetClassForObject(new com.db4o.StaticField());
			CLASS_STATICCLASS = j4o.lang.Class.GetClassForObject(new com.db4o.StaticClass());
			CLASS_TRANSIENTCLASS = Db4oClass("types.TransientClass");
			return null;
		}

		private static j4o.lang.Class Db4oClass(string name)
		{
			return ClassForName("com.db4o." + name);
		}

		private static j4o.lang.Class ClassForName(string name)
		{
			try
			{
				return j4o.lang.Class.ForName(name);
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		internal static readonly j4o.lang.Class[] ESSENTIAL_CLASSES = { CLASS_METAINDEX, 
			CLASS_METAFIELD, CLASS_METACLASS, CLASS_STATICFIELD, CLASS_STATICCLASS };

		public static readonly string VIRTUAL_FIELD_PREFIX = "v4o";

		public const int MAX_STACK_DEPTH = 100;
	}
}
