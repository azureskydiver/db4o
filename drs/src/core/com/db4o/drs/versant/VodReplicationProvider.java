/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;
import com.db4o.internal.encoding.*;

public class VodReplicationProvider implements TestableReplicationProviderInside{
	
	private final VodDatabase _vod;
	
	private final VodCobra _cobra;
	
	private final VodJdo _jdo;
	
	private ObjectReferenceMap _replicationReferences = new ObjectReferenceMap();
	
	private final Signatures _signatures = new Signatures();
	
	private final Hashtable<String, ClassMetadata> _knownClasses = new Hashtable<String, ClassMetadata>();
	
	public VodReplicationProvider(VodDatabase vod) {
		_vod = vod;
		_jdo = new VodJdo(vod);
		_cobra = new VodCobra(vod);
		loadSignatures();
		loadKnownClasses();
	}

	private void loadKnownClasses() {
		for (ClassMetadata classMetadata : _jdo.query(ClassMetadata.class)) {
			_knownClasses.put(classMetadata.fullyQualifiedName(), classMetadata);
		}
	}

	private void loadSignatures() {
		for (DatabaseSignature entry : _jdo.query(DatabaseSignature.class)) {
			if(DrsDebug.verbose){
				System.out.println(entry);
			}
			_signatures.add( entry.databaseId(),new Signature(entry.signature()));
		}
	}

	public void commit() {
		_jdo.commit();
	}

	public void delete(Object obj) {
		_jdo.delete(obj);
	}

	public void deleteAllInstances(Class clazz) {
		_jdo.deleteAll(clazz);
	}

	public ObjectSet getStoredObjects(Class clazz) {
		return new ObjectSetCollectionFacade(_jdo.query(clazz));
	}

	public void storeNew(Object obj) {
		if(obj == null){
			throw new IllegalArgumentException();
		}
		ensureClassKnown(obj);
		_jdo.store(obj);
	}

	private void ensureClassKnown(Object obj) {
		ensureClassKnown(obj.getClass());
	}

	private void ensureClassKnown(Class clazz) {
		String className = clazz.getName();
		
		if( _knownClasses.get(className) != null){
			return;
		}
		
		
		final VodJdo jdo = new VodJdo(_vod);
		
		ClassMetadata classMetadata = new ClassMetadata(_jdo.schemaName(clazz), className);
		
		jdo.store(classMetadata);
		_knownClasses.put(className, classMetadata);
		jdo.commit();
		
		final ByRef<ClassMetadata> classMetadataByRef = ByRef.newInstance(classMetadata);
		
		
		try{
			int timeoutInMillis = 10000;
			int millisecondsBetweenRetries = 50;
			boolean eventListenerHasChangedMonitored = Runtime4.retry(timeoutInMillis, millisecondsBetweenRetries, new Closure4<Boolean>() {
				public Boolean run() {
					// The eventlistener listens to creation of ClassMetadata objects.
					// It will change the monitored field as soon as the channel is up.
					classMetadataByRef.value = jdo.peek(classMetadataByRef.value);
					return classMetadataByRef.value.monitored();
				}
			});
			if(! eventListenerHasChangedMonitored){
				Class eventListenerProgram = com.db4o.drs.versant.eventlistener.Program.class;
				throw new IllegalStateException("Event listener process did not respond to ClassMetadata creation for " 
						+ className + ". Ensure that " + eventListenerProgram.getName() + " is running.");
				
			}
		} finally {
			jdo.close();
		}
	}


	public void update(Object obj) {
		// do nothing
		// JDO is transparent persistence
	}

	public void destroy() {
		_jdo.close();
		_cobra.close();
	}

	public void activate(Object object) {
		// do nothing
		// JDO is transparent activation
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
		
		// TODO: We have to do the following to turn the event listener
		//       on for this class. Make sure there is a test case.
		
		// ensureClassKnown(clazz);
		
		
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
		VodId vodId = _cobra.idFor(_jdo.loid(obj));
		Signature signature = produceSignatureFor(vodId.databaseId);
		VodUUID vodUUID = new VodUUID(signature, vodId);
		return new ReplicationReferenceImpl(obj, vodUUID, vodId.timestamp);
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
		
		Object obj = _jdo.objectByLoid(loid);
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
		_jdo.store(databaseSignature);
		_signatures.add(databaseId, signature);
		return signature;
	}
	
	private byte[] signatureBytes(int databaseId){
		return new LatinStringIO().write("vod-" + databaseId);
	}

	public long loid(Object obj) {
		return _jdo.loid(obj);
	}

}
