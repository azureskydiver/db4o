namespace com.db4o.drs.test
{
	public class SimpleArrayTest : com.db4o.drs.test.DrsTestCase
	{
		public virtual void Test()
		{
			StoreListToProviderA();
			ReplicateAllToProviderBFirstTime();
			ModifyInProviderB();
			ReplicateAllStep2();
			AddElementInProviderA();
			ReplicateHolderStep3();
		}

//		protected override void Clean()
//		{
//			Delete(new System.Type[] { typeof(com.db4o.drs.test.SimpleArrayHolder), 
//	                    typeof(com.db4o.drs.test.SimpleArrayContent
//				) });
//		}

		private void StoreListToProviderA()
		{
			com.db4o.drs.test.SimpleArrayHolder sah = new com.db4o.drs.test.SimpleArrayHolder
				("h1");
			com.db4o.drs.test.SimpleArrayContent sac1 = new com.db4o.drs.test.SimpleArrayContent
				("c1");
			com.db4o.drs.test.SimpleArrayContent sac2 = new com.db4o.drs.test.SimpleArrayContent
				("c2");
			sah.Add(sac1);
			sah.Add(sac2);
			A().Provider().StoreNew(sah);
			A().Provider().Commit();
			EnsureContent(A().Provider(), new string[] { "h1" }, new string[] { "c1", "c2" });
		}

		private void ReplicateAllToProviderBFirstTime()
		{
			ReplicateAll(A().Provider(), B().Provider());
			EnsureContent(A().Provider(), new string[] { "h1" }, new string[] { "c1", "c2" });
			EnsureContent(B().Provider(), new string[] { "h1" }, new string[] { "c1", "c2" });
		}

		private void ModifyInProviderB()
		{
			com.db4o.drs.test.SimpleArrayHolder sah = (com.db4o.drs.test.SimpleArrayHolder)GetOneInstance
				(B().Provider(), typeof(com.db4o.drs.test.SimpleArrayHolder));
			sah.SetName("h2");
			com.db4o.drs.test.SimpleArrayContent sac1 = sah.GetArr()[0];
			com.db4o.drs.test.SimpleArrayContent sac2 = sah.GetArr()[1];
			sac1.SetName("co1");
			sac2.SetName("co2");
			B().Provider().Update(sac1);
			B().Provider().Update(sac2);
			B().Provider().Update(sah);
			B().Provider().Commit();
			EnsureContent(B().Provider(), new string[] { "h2" }, new string[] { "co1", "co2" }
				);
		}

		private void ReplicateAllStep2()
		{
			ReplicateAll(B().Provider(), A().Provider());
			EnsureContent(B().Provider(), new string[] { "h2" }, new string[] { "co1", "co2" }
				);
			EnsureContent(A().Provider(), new string[] { "h2" }, new string[] { "co1", "co2" }
				);
		}

		private void AddElementInProviderA()
		{
			com.db4o.drs.test.SimpleArrayHolder sah = (com.db4o.drs.test.SimpleArrayHolder)GetOneInstance
				(A().Provider(), typeof(com.db4o.drs.test.SimpleArrayHolder));
			sah.SetName("h3");
			com.db4o.drs.test.SimpleArrayContent lc3 = new com.db4o.drs.test.SimpleArrayContent
				("co3");
			A().Provider().StoreNew(lc3);
			sah.Add(lc3);
			A().Provider().Update(sah);
			A().Provider().Commit();
			EnsureContent(A().Provider(), new string[] { "h3" }, new string[] { "co1", "co2", 
				"co3" });
		}

		private void ReplicateHolderStep3()
		{
			ReplicateClass(A().Provider(), B().Provider(), typeof(com.db4o.drs.test.SimpleArrayHolder
				));
			EnsureContent(A().Provider(), new string[] { "h3" }, new string[] { "co1", "co2", 
				"co3" });
			EnsureContent(B().Provider(), new string[] { "h3" }, new string[] { "co1", "co2", 
				"co3" });
		}

		private void EnsureContent(com.db4o.drs.inside.TestableReplicationProviderInside 
			provider, string[] holderNames, string[] contentNames)
		{
			int holderCount = holderNames.Length;
			int contentCount = contentNames.Length;
			EnsureInstanceCount(provider, typeof(com.db4o.drs.test.SimpleArrayHolder), holderCount
				);
			EnsureInstanceCount(provider, typeof(com.db4o.drs.test.SimpleArrayContent), contentCount
				);
			int i = 0;
			com.db4o.ObjectSet objectSet = provider.GetStoredObjects(typeof(com.db4o.drs.test.SimpleArrayHolder
				));
			while (objectSet.HasNext())
			{
				com.db4o.drs.test.SimpleArrayHolder lh = (com.db4o.drs.test.SimpleArrayHolder)objectSet
					.Next();
				Db4oUnit.Assert.AreEqual(holderNames[i], lh.GetName());
				com.db4o.drs.test.SimpleArrayContent[] sacs = lh.GetArr();
				for (int j = 0; j < contentNames.Length; j++)
				{
					Db4oUnit.Assert.AreEqual(contentNames[j], sacs[j].GetName());
				}
			}
		}
	}
}
