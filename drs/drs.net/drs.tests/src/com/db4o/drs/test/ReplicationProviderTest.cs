namespace com.db4o.drs.test
{
	public class ReplicationProviderTest : com.db4o.drs.test.DrsTestCase
	{
		protected byte[] B_SIGNATURE_BYTES;

		protected com.db4o.drs.inside.ReadonlyReplicationProviderSignature B_SIGNATURE;

		private com.db4o.drs.inside.ReadonlyReplicationProviderSignature A_SIGNATURE;

		public virtual void Test()
		{
			B_SIGNATURE_BYTES = B().Provider().GetSignature().GetSignature();
			A_SIGNATURE = A().Provider().GetSignature();
			B_SIGNATURE = B().Provider().GetSignature();
			TstObjectUpdate();
			TstSignature();
			TstObjectsChangedSinceLastReplication();
			TstReferences();
			TstStore();
			TstRollback();
			TstDeletion();
		}

		protected virtual void TstDeletion()
		{
			A().Provider().StoreNew(new com.db4o.drs.test.Pilot("Pilot1", 42));
			A().Provider().StoreNew(new com.db4o.drs.test.Pilot("Pilot2", 43));
			A().Provider().Commit();
			A().Provider().StoreNew(new com.db4o.drs.test.Pilot("Pilot3", 44));
			A().Provider().Delete(FindPilot("Pilot1"));
			com.db4o.drs.test.Car car = new com.db4o.drs.test.Car("Car1");
			car._pilot = FindPilot("Pilot2");
			A().Provider().StoreNew(car);
			A().Provider().Commit();
			StartReplication();
			com.db4o.ext.Db4oUUID uuidCar1 = Uuid(FindCar("Car1"));
			Db4oUnit.Assert.IsNotNull(uuidCar1);
			A().Provider().ReplicateDeletion(uuidCar1);
			CommitReplication();
			Db4oUnit.Assert.IsNull(FindCar("Car1"));
			StartReplication();
			com.db4o.ext.Db4oUUID uuidPilot2 = Uuid(FindPilot("Pilot2"));
			Db4oUnit.Assert.IsNotNull(uuidPilot2);
			A().Provider().ReplicateDeletion(uuidPilot2);
			CommitReplication();
			Db4oUnit.Assert.IsNull(FindPilot("Pilot2"));
		}

		private void CommitReplication()
		{
			long maxVersion = A().Provider().GetCurrentVersion() > B().Provider().GetCurrentVersion
				() ? A().Provider().GetCurrentVersion() : B().Provider().GetCurrentVersion();
			A().Provider().SyncVersionWithPeer(maxVersion);
			B().Provider().SyncVersionWithPeer(maxVersion);
			maxVersion++;
			A().Provider().CommitReplicationTransaction(maxVersion);
			B().Provider().CommitReplicationTransaction(maxVersion);
		}

		private object FindCar(string model)
		{
			com.db4o.ObjectSet cars = A().Provider().GetStoredObjects(typeof(com.db4o.drs.test.Car
				));
			while (cars.HasNext())
			{
				com.db4o.drs.test.Car candidate = (com.db4o.drs.test.Car)cars.Next();
				if (candidate.GetModel().Equals(model))
				{
					return candidate;
				}
			}
			return null;
		}

		private com.db4o.drs.test.Pilot FindPilot(string name)
		{
			com.db4o.ObjectSet pilots = A().Provider().GetStoredObjects(typeof(com.db4o.drs.test.Pilot
				));
			while (pilots.HasNext())
			{
				com.db4o.drs.test.Pilot candidate = (com.db4o.drs.test.Pilot)pilots.Next();
				if (candidate._name.Equals(name))
				{
					return candidate;
				}
			}
			return null;
		}

		private com.db4o.drs.test.SPCChild GetOneChildFromA()
		{
			com.db4o.ObjectSet storedObjects = A().Provider().GetStoredObjects(typeof(com.db4o.drs.test.SPCChild
				));
			Db4oUnit.Assert.AreEqual(1, storedObjects.Size());
			Db4oUnit.Assert.IsTrue(storedObjects.HasNext());
			return (com.db4o.drs.test.SPCChild)storedObjects.Next();
		}

		private void StartReplication()
		{
			A().Provider().StartReplicationTransaction(B_SIGNATURE);
			B().Provider().StartReplicationTransaction(A_SIGNATURE);
		}

		private void TstObjectUpdate()
		{
			com.db4o.drs.test.SPCChild child = new com.db4o.drs.test.SPCChild("c1");
			A().Provider().StoreNew(child);
			A().Provider().Commit();
			StartReplication();
			com.db4o.drs.test.SPCChild reloaded = GetOneChildFromA();
			long oldVer = A().Provider().ProduceReference(reloaded, null, null).Version();
			CommitReplication();
			com.db4o.drs.test.SPCChild reloaded2 = GetOneChildFromA();
			reloaded2.SetName("c3");
			A().Provider().Update(reloaded2);
			A().Provider().Commit();
			StartReplication();
			com.db4o.drs.test.SPCChild reloaded3 = GetOneChildFromA();
			long newVer = A().Provider().ProduceReference(reloaded3, null, null).Version();
			CommitReplication();
			Db4oUnit.Assert.IsTrue(newVer > oldVer);
		}

		private void TstObjectsChangedSinceLastReplication()
		{
			com.db4o.drs.test.Pilot object1 = new com.db4o.drs.test.Pilot("John Cleese", 42);
			com.db4o.drs.test.Pilot object2 = new com.db4o.drs.test.Pilot("Terry Gilliam", 53
				);
			com.db4o.drs.test.Car object3 = new com.db4o.drs.test.Car("Volvo");
			A().Provider().StoreNew(object1);
			A().Provider().StoreNew(object2);
			A().Provider().StoreNew(object3);
			A().Provider().Commit();
			StartReplication();
			int i = A().Provider().ObjectsChangedSinceLastReplication().Size();
			Db4oUnit.Assert.AreEqual(i, 3);
			com.db4o.ObjectSet pilots = A().Provider().ObjectsChangedSinceLastReplication(typeof(
				com.db4o.drs.test.Pilot));
			Db4oUnit.Assert.AreEqual(pilots.Size(), 2);
			com.db4o.ObjectSet cars = A().Provider().ObjectsChangedSinceLastReplication(typeof(
				com.db4o.drs.test.Car));
			Db4oUnit.Assert.IsTrue(cars.HasNext());
			Db4oUnit.Assert.AreEqual(((com.db4o.drs.test.Car)cars.Next()).GetModel(), "Volvo"
				);
			Db4oUnit.Assert.IsFalse(cars.HasNext());
			CommitReplication();
			StartReplication();
			Db4oUnit.Assert.IsFalse(A().Provider().ObjectsChangedSinceLastReplication().HasNext
				());
			CommitReplication();
			com.db4o.drs.test.Pilot pilot = (com.db4o.drs.test.Pilot)A().Provider().GetStoredObjects
				(typeof(com.db4o.drs.test.Pilot)).Next();
			pilot._name = "Terry Jones";
			com.db4o.drs.test.Car car = (com.db4o.drs.test.Car)A().Provider().GetStoredObjects
				(typeof(com.db4o.drs.test.Car)).Next();
			car.SetModel("McLaren");
			A().Provider().Update(pilot);
			A().Provider().Update(car);
			A().Provider().Commit();
			StartReplication();
			Db4oUnit.Assert.AreEqual(A().Provider().ObjectsChangedSinceLastReplication().Size
				(), 2);
			pilots = A().Provider().ObjectsChangedSinceLastReplication(typeof(com.db4o.drs.test.Pilot
				));
			Db4oUnit.Assert.AreEqual(((com.db4o.drs.test.Pilot)pilots.Next())._name, "Terry Jones"
				);
			Db4oUnit.Assert.IsFalse(pilots.HasNext());
			cars = A().Provider().ObjectsChangedSinceLastReplication(typeof(com.db4o.drs.test.Car
				));
			Db4oUnit.Assert.AreEqual(((com.db4o.drs.test.Car)cars.Next()).GetModel(), "McLaren"
				);
			Db4oUnit.Assert.IsFalse(cars.HasNext());
			CommitReplication();
			A().Provider().DeleteAllInstances(typeof(com.db4o.drs.test.Pilot));
			A().Provider().DeleteAllInstances(typeof(com.db4o.drs.test.Car));
			A().Provider().Commit();
		}

		private void TstReferences()
		{
			A().Provider().StoreNew(new com.db4o.drs.test.Pilot("tst References", 42));
			A().Provider().Commit();
			StartReplication();
			com.db4o.drs.test.Pilot object1 = (com.db4o.drs.test.Pilot)A().Provider().GetStoredObjects
				(typeof(com.db4o.drs.test.Pilot)).Next();
			com.db4o.drs.inside.ReplicationReference reference = A().Provider().ProduceReference
				(object1, null, null);
			Db4oUnit.Assert.AreEqual(reference.Object(), object1);
			com.db4o.ext.Db4oUUID uuid = reference.Uuid();
			com.db4o.drs.inside.ReplicationReference ref2 = A().Provider().ProduceReferenceByUUID
				(uuid, typeof(com.db4o.drs.test.Pilot));
			Db4oUnit.Assert.AreEqual(ref2, reference);
			A().Provider().ClearAllReferences();
			com.db4o.ext.Db4oUUID db4oUUID = A().Provider().ProduceReference(object1, null, null
				).Uuid();
			Db4oUnit.Assert.IsTrue(db4oUUID.Equals(uuid));
			CommitReplication();
			A().Provider().DeleteAllInstances(typeof(com.db4o.drs.test.Pilot));
			A().Provider().Commit();
		}

		private void TstRollback()
		{
			if (!A().Provider().SupportsRollback())
			{
				return;
			}
			if (!B().Provider().SupportsRollback())
			{
				return;
			}
			StartReplication();
			com.db4o.drs.test.Pilot object1 = new com.db4o.drs.test.Pilot("Albert Kwan", 25);
			com.db4o.ext.Db4oUUID uuid = new com.db4o.ext.Db4oUUID(5678, B_SIGNATURE_BYTES);
			com.db4o.drs.inside.ReplicationReference @ref = new com.db4o.drs.inside.ReplicationReferenceImpl
				(object1, uuid, 1);
			A().Provider().ReferenceNewObject(object1, @ref, null, null);
			A().Provider().StoreReplica(object1);
			Db4oUnit.Assert.IsFalse(A().Provider().WasModifiedSinceLastReplication(@ref));
			A().Provider().RollbackReplication();
			A().Provider().StartReplicationTransaction(B_SIGNATURE);
			Db4oUnit.Assert.IsNull(A().Provider().ProduceReference(object1, null, null));
			com.db4o.drs.inside.ReplicationReference byUUID = A().Provider().ProduceReferenceByUUID
				(uuid, object1.GetType());
			Db4oUnit.Assert.IsNull(byUUID);
			A().Provider().RollbackReplication();
			B().Provider().RollbackReplication();
		}

		private void TstSignature()
		{
			Db4oUnit.Assert.IsNotNull(A().Provider().GetSignature());
		}

		private void TstStore()
		{
			StartReplication();
			com.db4o.drs.test.Pilot object1 = new com.db4o.drs.test.Pilot("John Cleese", 42);
			com.db4o.ext.Db4oUUID uuid = new com.db4o.ext.Db4oUUID(1234, B_SIGNATURE_BYTES);
			com.db4o.drs.inside.ReplicationReference @ref = new com.db4o.drs.inside.ReplicationReferenceImpl
				("ignoredSinceInOtherProvider", uuid, 1);
			A().Provider().ReferenceNewObject(object1, @ref, null, null);
			A().Provider().StoreReplica(object1);
			com.db4o.drs.inside.ReplicationReference reference = A().Provider().ProduceReferenceByUUID
				(uuid, object1.GetType());
			Db4oUnit.Assert.AreEqual(A().Provider().ProduceReference(object1, null, null), reference
				);
			Db4oUnit.Assert.AreEqual(reference.Object(), object1);
			CommitReplication();
			StartReplication();
			com.db4o.ObjectSet storedObjects = A().Provider().GetStoredObjects(typeof(com.db4o.drs.test.Pilot
				));
			Db4oUnit.Assert.IsTrue(storedObjects.HasNext());
			com.db4o.drs.test.Pilot reloaded = (com.db4o.drs.test.Pilot)storedObjects.Next();
			Db4oUnit.Assert.IsFalse(storedObjects.HasNext());
			reference = A().Provider().ProduceReferenceByUUID(uuid, object1.GetType());
			Db4oUnit.Assert.AreEqual(A().Provider().ProduceReference(reloaded, null, null), reference
				);
			reloaded._name = "i am updated";
			A().Provider().StoreReplica(reloaded);
			A().Provider().ClearAllReferences();
			CommitReplication();
			StartReplication();
			reference = A().Provider().ProduceReferenceByUUID(uuid, reloaded.GetType());
			Db4oUnit.Assert.AreEqual(((com.db4o.drs.test.Pilot)reference.Object())._name, "i am updated"
				);
			CommitReplication();
			A().Provider().DeleteAllInstances(typeof(com.db4o.drs.test.Pilot));
			A().Provider().Commit();
		}

		private com.db4o.ext.Db4oUUID Uuid(object obj)
		{
			return A().Provider().ProduceReference(obj, null, null).Uuid();
		}
	}
}
