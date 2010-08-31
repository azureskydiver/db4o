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
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.ClientChannelControl;
import com.db4o.drs.versant.metadata.ClassMetadata;
import com.db4o.drs.versant.metadata.DatabaseSignature;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent;
import com.db4o.drs.versant.metadata.ReplicationCommitRecord;
import com.db4o.drs.versant.metadata.UuidMapping;
import com.db4o.foundation.Block4;
import com.db4o.foundation.SimpleTimer;
import com.db4o.foundation.TimeStampIdGenerator;
import com.db4o.foundation.Visitor4;
import com.db4o.internal.encoding.LatinStringIO;

public class VodReplicationProvider implements TestableReplicationProviderInside{
	
	private static final int COMM_HEARTBEAT = 2000;

	private final VodDatabase _vod;
	
	private final VodCobraFacade _cobra;
	
	private final VodJdoFacade _jdo;
	
	private final VodJvi _jvi;

	private ObjectReferenceMap _replicationReferences = new ObjectReferenceMap();
	
	private final Signatures _signatures = new Signatures();
	
	private final Map<String, Long> _knownClasses = new HashMap<String, Long>();

	private ReplicationReflector _replicationReflector;
	
	private ReplicationCommitRecord _replicationCommitRecord;
	
	private final ReadonlyReplicationProviderSignature _mySignature;
	
	private final short _myDatabaseId;
	
	SimpleTimer _heartbeatTimer = new SimpleTimer(
		new Runnable() {
			public void run() {
				if (!pinging()) {
					return;
				}
				asyncEventProcessor().ping();
			}}, 
		COMM_HEARTBEAT);

	private Thread _heartbeatThread = new Thread(_heartbeatTimer, "VodReplicationProvider heatbeat");

	private int expectedChangeCount = 0;

	private boolean _isolatedMode = false;

	private final ClientChannelControl _control;

	private boolean pinging = true;

	
	public VodReplicationProvider(VodDatabase vod) {
		_control = ObjectLifecycleMonitorNetworkFactory.newClient(vod);
		_vod = vod;
		_cobra = VodCobra.createInstance(vod);
		_jdo = VodJdo.createInstance(vod);
		_jvi = new VodJvi(vod);
		loadSignatures();
		loadKnownClasses();
		_myDatabaseId = _cobra.databaseId();
		final Signature mySignature = produceSignatureFor(_myDatabaseId);
		_jdo.commit();
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
		
		_heartbeatThread.setDaemon(true);
		_heartbeatThread.start();
		
		
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
		if (!_isolatedMode) {
			ensureChangeCount();
		}
	}

	public void delete(Object obj) {
		expectedChangeCount++;
		_jdo.delete(obj);
	}

	public void deleteAllInstances(Class clazz) {
		if(!_jdo.isKnownClass(clazz)) {
			return;
		}
		expectedChangeCount += _jdo.deleteAll(clazz);
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
		expectedChangeCount++;
		_jdo.store(obj);
	}

	private void ensureClassKnown(Object obj) {
		ensureClassKnown(obj.getClass());
	}

	private void ensureClassKnown(Class clazz) {
		String className = clazz.getName();
		
		if(_knownClasses.containsKey(className)){
			return;
		}
		String schemaName = _jdo.schemaName(clazz);
		ClassMetadata cm = new ClassMetadata(schemaName, className);
		_cobra.store(cm);
		_cobra.commit();
		_knownClasses.put(className, cm.loid());
		syncEventProcessor().ensureMonitoringEventsOn(className, schemaName, cm.loid());
	}

	public void update(Object obj) {
		// do nothing
		// JDO is transparent persistence
		expectedChangeCount++;
	}

	public void destroy() {
		_heartbeatTimer.stop();
		try {
			_heartbeatThread.join();
		} catch (InterruptedException e) {
		}
		_jdo.close();
		_cobra.close();
		_control.stop();
		try {
			_control.join();
		} catch (InterruptedException e) {
		}
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
		return true;
	}

	public void clearAllReferences() {
		_replicationReferences.clear();
	}

	public void commitReplicationTransaction(long raisedDatabaseVersion) {
		syncEventProcessor().syncTimestamp(raisedDatabaseVersion);
		_cobra.commit();
		_jdo.commit();
		
		// FileReplicationProvider does this:
		// _idsReplicatedInThisSession = null;
		
		
		// HibernateReplicationProvider does this:
		// _dirtyRefs.clear();
		// _inReplication = false;
		
	}

	public long getCurrentVersion() {
		return syncEventProcessor().requestTimestamp();
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
		return loidFrom(databaseId, db4oLongPart);
	}

	private long loidFrom(long databaseId, long db4oLongPart) {
		long vodObjectIdPart = 
			TimeStampIdGenerator.convert64BitIdTo48BitId(db4oLongPart);
		return (databaseId << 48) | vodObjectIdPart;
	}

	public ReplicationReference referenceNewObject(Object obj,
			ReplicationReference counterpartReference,
			ReplicationReference referencingObjRef, String fieldName) {
		
		DrsUUID uuid = counterpartReference.uuid();
		long version = counterpartReference.version();
		
		ReplicationReference ref = new VodReplicationReference(obj, uuid, version, true);
		_replicationReferences.put(ref);
		return ref;
	}

	public void replicateDeletion(DrsUUID uuid) {
		Object object = _jdo.objectByLoid(loidFrom(uuid));
		if (object == null) {
			return;
		}
		_jdo.delete(object);
	}

