package com.db4o.test.replication.provider;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.*;
import com.db4o.replication.hibernate.*;
import com.db4o.test.Test;
import com.db4o.ObjectSet;

import java.util.Vector;

public abstract class ReplicationProviderTest extends Test {

	private static final byte[] ARBITRARY_SIGNATURE = new byte[]{99, -1, 42, 17};
	private static final PeerSignature PEER_SIGNATURE = new PeerSignature(ARBITRARY_SIGNATURE);

	public void testReplicationProvider() {
		tstSignature();
		tstVersionIncrement();
		tstObjectsChangedSinceLastReplication();
		tstReferences();
		tstStore();
		tstRollback();
	}

	protected abstract TestableReplicationProviderInside prepareSubject();


	private void tstRollback() {
		if (!subjectSupportsRollback()) return;

		TestableReplicationProviderInside subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);
		Pilot object1 = new Pilot("John Cleese", 42);
		Db4oUUID uuid = new Db4oUUID(5678, ARBITRARY_SIGNATURE);
        
        ReplicationReference ref = new ReplicationReferenceImpl(object1, uuid, 1);
        subject.referenceNewObject(object1, ref);
        
		subject.storeReplica(object1);
		subject.rollbackReplication();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		ensure(null == subject.produceReference(object1));
		ReplicationReference byUUID = subject.produceReferenceByUUID(uuid, object1.getClass());
		ensure(null == byUUID);
	}

	protected abstract boolean subjectSupportsRollback();

	private void tstStore() {
		TestableReplicationProviderInside subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);
		Pilot object1 = new Pilot("John Cleese", 42);
		Db4oUUID uuid = new Db4oUUID(1234, ARBITRARY_SIGNATURE);
        
        ReplicationReference ref = new ReplicationReferenceImpl("ignoredSinceInOtherProvider", uuid, 1);
        subject.referenceNewObject(object1, ref);

		subject.storeReplica(object1);
		ReplicationReference reference = subject.produceReferenceByUUID(uuid, object1.getClass());
		ensure(subject.produceReference(object1) == reference);
		ensure(reference.object() == object1);
        subject.storeReplicationRecord(9);
		subject.commit(10);

		subject.startReplicationTransaction(PEER_SIGNATURE);
		object1._name = "i am updated";
		subject.storeReplica(object1);
        subject.storeReplicationRecord(14);
		subject.commit(15);

		subject.clearAllReferences();
		subject.startReplicationTransaction(PEER_SIGNATURE);

		reference = subject.produceReferenceByUUID(uuid, object1.getClass());
		ensure(((Pilot) reference.object())._name.equals("i am updated"));

	}

	private void tstReferences() {
		TestableReplicationProviderInside subject = prepareSubject();

		Pilot object1 = new Pilot("tst References", 42);
		ensure(subject.produceReference(object1) == null);

		subject.startReplicationTransaction(PEER_SIGNATURE);
		subject.storeNew(object1);
	   
		ReplicationReference reference = subject.produceReference(object1);
		ensure(reference.object() == object1);

		Db4oUUID uuid = reference.uuid();
		ReplicationReference ref2 = subject.produceReferenceByUUID(uuid, Pilot.class);
		ensure(ref2.equals(reference));

		subject.clearAllReferences();
		ensure(!subject.hasReplicationReferenceAlready(object1));
		Db4oUUID db4oUUID = subject.produceReference(object1).uuid();
		//TODO implements Db4oUUID.equals, don't use hashcode to compare
		ensure(db4oUUID.equals(uuid));
	}


	private void tstObjectsChangedSinceLastReplication() {
		TestableReplicationProviderInside subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);

		Pilot object1 = new Pilot("John Cleese", 42);
		Pilot object2 = new Pilot("Terry Gilliam", 53);
		Car object3 = new Car("Volvo");
		//Car object4 = new F1Car("Lotus");
		subject.storeNew(object1);
		subject.storeNew(object2);
		subject.storeNew(object3);
		//providerSpecificSave(object4, subject);

		Vector changed = toVector(subject.objectsChangedSinceLastReplication());
		ensure(changed.contains(object1));
		ensure(changed.contains(object2));
		ensure(changed.contains(object3));
		//ensure(changed.contains(object4));

		if (subject instanceof HibernateReplicationProviderImpl) {
			System.err.println("FIX ME, uncommen t, and debug from here ");
			return;
		}

		changed = toVector(subject.objectsChangedSinceLastReplication(Car.class));
		ensure(!changed.contains(object1));
		ensure(!changed.contains(object2));
		ensure(changed.contains(object3));
		//ensure(changed.contains(object4));
        
        subject.storeReplicationRecord(99);
        subject.commit(100);
        
		ensure(!subject.objectsChangedSinceLastReplication().hasNext());

		object1._name = "Terry Jones";
		object3.setModel("McLaren");
		subject.update(object1);
		subject.update(object3);

		changed = toVector(subject.objectsChangedSinceLastReplication());
		ensure(changed.contains(object1));
		ensure(!changed.contains(object2));
		ensure(changed.contains(object3));
	}


	private void tstVersionIncrement() {
		TestableReplicationProviderInside subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);
        
        // This won't work for db4o: There is no guarantee that the version starts with 1.
		// ensure(subject.getCurrentVersion() == 1);
        
        subject.storeReplicationRecord(59);
		subject.commit(60);

		subject.startReplicationTransaction(PEER_SIGNATURE);
        
        long version = subject.getCurrentVersion();
        
		ensure(version >= 60 && version <= 61);
        long v = subject.getLastReplicationVersion();
		ensure(subject.getLastReplicationVersion() == 59);
	}


	private TestableReplicationProviderInside tstSignature() {
		TestableReplicationProviderInside subject = prepareSubject();
		ensure(subject.getSignature() != null);
		return subject;
	}


	static private Vector toVector(ObjectSet  iterator) {
		Vector result = new Vector();
		while (iterator.hasNext()) result.add(iterator.next());
		return result;
	}


}
