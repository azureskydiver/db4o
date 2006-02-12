package com.db4o.test.replication.provider;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.ReplicationReferenceImpl;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.replication.hibernate.PeerSignature;
import com.db4o.test.Test;
import com.db4o.test.replication.collections.ListHolder;

import java.util.Collection;
import java.util.Vector;

public abstract class ReplicationProviderTest extends Test {

	private static final byte[] PEER_SIGNATURE_BYTES = new byte[]{99, -1, 42, 17};
	private static final PeerSignature PEER_SIGNATURE = new PeerSignature(PEER_SIGNATURE_BYTES);

	public void testReplicationProvider() {
		tstSignature();
		tstVersionIncrement();
		tstObjectsChangedSinceLastReplication();
		tstReferences();
		tstStore();
		tstRollback();
		tstCollection();
	}

	private void tstCollection() {
		TestableReplicationProviderInside subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);

		ListHolder lh = new ListHolder("i am a list");
		Db4oUUID lhUuid = new Db4oUUID(1234, PEER_SIGNATURE_BYTES);

		ReplicationReference ref = new ReplicationReferenceImpl("ignoredSinceInOtherProvider", lhUuid, 1000);
		subject.referenceNewObject(lh, ref, null, null);
		subject.storeReplica(lh);
		ensure(subject.produceReference(lh, null, null).object() == lh);

		Collection collection = lh.getList();
		Db4oUUID collectionUuid = new Db4oUUID(4567, PEER_SIGNATURE_BYTES);

		ReplicationReference collectionRef = new ReplicationReferenceImpl("ignoredSinceInOtherProvider", collectionUuid, 1000);
		subject.referenceNewObject(collection, collectionRef, ref, "list");
		subject.storeReplica(collection);
		ensure(subject.produceReference(collection, lh, "list").object() == collection);
		ensure(subject.produceReference(collection, null, null).object() == collection);
	}

	protected abstract TestableReplicationProviderInside prepareSubject();


	private void tstRollback() {
		if (!subjectSupportsRollback()) return;

		TestableReplicationProviderInside subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);
		Pilot object1 = new Pilot("John Cleese", 42);
		Db4oUUID uuid = new Db4oUUID(5678, PEER_SIGNATURE_BYTES);

		ReplicationReference ref = new ReplicationReferenceImpl(object1, uuid, 1);
		subject.referenceNewObject(object1, ref, null, null);

		subject.storeReplica(object1);
		ensure(!subject.wasChangedSinceLastReplication(ref));

		subject.rollbackReplication();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		ensure(null == subject.produceReference(object1, null, null));
		ReplicationReference byUUID = subject.produceReferenceByUUID(uuid, object1.getClass());
		ensure(null == byUUID);
	}

	protected abstract boolean subjectSupportsRollback();

	private void tstStore() {
		TestableReplicationProviderInside subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);
		Pilot object1 = new Pilot("John Cleese", 42);
		Db4oUUID uuid = new Db4oUUID(1234, PEER_SIGNATURE_BYTES);

		ReplicationReference ref = new ReplicationReferenceImpl("ignoredSinceInOtherProvider", uuid, 1);
		subject.referenceNewObject(object1, ref, null, null);

		subject.storeReplica(object1);
		ReplicationReference reference = subject.produceReferenceByUUID(uuid, object1.getClass());
		ensure(subject.produceReference(object1, null, null) == reference);
		ensure(reference.object() == object1);
		subject.syncVersionWithPeer(5000);
		subject.commitReplicationTransaction(5001);

		subject.startReplicationTransaction(PEER_SIGNATURE);
		object1._name = "i am updated";
		subject.storeReplica(object1);
		subject.syncVersionWithPeer(6000);
		subject.clearAllReferences();

		subject.commitReplicationTransaction(6001);

		subject.startReplicationTransaction(PEER_SIGNATURE);

		reference = subject.produceReferenceByUUID(uuid, object1.getClass());
		ensure(((Pilot) reference.object())._name.equals("i am updated"));

	}

	private void tstReferences() {
		TestableReplicationProviderInside subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);

		Pilot object1 = new Pilot("tst References", 42);
		ensure(subject.produceReference(object1, null, null) == null);

		subject.storeNew(object1);

		ReplicationReference reference = subject.produceReference(object1, null, null);
		ensure(reference.object() == object1);

		Db4oUUID uuid = reference.uuid();
		ReplicationReference ref2 = subject.produceReferenceByUUID(uuid, Pilot.class);
		ensure(ref2.equals(reference));

		subject.clearAllReferences();
		ensure(!subject.hasReplicationReferenceAlready(object1));
		Db4oUUID db4oUUID = subject.produceReference(object1, null, null).uuid();
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

		subject.syncVersionWithPeer(99);
		subject.commitReplicationTransaction(100);

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

		subject.syncVersionWithPeer(5000);
		subject.commitReplicationTransaction(5001);

		subject.startReplicationTransaction(PEER_SIGNATURE);

		long version = subject.getCurrentVersion();

		ensure(version >= 5000 && version <= 5002);
		long v = subject.getLastReplicationVersion();
		ensure(subject.getLastReplicationVersion() == 5000);
	}


	private TestableReplicationProviderInside tstSignature() {
		TestableReplicationProviderInside subject = prepareSubject();
		ensure(subject.getSignature() != null);
		return subject;
	}


	static private Vector toVector(ObjectSet iterator) {
		Vector result = new Vector();
		while (iterator.hasNext()) result.add(iterator.next());
		return result;
	}


}
