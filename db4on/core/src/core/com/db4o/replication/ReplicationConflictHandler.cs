namespace com.db4o.replication
{
	/// <summary>
	/// will be called by a
	/// <see cref="com.db4o.replication.ReplicationProcess">com.db4o.replication.ReplicationProcess
	/// 	</see>
	/// upon
	/// replication conflicts. Conflicts occur whenever
	/// <see cref="com.db4o.replication.ReplicationProcess.replicate">com.db4o.replication.ReplicationProcess.replicate
	/// 	</see>
	/// is called with an object that
	/// was modified in both ObjectContainers since the last replication run between
	/// the two.
	/// </summary>
	public interface ReplicationConflictHandler
	{
		/// <summary>the callback method to be implemented to resolve a conflict.</summary>
		/// <remarks>
		/// the callback method to be implemented to resolve a conflict. <br />
		/// <br />
		/// </remarks>
		/// <param name="replicationProcess">
		/// the
		/// <see cref="com.db4o.replication.ReplicationProcess">com.db4o.replication.ReplicationProcess
		/// 	</see>
		/// for which this
		/// ReplicationConflictHandler is registered
		/// </param>
		/// <param name="a">the object modified in the peerA ObjectContainer</param>
		/// <param name="b">the object modified in the peerB ObjectContainer</param>
		/// <returns>
		/// the object (a or b) that should prevail in the conflict or null,
		/// if no action is to be taken. If this would violate the direction
		/// set with
		/// <see cref="com.db4o.replication.ReplicationProcess.setDirection">com.db4o.replication.ReplicationProcess.setDirection
		/// 	</see>
		/// no action will be taken.
		/// </returns>
		/// <seealso cref="com.db4o.replication.ReplicationProcess.peerA">com.db4o.replication.ReplicationProcess.peerA
		/// 	</seealso>
		/// <seealso cref="com.db4o.replication.ReplicationProcess.peerB">com.db4o.replication.ReplicationProcess.peerB
		/// 	</seealso>
		object resolveConflict(com.db4o.replication.ReplicationProcess replicationProcess
			, object a, object b);
	}
}
