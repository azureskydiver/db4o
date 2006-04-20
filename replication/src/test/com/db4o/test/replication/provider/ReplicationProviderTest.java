package com.db4o.test.replication.provider;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.replication.hibernate.impl.ReplicationReferenceImpl;
import com.db4o.test.Test;
import com.db4o.test.replication.ReplicationTestCase;
import com.db4o.test.replication.collections.ListHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReplicationProviderTest extends ReplicationTestCase {
	protected byte[] B_SIGNATURE_BYTES;
	protected ReadonlyReplicationProviderSignature B_SIGNATURE;
	private ReadonlyReplicationProviderSignature A_SIGNATURE;

	protected void clean() {
		for (int i = 0; i < mappings.length; i++) {
			Class aClass = mappings[i];
			_providerA.deleteAllInstances(aClass);
		}
		_providerA.deleteAllInstances(ArrayList.class);

		_providerA.commit();
	}

	protected void tstDeletion() {
		//TODO deletion cases comment out, wait for new implementation
		if (true) return;
		_providerA.storeNew(new Pilot("Pilot1", 42));
		_providerA.storeNew(new Pilot("Pilot2", 43));

		_providerA.commit();

		startReplication();

		Db4oUUID uuidPilot1 = uuid(findPilot("Pilot1"));
		commitReplication();

		_providerA.storeNew(new Pilot("Pilot3", 44));

		_providerA.delete(findPilot("Pilot1"));

		Car car = new Car("Car1");
		car._pilot = findPilot("Pilot2");
		_providerA.storeNew(car);

		_providerA.commit();

		startReplication();

		ObjectSet deletedUuids = _providerA.uuidsDeletedSinceLastReplication();
		Test.ensure(deletedUuids.next().equals(uuidPilot1));
		Test.ensure(!deletedUuids.hasNext());

		Db4oUUID uuidCar1 = uuid(findCar("Car1"));
		Db4oUUID uuidPilot2 = uuid(findPilot("Pilot2"));

		commitReplication();

		_providerA.delete(findCar("Car1"));
		_providerA.delete(findPilot("Pilot2"));
		_providerA.commit();

		startReplication();

		deletedUuids = _providerA.uuidsDeletedSinceLastReplication();
		Test.ensure(deletedUuids.contains(uuidCar1));
		Test.ensure(deletedUuids.contains(uuidPilot2));
		Test.ensure(deletedUuids.size() == 2);

		commitReplication();

		_providerA.deleteAllInstances(Car.class);
		_providerA.deleteAllInstances(Pilot.class);
		_providerA.commit();
	}

	public void actualTest() {
		B_SIGNATURE_BYTES = _providerB.getSignature().getBytes();

		A_SIGNATURE = _providerA.getSignature();
		B_SIGNATURE = _providerB.getSignature();

		tstSignature();

		tstObjectsChangedSinceLastReplication();

		tstReferences();

		tstStore();

		tstRollback();

		tstDeletion();

		tstCollection();
	}

	public void commitReplication() {
		long maxVersion = _providerA.getCurrentVersion() > _providerB.getCurrentVersion()
				? _providerA.getCurrentVersion() : _providerB.getCurrentVersion();

		_providerA.syncVersionWithPeer(maxVersion);
		_providerB.syncVersionWithPeer(maxVersion);

		maxVersion ++;

		_providerA.commitReplicationTransaction(maxVersion);
		_providerB.commitReplicationTransaction(maxVersion);
	}

	private Object findCar(String model) {
		ObjectSet cars = _providerA.getStoredObjects(Car.class);
		while (cars.hasNext()) {
			Car candidate = (Car) cars.next();
			if (candidate.getModel().equals(model)) return candidate;
		}
		return null;
	}

	private Pilot findPilot(String name) {
		ObjectSet pilots = _providerA.getStoredObjects(Pilot.class);
		while (pilots.hasNext()) {
			Pilot candidate = (Pilot) pilots.next();
			if (candidate._name.equals(name)) return candidate;
		}
		return null;
	}

	private void startReplication() {
		_providerA.startReplicationTransaction(B_SIGNATURE);
		_providerB.startReplicationTransaction(A_SIGNATURE);
	}

	public void test() {
		super.test();
	}

	private void tstCollection() {
		clean();
		System.out.println("ReplicationProviderTest.tstCollection");
		startReplication();


		Db4oUUID listHolderUuid = new Db4oUUID(548494595, B_SIGNATURE_BYTES);

		ListHolder listHolderFromA = new ListHolder("i am a list");

		ReplicationReference refFromA = new ReplicationReferenceImpl(listHolderFromA, listHolderUuid, 9555);

		ListHolder listHolderClonedInB = new ListHolder("i am a list");

		_providerA.referenceNewObject(listHolderClonedInB, refFromA, null, null);
		_providerA.storeReplica(listHolderClonedInB);
		ReplicationReference listHolderFromBRef = _providerA.produceReference(listHolderClonedInB, null, null);
		Test.ensure(listHolderFromBRef.object() == listHolderClonedInB);

		Collection collectionInB = listHolderClonedInB.getList();

		final Db4oUUID collectionUuid = new Db4oUUID(9588, B_SIGNATURE_BYTES);

		ReplicationReference collectionRefFromA = new ReplicationReferenceImpl(new ArrayList(), collectionUuid, 9555);
		_providerA.referenceNewObject(collectionInB, collectionRefFromA, listHolderFromBRef, "list");
		_providerA.storeReplica(collectionInB);

		ReplicationReference collectionRefFromB = _providerA.produceReference(collectionInB, listHolderClonedInB, "list");
		Test.ensure(collectionRefFromB != null);
		Test.ensure(collectionRefFromB.object() == collectionInB);

		Test.ensure(_providerA.produceReference(collectionInB, null, null) == collectionRefFromB);
		Test.ensure(_providerA.produceReference(collectionInB, null, null).object() == collectionInB);

		final ReplicationReference byUuid = _providerA.produceReferenceByUUID(collectionUuid, List.class);
		Test.ensure(byUuid != null);

		_providerA.clearAllReferences();
		final ReplicationReference refFromBAfterClear = _providerA.produceReferenceByUUID(listHolderUuid, ListHolder.class);
		Test.ensure(refFromBAfterClear != null);

		final ListHolder listHolderInBAfterClear = ((ListHolder) refFromBAfterClear.object());
		final ReplicationReference collectionRefFromBAfterClear = _providerA.produceReference(listHolderInBAfterClear.getList(), listHolderInBAfterClear, "list");
		Test.ensure(collectionRefFromBAfterClear != null);

		Test.ensure(collectionRefFromBAfterClear.uuid().equals(collectionUuid));
		commitReplication();
	}

	private void tstObjectsChangedSinceLastReplication() {
		// FIXME:
		// This test can't work any longer since version numbers reflect a timestamp.
		if (true) {
			return;
		}


		Pilot object1 = new Pilot("John Cleese", 42);
		Pilot object2 = new Pilot("Terry Gilliam", 53);
		Car object3 = new Car("Volvo");

		_providerA.storeNew(object1);
		_providerA.storeNew(object2);
		_providerA.storeNew(object3);

		_providerA.commit();

		startReplication();


		int i = _providerA.objectsChangedSinceLastReplication().size();
		Test.ensure(i == 3);


		ObjectSet pilots = _providerA.objectsChangedSinceLastReplication(Pilot.class);
		Test.ensure(pilots.size() == 2);
		Test.ensure(pilots.contains(findPilot("John Cleese")));
		Test.ensure(pilots.contains(findPilot("Terry Gilliam")));

		ObjectSet cars = _providerA.objectsChangedSinceLastReplication(Car.class);
		Test.ensure(cars.hasNext());
		Test.ensure(((Car) cars.next()).getModel().equals("Volvo"));
		Test.ensure(!cars.hasNext());

		commitReplication();

		startReplication();

		Test.ensure(!_providerA.objectsChangedSinceLastReplication().hasNext());
		commitReplication();

		Pilot pilot = (Pilot) _providerA.getStoredObjects(Pilot.class).next();
		pilot._name = "Terry Jones";

		Car car = (Car) _providerA.getStoredObjects(Car.class).next();
		car.setModel("McLaren");

		_providerA.update(pilot);
		_providerA.update(car);

		_providerA.commit();

		startReplication();

		Test.ensure(_providerA.objectsChangedSinceLastReplication().size() == 2);

		pilots = _providerA.objectsChangedSinceLastReplication(Pilot.class);
		Test.ensure(((Pilot) pilots.next())._name.equals("Terry Jones"));
		Test.ensure(!pilots.hasNext());

		cars = _providerA.objectsChangedSinceLastReplication(Car.class);
		Test.ensure(((Car) cars.next()).getModel().equals("McLaren"));
		Test.ensure(!cars.hasNext());
		commitReplication();

		_providerA.deleteAllInstances(Pilot.class);
		_providerA.deleteAllInstances(Car.class);
		_providerA.commit();
	}

	private void tstReferences() {
		_providerA.storeNew(new Pilot("tst References", 42));
		_providerA.commit();

		startReplication();

		Pilot object1 = (Pilot) _providerA.getStoredObjects(Pilot.class).next();

		ReplicationReference reference = _providerA.produceReference(object1, null, null);
		Test.ensure(reference.object() == object1);

		Db4oUUID uuid = reference.uuid();
		ReplicationReference ref2 = _providerA.produceReferenceByUUID(uuid, Pilot.class);
		Test.ensure(ref2.equals(reference));

		_providerA.clearAllReferences();
		Test.ensure(!_providerA.hasReplicationReferenceAlready(object1));
		Db4oUUID db4oUUID = _providerA.produceReference(object1, null, null).uuid();
		//TODO implements Db4oUUID.equals, don't use hashcode to compare
		Test.ensure(db4oUUID.equals(uuid));
		commitReplication();

		_providerA.deleteAllInstances(Pilot.class);
		_providerA.commit();
	}

	private void tstRollback() {
		if (!_providerA.supportsRollback()) return;

		startReplication();

		Pilot object1 = new Pilot("John Cleese", 42);
		Db4oUUID uuid = new Db4oUUID(5678, B_SIGNATURE_BYTES);

		ReplicationReference ref = new ReplicationReferenceImpl(object1, uuid, 1);
		_providerA.referenceNewObject(object1, ref, null, null);

		_providerA.storeReplica(object1);
		Test.ensure(!_providerA.wasModifiedSinceLastReplication(ref));

		_providerA.rollbackReplication();

		_providerA.startReplicationTransaction(B_SIGNATURE);
		Test.ensure(null == _providerA.produceReference(object1, null, null));
		ReplicationReference byUUID = _providerA.produceReferenceByUUID(uuid, object1.getClass());
		Test.ensure(null == byUUID);
		_providerA.rollbackReplication();
		_providerB.rollbackReplication();
	}

	private void tstSignature() {
		Test.ensure(_providerA.getSignature() != null);
	}

	private void tstStore() {
		startReplication();

		Pilot object1 = new Pilot("John Cleese", 42);
		Db4oUUID uuid = new Db4oUUID(1234, B_SIGNATURE_BYTES);

		ReplicationReference ref = new ReplicationReferenceImpl("ignoredSinceInOtherProvider", uuid, 1);
		_providerA.referenceNewObject(object1, ref, null, null);

		_providerA.storeReplica(object1);
		ReplicationReference reference = _providerA.produceReferenceByUUID(uuid, object1.getClass());
		Test.ensure(_providerA.produceReference(object1, null, null) == reference);
		Test.ensure(reference.object() == object1);

		commitReplication();
		startReplication();

		ObjectSet<Pilot> storedObjects = _providerA.getStoredObjects(Pilot.class);
		Test.ensure(storedObjects.hasNext());

		Pilot reloaded = storedObjects.next();

		Test.ensure(!storedObjects.hasNext());

		Test.ensure(_providerA.produceReference(reloaded, null, null).equals(reference));

		reloaded._name = "i am updated";
		_providerA.storeReplica(reloaded);

		_providerA.clearAllReferences();

		commitReplication();

		startReplication();


		reference = _providerA.produceReferenceByUUID(uuid, reloaded.getClass());
		Test.ensure(((Pilot) reference.object())._name.equals("i am updated"));

		commitReplication();

		_providerA.deleteAllInstances(Pilot.class);
		_providerA.commit();
	}

	private Db4oUUID uuid(Object obj) {
		return _providerA.produceReference(obj, null, null).uuid();
	}
}
