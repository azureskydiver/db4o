namespace com.db4o.drs.test
{
	public class SimpleParentChild : com.db4o.drs.test.DrsTestCase
	{
		public virtual void Test()
		{
			StoreParentAndChildToProviderA();
			ReplicateAllToProviderBFirstTime();
			ModifyParentInProviderB();
			ReplicateAllStep2();
			ModifyParentAndChildInProviderA();
			ReplicateParentClassStep3();
		}

		private void EnsureNames(com.db4o.drs.inside.TestableReplicationProviderInside provider
			, string parentName, string childName)
		{
			EnsureOneInstanceOfParentAndChild(provider);
			com.db4o.drs.test.SPCParent parent = (com.db4o.drs.test.SPCParent)GetOneInstance(
				provider, typeof(com.db4o.drs.test.SPCParent));
			if (!parent.GetName().Equals(parentName))
			{
				j4o.lang.JavaSystem._out.Println("expected = " + parentName);
				j4o.lang.JavaSystem._out.Println("actual = " + parent.GetName());
			}
			Db4oUnit.Assert.AreEqual(parent.GetName(), parentName);
			Db4oUnit.Assert.AreEqual(parent.GetChild().GetName(), childName);
		}

		private void EnsureOneInstanceOfParentAndChild(com.db4o.drs.inside.TestableReplicationProviderInside
			 provider)
		{
			EnsureOneInstance(provider, typeof(com.db4o.drs.test.SPCParent));
			EnsureOneInstance(provider, typeof(com.db4o.drs.test.SPCChild));
		}

		private void ModifyParentAndChildInProviderA()
		{
			com.db4o.drs.test.SPCParent parent = (com.db4o.drs.test.SPCParent)GetOneInstance(
				A().Provider(), typeof(com.db4o.drs.test.SPCParent));
			parent.SetName("p3");
			com.db4o.drs.test.SPCChild child = parent.GetChild();
			child.SetName("c3");
			A().Provider().Update(parent);
			A().Provider().Update(child);
			A().Provider().Commit();
			EnsureNames(A().Provider(), "p3", "c3");
		}

		private void ModifyParentInProviderB()
		{
			com.db4o.drs.test.SPCParent parent = (com.db4o.drs.test.SPCParent)GetOneInstance(
				B().Provider(), typeof(com.db4o.drs.test.SPCParent));
			parent.SetName("p2");
			B().Provider().Update(parent);
			B().Provider().Commit();
			EnsureNames(B().Provider(), "p2", "c1");
		}

		private void ReplicateAllStep2()
		{
			ReplicateAll(B().Provider(), A().Provider());
			EnsureNames(A().Provider(), "p2", "c1");
			EnsureNames(B().Provider(), "p2", "c1");
		}

		private void ReplicateAllToProviderBFirstTime()
		{
			ReplicateAll(A().Provider(), B().Provider());
			EnsureNames(A().Provider(), "p1", "c1");
			EnsureNames(B().Provider(), "p1", "c1");
		}

		private void ReplicateParentClassStep3()
		{
			ReplicateClass(A().Provider(), B().Provider(), typeof(com.db4o.drs.test.SPCParent
				));
			EnsureNames(A().Provider(), "p3", "c3");
			EnsureNames(B().Provider(), "p3", "c3");
		}

		private void StoreParentAndChildToProviderA()
		{
			com.db4o.drs.test.SPCChild child = new com.db4o.drs.test.SPCChild("c1");
			com.db4o.drs.test.SPCParent parent = new com.db4o.drs.test.SPCParent(child, "p1");
			A().Provider().StoreNew(parent);
			A().Provider().Commit();
			EnsureNames(A().Provider(), "p1", "c1");
		}
	}
}
