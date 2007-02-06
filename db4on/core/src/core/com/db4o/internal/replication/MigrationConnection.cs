namespace com.db4o.@internal.replication
{
	/// <exclude></exclude>
	public class MigrationConnection
	{
		public readonly com.db4o.@internal.ObjectContainerBase _peerA;

		public readonly com.db4o.@internal.ObjectContainerBase _peerB;

		private readonly com.db4o.foundation.Hashtable4 _referenceMap;

		private readonly com.db4o.foundation.Hashtable4 _identityMap;

		public MigrationConnection(com.db4o.@internal.ObjectContainerBase peerA, com.db4o.@internal.ObjectContainerBase
			 peerB)
		{
			_referenceMap = new com.db4o.foundation.Hashtable4();
			_identityMap = new com.db4o.foundation.Hashtable4();
			_peerA = peerA;
			_peerB = peerB;
		}

		public virtual void MapReference(object obj, com.db4o.@internal.ObjectReference @ref
			)
		{
			_referenceMap.Put(j4o.lang.JavaSystem.IdentityHashCode(obj), @ref);
		}

		public virtual void MapIdentity(object obj, object otherObj)
		{
			_identityMap.Put(j4o.lang.JavaSystem.IdentityHashCode(obj), otherObj);
		}

		public virtual com.db4o.@internal.ObjectReference ReferenceFor(object obj)
		{
			int hcode = j4o.lang.JavaSystem.IdentityHashCode(obj);
			com.db4o.@internal.ObjectReference @ref = (com.db4o.@internal.ObjectReference)_referenceMap
				.Get(hcode);
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

		public virtual com.db4o.@internal.ObjectContainerBase Peer(com.db4o.@internal.ObjectContainerBase
			 stream)
		{
			if (_peerA == stream)
			{
				return _peerB;
			}
			return _peerA;
		}
	}
}
