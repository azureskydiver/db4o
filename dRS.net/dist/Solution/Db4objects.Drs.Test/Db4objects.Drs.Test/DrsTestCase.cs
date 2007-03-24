namespace Db4objects.Drs.Test
{
	public abstract class DrsTestCase : Db4oUnit.ITestCase, Db4oUnit.ITestLifeCycle
	{
		public static readonly System.Type[] mappings;

		public static readonly System.Type[] extraMappingsForCleaning = new System.Type[]
			 { typeof(System.Collections.IDictionary), typeof(System.Collections.IList) };

		static DrsTestCase()
		{
			mappings = new System.Type[] { typeof(Db4objects.Drs.Test.Replicated), typeof(Db4objects.Drs.Test.SPCParent)
				, typeof(Db4objects.Drs.Test.SPCChild), typeof(Db4objects.Drs.Test.ListHolder), 
				typeof(Db4objects.Drs.Test.ListContent), typeof(Db4objects.Drs.Test.MapContent), 
				typeof(Db4objects.Drs.Test.SimpleArrayContent), typeof(Db4objects.Drs.Test.SimpleArrayHolder)
				, typeof(Db4objects.Drs.Test.R0), typeof(Db4objects.Drs.Test.Pilot), typeof(Db4objects.Drs.Test.Car)
				, typeof(Db4objects.Drs.Test.Student), typeof(Db4objects.Drs.Test.Person) };
		}

		private Db4objects.Drs.Test.IDrsFixture _a;

		private Db4objects.Drs.Test.IDrsFixture _b;

		public virtual void SetUp()
		{
			CleanBoth();
			Configure();
			OpenBoth();
			Store();
			Reopen();
		}

		private void CleanBoth()
		{
			_a.Clean();
			_b.Clean();
		}

		protected virtual void Clean()
		{
			for (int i = 0; i < mappings.Length; i++)
			{
				A().Provider().DeleteAllInstances(mappings[i]);
				B().Provider().DeleteAllInstances(mappings[i]);
			}
			for (int i = 0; i < extraMappingsForCleaning.Length; i++)
			{
				A().Provider().DeleteAllInstances(extraMappingsForCleaning[i]);
				B().Provider().DeleteAllInstances(extraMappingsForCleaning[i]);
			}
			A().Provider().Commit();
			B().Provider().Commit();
		}

		protected virtual void Store()
		{
		}

		protected virtual void Configure()
		{
			Db4objects.Db4o.Db4oFactory.Configure().GenerateUUIDs(int.MaxValue);
			Db4objects.Db4o.Db4oFactory.Configure().GenerateVersionNumbers(int.MaxValue);
		}

		protected virtual void Reopen()
		{
			CloseBoth();
			OpenBoth();
		}

		private void OpenBoth()
		{
			_a.Open();
			_b.Open();
		}

		public virtual void TearDown()
		{
			CloseBoth();
			CleanBoth();
		}

		private void CloseBoth()
		{
			_a.Close();
			_b.Close();
		}

		public virtual void A(Db4objects.Drs.Test.IDrsFixture fixture)
		{
			_a = fixture;
		}

		public virtual void B(Db4objects.Drs.Test.IDrsFixture fixture)
		{
			_b = fixture;
		}

		public virtual Db4objects.Drs.Test.IDrsFixture A()
		{
			return _a;
		}

		public virtual Db4objects.Drs.Test.IDrsFixture B()
		{
			return _b;
		}

		protected virtual void EnsureOneInstance(Db4objects.Drs.Test.IDrsFixture fixture, 
			System.Type clazz)
		{
			EnsureInstanceCount(fixture, clazz, 1);
		}

		protected virtual void EnsureInstanceCount(Db4objects.Drs.Test.IDrsFixture fixture
			, System.Type clazz, int count)
		{
			Db4objects.Db4o.IObjectSet objectSet = fixture.Provider().GetStoredObjects(clazz);
			Db4oUnit.Assert.AreEqual(count, objectSet.Count);
		}

		protected virtual object GetOneInstance(Db4objects.Drs.Test.IDrsFixture fixture, 
			System.Type clazz)
		{
			System.Collections.IEnumerator objectSet = fixture.Provider().GetStoredObjects(clazz
				).GetEnumerator();
			object candidate = null;
			if (objectSet.MoveNext())
			{
				candidate = objectSet.Current;
				if (objectSet.MoveNext())
				{
					throw new System.Exception("Found more than one instance of + " + clazz + " in provider = "
						 + fixture);
				}
			}
			return candidate;
		}

		protected virtual void ReplicateAll(Db4objects.Drs.Inside.ITestableReplicationProviderInside
			 providerFrom, Db4objects.Drs.Inside.ITestableReplicationProviderInside providerTo
			)
		{
			Db4objects.Drs.IReplicationSession replication = Db4objects.Drs.Replication.Begin
				(providerFrom, providerTo);
			System.Collections.IEnumerator allObjects = providerFrom.ObjectsChangedSinceLastReplication
				().GetEnumerator();
			if (!allObjects.MoveNext())
			{
				throw new System.Exception("Can't find any objects to replicate");
			}
			while (allObjects.MoveNext())
			{
				object changed = allObjects.Current;
				replication.Replicate(changed);
			}
			replication.Commit();
		}

		protected virtual void ReplicateAll(Db4objects.Drs.Inside.ITestableReplicationProviderInside
			 from, Db4objects.Drs.Inside.ITestableReplicationProviderInside to, Db4objects.Drs.IReplicationEventListener
			 listener)
		{
			Db4objects.Drs.IReplicationSession replication = Db4objects.Drs.Replication.Begin
				(from, to, listener);
			System.Collections.IEnumerator allObjects = from.ObjectsChangedSinceLastReplication
				().GetEnumerator();
			while (allObjects.MoveNext())
			{
				object changed = allObjects.Current;
				replication.Replicate(changed);
			}
			replication.Commit();
		}

		protected virtual void Delete(System.Type[] classes)
		{
			for (int i = 0; i < classes.Length; i++)
			{
				A().Provider().DeleteAllInstances(classes[i]);
				B().Provider().DeleteAllInstances(classes[i]);
			}
			A().Provider().Commit();
			B().Provider().Commit();
		}

		protected virtual void ReplicateClass(Db4objects.Drs.Inside.ITestableReplicationProviderInside
			 providerA, Db4objects.Drs.Inside.ITestableReplicationProviderInside providerB, 
			System.Type clazz)
		{
			Db4objects.Drs.IReplicationSession replication = Db4objects.Drs.Replication.Begin
				(providerA, providerB);
			System.Collections.IEnumerator allObjects = providerA.ObjectsChangedSinceLastReplication
				(clazz).GetEnumerator();
			while (allObjects.MoveNext())
			{
				object obj = allObjects.Current;
				replication.Replicate(obj);
			}
			replication.Commit();
		}

		protected static void Sleep(int millis)
		{
			try
			{
				Sharpen.Lang.Thread.Sleep(millis);
			}
			catch (System.Exception e)
			{
				throw new System.Exception(e.ToString());
			}
		}
	}
}
