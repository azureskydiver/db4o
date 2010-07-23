/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import javax.jdo.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;
import com.db4o.internal.encoding.*;
import com.versant.core.vds.*;
import com.versant.odbms.*;
import com.versant.odbms.model.*;

public class VodReplicationProvider implements TestableReplicationProviderInside{
	
	private final VodDatabase _vod;
	
	private final PersistenceManager _pm;
	
	private final DatastoreManager _dm;
	
	private ObjectReferenceMap _replicationReferences = new ObjectReferenceMap();
	
	private final Signatures _signatures = new Signatures();
	
	private final Hashtable<String, ClassMetadata> _knownClasses = new Hashtable<String, ClassMetadata>();
	
	public VodReplicationProvider(VodDatabase vod) {
		_vod = vod;
		_pm = _vod.createPersistenceManager();
		_dm = _vod.createDatastoreManager();
		_pm.currentTransaction().begin();
		loadSignatures();
		loadKnownClasses();
	}

	private void loadKnownClasses() {
		Extent<ClassMetadata> extent = _pm.getExtent(ClassMetadata.class);
		for (ClassMetadata classMetadata : extent) {
			_knownClasses.put(classMetadata.name(), classMetadata);
		}
	}

	private void loadSignatures() {
		Extent<DatabaseSignature> extent = _pm.getExtent(DatabaseSignature.class);
		for (DatabaseSignature entry : extent) {
			if(DrsDebug.verbose){
				System.out.println(entry);
			}
			_signatures.add( entry.databaseId(),new Signature(entry.signature()));
		}
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
		if(obj == null){
			throw new IllegalArgumentException();
		}
		ensureClassKnown(obj);
		
		_pm.makePersistent(obj);
	}

	private void ensureClassKnown(Object obj) {
		
		Class clazz = obj.getClass();
		String className = clazz.getName();
		
		if( _knownClasses.get(className) != null){
			return;
		}
		
		PersistenceManager pm = _vod.createPersistenceManager();
		
		pm.currentTransaction().begin();
		ClassMetadata classMetadata = new ClassMetadata(_vod.schemaName(clazz), className);
		pm.makePersistent(classMetadata);
		_knownClasses.put(className, classMetadata);
		pm.currentTransaction().commit();
		pm.close();
	}


	public void update(Object obj) {
		// do nothing
		// JDO is transparent persistence
	}

	public void destroy() {
		_pm.currentTransaction().rollback();
		_pm.close();
		_dm.close();
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
		_replicationReferences.clear();
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
		reference = produceNewReference(obj);
		_replicationReferences.put(reference);
		return reference; 
	}
	

	private DatastoreObject datastoreObject(final Object obj) {
		DatastoreLoid datastoreLoid = new DatastoreLoid(VdsUtils.getLOID(obj, _pm));
		DatastoreObject[] loidsAsDSO = _dm.getLoidsAsDSO(new Object[] { datastoreLoid });
		_dm.groupReadObjects(loidsAsDSO, DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
		return loidsAsDSO[0];
	}

	private long loidFrom(DrsUUID uuid) {
		long databaseId = _signatures.idFor(uuid);
		if(databaseId == 0){
			return 0;
		}
		long db4oLongPart = uuid.getLongPart();
		long vodObjectIdPart = 
			TimeStampIdGenerator.convert64BitIdTo48BitId(db4oLongPart);
		return (databaseId << 48) | vodObjectIdPart;
	}

	public ReplicationReference referenceNewObject(Object obj,
			ReplicationReference counterpartReference,
			ReplicationReference referencingObjRef, String fieldName) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void replicateDeletion(DrsUUID uuid) {
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
	
	private ReplicationReference produceNewReference(final Object obj) {
		if(obj == null){
			throw new IllegalArgumentException();
		}
		
		DatastoreObject dso = datastoreObject(obj);
		
		DatastoreLoid datastoreLoid = new DatastoreLoid(dso.getLOID());
		int databaseId = datastoreLoid.getDatabaseId();
		Signature signature = produceSignatureFor(databaseId);
		
		VodUUID vodUUID = new VodUUID(signature, (short)databaseId, datastoreLoid.getObjectId1(), datastoreLoid.getObjectId2());
		
		return new ReplicationReferenceImpl(obj, vodUUID, dso.getTimestamp());
	}
	
	public ReplicationReference produceReferenceByUUID(DrsUUID uuid, Class hint) {
		if(uuid == null){
			throw new IllegalArgumentException();
		}
		ReplicationReference reference = _replicationReferences.getByUUID(uuid);
		if(reference != null){
			return reference;
		}
		long loid = loidFrom(uuid);
		
		if(loid == 0){
			return null;
		}
		
		Object obj = VdsUtils.getObjectByLOID(loid, true, _pm);
		if(obj == null){
			return null;
		}
		
		reference = produceNewReference(obj);
		_replicationReferences.put(reference);
		return reference; 
	}

	private Signature produceSignatureFor(int databaseId) {
		Signature signature = _signatures.signatureFor(databaseId);
		if(signature != null){
			return signature;
		}
		signature = new Signature(signatureBytes(databaseId));
		
		DatabaseSignature databaseSignature = new DatabaseSignature(databaseId, signature.bytes);
		_pm.makePersistent(databaseSignature);
		_signatures.add(databaseId, signature);
		return signature;
	}
	
	private byte[] signatureBytes(int databaseId){
		return new LatinStringIO().write("vod-" + databaseId);
	}

}
