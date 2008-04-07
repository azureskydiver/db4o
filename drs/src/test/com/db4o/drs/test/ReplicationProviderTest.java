/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.Iterator;

import com.db4o.ObjectSet;
import com.db4o.drs.inside.ReadonlyReplicationProviderSignature;
import com.db4o.drs.inside.ReplicationReference;
import com.db4o.drs.inside.ReplicationReferenceImpl;
import com.db4o.ext.Db4oUUID;
//import com.db4o.test.Test;

import db4ounit.Assert;


public class ReplicationProviderTest extends DrsTestCase {

	protected byte[] B_SIGNATURE_BYTES;
	protected ReadonlyReplicationProviderSignature B_SIGNATURE;
	private ReadonlyReplicationProviderSignature A_SIGNATURE;

	public void test() {
		B_SIGNATURE_BYTES = b().provider().getSignature().getSignature();

		A_SIGNATURE = a().provider().getSignature();
		B_SIGNATURE = b().provider().getSignature();

		tstObjectUpdate();

		tstSignature();

		tstObjectsChangedSinceLastReplication();

		tstReferences();

		tstStore();

		tstRollback();

		tstDeletion();
	}


	protected void tstDeletion() {
		a().provider().storeNew(new Pilot("Pilot1", 42));
		a().provider().storeNew(new Pilot("Pilot2", 43));

		a().provider().commit();

		a().provider().storeNew(new Pilot("Pilot3", 44));

		a().provider().delete(findPilot("Pilot1"));

		Car car = new Car("Car1");
		car._pilot = findPilot("Pilot2");
		a().provider().storeNew(car);

		a().provider().commit();

		startReplication();

		Db4oUUID uuidCar1 = uuid(findCar("Car1"));
		Assert.isNotNull(uuidCar1);

		a().provider().replicateDeletion(uuidCar1);

		commitReplication();

		Assert.isNull(findCar("Car1"));

		startReplication();

		Db4oUUID uuidPilot2 = uuid(findPilot("Pilot2"));
		Assert.isNotNull(uuidPilot2);
		a().provider().replicateDeletion(uuidPilot2);

		commitReplication();

		Assert.isNull(findPilot("Pilot2"));

	}

	private void commitReplication() {
		long maxVersion = a().provider().getCurrentVersion() > b().provider().getCurrentVersion()
				? a().provider().getCurrentVersion() : b().provider().getCurrentVersion();

		a().provider().syncVersionWithPeer(maxVersion);
		b().provider().syncVersionWithPeer(maxVersion);

		maxVersion ++;

		a().provider().commitReplicationTransaction(maxVersion);
		b().provider().commitReplicationTransaction(maxVersion);
	}

	private Object findCar(String model) {
		Iterator cars = a().provider().getStoredObjects(Car.class).iterator();
		while (cars.hasNext()) {
			Car candidate = (Car) cars.next();
			if (candidate.getModel().equals(model)) return candidate;
		}
		return null;
	}

	private Pilot findPilot(String name) {
		Iterator pilots = a().provider().getStoredObjects(Pilot.class).iterator();
		while (pilots.hasNext()) {
			Pilot candidate = (Pilot) pilots.next();
			if (candidate._name.equals(name)) return candidate;
		}
		return null;
	}

	private SPCChild getOneChildFromA() {
		ObjectSet storedObjects = a().provider().getStoredObjects(SPCChild.class);
		Assert.areEqual(1, storedObjects.size());
		
		Iterator iterator = storedObjects.iterator();
		Assert.isTrue(iterator.hasNext());
		return (SPCChild) iterator.next();
	}

	private void startReplication() {
		a().provider().startReplicationTransaction(B_SIGNATURE);
		b().provider().startReplicationTransaction(A_SIGNATURE);
	}

	private void tstObjectUpdate() {
		SPCChild child = new SPCChild("c1");
		a().provider().storeNew(child);
		a().provider().commit();

		startReplication();
		SPCChild reloaded = getOneChildFromA();
		long oldVer = a().provider().produceReference(reloaded, null, null).version();
		commitReplication();

		SPCChild reloaded2 = getOneChildFromA();
		reloaded2.setName("c3");

		//System.out.println("==============BEGIN DEBUG");
		a().provider().update(reloaded2);
		a().provider().commit();
		//System.out.println("==============END DEBUG");

		startReplication();
		SPCChild reloaded3 = getOneChildFromA();
		long newVer = a().provider().produceReference(reloaded3, null, null).version();
		commitReplication();

		Assert.isTrue(newVer > oldVer);
	}

