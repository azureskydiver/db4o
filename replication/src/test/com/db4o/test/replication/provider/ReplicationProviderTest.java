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
		clean();

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

		clean();

		destroySubject();
	}

	protected void clean() {
		//do nothing
	}

	protected void prepare() {
		if (subject != null) destroySubject();
		subject = prepareSubject();
	}

	protected void tstDeletion() {
		subject.storeNew(new Pilot("Pilot1", 42));
		subject.storeNew(new Pilot("Pilot2", 43));
		subject.commit();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		Db4oUUID uuidPilot1 = uuid(findPilot("Pilot1"));
		subject.syncVersionWithPeer(9999);
		subject.commitReplicationTransaction(10005);

		subject.storeNew(new Pilot("Pilot3", 44));

		subject.delete(findPilot("Pilot1"));

		Car car = new Car("Car1");
		car._pilot = findPilot("Pilot2");
		subject.storeNew(car);

		subject.commit();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		ObjectSet deletedUuids = subject.uuidsDeletedSinceLastReplication();
		ensure(deletedUuids.next().equals(uuidPilot1));
		ensure(!deletedUuids.hasNext());

		Db4oUUID uuidCar1 = uuid(findCar("Car1"));
		Db4oUUID uuidPilot2 = uuid(findPilot("Pilot2"));

		subject.syncVersionWithPeer(12000);
		subject.commitReplicationTransaction(12500);

		subject.delete(findCar("Car1"));
		subject.delete(findPilot("Pilot2"));
		subject.commit();

		subject.startReplicationTransaction(PEER_SIGNATURE);
		deletedUuids = subject.uuidsDeletedSinceLastReplication();
		ensure(deletedUuids.contains(uuidCar1));
		ensure(deletedUuids.contains(uuidPilot2));
		ensure(deletedUuids.size() == 2);
		subject.commitReplicationTransaction(13000);

		subject.deleteAllInstances(Car.class);
		subject.deleteAllInstances(Pilot.class);
		subject.commit();
	}

	private Db4oUUID uuid(Object obj) {
		return subject.produceReference(obj, null, null).uuid();
	}

	private Object findCar(String model) {
		ObjectSet cars = subject.getStoredObjects(Car.class);
		while (cars.hasNext()) {
			Car candidate = (Car) cars.next();
			if (candidate.getModel().equals(model)) return candidate;
		}
		return null;
	}

	private Pilot findPilot(String name) {
		ObjectSet pilots = subject.getStoredObjects(Pilot.class);
		while (pilots.hasNext()) {
			Pilot candidate = (Pilot) pilots.next();
			if (candidate._name.equals(name)) return candidate;
		}
		return null;
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
		ensure(!subject.wasModifiedSinceLastReplication(ref));

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
		subject.syncVersionWithPeer(8500);
		subject.commitReplicationTransaction(8800);

		subject.startReplicationTransaction(PEER_SIGNATURE);
		ObjectSet<Pilot> storedObjects = subject.getStoredObjects(Pilot.class);
		ensure(storedObjects.hasNext());

		Pilot reloaded = storedObjects.next();

		ensure(!storedObjects.hasNext());

		ensure(subject.produceReference(reloaded, null, null).equals(reference));

		reloaded._name = "i am updated";
		subject.storeReplica(reloaded);

		subject.clearAllReferences();

		subject.syncVersionWithPeer(9000);
		subject.commitReplicationTransaction(9250);

		subject.startReplicationTransaction(PEER_SIGNATURE);

		reference = subject.produceReferenceByUUID(uuid, reloaded.getClass());
		ensure(((Pilot) reference.object())._name.equals("i am updated"));
		subject.syncVersionWithPeer(9500);
		subject.commitReplicationTransaction(9800);
		subject.deleteAllInstances(Pilot.class);
		subject.commit();
	}

	private void tstReferences() {
		subject.storeNew(new Pilot("tst References", 42));
		subject.commit();

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
		subject.syncVersionWithPeer(7500);
		subject.commitReplicationTransaction(8000);
		subject.deleteAllInstances(Pilot.class);
		subject.commit();
	}


	private void tstObjectsChangedSinceLastReplication() {
		Pilot object1 = new Pilot("John Cleese", 42);
		Pilot object2 = new Pilot("Terry Gilliam", 53);
		Car object3 = new Car("Volvo");

		subject.storeNew(object1);
		subject.storeNew(object2);
		subject.storeNew(object3);

		subject.commit();

		subject.startReplicationTransaction(PEER_SIGNATURE);

		int i = subject.objectsChangedSinceLastReplication().size();
		ensure(i == 3);


		ObjectSet pilots = subject.objectsChangedSinceLastReplication(Pilot.class);
		ensure(pilots.size() == 2);
		ensure(pilots.contains(findPilot("John Cleese")));
		ensure(pilots.contains(findPilot("Terry Gilliam")));

		ObjectSet cars = subject.objectsChangedSinceLastReplication(Car.class);
		ensure(cars.hasNext());
		ensure(((Car) cars.next()).getModel().equals("Volvo"));
		ensure(!cars.hasNext());

		subject.syncVersionWithPeer(6000);
		subject.commitReplicationTransaction(6001);

		subject.startReplicationTransaction(PEER_SIGNATURE);

		ensure(!subject.objectsChangedSinceLastReplication().hasNext());
		subject.syncVersionWithPeer(6005);
		subject.commitReplicationTransaction(6500);

		Pilot pilot = (Pilot) subject.getStoredObjects(Pilot.class).next();
		pilot._name = "Terry Jones";

		Car car = (Car) subject.getStoredObjects(Car.class).next();
		car.setModel("McLaren");

		subject.update(pilot);
		subject.update(car);

		subject.startReplicationTransaction(PEER_SIGNATURE);
		ensure(subject.objectsChangedSinceLastReplication().size() == 2);

		pilots = subject.objectsChangedSinceLastReplication(Pilot.class);
		ensure(((Pilot) pilots.next())._name.equals("Terry Jones"));
		ensure(!pilots.hasNext());

		cars = subject.objectsChangedSinceLastReplication(Car.class);
		ensure(((Car) cars.next()).getModel().equals("McLaren"));
		ensure(!cars.hasNext());
		subject.syncVersionWithPeer(6800);
		subject.commitReplicationTransaction(7000);

		subject.deleteAllInstances(Pilot.class);
		subject.deleteAllInstances(Car.class);
		subject.commit();
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
