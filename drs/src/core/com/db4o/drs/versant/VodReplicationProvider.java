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
import com.db4o.drs.versant.ipc.EventProcessorNetwork.ClientChannelControl;
import com.db4o.drs.versant.jdo.reflect.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;
import com.db4o.internal.encoding.*;
import com.db4o.reflect.*;

public class VodReplicationProvider implements TestableReplicationProviderInside{
	
	private static final int COMM_HEARTBEAT = 2000;

	private final VodDatabase _vod;
	
	private final VodCobraFacade _cobra;
	
	private final VodJdoFacade _jdo;
	
	private final VodDatabaseIdFactory _idFactory;

	private GenericObjectReferenceMap<VodReplicationReference> _replicationReferences = new GenericObjectReferenceMap<VodReplicationReference>();
	
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

	
	public VodReplicationProvider(VodDatabase vod, VodDatabaseIdFactory idFactory) {
		_control = EventProcessorNetworkFactory.newClient(vod);
		_vod = vod;
		_cobra = VodCobra.createInstance(vod);
		_jdo = VodJdo.createInstance(vod);
		_idFactory = idFactory;
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
		if(!_vod.isKnownClass(clazz)) {
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
		String schemaName = _vod.schemaName(clazz);
		ClassMetadata cm = new ClassMetadata(schemaName, className);
		_jdo.store(cm);
		_jdo.commit();
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
		VodReplicationReference reference = _replicationReferences.get(obj);
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
		
		VodReplicationReference ref = new VodReplicationReference(obj, uuid, version, true);
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
	}

	public void startReplicationTransaction(ReadonlyReplicationProviderSignature peer) {
		clearAllReferences();
		byte[] signature = peer.getSignature();
		int peerId = dbIdFrom(signature);
		int lowerId = Math.min(peerId, _myDatabaseId);
		int higherId = Math.max(peerId, _myDatabaseId);
		
		
		// TODO: Enhance QLin for deep queries. Retrieve from Cobra directly here.
		
		String filter = "this.lowerPeer.databaseId == " + lowerId + " & this.higherPeer.databaseId == " + higherId;
		_replicationCommitRecord = _jdo.queryOneOrNone(ReplicationCommitRecord.class, filter);
		
		if(_replicationCommitRecord == null){
			_replicationCommitRecord = new ReplicationCommitRecord(databaseSignature(lowerId), databaseSignature(higherId));
			_jdo.store(_replicationCommitRecord);
			_jdo.commit();
			return;
		}
	}

	private int dbIdFrom(byte[] signature) {
		Signature peerSignature = new Signature(signature);
		int peerId = _signatures.idFor(peerSignature);
		if(peerId == 0){
			peerId = _idFactory.createDatabaseIdFor(VodJvi.safeDatabaseName(peerSignature.toString()));
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
		
		if (!(obj instanceof PersistenceCapable)) {
			throw new IllegalArgumentException(VodReplicationProvider.class.getSimpleName()+" can only handle " + PersistenceCapable.class.getSimpleName() + " objects");
		}
		
		VodReplicationReference ref = _replicationReferences.get(obj);
		
		long loid = 0;
		if (ref.isNew()) {
			
			int otherDb = _signatures.idFor(new Signature(ref.uuid().getSignaturePart()));
			if(otherDb == 0) {
				throw new IllegalArgumentException("Unknown db id for " + ref.uuid());
			}
			long otherLongPart = ref.uuid().getLongPart();
			
			loid = loidFrom(otherDb, otherLongPart);			
			_cobra.create(loid, obj);
			_cobra.commit();
			_replicationReflector.copyState(_jdo.objectByLoid(loid), obj);
				
		} else {

			loid = loidFrom(ref.uuid());
			
			_jdo.store(obj);
		}
		
		logIdentity(obj, String.valueOf(loid));
	}

	public void syncVersionWithPeer(long maxVersion) {
		_replicationCommitRecord.timestamp(maxVersion);
		_jdo.store(_replicationCommitRecord);
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
		if(query.length() > 0) {
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
	
	private VodReplicationReference produceNewReference(final Object obj) {
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
		return new VodReplicationReference(obj, vodUUID, vodId.timestamp);
	}
	
	public VodReplicationReference produceReferenceByUUID(DrsUUID uuid, Class hint) {
		if(uuid == null){
			throw new IllegalArgumentException();
		}
		VodReplicationReference reference = _replicationReferences.getByUUID(uuid);
		if(reference != null){
			return reference;
		}

		
		long loid = loidFrom(uuid);
		
		if(loid == 0){
			throw new IllegalStateException("Could not create loid from " + uuid);
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
			
			// big fat hack. related to DRS-146 and DRS-151
			if (EventProcessorNetworkFactory.USE_IN_BAND_COMMUNICATION) {
				Runtime4.sleepThrowsOnInterrupt(300);
			} else {
				syncEventProcessor().ensureChangecount(expectedChangeCount);
			}
			expectedChangeCount = 0;
		}
	}

	public EventProcessor syncEventProcessor() {
		return _control.sync();
	}
	
	public EventProcessor asyncEventProcessor() {
		return _control.async();
	}

	public void pinging(boolean pinging) {
		this.pinging = pinging;
	}

	public boolean pinging() {
		return pinging;
	}
	
}
