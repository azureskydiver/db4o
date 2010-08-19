/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import static com.db4o.drs.foundation.Logger4Support.*;
import static com.db4o.qlin.QLinSupport.*;

import java.util.*;

import javax.jdo.spi.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.inband.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;
import com.db4o.internal.encoding.*;

public class VodReplicationProvider implements TestableReplicationProviderInside{
	
	private final VodDatabase _vod;
	
	private final VodCobra _cobra;
	
	private final VodJdo _jdo;
	
	private final VodJvi _jvi;

	private final ProviderSideCommunication _comm;
	
	private ObjectReferenceMap _replicationReferences = new ObjectReferenceMap();
	
	private final Signatures _signatures = new Signatures();
	
	private final Map<String, Long> _knownClasses = new HashMap<String, Long>();

	private ReplicationReflector _replicationReflector;
	
	private ReplicationCommitRecord _replicationCommitRecord;
	
	private final ReadonlyReplicationProviderSignature _mySignature;
	
	private final short _myDatabaseId;
	
	public VodReplicationProvider(VodDatabase vod, VodCobra cobra, ProviderSideCommunication comm) {
		_comm = comm;
		_vod = vod;
		_cobra = cobra;
		_jdo = new VodJdo(vod);
		_jvi = new VodJvi(vod);
		loadSignatures();
		loadKnownClasses();
		_myDatabaseId = _cobra.databaseId();
		final Signature mySignature = produceSignatureFor(_myDatabaseId);
		_mySignature = new ReadonlyReplicationProviderSignature() {
			public byte[] getSignature() {
				return mySignature.bytes;
			}
			public long getId() {
				return 0;
			}
			public long getCreated() {
				return _myDatabaseId;
			}
		};
		_jdo.deleteAll(RMIMessage.class);
	}

