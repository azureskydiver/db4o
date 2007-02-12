/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs.inside;

import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationProvider;
import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Visitor4;


public interface ReplicationProviderInside extends ReplicationProvider {
	void activate(Object object);

	/**
	 * Activates the fields, e.g. Collections, arrays, of an object
	 * <p/>
	 * /** Clear the  ReplicationReference cache
	 */
	void clearAllReferences();

	void commitReplicationTransaction(long raisedDatabaseVersion);

	/**
	 * Destroys this provider and frees up resources.
	 */
	void destroy();

	public ObjectSet getStoredObjects(Class type);

	/**
	 * Returns the current transaction serial number.
	 *
	 * @return the current transaction serial number
	 */
	long getCurrentVersion();

	long getLastReplicationVersion();

	Object getMonitor();

	String getName();

	ReadonlyReplicationProviderSignature getSignature();

	/**
	 * Returns the ReplicationReference of an object
	 *
	 * @param obj            object queried
	 * @param referencingObj
	 * @param fieldName
	 * @return null if the object is not owned by this ReplicationProvider.
	 */
	ReplicationReference produceReference(Object obj, Object referencingObj, String fieldName);

	/**
	 * Returns the ReplicationReference of an object by specifying the uuid of the object.
	 *
	 * @param uuid the uuid of the object
	 * @param hint the type of the object
	 * @return the ReplicationReference or null if the reference cannot be found
	 */
	ReplicationReference produceReferenceByUUID(Db4oUUID uuid, Class hint);

	ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference, ReplicationReference referencingObjRef, String fieldName);

	/**
	 * Rollbacks all changes done during the replication session  and terminates the Transaction.
	 * Guarantees the changes will not be applied to the underlying databases.
	 */
	void rollbackReplication();

	/**
	 * Start a Replication Transaction with another ReplicationProvider
	 *
	 * @param peerSignature the signature of another ReplicationProvider.
	 */
	void startReplicationTransaction(ReadonlyReplicationProviderSignature peerSignature);

	/**
	 * Stores the new replicated state of obj. It can also be a new object to this
	 * provider.
	 *
	 * @param obj Object with updated state or a clone of new object in the peer.
	 */
	void storeReplica(Object obj);

	void syncVersionWithPeer(long maxVersion);

	/**
	 * Visits the object of each cached ReplicationReference.
	 *
	 * @param visitor implements the visit functions, including copying of object states, and storing of changed objects
	 */
	void visitCachedReferences(Visitor4 visitor);

	boolean wasModifiedSinceLastReplication(ReplicationReference reference);

	void replicateDeletion(Db4oUUID uuid);
}
