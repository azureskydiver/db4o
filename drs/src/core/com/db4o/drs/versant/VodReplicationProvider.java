/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import static com.db4o.drs.foundation.Logger4Support.*;
import static com.db4o.qlin.QLinSupport.*;

import java.util.*;

import javax.jdo.spi.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.versant.VodJdo.ObjectCommittedListener;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.EventProcessor.EventProcessorListener;
import com.db4o.drs.versant.ipc.EventProcessorNetwork.ClientChannelControl;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectInfo.Operations;
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

	private ReplicationCommitRecord _replicationCommitRecord;
	
	private final ReadonlyReplicationProviderSignature _mySignature;
	
	private final short _myDatabaseId;
	
	private volatile long _timeStamp;
	
	private List<Pair<Long, Long>> _loidTimeStamps = new ArrayList<Pair<Long, Long>>(); 
	
	List<Long> _ignoreEventsForLoid = new java.util.LinkedList<Long>();
	
	private final Map<Class, List<Class>> _classHierarchy = new HashMap<Class, List<Class>>();
	
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

	private long _sampleCommitLoid = 0;
	
	private boolean _waitForCommitLoid = false;
	
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
		_jdo.addObjectCommittedListener(new ObjectCommittedListener() {
			public void committed(Object object) {
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
				_sampleCommitLoid = loid;
			}
		});
	}

	private long ensureClassKnown(Class clazz) {
		ensureClassHierarchyKnown(clazz);
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
			try {
				ensureClassHierarchyKnown(Class.forName(classMetadata.fullyQualifiedName()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void ensureClassHierarchyKnown(Class clazz) {
		if(clazz == Object.class){
			return;
		}
		if (_classHierarchy.containsKey(clazz)){
			return;
		}
		addToHierarchy(clazz);
	}

	private void addToHierarchy(Class clazz) {
		Class superclass = clazz.getSuperclass();
		ensureClassHierarchyKnown(superclass);
		if(superclass != Object.class){
			List<Class> children = _classHierarchy.get(superclass);
			children.add(clazz);
		}
		_classHierarchy.put(clazz, new ArrayList<Class>());
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
		
		if(! _waitForCommitLoid){
			internalCommit();
			return;
		}
		final long timeStampToCompare = timeStamp();
		final BlockingQueue<Long> processedObjects = new BlockingQueue<Long>();
		EventProcessorListener listener = new AbstractEventProcessorListener() {
			@Override
			public void onEvent(long loid, long version) {
				if(version >= timeStampToCompare){
					processedObjects.add(loid);
				}
			}
		};
		syncEventProcessor().addListener(listener);
		
		internalCommit();
		
		while(true) {
			if (_sampleCommitLoid == (long)processedObjects.next()) {
				break;
			}
		}
		_sampleCommitLoid = 0;
		_waitForCommitLoid = false;
		syncEventProcessor().removeListener(listener);
	}

	private void internalCommit() {
		timeStamp(syncEventProcessor().generateTimestamp());
		syncEventProcessor().requestIsolation(true);
		try {
			_jdo.commit();
			syncEventProcessor().forceTimestamps(_loidTimeStamps);
		} finally {
			syncEventProcessor().requestIsolation(false);
		}
		_loidTimeStamps.clear();
	}

	public void delete(Object obj) {
		_jdo.delete(obj);
		_waitForCommitLoid = true;
	}

	public void deleteAllInstances(Class clazz) {
		if(!_cobra.isKnownClass(clazz)) {
			return;
		}
		if(_jdo.deleteAll(clazz) > 0){
			_waitForCommitLoid = true;
		}
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
		_waitForCommitLoid = true;
	}

	public void update(Object obj) {
		_waitForCommitLoid = true;
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
		return false;
	}

	public boolean supportsMultiDimensionalArrays() {
		return true;
	}

	public boolean supportsRollback() {
		return true;
	}

	public void clearAllReferences() {
		_replicationReferences.clear();
	}

	public void commitReplicationTransaction(long raisedDatabaseVersion) {
		timeStamp(raisedDatabaseVersion - 1);
		
		_jdo.commit();
		syncEventProcessor().forceTimestamps(_loidTimeStamps);
		_loidTimeStamps.clear();
		
		syncEventProcessor().syncTimestamp(raisedDatabaseVersion);
		timeStamp(raisedDatabaseVersion);
		
		_replicationReferences = new GenericObjectReferenceMap<ReplicationReferenceImpl>();
	}

	public long getCurrentVersion() {
		_timeStamp = syncEventProcessor().lastTimestamp(); 
		return _timeStamp;
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
		
		long classMetadataLoid = ensureClassKnown(obj.getClass());
		long loid = _jdo.loid(obj);
		
		boolean isNew = loid == 0;
		
		if(isNew){
			_jdo.store(obj);
			loid = _jdo.loid(obj);
		} else {
			isNew = ! objectInfoFoundFor(loid);
		}

		if (isNew) {
			Signature signature = new Signature(ref.uuid().getSignaturePart());
			int otherDb = _signatures.idFor(signature);
			if(otherDb == 0) {
				throw new IllegalArgumentException("Unknown db id for " + ref.uuid());
			}
			long otherLongPart = ref.uuid().getLongPart();
			long signatureLoid = _signatures.loidFor(signature);
			ObjectInfo objectInfo = new ObjectInfo(signatureLoid, classMetadataLoid, loid,  otherLongPart, timeStamp(), Operations.CREATE.value);
			_cobra.store(objectInfo);
			_cobra.commit();
		}
		
		logIdentity(obj, String.valueOf(loid));
	}

	private boolean objectInfoFoundFor(long loid) {
		ObjectInfo objectInfo = prototype(ObjectInfo.class);
		ObjectInfo foundInfo = _cobra.from(ObjectInfo.class).where(objectInfo.objectLoid()).equal(loid).singleOrDefault(null);
		return foundInfo != null;
	}

	public void syncVersionWithPeer(long maxVersion) {
		log(" version synced to " + maxVersion);
		_replicationCommitRecord.timestamp(maxVersion);
		_cobra.store(_replicationCommitRecord);
		_cobra.commit();
		syncEventProcessor().syncTimestamp(maxVersion);
		_timeStamp = maxVersion;
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
		String filter = "this.version > " + lastReplicationVersion;
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
		long lastReplicationVersion = getLastReplicationVersion();
		Set<Long> loids = queryForModifiedObjects(clazz, lastReplicationVersion, true);
		Collection<Object> objects = new ArrayList<Object>(loids.size());
		for (Long loid : loids) {
			objects.add(_jdo.objectByLoid(loid));
		}
		return new ObjectSetCollectionFacade(objects);
	}

	private Set<Long> queryForModifiedObjects(Class clazz, long lastReplicationVersion, boolean withClassMetadataLoid) {
		String filter = "this.version > " + lastReplicationVersion;
		if(withClassMetadataLoid){
			filter += " && (" + classMetadataLoidFilter(clazz) + ")";
		}
		Set<Long> loids = new HashSet<Long>();
		Collection<ObjectInfo> infos = _jdo.query(ObjectInfo.class, filter);
		for (ObjectInfo info : infos) {
			_jdo.refresh(info);
			if(Operations.forValue(info.operation()) != Operations.DELETE){
				loids.add(info.objectLoid());
			}
		}
		return loids;
	}

	private String classMetadataLoidFilter(Class clazz) {
		String filter = "this.classMetadataLoid == " + _knownClasses.get(clazz.getName());
		List<Class> children = _classHierarchy.get(clazz);
		if(children != null){
			for (Class childClass : children) {
				filter += " || " + classMetadataLoidFilter(childClass);
			}
		}
		return filter;
	}
	
	public void debug(){
		// useful debug code left here, 
		// just set the class to print all ObjectInfos for a class
		Class clazz = null;
		logObjectInfoJdo(clazz);
		logObjectInfoCobra(clazz);
	}
	
	private void logObjectInfoJdo(Class clazz) {
		System.err.println("JDO");
		String filter = classMetadataLoidFilter(clazz);
		Collection<ObjectInfo> infos = _jdo.query(ObjectInfo.class, filter);
		for (ObjectInfo info : infos) {
			if(Operations.forValue(info.operation()) != Operations.DELETE){
				System.err.println(info);
			}
		}
	}
	
	private void logObjectInfoCobra(Class clazz) {
		System.err.println("Cobra");
		ObjectInfo objectInfo = prototype(ObjectInfo.class);
		ObjectSet<ObjectInfo> infos = _cobra.from(ObjectInfo.class).where(objectInfo.classMetadataLoid()).equal(_knownClasses.get(clazz.getName())).select();
		for (ObjectInfo info : infos) {
			if(Operations.forValue(info.operation()) != Operations.DELETE){
				System.err.println(info);
			}
		}
	}

	public void replicationReflector(ReplicationReflector replicationReflector) {
	}

	public boolean isProviderSpecific(Object original) {
		return original.getClass().getName().startsWith("com.versant.");
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
			return null;
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
	
}