	private void loadKnownClasses() {
		for (ClassMetadata classMetadata : _jdo.query(ClassMetadata.class)) {
			_knownClasses.put(classMetadata.fullyQualifiedName(), classMetadata.loid());
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
		logIdentity(obj, getName());
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
		
		_knownClasses.put(className, -1L);

		_comm.ensureMonitoringEventsOn(className, _jdo.schemaName(clazz));
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
		_comm.syncTimestamp(raisedDatabaseVersion);
		_jdo.commit();
		
		// FileReplicationProvider does this:
		// _idsReplicatedInThisSession = null;
		
		
		// HibernateReplicationProvider does this:
		// _dirtyRefs.clear();
		// _inReplication = false;
		
	}

	public long getCurrentVersion() {
		return _comm.requestTimestamp();
	}

	public long getLastReplicationVersion() {
		return _replicationCommitRecord.timestamp();
	}

	public String getName() {
		return _vod.databaseName();
	}

	public ReadonlyReplicationProviderSignature getSignature() {
		return _mySignature;
	}

	public ReplicationReference produceReference(final Object obj, Object referencingObj, String fieldName) {
		ReplicationReference reference = _replicationReferences.get(obj);
		if (reference != null){
			return reference;
		}
		reference = produceNewReference(obj);
		if(reference == null) {
			return null;
		}
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

	public void startReplicationTransaction(ReadonlyReplicationProviderSignature peer) {
		clearAllReferences();
		Signature peerSignature = new Signature(peer.getSignature());
		int peerId = _signatures.idFor(peerSignature);
		if(peerId == 0){
			peerId = _jvi.newDbId( peerSignature.toString() );
			storeSignature(peerId, peerSignature);
			_signatures.add(peerId, peerSignature);
		}
		int lowerId = Math.min(peerId, _myDatabaseId);
		int higherId = Math.max(peerId, _myDatabaseId);
		
		
		// TODO: Enhance QLin for deep queries. Retrieve from Cobra directly here.
		
		String filter = "this.lowerPeer.databaseId == " + lowerId + " & this.higherPeer.databaseId == " + higherId;
		ReplicationCommitRecord replicationCommitRecordByJdo = _jdo.queryOneOrNone(ReplicationCommitRecord.class, filter);
		
		if(replicationCommitRecordByJdo == null){
			_replicationCommitRecord = new ReplicationCommitRecord(databaseSignature(lowerId), databaseSignature(higherId));
			_cobra.store(_replicationCommitRecord);
			return;
		}
		
		long commitRecordLoid = _jdo.loid(replicationCommitRecordByJdo);
		_replicationCommitRecord = _cobra.objectByLoid(commitRecordLoid);
		if(_replicationCommitRecord == null){
			throw new IllegalStateException("Commit record could not be found. loid: " + commitRecordLoid);
		}
	}

	private DatabaseSignature databaseSignature(int databaseId) {
		DatabaseSignature sig = prototype(DatabaseSignature.class);
		return _cobra.from(DatabaseSignature.class).where(sig.databaseId()).equal(databaseId).single();
	}

	public void storeReplica(Object obj) {
		logIdentity(obj, getName());
		ReplicationReference ref = _replicationReferences.get(obj);
		long loid = loidFrom(ref.uuid());
		logIdentity(obj, String.valueOf(loid));
		_cobra.store(loid, obj);
	}

	public void syncVersionWithPeer(long maxVersion) {
		_replicationCommitRecord.timestamp(maxVersion);
		_cobra.store(_replicationCommitRecord);
		_comm.syncTimestamp(maxVersion);
	}

	public void visitCachedReferences(Visitor4 visitor) {
		_replicationReferences.visitEntries(visitor);
	}

	public boolean wasModifiedSinceLastReplication(ReplicationReference reference) {
		return reference.version() > getLastReplicationVersion();
	}

	public ObjectSet objectsChangedSinceLastReplication() {
		return internalObjectsChangedSinceLastReplication("");
	}

	public ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		ensureClassKnown(clazz);
		Long classMetadataLoid = _knownClasses.get(clazz.getName());
		if (classMetadataLoid == null) {
			ClassMetadata cm = prototype(ClassMetadata.class);
			classMetadataLoid = _cobra.from(ClassMetadata.class).where(cm.fullyQualifiedName()).equal(clazz.getName()).single().loid();
			_knownClasses.put(clazz.getName(), classMetadataLoid);
		}
		return internalObjectsChangedSinceLastReplication("this.classMetadataLoid == " + classMetadataLoid);
	}

	private ObjectSet internalObjectsChangedSinceLastReplication(String query) {
		Set<Long> loids = new HashSet<Long>();
		Collection<ObjectLifecycleEvent> allEvents = _jdo.query(ObjectLifecycleEvent.class, query);
		for (ObjectLifecycleEvent event : allEvents) {
			loids.add(event.objectLoid());
		}
		Collection<Object> objects = new ArrayList<Object>(loids.size());
		for (Long loid : loids) {
			objects.add(_jdo.objectByLoid(loid));
		}
		return new ObjectSetCollectionFacade(objects);
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
		long loid = _jdo.loid(obj);
		if(loid == 0) {
			return null;
		}
		VodId vodId = _cobra.idFor(loid);
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
		storeSignature(databaseId, signature);
		return signature;
	}

	private void storeSignature(int databaseId, Signature signature) {
		DatabaseSignature databaseSignature = new DatabaseSignature(databaseId, signature.bytes);
		_jdo.store(databaseSignature);
		_signatures.add(databaseId, signature);
	}
	
	private byte[] signatureBytes(int databaseId){
		return new LatinStringIO().write("vod-" + databaseId);
	}

	public long loid(Object obj) {
		return _jdo.loid(obj);
	}

	public void runIsolated(Block4 block) {
		_comm.requestIsolation(IsolationMode.DELAYED);
		try {
			block.run();
		}
		finally {
			_comm.requestIsolation(IsolationMode.IMMEDIATE);
		}
	}

}
