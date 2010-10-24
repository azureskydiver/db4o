/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import static com.db4o.drs.foundation.Logger4Support.*;
import static com.db4o.qlin.QLinSupport.*;

import java.util.*;

import javax.jdo.spi.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.versant.VodJdo.PreStoreListener;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.EventProcessor.EventProcessorListener;
import com.db4o.drs.versant.ipc.EventProcessorNetwork.ClientChannelControl;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectInfo.*;
import com.db4o.foundation.*;


public class VodReplicationProvider implements TestableReplicationProviderInside, LoidProvider {
	
	private static final int COMM_HEARTBEAT = 2000;

	private final VodDatabase _vod;
	
	private final VodCobraFacade _cobra;
	
	private final VodJdoFacade _jdo;
	
	private final VodDatabaseIdFactory _idFactory;

	private GenericObjectReferenceMap<ReplicationReferenceImpl> _replicationReferences = new GenericObjectReferenceMap<ReplicationReferenceImpl>();
	
	private final Signatures _signatures = new Signatures();
	
	private final Map<String, Long> _knownClasses = new HashMap<String, Long>();

	private ReplicationReflector _replicationReflector;
	
	private ReplicationCommitRecord _replicationCommitRecord;
	
	private final ReadonlyReplicationProviderSignature _mySignature;
	
	private final short _myDatabaseId;
	
	private volatile long _timeStamp;
	
	private List<Pair<Long, Long>> _loidTimeStamps = new ArrayList<Pair<Long, Long>>(); 
	
	private List<LoidSignatureLongPart> _loidSignatures = new ArrayList<LoidSignatureLongPart>();
	
	List<Long> _ignoreEventsForLoid = new java.util.LinkedList<Long>();
	
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
		
		final Signature mySignature = produceSignatureForDatabaseId(_myDatabaseId);
		_jdo.commit();
		prepareJdoListener();
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
	
	
	private static final Class[] IGNORED_CLASSES = {ObjectInfo.class};

	private void prepareJdoListener() {
		_jdo.addPreStoreListener(new PreStoreListener() {
			public void preStore(Object object) {
				Class clazz = object.getClass();
				for (int i = 0; i < IGNORED_CLASSES.length; i++) {
					if(clazz == IGNORED_CLASSES[i]){
						return;
					}
				}
				String className = clazz.getName();
				Long classMetadataLoid = _knownClasses.get(className);
				if(classMetadataLoid == null) {
					 classMetadataLoid = ensureClassKnown(clazz);
				}
				long loid = loid(object);
				log("Timestamp for " + loid + " preset to " + timeStamp());
				_loidTimeStamps.add(new Pair(loid, timeStamp()));
			}
		});
	}

	private long ensureClassKnown(Class clazz) {
		String className = clazz.getName();
		Long loid = _knownClasses.get(className);
		if (loid != null) {
			return loid;
		}
		String schemaName = _cobra.schemaName(clazz);
		ClassMetadata cm = new ClassMetadata(schemaName, className);
		_cobra.store(cm);
		log("Classmetadata stored " + className);
		_cobra.commit();
		loid = cm.loid();
		_knownClasses.put(className, loid);
		syncEventProcessor().ensureMonitoringEventsOn(className);
		return loid; 
	}
	
	private void loadKnownClasses() {
		for (ClassMetadata classMetadata : _cobra.query(ClassMetadata.class)) {
			_knownClasses.put(classMetadata.fullyQualifiedName(), classMetadata.loid());
		}
	}

	private void loadSignatures() {
		for (DatabaseSignature entry : _cobra.query(DatabaseSignature.class)) {
			if(DrsDebug.verbose){
				System.out.println(entry);
			}
			_signatures.add( entry.databaseId(),new Signature(entry.signature()), entry.loid());
		}
	}
	
	
	public void commit() {
		timeStamp(syncEventProcessor().generateTimestamp());
		_jdo.commit();
		syncEventProcessor().forceTimestampsAndSignatures(_loidTimeStamps, _loidSignatures);
		_loidTimeStamps.clear();
		_loidSignatures.clear();
	}

	public void delete(Object obj) {
		_jdo.delete(obj);
	}

	public void deleteAllInstances(Class clazz) {
		if(!_cobra.isKnownClass(clazz)) {
			return;
		}
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
		ensureClassKnown(obj.getClass());
		_jdo.store(obj);
	}

	public void update(Object obj) {
		// do nothing
		// JDO is transparent persistence
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
		timeStamp(syncEventProcessor().generateTimestamp());
		timeStamp(raisedDatabaseVersion - 1);
		
		_jdo.commit();
		syncEventProcessor().forceTimestampsAndSignatures(_loidTimeStamps, _loidSignatures);
		_loidTimeStamps.clear();
		_loidSignatures.clear();
		
		syncEventProcessor().syncTimestamp(raisedDatabaseVersion -1);
		timeStamp(raisedDatabaseVersion);
		
		// _timeStamp  = UNDEFINED;
		
		// FileReplicationProvider does this:
		// _idsReplicatedInThisSession = null;
		
		// HibernateReplicationProvider does this:
		// _dirtyRefs.clear();
		// _inReplication = false;
		
	}

