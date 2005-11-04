namespace com.db4o.replication
{
	/// <summary>db4o replication interface.</summary>
	/// <remarks>db4o replication interface.</remarks>
	/// <seealso cref="com.db4o.ext.ExtObjectContainer.replicationBegin">com.db4o.ext.ExtObjectContainer.replicationBegin
	/// 	</seealso>
	public interface ReplicationProcess
	{
		/// <summary>
		/// checks if an object has been modified in both ObjectContainers involved
		/// in the replication process since the last time the two ObjectContainers
		/// were replicated.
		/// </summary>
		/// <remarks>
		/// checks if an object has been modified in both ObjectContainers involved
		/// in the replication process since the last time the two ObjectContainers
		/// were replicated.
		/// </remarks>
		/// <param name="obj">- the object to check for a conflict.</param>
		void checkConflict(object obj);

		/// <summary>commits the replication task to both involved ObjectContainers.</summary>
		/// <remarks>
		/// commits the replication task to both involved ObjectContainers.
		/// <br /><br />Call this method after replication is completed to
		/// write all changes back to the database files. This method
		/// synchronizes both ObjectContainers by setting the transaction
		/// serial number (@link ExtObjectContainer#version()) on both
		/// ObjectContainers to be equal
		/// to the higher version number among the two. A record with
		/// information about this replication task, including the
		/// synchronized version number is stored to both ObjectContainers
		/// to allow future incremental replication.
		/// </remarks>
		void commit();

		/// <summary>returns the "peerA" ObjectContainer involved in this ReplicationProcess.
		/// 	</summary>
		/// <remarks>returns the "peerA" ObjectContainer involved in this ReplicationProcess.
		/// 	</remarks>
		com.db4o.ObjectContainer peerA();

		/// <summary>returns the "peerB" ObjectContainer involved in this ReplicationProcess.
		/// 	</summary>
		/// <remarks>returns the "peerB" ObjectContainer involved in this ReplicationProcess.
		/// 	</remarks>
		com.db4o.ObjectContainer peerB();

		/// <summary>replicates an object.</summary>
		/// <remarks>
		/// replicates an object.
		/// <br /><br />By default the version number of the object is checked in
		/// both ObjectContainers involved in the replication process. If the
		/// version number has not changed since the last time the two
		/// ObjectContainers were replicated
		/// </remarks>
		/// <param name="obj"></param>
		void replicate(object obj);

		/// <summary>ends a replication task without committing any changes.</summary>
		/// <remarks>ends a replication task without committing any changes.</remarks>
		void rollback();

		/// <summary>
		/// modifies the replication policy, what to do on a call to
		/// <see cref="com.db4o.replication.ReplicationProcess.replicate">com.db4o.replication.ReplicationProcess.replicate
		/// 	</see>
		/// .
		/// <br /><br />If no direction is set, the replication process will be bidirectional by
		/// default.
		/// </summary>
		/// <param name="relicateFrom">the ObjectContainer to replicate from</param>
		/// <param name="replicateTo">the ObjectContainer to replicate to</param>
		void setDirection(com.db4o.ObjectContainer relicateFrom, com.db4o.ObjectContainer
			 replicateTo);

		/// <summary>
		/// adds a constraint to the passed Query to query only for objects that
		/// were modified since the last replication process between the two
		/// ObjectContainers involved in this replication process.
		/// </summary>
		/// <remarks>
		/// adds a constraint to the passed Query to query only for objects that
		/// were modified since the last replication process between the two
		/// ObjectContainers involved in this replication process.
		/// </remarks>
		/// <param name="query">the Query to be constrained</param>
		void whereModified(com.db4o.query.Query query);
	}
}
