/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.replication;

import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Visitor4;
import com.db4o.replication.ReplicationProvider;


public interface ReplicationProviderInside extends ReplicationProvider {

	public void activate(Object object);

	public ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference, ReplicationReference referencingObjRef, String fieldName);

	/**
	 * Activates the fields, e.g. Collections, arrays, of an object
	 * <p/>
	 * /** Clear the  ReplicationReference cache
	 */
	void clearAllReferences();

	/**
	 * Destroys this provider and frees up resources.
	 */
	public void destroy();

	public void commitReplicationTransaction(long raisedDatabaseVersion);

	/**
	 * Returns the current transaction serial number.
	 *
	 * @return the current transaction serial number
	 */
	long getCurrentVersion();

	public Object getMonitor();

	ReadonlyReplicationProviderSignature getSignature();

	/**
	 * Determines if this Provider has the ReplicationReference of an object
	 *
	 * @param obj object concerned
	 * @return true if this provider has the ReplicationReference
	 */
	boolean hasReplicationReferenceAlready(Object obj);

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

	boolean wasChangedSinceLastReplication(ReplicationReference reference);
	boolean wasDeletedSinceLastReplication(Db4oUUID uuid);


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


	public void syncVersionWithPeer(long maxVersion);


	/**
	 * Visits the object of each cached ReplicationReference.
	 *
	 * @param visitor implements the visit functions, including copying of object states, and storing of changed objects
	 */
	void visitCachedReferences(Visitor4 visitor);

	String getName();
}
