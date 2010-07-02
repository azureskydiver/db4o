/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import javax.jdo.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

public class VodReplicationProvider implements TestableReplicationProviderInside{
	
	private final VodDatabase _vod;
	
	private final PersistenceManager _pm;
	
	private ObjectReferenceMap _replicationReferences = new ObjectReferenceMap();
	
	public VodReplicationProvider(VodDatabase vod) {
		_vod = vod;
		_pm = vod.createPersistenceManager();
		_pm.currentTransaction().begin();
	}

	public void commit() {
		_pm.currentTransaction().commit();
		_pm.currentTransaction().begin();
	}

	public void delete(Object obj) {
		_pm.deletePersistent(obj);
	}

	public void deleteAllInstances(Class clazz) {
		_pm.deletePersistentAll((Collection) _pm.newQuery(clazz).execute());
	}

	public ObjectSet getStoredObjects(Class type) {
		Collection collection = (Collection) _pm.newQuery(type).execute();
		return new ObjectSetCollectionFacade(collection);
	}

	public void storeNew(Object obj) {
		_pm.makePersistent(obj);
	}

	public void update(Object obj) {
		// do nothing
		// JDO is transparent persistence
	}

	public void destroy() {
		_pm.currentTransaction().rollback();
		_pm.close();
	}

	public void activate(Object object) {
		// do nothing
		// JDO is transparent activation
	}

	public boolean supportsCascadeDelete() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean supportsHybridCollection() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean supportsMultiDimensionalArrays() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean supportsRollback() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void clearAllReferences() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void commitReplicationTransaction(long raisedDatabaseVersion) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public long getCurrentVersion() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public long getLastReplicationVersion() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public Object getMonitor() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public String getName() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public ReadonlyReplicationProviderSignature getSignature() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public ReplicationReference produceReference(final Object obj, Object referencingObj, String fieldName) {
		ReplicationReference reference = _replicationReferences.get(obj);
		if (reference != null){
			return reference;
		}
		reference = produceNewReference(obj, referencingObj, fieldName);
		_replicationReferences.put(reference);
		return reference; 
	}
	
	private ReplicationReference produceNewReference(final Object obj, Object referencingObj, String fieldName) {
		return new ReplicationReferenceImpl(obj, null, 0);
	}

	public ReplicationReference produceReferenceByUUID(Db4oUUID uuid, Class hint) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public ReplicationReference referenceNewObject(Object obj,
			ReplicationReference counterpartReference,
			ReplicationReference referencingObjRef, String fieldName) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void replicateDeletion(Db4oUUID uuid) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void rollbackReplication() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void startReplicationTransaction(
			ReadonlyReplicationProviderSignature peerSignature) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void storeReplica(Object obj) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void syncVersionWithPeer(long maxVersion) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void visitCachedReferences(Visitor4 visitor) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean wasModifiedSinceLastReplication(
			ReplicationReference reference) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public ObjectSet objectsChangedSinceLastReplication() {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void replicationReflector(ReplicationReflector replicationReflector) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean isProviderSpecific(Object original) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}
	
	public ReplicationReference produceReference(Object obj) {
		return produceReference(obj, null, null);
	}
	

}
