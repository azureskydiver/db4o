namespace Db4objects.Drs.Test
{
	public class TheSimplest : Db4objects.Drs.Test.DrsTestCase
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
			ReplicateClass(A().Provider(), B().Provider(), typeof(Db4objects.Drs.Test.SPCChild)
				);
			EnsureNames(A().Provider(), "c3");
			EnsureNames(B().Provider(), "c3");
		}

		private void ModifyInA()
		{
			Db4objects.Drs.Test.SPCChild child = GetTheObject(A().Provider());
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
			Db4objects.Drs.Test.SPCChild child = new Db4objects.Drs.Test.SPCChild("c1");
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
			Db4objects.Drs.Test.SPCChild child = GetTheObject(B().Provider());
			child.SetName("c2");
			B().Provider().Update(child);
			B().Provider().Commit();
			EnsureNames(B().Provider(), "c2");
		}

		private void EnsureNames(Db4objects.Drs.Inside.ITestableReplicationProviderInside
			 provider, string childName)
		{
			EnsureOneInstance(provider, typeof(Db4objects.Drs.Test.SPCChild));
			Db4objects.Drs.Test.SPCChild child = GetTheObject(provider);
			Db4oUnit.Assert.AreEqual(childName, child.GetName());
		}

		private Db4objects.Drs.Test.SPCChild GetTheObject(Db4objects.Drs.Inside.ITestableReplicationProviderInside
			 provider)
		{
			return (Db4objects.Drs.Test.SPCChild)GetOneInstance(provider, typeof(Db4objects.Drs.Test.SPCChild)
				);
		}

		protected override void ReplicateClass(Db4objects.Drs.Inside.ITestableReplicationProviderInside
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
	}
}
