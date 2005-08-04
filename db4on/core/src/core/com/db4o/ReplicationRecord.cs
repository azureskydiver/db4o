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
	public class ReplicationRecord : com.db4o.Internal
	{
		public com.db4o.ext.Db4oDatabase _youngerPeer;

		public com.db4o.ext.Db4oDatabase _olderPeer;

		public long _version;

		public ReplicationRecord()
		{
		}

		private ReplicationRecord(com.db4o.ext.Db4oDatabase younger, com.db4o.ext.Db4oDatabase
			 older)
		{
			_youngerPeer = younger;
			_olderPeer = older;
		}

		public virtual void setVersion(long version)
		{
			_version = version;
		}

		public virtual void store(com.db4o.YapStream stream)
		{
			stream.showInternalClasses(true);
			com.db4o.Transaction ta = stream.checkTransaction(null);
			stream.setAfterReplication(ta, this, 1, false);
			stream.commit();
			stream.showInternalClasses(false);
		}

		public static com.db4o.ReplicationRecord beginReplication(com.db4o.Transaction transA
			, com.db4o.Transaction transB)
		{
			com.db4o.YapStream peerA = transA.i_stream;
			com.db4o.YapStream peerB = transB.i_stream;
			com.db4o.ext.Db4oDatabase dbA = peerA.identity();
			com.db4o.ext.Db4oDatabase dbB = peerB.identity();
			transA.ensureDb4oDatabase(dbB);
			transB.ensureDb4oDatabase(dbA);
			com.db4o.ext.Db4oDatabase younger = null;
			com.db4o.ext.Db4oDatabase older = null;
			if (dbA.isOlderThan(dbB))
			{
				younger = dbB;
				older = dbA;
			}
			else
			{
				younger = dbA;
				older = dbB;
			}
			com.db4o.ReplicationRecord rrA = queryForReplicationRecord(peerA, younger, older);
			com.db4o.ReplicationRecord rrB = queryForReplicationRecord(peerB, younger, older);
			if (rrA == null)
			{
				if (rrB == null)
				{
					return new com.db4o.ReplicationRecord(younger, older);
				}
				rrB.store(peerA);
				return rrB;
			}
			if (rrB == null)
			{
				rrA.store(peerB);
				return rrA;
			}
			if (rrA != rrB)
			{
				peerB.showInternalClasses(true);
				int id = peerB.getID1(transB, rrB);
				peerB.bind1(transB, rrA, id);
				peerB.showInternalClasses(false);
			}
			return rrA;
		}

		private static com.db4o.ReplicationRecord queryForReplicationRecord(com.db4o.YapStream
			 stream, com.db4o.ext.Db4oDatabase younger, com.db4o.ext.Db4oDatabase older)
		{
			com.db4o.ReplicationRecord res = null;
			stream.showInternalClasses(true);
			com.db4o.query.Query q = stream.querySharpenBug();
			q.constrain(com.db4o.YapConst.CLASS_REPLICATIONRECORD);
			q.descend("_youngerPeer").constrain(younger).identity();
			q.descend("_olderPeer").constrain(older).identity();
			com.db4o.ObjectSet objectSet = q.execute();
			if (objectSet.hasNext())
			{
				res = (com.db4o.ReplicationRecord)objectSet.next();
			}
			stream.showInternalClasses(false);
			return res;
		}
	}
}