	public long getCurrentVersion() {
		return syncEventProcessor().lastTimestamp();
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
		ReplicationReferenceImpl reference = _replicationReferences.get(obj);
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
		Signature signature = new Signature(uuid.getSignaturePart()); 
		ObjectInfo objectInfo = prototype(ObjectInfo.class);
		ObjectSet<ObjectInfo> infos = _cobra.from(ObjectInfo.class)
			.where(objectInfo.uuidLongPart()).equal(uuid.getLongPart()).select();
		for (ObjectInfo info : infos) {
			if(signature.equals(_signatures.signatureForLoid(info.signatureLoid()))){
				return info.objectLoid();
			}
		}
		return 0;
	}

	public ReplicationReference referenceNewObject(Object obj,
			ReplicationReference counterpartReference,
			ReplicationReference referencingObjRef, String fieldName) {
		
		DrsUUID uuid = counterpartReference.uuid();
		
		long version = counterpartReference.version();
		
		ReplicationReferenceImpl ref = new ReplicationReferenceImpl(obj, uuid, version);
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
		
		produceCommitRecord(lowerId, higherId);
	}

	private void produceCommitRecord(int lowerId, int higherId) {
		_replicationCommitRecord = commitRecordFor(lowerId, higherId);
		
		if(_replicationCommitRecord == null){
			_replicationCommitRecord = new ReplicationCommitRecord(databaseSignature(lowerId), databaseSignature(higherId));
			_cobra.store(_replicationCommitRecord);
			_cobra.commit();
		}
	}

	private ReplicationCommitRecord commitRecordFor(int lowerId, int higherId) {
		Collection<ReplicationCommitRecord> q = _cobra.query(ReplicationCommitRecord.class);
		for (ReplicationCommitRecord r : q) {
			if (r.lowerPeer().databaseId() == lowerId && r.higherPeer().databaseId() == higherId) {
				return r;
			}
		}
		return null;
	}

