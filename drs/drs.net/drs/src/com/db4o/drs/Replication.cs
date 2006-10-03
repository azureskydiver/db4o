namespace com.db4o.drs
{
	/// <summary>Factory to create ReplicationSessions.</summary>
	/// <remarks>Factory to create ReplicationSessions.</remarks>
	/// <author>Albert Kwan</author>
	/// <author>Klaus Wuestefeld</author>
	/// <version>1.2</version>
	/// <seealso cref="com.db4o.drs.hibernate.HibernateReplication">com.db4o.drs.hibernate.HibernateReplication
	/// 	</seealso>
	/// <seealso cref="com.db4o.drs.ReplicationProvider">com.db4o.drs.ReplicationProvider
	/// 	</seealso>
	/// <seealso cref="com.db4o.drs.ReplicationEventListener">com.db4o.drs.ReplicationEventListener
	/// 	</seealso>
	/// <since>dRS 1.0</since>
	public class Replication
	{
		/// <summary>Begins a replication session between two ReplicationProviders without ReplicationEventListener.
		/// 	</summary>
		/// <remarks>Begins a replication session between two ReplicationProviders without ReplicationEventListener.
		/// 	</remarks>
		/// <exception cref="com.db4o.drs.ReplicationConflictException">when conflicts occur</exception>
		/// <seealso cref="com.db4o.drs.ReplicationEventListener">com.db4o.drs.ReplicationEventListener
		/// 	</seealso>
		public static com.db4o.drs.ReplicationSession Begin(com.db4o.drs.ReplicationProvider
			 providerA, com.db4o.drs.ReplicationProvider providerB)
		{
			return Begin(providerA, providerB, null);
		}

		/// <summary>Begins a replication session between db4o and db4o without ReplicationEventListener.
		/// 	</summary>
		/// <remarks>Begins a replication session between db4o and db4o without ReplicationEventListener.
		/// 	</remarks>
		/// <exception cref="com.db4o.drs.ReplicationConflictException">when conflicts occur</exception>
		/// <seealso cref="com.db4o.drs.ReplicationEventListener">com.db4o.drs.ReplicationEventListener
		/// 	</seealso>
		public static com.db4o.drs.ReplicationSession Begin(com.db4o.ObjectContainer oc1, 
			com.db4o.ObjectContainer oc2)
		{
			return Begin(oc1, oc2, null);
		}

		/// <summary>Begins a replication session between two ReplicatoinProviders.</summary>
		/// <remarks>Begins a replication session between two ReplicatoinProviders.</remarks>
		public static com.db4o.drs.ReplicationSession Begin(com.db4o.drs.ReplicationProvider
			 providerA, com.db4o.drs.ReplicationProvider providerB, com.db4o.drs.ReplicationEventListener
			 listener)
		{
			if (listener == null)
			{
				listener = new com.db4o.drs.inside.DefaultReplicationEventListener();
			}
			return new com.db4o.drs.inside.GenericReplicationSession(providerA, providerB, listener
				);
		}

		/// <summary>Begins a replication session between db4o and db4o.</summary>
		/// <remarks>Begins a replication session between db4o and db4o.</remarks>
		public static com.db4o.drs.ReplicationSession Begin(com.db4o.ObjectContainer oc1, 
			com.db4o.ObjectContainer oc2, com.db4o.drs.ReplicationEventListener listener)
		{
			return Begin(new com.db4o.drs.db4o.Db4oReplicationProvider(oc1), new com.db4o.drs.db4o.Db4oReplicationProvider
				(oc2), listener);
		}
	}
}
