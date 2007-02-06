namespace com.db4o
{
	/// <summary>
	/// tracks the version of the last replication between
	/// two Objectcontainers.
	/// </summary>
	/// <remarks>
	/// tracks the version of the last replication between
	/// two Objectcontainers.
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class ReplicationRecord : com.db4o.Internal4
	{
		public com.db4o.ext.Db4oDatabase _youngerPeer;

		public com.db4o.ext.Db4oDatabase _olderPeer;

		public long _version;

		public ReplicationRecord()
		{
		}

		public ReplicationRecord(com.db4o.ext.Db4oDatabase younger, com.db4o.ext.Db4oDatabase
			 older)
		{
			_youngerPeer = younger;
			_olderPeer = older;
		}

		public virtual void SetVersion(long version)
		{
			_version = version;
		}

		public virtual void Store(com.db4o.@internal.ObjectContainerBase stream)
		{
			stream.ShowInternalClasses(true);
			com.db4o.@internal.Transaction ta = stream.CheckTransaction(null);
			stream.SetAfterReplication(ta, this, 1, false);
			stream.Commit();
			stream.ShowInternalClasses(false);
		}

		public static com.db4o.ReplicationRecord BeginReplication(com.db4o.@internal.Transaction
			 transA, com.db4o.@internal.Transaction transB)
		{
			com.db4o.@internal.ObjectContainerBase peerA = transA.Stream();
			com.db4o.@internal.ObjectContainerBase peerB = transB.Stream();
			com.db4o.ext.Db4oDatabase dbA = peerA.Identity();
			com.db4o.ext.Db4oDatabase dbB = peerB.Identity();
			dbB.Bind(transA);
			dbA.Bind(transB);
			com.db4o.ext.Db4oDatabase younger = null;
			com.db4o.ext.Db4oDatabase older = null;
			if (dbA.IsOlderThan(dbB))
			{
				younger = dbB;
				older = dbA;
			}
			else
			{
				younger = dbA;
				older = dbB;
			}
			com.db4o.ReplicationRecord rrA = QueryForReplicationRecord(peerA, younger, older);
			com.db4o.ReplicationRecord rrB = QueryForReplicationRecord(peerB, younger, older);
			if (rrA == null)
			{
				if (rrB == null)
				{
					return new com.db4o.ReplicationRecord(younger, older);
				}
				rrB.Store(peerA);
				return rrB;
			}
			if (rrB == null)
			{
				rrA.Store(peerB);
				return rrA;
			}
			if (rrA != rrB)
			{
				peerB.ShowInternalClasses(true);
				int id = peerB.GetID1(rrB);
				peerB.Bind1(transB, rrA, id);
				peerB.ShowInternalClasses(false);
			}
			return rrA;
		}

		public static com.db4o.ReplicationRecord QueryForReplicationRecord(com.db4o.@internal.ObjectContainerBase
			 stream, com.db4o.ext.Db4oDatabase younger, com.db4o.ext.Db4oDatabase older)
		{
			com.db4o.ReplicationRecord res = null;
			stream.ShowInternalClasses(true);
			com.db4o.query.Query q = stream.Query();
			q.Constrain(com.db4o.@internal.Const4.CLASS_REPLICATIONRECORD);
			q.Descend("_youngerPeer").Constrain(younger).Identity();
			q.Descend("_olderPeer").Constrain(older).Identity();
			com.db4o.ObjectSet objectSet = q.Execute();
			if (objectSet.HasNext())
			{
				res = (com.db4o.ReplicationRecord)objectSet.Next();
			}
			stream.ShowInternalClasses(false);
			return res;
		}
	}
}
