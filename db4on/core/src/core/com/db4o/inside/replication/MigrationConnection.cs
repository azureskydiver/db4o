namespace com.db4o.inside.replication
{
	/// <exclude></exclude>
	public class MigrationConnection
	{
		public readonly com.db4o.YapStream _peerA;

		public readonly com.db4o.YapStream _peerB;

		private readonly com.db4o.foundation.Hashtable4 _referenceMap;

		private readonly com.db4o.foundation.Hashtable4 _identityMap;

		public MigrationConnection(com.db4o.YapStream peerA, com.db4o.YapStream peerB)
		{
			_referenceMap = new com.db4o.foundation.Hashtable4(1);
			_identityMap = new com.db4o.foundation.Hashtable4(1);
			_peerA = peerA;
			_peerB = peerB;
		}

		public virtual void mapReference(object obj, com.db4o.YapObject _ref)
		{
			_referenceMap.put(j4o.lang.JavaSystem.identityHashCode(obj), _ref);
		}

		public virtual void mapIdentity(object obj, object otherObj)
		{
			_identityMap.put(j4o.lang.JavaSystem.identityHashCode(obj), otherObj);
		}

		public virtual com.db4o.YapObject referenceFor(object obj)
		{
			int hcode = j4o.lang.JavaSystem.identityHashCode(obj);
			com.db4o.YapObject _ref = (com.db4o.YapObject)_referenceMap.get(hcode);
			_referenceMap.remove(hcode);
			return _ref;
		}

		public virtual object identityFor(object obj)
		{
			int hcode = j4o.lang.JavaSystem.identityHashCode(obj);
			return _identityMap.get(hcode);
		}

		public virtual void terminate()
		{
			_peerA.migrateFrom(null);
			_peerB.migrateFrom(null);
		}

		public virtual com.db4o.YapStream peer(com.db4o.YapStream stream)
		{
			if (_peerA == stream)
			{
				return _peerB;
			}
			return _peerA;
		}
	}
}