	private int dbIdFrom(byte[] signature) {
		Signature peerSignature = new Signature(signature);
		int peerId = _signatures.idFor(peerSignature);
		if(peerId == 0){
			peerId = _idFactory.createDatabaseIdFor(VodJvi.safeDatabaseName(peerSignature.asString()));
			storeSignature(peerId, peerSignature);
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
		
		ReplicationReferenceImpl ref = _replicationReferences.get(obj);
		if (ref == null) {
			throw new RuntimeException("Reference should always be available before storeReplica");
		}
		
		ensureClassKnown(obj.getClass());
		
		long loid = _jdo.loid(obj);
		
		boolean isNew = loid == 0;
		
		_jdo.store(obj);
		loid = _jdo.loid(obj);

		if (isNew) {
			Signature signature = new Signature(ref.uuid().getSignaturePart());
			int otherDb = _signatures.idFor(signature);
			if(otherDb == 0) {
				throw new IllegalArgumentException("Unknown db id for " + ref.uuid());
			}
			long otherLongPart = ref.uuid().getLongPart();
			
			_loidSignatures.add(
					new LoidSignatureLongPart(
							loid, 
							_signatures.loidFor(signature), 
							otherLongPart));
		}
		
		logIdentity(obj, String.valueOf(loid));
	}

	public void syncVersionWithPeer(long maxVersion) {
		log(" version synced to " + maxVersion);
		_replicationCommitRecord.timestamp(maxVersion);
		_cobra.store(_replicationCommitRecord);
		_cobra.commit();
		syncEventProcessor().syncTimestamp(maxVersion);
	}

	public void visitCachedReferences(Visitor4 visitor) {
		_replicationReferences.visitEntries(visitor);
	}

	public boolean wasModifiedSinceLastReplication(ReplicationReference reference) {
		log("comparing versions reference.version()=" + reference.version() + " getLastReplicationVersion()=" + getLastReplicationVersion());
		return reference.version() > getLastReplicationVersion();
	}

	public ObjectSet objectsChangedSinceLastReplication() {
		long lastReplicationVersion = getLastReplicationVersion();
		String filter = "this.modificationVersion > " + lastReplicationVersion;
		Set<Long> loids = new HashSet<Long>();
		Collection<ObjectInfo> infos = _jdo.query(ObjectInfo.class, filter);
		for (ObjectInfo info : infos) {
			if(Operations.forValue(info.operation()) != Operations.DELETE){
				loids.add(info.objectLoid());
			}
		}
		Collection<Object> objects = new ArrayList<Object>(loids.size());
		for (Long loid : loids) {
			objects.add(_jdo.objectByLoid(loid));
		}
		return new ObjectSetCollectionFacade(objects);
	}

	public ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		String query = "this.classMetadataLoid == " + _knownClasses.get(clazz.getName());
		long lastReplicationVersion = getLastReplicationVersion();
		String fullQuery = "this.modificationVersion > " + lastReplicationVersion;
		if(query.length() > 0) {
			fullQuery += " && " + query;
		}
		Set<Long> loids = new HashSet<Long>();
		Collection<ObjectInfo> infos = _jdo.query(ObjectInfo.class, fullQuery);
		for (ObjectInfo info : infos) {
			if(Operations.forValue(info.operation()) != Operations.DELETE){
				loids.add(info.objectLoid());
			}
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
	
	private ReplicationReferenceImpl produceNewReference(final Object obj) {
		if(obj == null){
			throw new IllegalArgumentException();
		}
		long loid = _jdo.loid(obj);
		if(loid == 0) {
			return null;
		}
		ObjectInfo objectInfo = prototype(ObjectInfo.class);
		ObjectInfo info = 
			_cobra.from(ObjectInfo.class)
				.where(objectInfo.objectLoid())
				.equal(loid)
				.singleOrDefault(null);
		if(DrsDebug.verbose){
			System.out.println("#creationVersion() found: " + info);
		}
		if(info == null){
			throw new IllegalStateException("no ObjectLifecycleEvent found");
		}
		Signature signature = _signatures.signatureForLoid(info.signatureLoid());
		if(signature == null){
			throw new IllegalStateException("signature not expected to be null ");
		}
		DrsUUIDImpl uuid = new DrsUUIDImpl(signature, info.uuidLongPart());
		return new ReplicationReferenceImpl(obj, uuid, info.modificationVersion());
	}
	
	public ReplicationReferenceImpl produceReferenceByUUID(DrsUUID uuid, Class hint) {
		if(uuid == null){
			throw new IllegalArgumentException();
		}
		ReplicationReferenceImpl reference = _replicationReferences.getByUUID(uuid);
		if(reference != null){
			return reference;
		}
		ObjectInfo objectInfo = prototype(ObjectInfo.class);
		ObjectSet<ObjectInfo> infos = _cobra
			.from(ObjectInfo.class)
			.where(objectInfo.uuidLongPart())
			.equal(uuid.getLongPart())
			.select();
		if(infos.size() == 0){
			return null;
		}
		
		long signatureLoid = _signatures.loidFor(new Signature(uuid.getSignaturePart()));
		long loid = 0;
		for (ObjectInfo info : infos) {
			if(info.signatureLoid() == signatureLoid){
				loid = info.objectLoid();
				break;
			}
		}
		
		if(loid == 0){
			throw new IllegalStateException("Could not create loid from " + uuid);
		}
		
		// TODO: remove this checking after VdsUtils#getObjectByLOID is fixed
		if (!_cobra.containsLoid(loid)) {
			return null;
		}
		reference = produceNewReference(_jdo.objectByLoid(loid));
		_replicationReferences.put(reference);
		return reference; 
	}

	private Signature produceSignatureForDatabaseId(int databaseId) {
		Signature signature = _signatures.signatureForDatabaseId(databaseId);
		if(signature != null){
			return signature;
		}
		signature = new Signature(_cobra.signatureBytes(databaseId));
		storeSignature(databaseId, signature);
		return signature;
	}

	private void storeSignature(int databaseId, Signature signature) {
		DatabaseSignature databaseSignature = new DatabaseSignature(databaseId, signature.bytes);
		_cobra.store(databaseSignature);
		_cobra.commit();
		_signatures.add(databaseId, signature, databaseSignature.loid());
	}
	

	public long loid(Object obj) {
		return _jdo.loid(obj);
	}

	public void runIsolated(Block4 block) {
		syncEventProcessor().requestIsolation(true);
		try {
			block.run();
		}
		finally {
			syncEventProcessor().requestIsolation(false);
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

	private void timeStamp(long timeStamp) {
		_timeStamp = timeStamp;
	}

	private long timeStamp() {
		return _timeStamp;
	}

	@Override
	public String toString() {
		return getName();
	}

	public void commitAndWaitFor(Object modifiedObject) {
		long expectedLoid = loid(modifiedObject);
		final BlockingQueue<Long> events = new BlockingQueue<Long>();
		EventProcessorListener listener = new EventProcessorListener() {
			public void ready() {
				
			}
			
			public void onEvent(long loid) {
				events.add(loid);
			}
			
			public void committed(String transactionId) {
				
			}
		};
		syncEventProcessor().addListener(listener);
		commit();
		long actualLoid = events.next();
		while(expectedLoid != actualLoid){
			actualLoid = events.next();
		}
		syncEventProcessor().removeListener(listener);
	}
	
}
