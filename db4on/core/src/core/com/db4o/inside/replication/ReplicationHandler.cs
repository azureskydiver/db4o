namespace com.db4o.inside.replication
{
	/// <summary>ReplicationHandler for the new external Replication code</summary>
	/// <exclude></exclude>
	public class ReplicationHandler
	{
		public const int OLD = -1;

		public const int NONE = 0;

		public const int NEW = 1;

		private object _currentObject;

		private long _currentUuidLong;

		private com.db4o.ext.Db4oDatabase _currentProvider;

		private long _currentVersion;

		public virtual void associateObjectWith(object obj, com.db4o.ext.Db4oDatabase provider
			, long uuidLong, long version)
		{
			_currentObject = obj;
			_currentUuidLong = uuidLong;
			_currentProvider = provider;
			_currentVersion = version;
		}

		public virtual long uuidLongFor(object obj)
		{
			if (_currentObject == obj)
			{
				return _currentUuidLong;
			}
			return 0;
		}

		public virtual com.db4o.ext.Db4oDatabase providerFor(object obj)
		{
			if (_currentObject == obj)
			{
				return _currentProvider;
			}
			return null;
		}

		public virtual long versionFor(object obj)
		{
			if (_currentObject == obj)
			{
				return _currentVersion;
			}
			return 0;
		}
	}
}
