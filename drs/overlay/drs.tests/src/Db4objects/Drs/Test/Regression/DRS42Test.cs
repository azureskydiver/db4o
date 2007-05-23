/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

namespace Db4objects.Drs.Test.Regression
{
	public class DRS42Test : Db4objects.Drs.Test.DrsTestCase
	{
		internal Db4objects.Drs.Test.Regression.NewPilot andrew = new Db4objects.Drs.Test.Regression.NewPilot
			("Andrew", 100, new int[] { 100, 200, 300 });

		public virtual void Test()
		{
			StoreToProviderA();
			ReplicateAllToProviderB();
		}

		internal virtual void StoreToProviderA()
		{
			Db4objects.Drs.Inside.ITestableReplicationProviderInside provider = A().Provider(
				);
			provider.StoreNew(andrew);
			provider.Commit();
			EnsureContent(andrew, provider);
		}

		internal virtual void ReplicateAllToProviderB()
		{
			ReplicateAll(A().Provider(), B().Provider());
			EnsureContent(andrew, B().Provider());
		}

		private void EnsureContent(Db4objects.Drs.Test.Regression.NewPilot newPilot, Db4objects.Drs.Inside.ITestableReplicationProviderInside
			 provider)
		{
			Db4objects.Db4o.IObjectSet result = provider.GetStoredObjects(typeof(Db4objects.Drs.Test.Regression.NewPilot)
				);
			Db4oUnit.Assert.AreEqual(1, result.Count);
			Db4objects.Drs.Test.Regression.NewPilot p = (Db4objects.Drs.Test.Regression.NewPilot
				)result.Next();
			Db4oUnit.Assert.AreEqual(newPilot.GetName(), p.GetName());
			Db4oUnit.Assert.AreEqual(newPilot.GetPoints(), p.GetPoints());
			for (int j = 0; j < newPilot.GetArr().Length; j++)
			{
				Db4oUnit.Assert.AreEqual(newPilot.GetArr()[j], p.GetArr()[j]);
			}
		}
	}
}
