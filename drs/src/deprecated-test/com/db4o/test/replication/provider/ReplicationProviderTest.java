package com.db4o.test.replication.provider;

import com.db4o.ObjectSet;
import com.db4o.drs.hibernate.impl.ReplicationReferenceImpl;
import com.db4o.drs.inside.ReadonlyReplicationProviderSignature;
import com.db4o.drs.inside.ReplicationReference;
import com.db4o.ext.Db4oUUID;
import com.db4o.test.Test;
import com.db4o.test.replication.ReplicationTestCase;
import com.db4o.test.replication.SPCChild;

//FIXME This test should test a single ReplicationProvider and does not require _providerB. It does not need to extend ReplicationTestCase with all its provider combinations.

public class ReplicationProviderTest extends ReplicationTestCase {
	protected byte[] B_SIGNATURE_BYTES;
	protected ReadonlyReplicationProviderSignature B_SIGNATURE;
	private ReadonlyReplicationProviderSignature A_SIGNATURE;

	public void actualTest() {
		B_SIGNATURE_BYTES = _providerB.getSignature().getSignature();

		A_SIGNATURE = _providerA.getSignature();
		B_SIGNATURE = _providerB.getSignature();

		tstObjectUpdate();

		tstSignature();

		tstObjectsChangedSinceLastReplication();

		tstReferences();

		tstStore();

		tstRollback();

		tstDeletion();
	}

	public void test() {
		super.test();
	}

	protected void tstDeletion() {
		_providerA.storeNew(new Pilot("Pilot1", 42));
		_providerA.storeNew(new Pilot("Pilot2", 43));

		_providerA.commit();

		_providerA.storeNew(new Pilot("Pilot3", 44));

		_providerA.delete(findPilot("Pilot1"));

		Car car = new Car("Car1");
		car._pilot = findPilot("Pilot2");
		_providerA.storeNew(car);

		_providerA.commit();

		startReplication();

		Db4oUUID uuidCar1 = uuid(findCar("Car1"));
		Test.ensure(uuidCar1 != null);
		_providerA.replicateDeletion(uuidCar1);

		commitReplication();

		Test.ensure(findCar("Car1") == null);

		startReplication();

		Db4oUUID uuidPilot2 = uuid(findPilot("Pilot2"));
		Test.ensure(uuidPilot2 != null);
		_providerA.replicateDeletion(uuidPilot2);

		commitReplication();

		Test.ensure(findPilot("Pilot2") == null);
	}

	private void commitReplication() {
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

	private SPCChild getOneChildFromA() {
		ObjectSet storedObjects = _providerA.getStoredObjects(SPCChild.class);
		Test.ensureEquals(1, storedObjects.size());
		Test.ensure(storedObjects.hasNext());
		return (SPCChild) storedObjects.next();
	}

	private void startReplication() {
		_providerA.startReplicationTransaction(B_SIGNATURE);
		_providerB.startReplicationTransaction(A_SIGNATURE);
	}

	private void tstObjectUpdate() {
		SPCChild child = new SPCChild("c1");
		_providerA.storeNew(child);
		_providerA.commit();

		startReplication();
		SPCChild reloaded = getOneChildFromA();
		long oldVer = _providerA.produceReference(reloaded, null, null).version();
		commitReplication();

		SPCChild reloaded2 = getOneChildFromA();
		reloaded2.setName("c3");

		//System.out.println("==============BEGIN DEBUG");
		_providerA.update(reloaded2);
		_providerA.commit();
		//System.out.println("==============END DEBUG");

		startReplication();
		SPCChild reloaded3 = getOneChildFromA();
		long newVer = _providerA.produceReference(reloaded3, null, null).version();
		commitReplication();

		Test.ensure(newVer > oldVer);
	}

	private void tstObjectsChangedSinceLastReplication() {
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
		Db4oUUID db4oUUID = _providerA.produceReference(object1, null, null).uuid();
		Test.ensure(db4oUUID.equals(uuid));
		commitReplication();

		_providerA.deleteAllInstances(Pilot.class);
		_providerA.commit();
	}

	private void tstRollback() {
		if (!_providerA.supportsRollback()) return;
		if (!_providerB.supportsRollback()) return;

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
		Test.ensure(_providerA.produceReference(object1, null, null).equals(reference));
		Test.ensure(reference.object() == object1);

		commitReplication();
		startReplication();

		ObjectSet<Pilot> storedObjects = _providerA.getStoredObjects(Pilot.class);
		Test.ensure(storedObjects.hasNext());

		Pilot reloaded = storedObjects.next();

		Test.ensure(!storedObjects.hasNext());

		reference = _providerA.produceReferenceByUUID(uuid, object1.getClass());
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
