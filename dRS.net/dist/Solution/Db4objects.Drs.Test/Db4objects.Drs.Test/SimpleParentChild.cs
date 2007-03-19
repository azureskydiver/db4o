namespace Db4objects.Drs.Test
{
	public class SimpleParentChild : Db4objects.Drs.Test.DrsTestCase
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

		private void EnsureNames(Db4objects.Drs.Inside.ITestableReplicationProviderInside
			 provider, string parentName, string childName)
		{
			EnsureOneInstanceOfParentAndChild(provider);
			Db4objects.Drs.Test.SPCParent parent = (Db4objects.Drs.Test.SPCParent)GetOneInstance
				(provider, typeof(Db4objects.Drs.Test.SPCParent));
			if (!parent.GetName().Equals(parentName))
			{
				Sharpen.Runtime.Out.WriteLine("expected = " + parentName);
				Sharpen.Runtime.Out.WriteLine("actual = " + parent.GetName());
			}
			Db4oUnit.Assert.AreEqual(parent.GetName(), parentName);
			Db4oUnit.Assert.AreEqual(parent.GetChild().GetName(), childName);
		}

		private void EnsureOneInstanceOfParentAndChild(Db4objects.Drs.Inside.ITestableReplicationProviderInside
			 provider)
		{
			EnsureOneInstance(provider, typeof(Db4objects.Drs.Test.SPCParent));
			EnsureOneInstance(provider, typeof(Db4objects.Drs.Test.SPCChild));
		}

		private void ModifyParentAndChildInProviderA()
		{
			Db4objects.Drs.Test.SPCParent parent = (Db4objects.Drs.Test.SPCParent)GetOneInstance
				(A().Provider(), typeof(Db4objects.Drs.Test.SPCParent));
			parent.SetName("p3");
			Db4objects.Drs.Test.SPCChild child = parent.GetChild();
			child.SetName("c3");
			A().Provider().Update(parent);
			A().Provider().Update(child);
			A().Provider().Commit();
			EnsureNames(A().Provider(), "p3", "c3");
		}

		private void ModifyParentInProviderB()
		{
			Db4objects.Drs.Test.SPCParent parent = (Db4objects.Drs.Test.SPCParent)GetOneInstance
				(B().Provider(), typeof(Db4objects.Drs.Test.SPCParent));
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
			ReplicateClass(A().Provider(), B().Provider(), typeof(Db4objects.Drs.Test.SPCParent)
				);
			EnsureNames(A().Provider(), "p3", "c3");
			EnsureNames(B().Provider(), "p3", "c3");
		}

		private void StoreParentAndChildToProviderA()
		{
			Db4objects.Drs.Test.SPCChild child = new Db4objects.Drs.Test.SPCChild("c1");
			Db4objects.Drs.Test.SPCParent parent = new Db4objects.Drs.Test.SPCParent(child, "p1"
				);
			A().Provider().StoreNew(parent);
			A().Provider().Commit();
			EnsureNames(A().Provider(), "p1", "c1");
		}
	}
}
