package com.db4o.test.replication.provider;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ReplicationReferenceImpl;
import com.db4o.replication.hibernate.metadata.PeerSignature;
import com.db4o.test.Test;

public abstract class ReplicationProviderTest extends Test {
	protected TestableReplicationProviderInside subject;
	protected static final byte[] PEER_SIGNATURE_BYTES = new byte[]{1, 2, 3, 4};
	protected static final PeerSignature PEER_SIGNATURE = new PeerSignature(PEER_SIGNATURE_BYTES);

	public void testReplicationProvider() {
		prepare();
		tstSignature();

		prepare();
		tstVersionIncrement();

		prepare();
		tstObjectsChangedSinceLastReplication();

		prepare();
		tstReferences();

		prepare();
		tstStore();

		prepare();
		tstRollback();

		prepare();
		tstDeletion();

		destroySubject();
	}

	protected void prepare() {
		if (subject != null) destroySubject();
		subject = prepareSubject();
	}

	protected void tstDeletion() {
		subject.storeNew(new Pilot("John Cleese", 42));
		subject.commit();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		subject.syncVersionWithPeer(105);
		subject.commitReplicationTransaction(106);

		subject.delete(Pilot.class);
		subject.commit();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		ObjectSet deletedUuids = subject.uuidsDeletedSinceLastReplication();
		deletedUuids.next();
		ensure(!deletedUuids.hasNext());
		subject.syncVersionWithPeer(106);
		subject.commitReplicationTransaction(107);

		Car root = new Car("Ferrari");
		root._pilot = new Pilot("Terry Gilliam", 33);
		subject.storeNew(root);
		subject.commit();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		subject.syncVersionWithPeer(107);
		subject.commitReplicationTransaction(108);

		ObjectSet cars = subject.getStoredObjects(Car.class);
		ensure(cars.hasNext());
		Car loadedRoot = (Car) cars.next();
		subject.deleteGraph(loadedRoot);
		subject.commit();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		deletedUuids = subject.uuidsDeletedSinceLastReplication();
		deletedUuids.next();
		deletedUuids.next();
		ensure(!deletedUuids.hasNext());

	}

	protected abstract TestableReplicationProviderInside prepareSubject();

	protected abstract void destroySubject();

	private void tstRollback() {
		if (!subjectSupportsRollback()) return;

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

		subject.clearAllReferences();

		subject.syncVersionWithPeer(6000);
		subject.commitReplicationTransaction(6001);

		subject.startReplicationTransaction(PEER_SIGNATURE);

		System.err.println("Uncomment in ReplicationProviderTest");
		//reference = subject.produceReferenceByUUID(uuid, object1.getClass());
		//ensure(((Pilot) reference.object())._name.equals("i am updated"));
	}

	private void tstReferences() {
		subject.storeNew(new Pilot("tst References", 42));

		subject.startReplicationTransaction(PEER_SIGNATURE);

		Pilot object1 = (Pilot) subject.getStoredObjects(Pilot.class).next();

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
		Pilot object1 = new Pilot("John Cleese", 42);
		Pilot object2 = new Pilot("Terry Gilliam", 53);
		Car object3 = new Car("Volvo");

		subject.storeNew(object1);
		subject.storeNew(object2);
		subject.storeNew(object3);

		subject.startReplicationTransaction(PEER_SIGNATURE);

		System.err.println("Uncomment in ReplicationProviderTest");
		//ensure(subject.objectsChangedSinceLastReplication().size() == 3);

		ObjectSet pilots = subject.objectsChangedSinceLastReplication(Pilot.class);
		String name1 = ((Pilot) pilots.next())._name;
		String name2 = ((Pilot) pilots.next())._name;
		ensure(name1.equals("John Cleese") || name1.equals("Terry Gilliam"));
		ensure(name2.equals("John Cleese") || name2.equals("Terry Gilliam"));
		ensure(!pilots.hasNext());

		ObjectSet cars = subject.objectsChangedSinceLastReplication(Car.class);
		ensure(((Car) cars.next()).getModel().equals("Volvo"));
		ensure(!cars.hasNext());

		subject.syncVersionWithPeer(9800);
		subject.commitReplicationTransaction(9801);

		subject.startReplicationTransaction(PEER_SIGNATURE);

		ensure(!subject.objectsChangedSinceLastReplication().hasNext());
		subject.syncVersionWithPeer(9908);
		subject.commitReplicationTransaction(9909);

		Pilot pilot = (Pilot) subject.getStoredObjects(Pilot.class).next();
		pilot._name = "Terry Jones";

		Car car = (Car) subject.getStoredObjects(Car.class).next();
		car.setModel("McLaren");

		subject.update(pilot);
		subject.update(car);

		subject.startReplicationTransaction(PEER_SIGNATURE);
		System.err.println("Uncomment in ReplicationProviderTest");
		//ensure(subject.objectsChangedSinceLastReplication().size() == 2);

		pilots = subject.objectsChangedSinceLastReplication(Pilot.class);
		ensure(((Pilot) pilots.next())._name.equals("Terry Jones"));
		ensure(!pilots.hasNext());

		cars = subject.objectsChangedSinceLastReplication(Car.class);
		ensure(((Car) cars.next()).getModel().equals("McLaren"));
		ensure(!cars.hasNext());
	}


	private void tstVersionIncrement() {
		subject.startReplicationTransaction(PEER_SIGNATURE);

		// This won't work for db4o: There is no guarantee that the version starts with 1.
		// ensure(subject.getCurrentVersion() == 1);

		subject.syncVersionWithPeer(5000);
		subject.commitReplicationTransaction(5001);

		subject.startReplicationTransaction(PEER_SIGNATURE);

		long version = subject.getCurrentVersion();

		ensure(version >= 5000 && version <= 5002);
	}


	private void tstSignature() {
		ensure(subject.getSignature() != null);
	}


}