	public void rollbackReplication() {
		clearAllReferences();
		_jdo.rollback();
		_cobra.rollback();
	}

	public void startReplicationTransaction(ReadonlyReplicationProviderSignature peer) {
		clearAllReferences();
		byte[] signature = peer.getSignature();
		int peerId = dbIdFrom(signature);
		int lowerId = Math.min(peerId, _myDatabaseId);
		int higherId = Math.max(peerId, _myDatabaseId);
		
		
		// TODO: Enhance QLin for deep queries. Retrieve from Cobra directly here.
		
		String filter = "this.lowerPeer.databaseId == " + lowerId + " & this.higherPeer.databaseId == " + higherId;
		ReplicationCommitRecord replicationCommitRecordByJdo = _jdo.queryOneOrNone(ReplicationCommitRecord.class, filter);
		
		if(replicationCommitRecordByJdo == null){
			_replicationCommitRecord = new ReplicationCommitRecord(databaseSignature(lowerId), databaseSignature(higherId));
			_cobra.store(_replicationCommitRecord);
			_cobra.commit();
			return;
		}
		
		long commitRecordLoid = _jdo.loid(replicationCommitRecordByJdo);
		_replicationCommitRecord = _cobra.objectByLoid(commitRecordLoid);
		if(_replicationCommitRecord == null){
			throw new IllegalStateException("Commit record could not be found. loid: " + commitRecordLoid);
		}
	}

	private int dbIdFrom(byte[] signature) {
		Signature peerSignature = new Signature(signature);
		int peerId = _signatures.idFor(peerSignature);
		if(peerId == 0){
			peerId = _jvi.newDbId( peerSignature.toString() );
			storeSignature(peerId, peerSignature);
			_signatures.add(peerId, peerSignature);
		}
		return peerId;
	}

	private DatabaseSignature databaseSignature(int databaseId) {
		DatabaseSignature sig = prototype(DatabaseSignature.class);
		return _cobra.from(DatabaseSignature.class).where(sig.databaseId()).equal(databaseId).single();
	}

	public void storeReplica(Object obj) {
		logIdentity(obj, getName());
		ReplicationReference ref = _replicationReferences.get(obj);
		
		long loid = 0;
		if (ref instanceof VodReplicationReference) {
			
			int otherDb = dbIdFrom(ref.uuid().getSignaturePart());
			long otherLongPart = ref.uuid().getLongPart();
			
			loid = _cobra.store(obj);
			
			VodId vodId = _cobra.idFor(loid);
			Signature signature = produceSignatureFor(vodId.databaseId);
			VodUUID uuid = new VodUUID(signature, vodId);
			
			_cobra.store(new UuidMapping(otherDb, otherLongPart, dbIdFrom(uuid.getSignaturePart()), uuid.getLongPart()));
				
		} else {

			loid = loidFrom(ref.uuid());
			
			_cobra.store(loid, obj);
		}
		
		logIdentity(obj, String.valueOf(loid));
	}

	public void syncVersionWithPeer(long maxVersion) {
		_replicationCommitRecord.timestamp(maxVersion);
		_cobra.store(_replicationCommitRecord);
		syncEventProcessor().syncTimestamp(maxVersion);
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
		return internalObjectsChangedSinceLastReplication("this.classMetadataLoid == " + _knownClasses.get(clazz.getName()));
	}

	private ObjectSet internalObjectsChangedSinceLastReplication(String query) {
		long lastReplicationVersion = getLastReplicationVersion();
		String fullQuery = "this.timestamp > " + lastReplicationVersion;
		if(!query.isEmpty()) {
			fullQuery += " && " + query;
		}
		Set<Long> loids = new HashSet<Long>();
		Collection<ObjectLifecycleEvent> allEvents = _jdo.query(ObjectLifecycleEvent.class, fullQuery);
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

		
		long loid;
		
		
		loid = tryMapping(uuid);
		
		if (loid == 0) {
			loid = loidFrom(uuid);
		}
		
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

	private long tryMapping(DrsUUID uuid) {

		int otherDb = dbIdFrom(uuid.getSignaturePart());
		long otherLongPart = uuid.getLongPart();

		UuidMapping p = prototype(UuidMapping.class);
		ObjectSet<UuidMapping> mapping = _cobra.from(UuidMapping.class).where(p.otherLongPart()).equal(otherLongPart).select();
		for (UuidMapping uuidMapping : mapping) {
			if (otherDb == uuidMapping.otherDb()) {
				return loidFrom(uuidMapping.mineDb(), uuidMapping.mineLongPart());
			}
		}
		return 0;
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
		_isolatedMode = true;
		syncEventProcessor().requestIsolation(true);
		try {
			block.run();
		}
		finally {
			syncEventProcessor().requestIsolation(false);
			_isolatedMode = false;
		}
		ensureChangeCount();
	}

	private void ensureChangeCount() {
		if (expectedChangeCount > 0) {
			syncEventProcessor().ensureChangecount(expectedChangeCount);
			expectedChangeCount = 0;
		}
	}

	public ObjectLifecycleMonitor syncEventProcessor() {
		return _control.sync();
	}
	
	public ObjectLifecycleMonitor asyncEventProcessor() {
		return _control.async();
	}

	public void pinging(boolean pinging) {
		this.pinging = pinging;
	}

	public boolean pinging() {
		return pinging;
	}
	
}
