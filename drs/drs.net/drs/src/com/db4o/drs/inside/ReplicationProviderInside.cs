namespace com.db4o.drs.inside
{
	public interface ReplicationProviderInside : com.db4o.drs.ReplicationProvider
	{
		void Activate(object _object);

		/// <summary>Activates the fields, e.g.</summary>
		/// <remarks>
		/// Activates the fields, e.g. Collections, arrays, of an object
		/// <p/>
		/// /** Clear the  ReplicationReference cache
		/// </remarks>
		void ClearAllReferences();

		void CommitReplicationTransaction(long raisedDatabaseVersion);

		/// <summary>Destroys this provider and frees up resources.</summary>
		/// <remarks>Destroys this provider and frees up resources.</remarks>
		void Destroy();

		com.db4o.ObjectSet GetStoredObjects(System.Type type);

		/// <summary>Returns the current transaction serial number.</summary>
		/// <remarks>Returns the current transaction serial number.</remarks>
		/// <returns>the current transaction serial number</returns>
		long GetCurrentVersion();

		long GetLastReplicationVersion();

		object GetMonitor();

		string GetName();

		com.db4o.drs.inside.ReadonlyReplicationProviderSignature GetSignature();

		/// <summary>Returns the ReplicationReference of an object</summary>
		/// <param name="obj">object queried</param>
		/// <param name="referencingObj"></param>
		/// <param name="fieldName"></param>
		/// <returns>null if the object is not owned by this ReplicationProvider.</returns>
		com.db4o.drs.inside.ReplicationReference ProduceReference(object obj, object referencingObj
			, string fieldName);

		/// <summary>Returns the ReplicationReference of an object by specifying the uuid of the object.
		/// 	</summary>
		/// <remarks>Returns the ReplicationReference of an object by specifying the uuid of the object.
		/// 	</remarks>
		/// <param name="uuid">the uuid of the object</param>
		/// <param name="hint">the type of the object</param>
		/// <returns>the ReplicationReference or null if the reference cannot be found</returns>
		com.db4o.drs.inside.ReplicationReference ProduceReferenceByUUID(com.db4o.ext.Db4oUUID
			 uuid, System.Type hint);

		com.db4o.drs.inside.ReplicationReference ReferenceNewObject(object obj, com.db4o.drs.inside.ReplicationReference
			 counterpartReference, com.db4o.drs.inside.ReplicationReference referencingObjRef
			, string fieldName);

		/// <summary>Rollbacks all changes done during the replication session  and terminates the Transaction.
		/// 	</summary>
		/// <remarks>
		/// Rollbacks all changes done during the replication session  and terminates the Transaction.
		/// Guarantees the changes will not be applied to the underlying databases.
		/// </remarks>
		void RollbackReplication();

		/// <summary>Start a Replication Transaction with another ReplicationProvider</summary>
		/// <param name="peerSignature">the signature of another ReplicationProvider.</param>
		void StartReplicationTransaction(com.db4o.drs.inside.ReadonlyReplicationProviderSignature
			 peerSignature);

		/// <summary>Stores the new replicated state of obj.</summary>
		/// <remarks>
		/// Stores the new replicated state of obj. It can also be a new object to this
		/// provider.
		/// </remarks>
		/// <param name="obj">Object with updated state or a clone of new object in the peer.
		/// 	</param>
		void StoreReplica(object obj);

		void SyncVersionWithPeer(long maxVersion);

		void UpdateCounterpart(object updated);

		/// <summary>Visits the object of each cached ReplicationReference.</summary>
		/// <remarks>Visits the object of each cached ReplicationReference.</remarks>
		/// <param name="visitor">implements the visit functions, including copying of object states, and storing of changed objects
		/// 	</param>
		void VisitCachedReferences(com.db4o.foundation.Visitor4 visitor);

		bool WasModifiedSinceLastReplication(com.db4o.drs.inside.ReplicationReference reference
			);

		void ReplicateDeletion(com.db4o.ext.Db4oUUID uuid);
	}
}
