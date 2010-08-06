/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import javax.jdo.spi.*;

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

	private ReplicationReflector _replicationReflector;
	
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
		if(! (obj instanceof PersistenceCapable)){
			String msg = "Object of " + obj.getClass() + " does not implement PersistenceCapable. Recommended action: Enhance all persistent classes for JDO using an enhancer.";
			throw new IllegalStateException(msg);
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
		
		ClassMetadata classMetadata = new ClassMetadata(_jdo.schemaName(clazz), className);
		_knownClasses.put(className, classMetadata);
		
		ClassMetadata changed = ensureStoreChanged(classMetadata, new Function4<ClassMetadata, Boolean>() {
			public Boolean apply(ClassMetadata value) {
				return value.monitored();
			}
		});
		if(changed == null){
			Class eventListenerProgram = com.db4o.drs.versant.eventlistener.Program.class;
			throw new IllegalStateException("Event listener process did not respond to ClassMetadata creation for " 
					+ className + ". Ensure that " + eventListenerProgram.getName() + " is running.");
		}
	}

	private <T> T ensureStoreChanged(T obj, final Function4<T, Boolean> modifiedCheck) {
		final VodJdo jdo = new VodJdo(_vod);
		jdo.store(obj);
		jdo.commit();
		
		final ByRef<T> peeked = ByRef.newInstance(obj);
		
		try{
			int timeoutInMillis = 10000;
			int millisecondsBetweenRetries = 50;
			boolean changed = Runtime4.retry(timeoutInMillis, millisecondsBetweenRetries, new Closure4<Boolean>() {
				public Boolean run() {
					peeked.value = jdo.peek(peeked.value);
					return modifiedCheck.apply(peeked.value);
				}
			});
			return changed ? peeked.value : null;
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
		Long syncRequestLoid = _cobra.singleInstanceLoid(TimestampSyncRequest.class);
		TimestampSyncRequest syncRequest =
				syncRequestLoid == null 
				? new TimestampSyncRequest() 
				: (TimestampSyncRequest)_cobra.objectByLoid(syncRequestLoid);
		TimestampSyncRequest response = ensureStoreChanged(syncRequest, new Function4<TimestampSyncRequest, Boolean>() {
			public Boolean apply(TimestampSyncRequest syncRequest) {
				return syncRequest.isAnswered();
			}
		});
		if(response == null || !response.isAnswered()) {
			throw new IllegalStateException("No timestamp sync response received.");
		}
		return response.timestamp();
	}

	public long getLastReplicationVersion() {
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
		
		// clearAllReferences();
		
		
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
		_replicationReferences.visitEntries(visitor);
	}

	public boolean wasModifiedSinceLastReplication(
			ReplicationReference reference) {
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public ObjectSet objectsChangedSinceLastReplication() {
		Set<Long> loids = new HashSet<Long>();
		Collection<ObjectLifecycleEvent> allEvents = _jdo.query(ObjectLifecycleEvent.class, "");
		for (ObjectLifecycleEvent event : allEvents) {
			loids.add(event.objectLoid());
		}
		Collection<Object> objects = new ArrayList<Object>(loids.size());
		for (Long loid : loids) {
			objects.add(_jdo.objectByLoid(loid));
		}
		return new ObjectSetCollectionFacade(objects);
	}

	public ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		ensureClassKnown(clazz);
		
		
		
		// TODO Auto-generated method stub
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void replicationReflector(ReplicationReflector replicationReflector) {
		_replicationReflector = replicationReflector;
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

	public void runIsolated(Block4 block) {
		System.err.println("FIXMEPLEASE VodReplicationProvider#runIsolated IS NOT ISOLATED");
		block.run();
	}

}
