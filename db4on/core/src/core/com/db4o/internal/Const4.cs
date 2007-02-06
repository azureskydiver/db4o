namespace com.db4o.@internal
{
	/// <exclude>TODO: Split into separate enums with defined range and values.</exclude>
	public sealed class Const4
	{
		public static readonly object initMe = Init();

		public const byte YAPFILEVERSION = 4;

		public const byte YAPBEGIN = (byte)'{';

		public const byte YAPFILE = (byte)'Y';

		public const byte YAPID = (byte)'#';

		public const byte YAPPOINTER = (byte)'>';

		public const byte YAPCLASSCOLLECTION = (byte)'A';

		public const byte YAPCLASS = (byte)'C';

		public const byte YAPFIELD = (byte)'F';

		public const byte YAPOBJECT = (byte)'O';

		public const byte YAPARRAY = (byte)'N';

		public const byte YAPARRAYN = (byte)'Z';

		public const byte YAPINDEX = (byte)'X';

		public const byte YAPSTRING = (byte)'S';

		public const byte YAPLONG = (byte)'l';

		public const byte YAPINTEGER = (byte)'i';

		public const byte YAPBOOLEAN = (byte)'=';

		public const byte YAPDOUBLE = (byte)'d';

		public const byte YAPBYTE = (byte)'b';

		public const byte YAPSHORT = (byte)'s';

		public const byte YAPCHAR = (byte)'c';

		public const byte YAPFLOAT = (byte)'f';

		public const byte YAPEND = (byte)'}';

		public const byte YAPNULL = (byte)'0';

		public const byte BTREE = (byte)'T';

		public const byte BTREE_NODE = (byte)'B';

		public const byte HEADER = (byte)'H';

		public const int IDENTIFIER_LENGTH = (com.db4o.Deploy.debug && com.db4o.Deploy.identifiers
			) ? 1 : 0;

		public const int BRACKETS_BYTES = (com.db4o.Deploy.debug && com.db4o.Deploy.brackets
			) ? 1 : 0;

		public const int BRACKETS_LENGTH = BRACKETS_BYTES * 2;

		public const int LEADING_LENGTH = IDENTIFIER_LENGTH + BRACKETS_BYTES;

		public const int ADDED_LENGTH = IDENTIFIER_LENGTH + BRACKETS_LENGTH;

		public const int SHORT_BYTES = 2;

		public const int INTEGER_BYTES = (com.db4o.Deploy.debug && com.db4o.Deploy.debugLong
			) ? 11 : 4;

		public const int LONG_BYTES = (com.db4o.Deploy.debug && com.db4o.Deploy.debugLong
			) ? 20 : 8;

		public const int CHAR_BYTES = 2;

		public const int UNSPECIFIED = int.MinValue + 100;

		public const int INT_LENGTH = INTEGER_BYTES + ADDED_LENGTH;

		public const int ID_LENGTH = INT_LENGTH;

		public const int LONG_LENGTH = LONG_BYTES + ADDED_LENGTH;

		public const int WRITE_LOOP = (INTEGER_BYTES - 1) * 8;

		public const int OBJECT_LENGTH = ADDED_LENGTH;

		public const int POINTER_LENGTH = (INT_LENGTH * 2) + ADDED_LENGTH;

		public const int MESSAGE_LENGTH = INT_LENGTH * 2 + 1;

		public const byte SYSTEM_TRANS = (byte)'s';

		public const byte USER_TRANS = (byte)'u';

		public const byte XBYTE = (byte)'X';

		public const int IGNORE_ID = -99999;

		public const int PRIMITIVE = -2000000000;

		public const int TYPE_SIMPLE = 1;

		public const int TYPE_CLASS = 2;

		public const int TYPE_ARRAY = 3;

		public const int TYPE_NARRAY = 4;

		public const int NONE = 0;

		public const int STATE = 1;

		public const int ACTIVATION = 2;

		public const int TRANSIENT = -1;

		public const int ADD_MEMBERS_TO_ID_TREE_ONLY = 0;

		public const int ADD_TO_ID_TREE = 1;

		public const byte ISO8859 = (byte)1;

		public const byte UNICODE = (byte)2;

		public const int LOCK_TIME_INTERVAL = 1000;

		public const int SERVER_SOCKET_TIMEOUT = com.db4o.Debug.longTimeOuts ? 1000000 : 
			5000;

		public const int CLIENT_SOCKET_TIMEOUT = 300000;

		public const int CONNECTION_TIMEOUT = com.db4o.Debug.longTimeOuts ? 1000000 : 180000;

		public const int MAXIMUM_BLOCK_SIZE = 70000000;

		public const int MAXIMUM_ARRAY_ENTRIES = 7000000;

		public const int MAXIMUM_ARRAY_ENTRIES_PRIMITIVE = MAXIMUM_ARRAY_ENTRIES * 100;

		public static j4o.lang.Class CLASS_COMPARE;

		public static j4o.lang.Class CLASS_DB4OTYPE;

		public static j4o.lang.Class CLASS_DB4OTYPEIMPL;

		public static j4o.lang.Class CLASS_INTERNAL;

		public static j4o.lang.Class CLASS_UNVERSIONED;

		public static j4o.lang.Class CLASS_OBJECT;

		public static j4o.lang.Class CLASS_OBJECTCONTAINER;

		public static j4o.lang.Class CLASS_REPLICATIONRECORD;

		public static j4o.lang.Class CLASS_STATICFIELD;

		public static j4o.lang.Class CLASS_STATICCLASS;

		public static j4o.lang.Class CLASS_TRANSIENTCLASS;

		public static readonly string EMBEDDED_CLIENT_USER = "embedded client";

		public const int CLEAN = 0;

		public const int ACTIVE = 1;

		public const int PROCESSING = 2;

		public const int CACHED_DIRTY = 3;

		public const int CONTINUE = 4;

		public const int STATIC_FIELDS_STORED = 5;

		public const int CHECKED_CHANGES = 6;

		public const int DEAD = 7;

		public const int READING = 8;

		public const int UNCHECKED = 0;

		public const int NO = -1;

		public const int YES = 1;

		public const int DEFAULT = 0;

		public const int UNKNOWN = 0;

		public const int OLD = -1;

		public const int NEW = 1;

		public static readonly com.db4o.@internal.UnicodeStringIO stringIO = new com.db4o.@internal.UnicodeStringIO
			();

		private static object Init()
		{
			CLASS_OBJECT = j4o.lang.JavaSystem.GetClassForObject(new object());
			CLASS_COMPARE = j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.config.Compare)
				);
			CLASS_DB4OTYPE = j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.types.Db4oType)
				);
			CLASS_DB4OTYPEIMPL = j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.@internal.Db4oTypeImpl)
				);
			CLASS_INTERNAL = j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.Internal4));
			CLASS_UNVERSIONED = j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.types.Unversioned)
				);
			CLASS_OBJECTCONTAINER = j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.ObjectContainer)
				);
			CLASS_REPLICATIONRECORD = j4o.lang.JavaSystem.GetClassForObject(new com.db4o.ReplicationRecord
				());
			CLASS_STATICFIELD = j4o.lang.JavaSystem.GetClassForObject(new com.db4o.StaticField
				());
			CLASS_STATICCLASS = j4o.lang.JavaSystem.GetClassForObject(new com.db4o.StaticClass
				());
			CLASS_TRANSIENTCLASS = j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.types.TransientClass)
				);
			return null;
		}

		public static readonly j4o.lang.Class[] ESSENTIAL_CLASSES = { CLASS_STATICFIELD, 
			CLASS_STATICCLASS };

		public static readonly string VIRTUAL_FIELD_PREFIX = "v4o";

		public const int MAX_STACK_DEPTH = 20;
	}
}
