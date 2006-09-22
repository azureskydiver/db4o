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
			_referenceMap = new com.db4o.foundation.Hashtable4();
			_identityMap = new com.db4o.foundation.Hashtable4();
			_peerA = peerA;
			_peerB = peerB;
		}

		public virtual void MapReference(object obj, com.db4o.YapObject @ref)
		{
			_referenceMap.Put(j4o.lang.JavaSystem.IdentityHashCode(obj), @ref);
		}

		public virtual void MapIdentity(object obj, object otherObj)
		{
			_identityMap.Put(j4o.lang.JavaSystem.IdentityHashCode(obj), otherObj);
		}

		public virtual com.db4o.YapObject ReferenceFor(object obj)
		{
			int hcode = j4o.lang.JavaSystem.IdentityHashCode(obj);
			com.db4o.YapObject @ref = (com.db4o.YapObject)_referenceMap.Get(hcode);
			_referenceMap.Remove(hcode);
			return @ref;
		}

		public virtual object IdentityFor(object obj)
		{
			int hcode = j4o.lang.JavaSystem.IdentityHashCode(obj);
			return _identityMap.Get(hcode);
		}

		public virtual void Terminate()
		{
			_peerA.MigrateFrom(null);
			_peerB.MigrateFrom(null);
		}

		public virtual com.db4o.YapStream Peer(com.db4o.YapStream stream)
		{
			if (_peerA == stream)
			{
				return _peerB;
			}
			return _peerA;
		}
	}
}
