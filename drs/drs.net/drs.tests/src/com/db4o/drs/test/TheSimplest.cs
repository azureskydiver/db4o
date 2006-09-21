namespace com.db4o.drs.test
{
	public class TheSimplest : com.db4o.drs.test.DrsTestCase
	{
		public virtual void Test()
		{
			StoreInA();
			Replicate();
			ModifyInB();
			Replicate2();
			ModifyInA();
			Replicate3();
		}

		private void Replicate3()
		{
			ReplicateClass(A().Provider(), B().Provider(), typeof(com.db4o.drs.test.SPCChild)
				);
			EnsureNames(A().Provider(), "c3");
			EnsureNames(B().Provider(), "c3");
		}

		private void ModifyInA()
		{
			com.db4o.drs.test.SPCChild child = GetTheObject(A().Provider());
			child.SetName("c3");
			A().Provider().Update(child);
			A().Provider().Commit();
			EnsureNames(A().Provider(), "c3");
		}

		private void Replicate2()
		{
			ReplicateAll(B().Provider(), A().Provider());
			EnsureNames(A().Provider(), "c2");
			EnsureNames(B().Provider(), "c2");
		}

		private void StoreInA()
		{
			com.db4o.drs.test.SPCChild child = new com.db4o.drs.test.SPCChild("c1");
			A().Provider().StoreNew(child);
			A().Provider().Commit();
			EnsureNames(A().Provider(), "c1");
		}

		private void Replicate()
		{
			ReplicateAll(A().Provider(), B().Provider());
			EnsureNames(A().Provider(), "c1");
			EnsureNames(B().Provider(), "c1");
		}

		private void ModifyInB()
		{
			com.db4o.drs.test.SPCChild child = GetTheObject(B().Provider());
			child.SetName("c2");
			B().Provider().Update(child);
			B().Provider().Commit();
			EnsureNames(B().Provider(), "c2");
		}

		private void EnsureNames(com.db4o.drs.inside.TestableReplicationProviderInside provider
			, string childName)
		{
			EnsureOneInstance(provider, typeof(com.db4o.drs.test.SPCChild));
			com.db4o.drs.test.SPCChild child = GetTheObject(provider);
			Db4oUnit.Assert.AreEqual(childName, child.GetName());
		}

		private com.db4o.drs.test.SPCChild GetTheObject(com.db4o.drs.inside.TestableReplicationProviderInside
			 provider)
		{
			return (com.db4o.drs.test.SPCChild)GetOneInstance(provider, typeof(com.db4o.drs.test.SPCChild
				));
		}

		protected override void ReplicateClass(com.db4o.drs.inside.TestableReplicationProviderInside
			 providerA, com.db4o.drs.inside.TestableReplicationProviderInside providerB, System.Type
			 clazz)
		{
			com.db4o.drs.ReplicationSession replication = com.db4o.drs.Replication.Begin(providerA
				, providerB);
			com.db4o.ObjectSet allObjects = providerA.ObjectsChangedSinceLastReplication(clazz
				);
			while (allObjects.HasNext())
			{
				object obj = allObjects.Next();
				replication.Replicate(obj);
			}
			replication.Commit();
		}
	}
}