	private void tstObjectsChangedSinceLastReplication() {
		Pilot object1 = new Pilot("John Cleese", 42);
		Pilot object2 = new Pilot("Terry Gilliam", 53);
		Car object3 = new Car("Volvo");

		a().provider().storeNew(object1);
		a().provider().storeNew(object2);
		a().provider().storeNew(object3);

		a().provider().commit();

		startReplication();


		int i = a().provider().objectsChangedSinceLastReplication().size();
		Assert.areEqual(i, 3);

		ObjectSet os = a().provider().objectsChangedSinceLastReplication(Pilot.class);
		Assert.areEqual(os.size(), 2);
		
		Iterator pilots = os.iterator();
//		Assert.isTrue(pilots.contains(findPilot("John Cleese")));
	//	Assert.isTrue(pilots.contains(findPilot("Terry Gilliam")));
		
		Iterator cars = a().provider().objectsChangedSinceLastReplication(Car.class).iterator();		
		Assert.areEqual(((Car) next(cars)).getModel(), "Volvo");
		Assert.isFalse(cars.hasNext());

		commitReplication();

		startReplication();

		Assert.isFalse(a().provider().objectsChangedSinceLastReplication().iterator().hasNext());
		commitReplication();

		Pilot pilot = (Pilot) next(a().provider().getStoredObjects(Pilot.class).iterator());
		pilot._name = "Terry Jones";

		Car car = (Car) next(a().provider().getStoredObjects(Car.class).iterator());
		car.setModel("McLaren");

		a().provider().update(pilot);
		a().provider().update(car);

		a().provider().commit();

		startReplication();

		Assert.areEqual(a().provider().objectsChangedSinceLastReplication().size(), 2);

		pilots = a().provider().objectsChangedSinceLastReplication(Pilot.class).iterator();
		Assert.areEqual(((Pilot) next(pilots))._name, "Terry Jones");
		Assert.isFalse(pilots.hasNext());

		cars = a().provider().objectsChangedSinceLastReplication(Car.class).iterator();		
		Assert.areEqual(((Car) next(cars)).getModel(), "McLaren");
		Assert.isFalse(cars.hasNext());
		commitReplication();

		a().provider().deleteAllInstances(Pilot.class);
		a().provider().deleteAllInstances(Car.class);
		a().provider().commit();
	}


	private Object next(Iterator iterator) {
		Assert.isTrue(iterator.hasNext());
		return iterator.next();
	}

	private void tstReferences() {
		a().provider().storeNew(new Pilot("tst References", 42));
		a().provider().commit();

		startReplication();

		Pilot object1 = (Pilot) next(a().provider().getStoredObjects(Pilot.class).iterator());

		ReplicationReference reference = a().provider().produceReference(object1, null, null);
		Assert.areEqual(reference.object(), object1);

		Db4oUUID uuid = reference.uuid();
		ReplicationReference ref2 = a().provider().produceReferenceByUUID(uuid, Pilot.class);
		Assert.areEqual(ref2, reference);

		a().provider().clearAllReferences();
		Db4oUUID db4oUUID = a().provider().produceReference(object1, null, null).uuid();
		Assert.isTrue(db4oUUID.equals(uuid));
		commitReplication();

		a().provider().deleteAllInstances(Pilot.class);
		a().provider().commit();
	}

	private void tstRollback() {
		if (!a().provider().supportsRollback()) return;
		if (!b().provider().supportsRollback()) return;

		startReplication();

		Pilot object1 = new Pilot("Albert Kwan", 25);
		Db4oUUID uuid = new Db4oUUID(5678, B_SIGNATURE_BYTES);

		ReplicationReference ref = new ReplicationReferenceImpl(object1, uuid, 1);
		a().provider().referenceNewObject(object1, ref, null, null);

		a().provider().storeReplica(object1);
		Assert.isFalse(a().provider().wasModifiedSinceLastReplication(ref));
		
		a().provider().rollbackReplication();

		a().provider().startReplicationTransaction(B_SIGNATURE);
		Assert.isNull(a().provider().produceReference(object1, null, null));
		ReplicationReference byUUID = a().provider().produceReferenceByUUID(uuid, object1.getClass());
		Assert.isNull(byUUID);
		
		a().provider().rollbackReplication();
		b().provider().rollbackReplication();
	}

	private void tstSignature() {
		Assert.isNotNull(a().provider().getSignature());
	}

	private void tstStore() {
		startReplication();

		Pilot object1 = new Pilot("John Cleese", 42);
		Db4oUUID uuid = new Db4oUUID(1234, B_SIGNATURE_BYTES);

		ReplicationReference ref = new ReplicationReferenceImpl("ignoredSinceInOtherProvider", uuid, 1);

		a().provider().referenceNewObject(object1, ref, null, null);

		a().provider().storeReplica(object1);
		ReplicationReference reference = a().provider().produceReferenceByUUID(uuid, object1.getClass());
		Assert.areEqual(a().provider().produceReference(object1, null, null), reference);
		Assert.areEqual(reference.object(), object1);

		commitReplication();
		startReplication();

		Iterator storedObjects = a().provider().getStoredObjects(Pilot.class).iterator();		
		Pilot reloaded = (Pilot)next(storedObjects);

		Assert.isFalse(storedObjects.hasNext());
		
		reference = a().provider().produceReferenceByUUID(uuid, object1.getClass());
		Assert.areEqual(a().provider().produceReference(reloaded, null, null), reference);

		reloaded._name = "i am updated";
		a().provider().storeReplica(reloaded);

		a().provider().clearAllReferences();

		commitReplication();

		startReplication();

		reference = a().provider().produceReferenceByUUID(uuid, reloaded.getClass());
		Assert.areEqual(((Pilot) reference.object())._name, "i am updated");

		commitReplication();

		a().provider().deleteAllInstances(Pilot.class);
		a().provider().commit();
	}

	private Db4oUUID uuid(Object obj) {
		return a().provider().produceReference(obj, null, null).uuid();
	}

}
